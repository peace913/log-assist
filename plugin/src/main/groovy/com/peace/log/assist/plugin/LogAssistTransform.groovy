package com.peace.log.assist.plugin

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.ide.common.internal.WaitableExecutor
import com.google.common.collect.Sets
import com.peace.log.assist.plugin.util.Compressor
import com.peace.log.assist.plugin.util.Decompressor
import com.peace.log.assist.plugin.visitor.CommonClassVisitor
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.concurrent.Callable

class LogAssistTransform extends Transform {
    static Logger logger

    LogAssistTransform(Project project) {
        logger = project.logger
    }

    @Override
    String getName() {
        return "LogAssist"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return Collections.singleton(QualifiedContent.DefaultContentType.CLASSES)
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return Sets.immutableEnumSet(
                QualifiedContent.Scope.PROJECT,
                QualifiedContent.Scope.SUB_PROJECTS,
                QualifiedContent.Scope.EXTERNAL_LIBRARIES
        )
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        logger.info("incremental: ${transformInvocation.incremental}")

        if (!transformInvocation.incremental) {
            transformInvocation.outputProvider.deleteAll()
        }

        WaitableExecutor waitableExecutor = WaitableExecutor.useGlobalSharedThreadPool()
        transformInvocation.inputs.each {input ->
            input.directoryInputs.each {dirInput ->
                waitableExecutor.execute(new Callable<Object>() {
                    @Override
                    Object call() throws Exception {
                        traverseDir(transformInvocation, dirInput)
                        return null
                    }
                })
            }

            input.jarInputs.each {jarInput ->
                waitableExecutor.execute(new Callable<Object>() {
                    @Override
                    Object call() throws Exception {
                        traverseJar(transformInvocation, jarInput)
                        return null
                    }
                })
            }
        }
        waitableExecutor.waitForTasksWithQuickFail(true)
    }

    private static void traverseDir(TransformInvocation transformInvocation, DirectoryInput dirInput) {
        def outDir = transformInvocation.outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
        int pathLen = dirInput.file.toString().length()

        String name = getProjectName(dirInput)

        if (transformInvocation.incremental) {
            //增量编译
            Map<File, Status> changedFiles = dirInput.changedFiles
            if (changedFiles != null && !changedFiles.isEmpty()) {
                File file
                Status status
                for (Map.Entry<File, Status> entry : changedFiles.entrySet()) {
                    file = entry.key
                    status = entry.value
                    logger.info("changedFile: ${file.name}, status: ${status}, exist: ${file.exists()}")

                    def path = "${file.toString().substring(pathLen)}"
                    def output = new File(outDir, path)

                    switch (status) {
                        case Status.REMOVED:
                            if (output.exists()) {
                                FileUtils.forceDelete(output)
                            }
                            break
                        case Status.ADDED:
                        case Status.CHANGED:
                            traverseClass("PROJECT", name, file, output)
                            break
                    }
                }
            }
        } else {
            //非增量编译
            if (dirInput.file != null && dirInput.file.exists()) {
                dirInput.file.traverse {File file ->
                    def path = "${file.toString().substring(pathLen)}"
                    def output = new File(outDir, path)
                    traverseClass("PROJECT", name, file, output)
                }
            }
        }
    }

    private static void traverseClass(String scope, String sdkName, File inputFile, File outputFile) {
        if (inputFile.exists()) {
            if (inputFile.isDirectory()) {
                if (!outputFile.exists()) {
                    outputFile.mkdirs()
                }
            } else {
                if (!outputFile.parentFile.exists()) {
                    outputFile.parentFile.mkdirs()
                }

                injectClass(scope, sdkName, inputFile, outputFile)
            }
        }
    }

    private static void injectClass(String scope, String sdkName, File input, File output) {
        if (!input.exists()) {
            return
        }

        if (!input.name.endsWith('.class')) {
            output.bytes = input.bytes
            return
        }

        def inputStream = new FileInputStream(input)
        ClassReader cr = new ClassReader(inputStream)
        if (needInject(cr.getClassName())) {
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
            ClassVisitor cv = new CommonClassVisitor(Opcodes.ASM6, cw, scope, sdkName)
            cr.accept(cv, 0)
            output.bytes = cw.toByteArray()
        } else {
            output.bytes = input.bytes
        }
        inputStream.close()
    }

    private static boolean needInject(String className) {
        switch (className) {
            case 'com/peace/log/assist/LogAssistService':
                return false
        }

        return true
    }

    private static void traverseJar(TransformInvocation transformInvocation, JarInput jarInput) {
        if (!jarInput.file.exists()) {
            return
        }

        if (transformInvocation.incremental) {
            def dest = transformInvocation.outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
            switch (jarInput.status) {
                case Status.REMOVED:
                    if (dest.exists()) {
                        FileUtils.forceDelete(dest)
                    }
                    break
                case Status.ADDED:
                case Status.CHANGED:
                    if (dest.exists()) {
                        FileUtils.forceDelete(dest)
                    }
                    injectJar(transformInvocation, jarInput)
                    break
            }
        } else {
            injectJar(transformInvocation, jarInput)
        }
    }

    private static void injectJar(TransformInvocation transformInvocation, JarInput jarInput) {
        def jarName = jarInput.file.name

        if (jarName.endsWith('.jar')) {
            jarName = jarName.substring(0, jarName.indexOf('.jar'))
        }

        File unzipDir = new File(jarInput.file.getParent(), jarName + '_unzip')
        if (unzipDir.exists()) {
            FileUtils.forceDelete(unzipDir)
        }

        unzipDir.mkdirs()
        Decompressor.uncompress(jarInput.file, unzipDir)

        File repackageDir = new File(jarInput.file.getParent(), jarName + '_repackage')
        if (repackageDir.exists()) {
            FileUtils.forceDelete(repackageDir)
        }

        //文件处理
        unzipDir.eachFileRecurse({
            File outputFile = new File(repackageDir, it.absolutePath.split('_unzip')[1])

            if (it.isDirectory()) {
                if (!outputFile.exists()) {
                    outputFile.mkdirs()
                }
            } else {
                if (!outputFile.parentFile.exists()) {
                    outputFile.parentFile.mkdirs()
                }

                String scope = ""
                switch (jarInput.scopes[0]) {
                    case QualifiedContent.Scope.PROJECT:
                        scope = "PROJECT"
                        break
                    case QualifiedContent.Scope.SUB_PROJECTS:
                        scope = "MODULE"
                        break
                    case QualifiedContent.Scope.EXTERNAL_LIBRARIES:
                        scope = "JAR"
                        break
                }
                injectClass(scope, jarName, it, outputFile)
            }
        })

        def dest = transformInvocation.outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
        Compressor cp = new Compressor(dest.absolutePath)
        cp.compress(repackageDir.absolutePath)
    }

    private static String getProjectName(DirectoryInput dirInput) {
        File dirFile = dirInput.file
        while (dirFile != null && dirFile.name != 'build') {
            dirFile = dirFile.parentFile
        }

        if (dirFile != null && dirFile.name == 'build') {
            return dirFile.parentFile.name
        }

        return ""
    }
}
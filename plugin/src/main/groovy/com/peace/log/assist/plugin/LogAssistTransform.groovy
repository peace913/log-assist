package com.peace.log.assist.plugin

import com.android.SdkConstants
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
import com.google.common.io.Files
import com.peace.log.assist.plugin.visitor.CommonClassVisitor
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.concurrent.Callable
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

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
                    logger.info("changedFile: ${file.name}, status: ${status}}")

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
                            transformFile("PROJECT", name, file, output)
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
                    transformFile("PROJECT", name, file, output)
                }
            }
        }
    }

    private static void transformFile(String scope, String sdkName, File inputFile, File outputFile) {
        if (inputFile.exists()) {
            if (inputFile.isDirectory()) {
                if (!outputFile.exists()) {
                    outputFile.mkdirs()
                }
            } else {
                if (!outputFile.parentFile.exists()) {
                    outputFile.parentFile.mkdirs()
                }

                if (!inputFile.name.endsWith(SdkConstants.DOT_CLASS)) {
                    //资源文件直接复制
                    outputFile.bytes = inputFile.bytes
                    return
                }

                FileInputStream fis = null
                FileOutputStream fos = null
                try {
                    fis = new FileInputStream(inputFile)
                    fos = new FileOutputStream(outputFile)
                    injectClass(scope, sdkName, fis, fos)
                } finally {
                    DefaultGroovyMethodsSupport.closeWithWarning(fis)
                    DefaultGroovyMethodsSupport.closeWithWarning(fos)
                }
            }
        }
    }

    private static void injectClass(String scope, String sdkName, InputStream inputStream, OutputStream output) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        transferBytes(inputStream, baos)
        byte [] input = baos.toByteArray()

        ClassReader cr = new ClassReader(input)
        if (needInject(cr.getClassName())) {
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
            ClassVisitor cv = new CommonClassVisitor(Opcodes.ASM6, cw, scope, sdkName)
            cr.accept(cv, 0)
            output.write(cw.toByteArray())
        } else {
            output.write(input)
        }
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
                    transformJar(transformInvocation, jarInput)
                    break
            }
        } else {
            transformJar(transformInvocation, jarInput)
        }
    }

    private static void transformJar(TransformInvocation transformInvocation, JarInput jarInput)
            throws IOException {
        logger.info("changedJar: ${jarInput.file}, status: ${jarInput.status.name()}}")
        File outputJar = transformInvocation.outputProvider.getContentLocation(jarInput.name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
        Files.createParentDirs(outputJar)

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

        ZipInputStream zis = null
        ZipOutputStream zos = null
        try {
            FileInputStream fis = new FileInputStream(jarInput.file)
            zis = new ZipInputStream(fis)
            FileOutputStream fos = new FileOutputStream(outputJar)
            zos = new ZipOutputStream(fos)
            ZipEntry entry = zis.getNextEntry()

            while (entry != null) {
                if (!entry.isDirectory()) {
                    if (entry.getName().endsWith(SdkConstants.DOT_CLASS) ) {
                        zos.putNextEntry(new ZipEntry(entry.getName()))
                        injectClass(scope, jarInput.name, zis, zos)
                    } else {
                        //资源文件直接复制
                        zos.putNextEntry(new ZipEntry(entry.getName()))
                        transferBytes(zis, zos)
                    }
                }
                entry = zis.getNextEntry()
            }
        } finally {
            DefaultGroovyMethodsSupport.closeWithWarning(zis)
            DefaultGroovyMethodsSupport.closeWithWarning(zos)
        }
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

    private static void transferBytes(InputStream is, OutputStream os) throws IOException {
        // reading the content of the file within a byte buffer
        byte[] byteBuffer = new byte[8192]
        int nbByteRead /* = 0*/

        while ((nbByteRead = is.read(byteBuffer)) != -1) {
            // appends buffer
            os.write(byteBuffer, 0, nbByteRead)
        }
    }
}
package com.peace.log.assist.plugin.visitor

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class CommonMethodVisitor extends MethodVisitor {
    private String scope
    private String sname
    private String cname
    private String mname
    private int line

    CommonMethodVisitor(int api, MethodVisitor mv, String scope, String sdkName, String className, String methodName) {
        super(api, mv)
        this.scope = scope
        sname = sdkName
        cname = className
        mname = methodName
    }

    @Override
    void visitLineNumber(int line, Label start) {
        super.visitLineNumber(line, start)
        this.line = line
    }

    @Override
    void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (injectLog(opcode, owner, name, desc, itf)) {
            return
        }

        super.visitMethodInsn(opcode, owner, name, desc, itf)
    }

    private boolean injectLog(int opcode, String owner, String name, String desc, boolean itf) {
        boolean injected = false
        if (opcode == Opcodes.INVOKESTATIC && owner == 'android/util/Log') {
            if (name == 'd') {
                if (desc == '(Ljava/lang/String;Ljava/lang/String;)I') {
                    mv.visitLdcInsn(scope)
                    mv.visitLdcInsn(sname)
                    mv.visitLdcInsn(cname)
                    mv.visitLdcInsn(mname)
                    mv.visitLdcInsn(line)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 'com/peace/log/assist/LogAssistService', 'd', '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I', false)
                    injected = true
                } else if (desc == '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I') {
                    mv.visitLdcInsn(scope)
                    mv.visitLdcInsn(sname)
                    mv.visitLdcInsn(cname)
                    mv.visitLdcInsn(mname)
                    mv.visitLdcInsn(line)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 'com/peace/log/assist/LogAssistService', 'd', '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I', false)
                    injected = true
                }
            } else if (name == 'v') {
                if (desc == '(Ljava/lang/String;Ljava/lang/String;)I') {
                    mv.visitLdcInsn(scope)
                    mv.visitLdcInsn(sname)
                    mv.visitLdcInsn(cname)
                    mv.visitLdcInsn(mname)
                    mv.visitLdcInsn(line)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 'com/peace/log/assist/LogAssistService', 'v', '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I', false)
                    injected = true
                } else if (desc == '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I') {
                    mv.visitLdcInsn(scope)
                    mv.visitLdcInsn(sname)
                    mv.visitLdcInsn(cname)
                    mv.visitLdcInsn(mname)
                    mv.visitLdcInsn(line)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 'com/peace/log/assist/LogAssistService', 'v', '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I', false)
                    injected = true
                }
            } else if (name == 'i') {
                if (desc == '(Ljava/lang/String;Ljava/lang/String;)I') {
                    mv.visitLdcInsn(scope)
                    mv.visitLdcInsn(sname)
                    mv.visitLdcInsn(cname)
                    mv.visitLdcInsn(mname)
                    mv.visitLdcInsn(line)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 'com/peace/log/assist/LogAssistService', 'i', '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I', false)
                    injected = true
                } else if (desc == '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I') {
                    mv.visitLdcInsn(scope)
                    mv.visitLdcInsn(sname)
                    mv.visitLdcInsn(cname)
                    mv.visitLdcInsn(mname)
                    mv.visitLdcInsn(line)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 'com/peace/log/assist/LogAssistService', 'i', '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I', false)
                    injected = true
                }
            } else if (name == 'w') {
                if (desc == '(Ljava/lang/String;Ljava/lang/String;)I') {
                    mv.visitLdcInsn(scope)
                    mv.visitLdcInsn(sname)
                    mv.visitLdcInsn(cname)
                    mv.visitLdcInsn(mname)
                    mv.visitLdcInsn(line)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 'com/peace/log/assist/LogAssistService', 'w', '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I', false)
                    injected = true
                } else if (desc == '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I') {
                    mv.visitLdcInsn(scope)
                    mv.visitLdcInsn(sname)
                    mv.visitLdcInsn(cname)
                    mv.visitLdcInsn(mname)
                    mv.visitLdcInsn(line)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 'com/peace/log/assist/LogAssistService', 'w', '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I', false)
                    injected = true
                } else if (desc == '(Ljava/lang/String;Ljava/lang/Throwable;)I') {
                    mv.visitLdcInsn(scope)
                    mv.visitLdcInsn(sname)
                    mv.visitLdcInsn(cname)
                    mv.visitLdcInsn(mname)
                    mv.visitLdcInsn(line)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 'com/peace/log/assist/LogAssistService', 'w', '(Ljava/lang/String;Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I', false)
                    injected = true
                }
            } else if (name == 'e') {
                if (desc == '(Ljava/lang/String;Ljava/lang/String;)I') {
                    mv.visitLdcInsn(scope)
                    mv.visitLdcInsn(sname)
                    mv.visitLdcInsn(cname)
                    mv.visitLdcInsn(mname)
                    mv.visitLdcInsn(line)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 'com/peace/log/assist/LogAssistService', 'e', '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I', false)
                    injected = true
                } else if (desc == '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I') {
                    mv.visitLdcInsn(scope)
                    mv.visitLdcInsn(sname)
                    mv.visitLdcInsn(cname)
                    mv.visitLdcInsn(mname)
                    mv.visitLdcInsn(line)
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, 'com/peace/log/assist/LogAssistService', 'e', '(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I', false)
                    injected = true
                }
            }
        }

        return injected
    }
}
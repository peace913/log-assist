package com.peace.log.assist.plugin.visitor

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor

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
        //TODO
    }
}
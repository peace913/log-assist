package com.peace.log.assist.plugin.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class CommonClassVisitor extends ClassVisitor {
    private String scope
    private String sname
    private String cname
    private boolean injected = true

    CommonClassVisitor(int api, ClassVisitor cv, String scope, String sdkName) {
        super(api, cv)
        this.scope = scope
        sname = sdkName
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        cname = name

        if (interfaces != null) {
            for (int i = 0; i != interfaces.length; ++i) {
                if ('com/peace/log/assist/LogListener' == interfaces[i]) {
                    //避免死循环，LogListener的实现类里的Log不注入
                    injected = false
                    break
                }
            }
        }
    }

    @Override
    void visitSource(String source, String debug) {
        super.visitSource(source, debug)
        if (source != null) {
            def index = source.lastIndexOf('.')
            if (index >= 0) {
                cname += source.substring(index)
            }
        }
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions)

        if (injected && mv != null) {
            return new CommonMethodVisitor(Opcodes.ASM6, mv, scope, sname, cname, name)
        }

        return mv
    }
}
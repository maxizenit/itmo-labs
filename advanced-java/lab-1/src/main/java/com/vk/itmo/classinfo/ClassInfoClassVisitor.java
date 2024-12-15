package com.vk.itmo.classinfo;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassInfoClassVisitor extends ClassVisitor {

    private final ClassInfoStorage classInfoStorage;

    private String currentClassName;

    public ClassInfoClassVisitor(ClassInfoStorage classInfoStorage) {
        super(Opcodes.ASM9);
        this.classInfoStorage = classInfoStorage;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (currentClassName != null) {
            throw new IllegalStateException("Repeated method invocation is prohibited");
        }

        currentClassName = name;
        classInfoStorage.addNewClass(name, superName);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        classInfoStorage.incrementFieldsCountInClass(currentClassName);
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        classInfoStorage.addMethodWithDescriptor(currentClassName, name, descriptor);
        return new ClassInfoMethodVisitor(classInfoStorage, currentClassName);
    }
}

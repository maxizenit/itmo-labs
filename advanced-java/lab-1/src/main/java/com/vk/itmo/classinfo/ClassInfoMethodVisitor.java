package com.vk.itmo.classinfo;

import com.vk.itmo.enm.AbcMetrics;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassInfoMethodVisitor extends MethodVisitor {

    private final ClassInfoStorage classInfoStorage;
    private final String currentClassName;

    protected ClassInfoMethodVisitor(ClassInfoStorage classInfoStorage, String currentClassName) {
        super(Opcodes.ASM9);
        this.classInfoStorage = classInfoStorage;
        this.currentClassName = currentClassName;
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        if (AbcMetrics.ASSIGNMENT.getOpcodes().contains(opcode)) {
            classInfoStorage.incrementAssignmentsCountInClass(currentClassName);
        }
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        classInfoStorage.incrementBranchesCountInClass(currentClassName);
        if (AbcMetrics.CONDITION.getOpcodes().contains(opcode)) {
            classInfoStorage.incrementConditionsCountInClass(currentClassName);
        }
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        classInfoStorage.incrementBranchesCountInClass(currentClassName);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        classInfoStorage.incrementBranchesCountInClass(currentClassName);
    }
}

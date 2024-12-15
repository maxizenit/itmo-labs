package com.vk.itmo.enm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

@Getter
@RequiredArgsConstructor
public enum AbcMetrics {

    ASSIGNMENT(Set.of(
            ISTORE,
            LSTORE,
            FSTORE,
            DSTORE,
            ASTORE
    )),
    BRANCH(Set.of(
            IFEQ,
            IFNE,
            IFLT,
            IFGE,
            IFGT,
            IFLE,
            IF_ICMPEQ,
            IF_ICMPNE,
            IF_ICMPGE,
            IF_ICMPGT,
            IF_ICMPLE,
            IF_ACMPEQ,
            IF_ACMPNE,
            IFNULL,
            IFNONNULL,
            GOTO,
            JSR
    )),
    CONDITION(Set.of(
            IFEQ,
            IFNE,
            IFLT,
            IFGE,
            IFGT,
            IFLE,
            IF_ICMPEQ,
            IF_ICMPNE,
            IF_ICMPGE,
            IF_ICMPGT,
            IF_ICMPLE,
            IF_ACMPEQ,
            IF_ACMPNE,
            IFNULL,
            IFNONNULL
    ));

    private final Set<Integer> opcodes;
}

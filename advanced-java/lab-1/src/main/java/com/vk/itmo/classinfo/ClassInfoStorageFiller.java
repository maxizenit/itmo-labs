package com.vk.itmo.classinfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Getter
@RequiredArgsConstructor
public class ClassInfoStorageFiller {

    private final ClassInfoStorage storage;

    public void fillFromJarFile(String fileName) throws IOException {
        try (JarFile jar = new JarFile(fileName)) {
            for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    ClassReader reader = new ClassReader(jar.getInputStream(entry));
                    ClassInfoClassVisitor visitor = new ClassInfoClassVisitor(storage);
                    reader.accept(visitor, 0);
                }
            }
        }
    }
}

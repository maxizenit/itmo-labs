package ru.itmo.textanalyzer.jmorphy.core.util.fileloader;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
public class FSFileLoader implements FileLoader {

    private final String basePath;

    @Override
    public InputStream newStream(String filename) throws IOException {
        return new FileInputStream(new File(basePath, filename));
    }
}

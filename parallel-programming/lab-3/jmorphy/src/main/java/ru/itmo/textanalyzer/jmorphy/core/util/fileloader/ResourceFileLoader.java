package ru.itmo.textanalyzer.jmorphy.core.util.fileloader;

import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@RequiredArgsConstructor
public class ResourceFileLoader implements FileLoader {

    private final String basePath;

    @Override
    public InputStream newStream(String filename) {
        return getClass().getResourceAsStream(basePath + "/" + filename);
    }
}

package ru.itmo.textanalyzer.jmorphy.core.util.fileloader;

import java.io.IOException;
import java.io.InputStream;

public interface FileLoader {

    InputStream newStream(String filename) throws IOException;
}

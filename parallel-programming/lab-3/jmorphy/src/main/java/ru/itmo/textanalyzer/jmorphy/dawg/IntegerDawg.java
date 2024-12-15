package ru.itmo.textanalyzer.jmorphy.dawg;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class IntegerDawg extends Dawg {

    public IntegerDawg(InputStream stream) throws IOException {
        super(stream);
    }

    public Integer get(String key) {
        return get(key, null);
    }

    public Integer get(String key, Integer defaultValue) {
        int res = dict.find(key.getBytes(StandardCharsets.UTF_8));
        if (res == Dict.MISSING) {
            return defaultValue;
        }
        return res;
    }
}

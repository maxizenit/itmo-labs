package ru.itmo.textanalyzer.jmorphy.dawg;

import org.apache.commons.io.input.SwappedDataInputStream;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Dawg {

    protected final DataInput input;
    protected final Dict dict;

    public Dawg(InputStream stream) throws IOException {
        input = new SwappedDataInputStream(new BufferedInputStream(stream));
        dict = new Dict(input);
    }

    public List<String> prefixes(String key) {
        int index = Dict.ROOT;
        List<String> res = new ArrayList<>();
        int pos = 1;
        for (byte b : key.getBytes(StandardCharsets.UTF_8)) {
            index = dict.followByte(b, index);
            if (index == Dict.MISSING) {
                break;
            }
            if (dict.hasValue(index)) {
                res.add(key.substring(0, pos / 2));
            }
            pos++;
        }
        return res;
    }
}

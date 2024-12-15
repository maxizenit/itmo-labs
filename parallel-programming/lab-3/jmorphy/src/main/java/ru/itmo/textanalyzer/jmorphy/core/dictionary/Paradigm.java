package ru.itmo.textanalyzer.jmorphy.core.dictionary;

import java.io.DataInput;
import java.io.IOException;

public class Paradigm {

    private final short[] data;
    private final int length;

    public Paradigm(DataInput input) throws IOException {
        short size = input.readShort();
        assert size % 3 == 0 : size;

        this.data = new short[size];
        for (int i = 0; i < size; i++) {
            this.data[i] = input.readShort();
        }
        this.length = size / 3;
    }

    public int getNormSuffixId() {
        return data[0];
    }

    public int getNormPrefixId() {
        return data[length * 2];
    }

    public int getStemSuffixId(short idx) {
        return data[idx];
    }

    public int getStemPrefixId(short idx) {
        return data[length * 2 + idx];
    }

    public int getTagId(short idx) {
        return data[length + idx];
    }

    public int size() {
        return length;
    }
}
package ru.itmo.textanalyzer.jmorphy.dawg.payload;

import java.io.DataInput;
import java.io.IOException;

public class Guide {

    private final byte[] units;

    public Guide(DataInput input) throws IOException {
        int baseSize = input.readInt();
        int size = baseSize * 2;
        units = new byte[size];
        for (int i = 0; i < size; i++) {
            units[i] = input.readByte();
        }
    }

    public byte child(int index) {
        return units[index * 2];
    }

    public byte sibling(int index) {
        return units[index * 2 + 1];
    }

    public int size() {
        return units.length;
    }
}

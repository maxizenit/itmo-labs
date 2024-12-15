package ru.itmo.textanalyzer.jmorphy.dawg.payload;

import lombok.RequiredArgsConstructor;
import ru.itmo.textanalyzer.jmorphy.dawg.Dict;

import java.util.ArrayList;

@RequiredArgsConstructor
public class Completer {

    private static final int INITIAL_KEY_LENGTH = 9;

    private final Dict dict;
    private final Guide guide;

    private byte[] key;
    private int keyLength;
    private ArrayList<Integer> indexStack;
    private int lastIndex;

    public void start(int index) {
        key = new byte[INITIAL_KEY_LENGTH];
        keyLength = 0;
        indexStack = new ArrayList<>();
        indexStack.add(index);
        lastIndex = 0;
    }

    public boolean next() {
        int index = indexStack.getLast();

        if (lastIndex != 0) {
            byte childLabel = guide.child(index);

            if (childLabel != 0) {
                index = follow(childLabel, index);
                if (index == Dict.MISSING) {
                    return false;
                }
            } else {
                while (true) {
                    byte siblingLabel = guide.sibling(index);

                    if (keyLength > 0) {
                        keyLength--;
                    }

                    indexStack.removeLast();
                    if (indexStack.isEmpty()) {
                        return false;
                    }

                    index = indexStack.getLast();
                    if (siblingLabel != 0) {
                        index = follow(siblingLabel, index);
                        if (index == Dict.MISSING) {
                            return false;
                        }

                        break;
                    }
                }
            }
        }

        return findTerminal(index);
    }

    public byte[] getKey() {
        byte[] newKey = new byte[keyLength];
        System.arraycopy(key, 0, newKey, 0, keyLength);
        return newKey;
    }

    private int follow(byte label, int index) {
        int nextIndex = dict.followByte(label, index);
        if (nextIndex == Dict.MISSING) {
            return Dict.MISSING;
        }

        addLabel(label);
        indexStack.add(nextIndex);
        return nextIndex;
    }

    private boolean findTerminal(int index) {
        while (!dict.hasValue(index)) {
            byte label = guide.child(index);

            index = dict.followByte(label, index);
            if (index == Dict.MISSING) {
                return false;
            }

            addLabel(label);
            indexStack.add(index);
        }

        lastIndex = index;
        return true;
    }

    private void addLabel(byte label) {
        if (keyLength == key.length) {
            byte[] newKey = new byte[key.length * 2];
            System.arraycopy(key, 0, newKey, 0, key.length);
            key = newKey;
        }
        key[keyLength] = label;
        keyLength++;
    }
}
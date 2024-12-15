package ru.itmo.textanalyzer.jmorphy.dawg;

import org.apache.commons.codec.binary.Base64;
import ru.itmo.textanalyzer.jmorphy.core.model.Payload;
import ru.itmo.textanalyzer.jmorphy.dawg.payload.Completer;
import ru.itmo.textanalyzer.jmorphy.dawg.payload.Guide;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PayloadsDawg extends Dawg {

    private static final byte PAYLOAD_SEPARATOR = 0x01;

    private final Guide guide;

    public PayloadsDawg(InputStream stream) throws IOException {
        super(stream);
        guide = new Guide(input);
    }

    protected List<Payload> valueForIndex(int index, String key) {
        List<Payload> values = new ArrayList<>();
        Completer completer = new Completer(dict, guide);
        completer.start(index);
        while (completer.next()) {
            values.add(new Payload(key, decodeValue(completer.getKey())));
        }
        return values;
    }

    protected byte[] decodeValue(byte[] value) {
        return Base64.decodeBase64(value);
    }

    public List<Payload> similarItems(String key) {
        return similarItems(key, null);
    }

    public List<Payload> similarItems(String key, Map<Character, String> replaceChars) {
        return similarItems(key, replaceChars, "", Dict.ROOT);
    }

    private List<Payload> similarItems(String key, Map<Character, String> replaceChars, String prefix, int index) {
        List<Payload> items = new ArrayList<>();
        int keyLength = key.length();
        int prefixLength = prefix.length();

        for (int i = prefixLength; i < keyLength; i++) {
            char c = key.charAt(i);

            if (replaceChars != null) {
                String replaces = replaceChars.get(c);
                if (replaces != null) {
                    int replacesLength = replaces.length();
                    for (int j = 0; j < replacesLength; j++) {
                        String r = replaces.substring(j, j + 1);
                        int nextIndex = dict.followBytes(r.getBytes(StandardCharsets.UTF_8), index);
                        if (nextIndex != Dict.MISSING) {
                            String nextPrefix = prefix + key.substring(prefixLength, i) + r;
                            items.addAll(similarItems(key, replaceChars, nextPrefix, nextIndex));
                        }
                    }
                }
            }

            index = dict.followBytes(Character.toString(c).getBytes(StandardCharsets.UTF_8), index);
            if (index == Dict.MISSING) {
                return items;
            }
        }

        if (index == Dict.MISSING) {
            return items;
        }

        index = dict.followByte(PAYLOAD_SEPARATOR, index);
        if (index == Dict.MISSING) {
            return items;
        }

        return valueForIndex(index, prefix + key.substring(prefixLength));
    }
}

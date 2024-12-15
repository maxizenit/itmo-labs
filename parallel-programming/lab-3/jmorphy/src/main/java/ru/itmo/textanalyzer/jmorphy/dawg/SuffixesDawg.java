package ru.itmo.textanalyzer.jmorphy.dawg;

import ru.itmo.textanalyzer.jmorphy.core.model.Payload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SuffixesDawg extends PayloadsDawg {

    public SuffixesDawg(InputStream stream) throws IOException {
        super(stream);
    }

    protected SuffixForm decodePayload(Payload payload) {
        ByteBuffer data = ByteBuffer.wrap(payload.value());
        short count = data.getShort();
        short paradigmId = data.getShort();
        short idx = data.getShort();
        return new SuffixForm(payload.key(), count, paradigmId, idx);
    }

    public List<SuffixForm> similarSuffixes(String word, Map<Character, String> replaceChars) {
        List<SuffixForm> foundSuffixes = new ArrayList<>();
        for (Payload payload : similarItems(word, replaceChars)) {
            foundSuffixes.add(decodePayload(payload));
        }
        return foundSuffixes;
    }

    public record SuffixForm(String word, short count, short paradigmId, short idx) {
    }
}

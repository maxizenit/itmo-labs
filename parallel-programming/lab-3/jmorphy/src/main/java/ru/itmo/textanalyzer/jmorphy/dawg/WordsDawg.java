package ru.itmo.textanalyzer.jmorphy.dawg;

import ru.itmo.textanalyzer.jmorphy.core.model.Payload;
import ru.itmo.textanalyzer.jmorphy.core.model.WordForm;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class WordsDawg extends PayloadsDawg {

    public WordsDawg(InputStream stream) throws IOException {
        super(stream);
    }

    protected WordForm decodePayload(Payload payload) {
        ByteBuffer data = ByteBuffer.wrap(payload.value());
        short paradigmId = data.getShort();
        short idx = data.getShort();
        return new WordForm(payload.key(), paradigmId, idx);
    }

    public List<WordForm> similarWords(String word, Map<Character, String> replaceChars) {
        List<WordForm> foundWords = new ArrayList<>();
        for (Payload payload : similarItems(word, replaceChars)) {
            foundWords.add(decodePayload(payload));
        }
        return foundWords;
    }


}

package ru.itmo.textanalyzer.worker.util;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NamesReplacer {

    public String replaceAllNames(String text, Set<String> names, String replacement) {
        for (String name : names) {
            Pattern pattern =
                    Pattern.compile("\\b" + Pattern.quote(name) + "\\b", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CHARACTER_CLASS);
            Matcher matcher = pattern.matcher(text);
            text = matcher.replaceAll(replacement);
        }
        return text;
    }
}

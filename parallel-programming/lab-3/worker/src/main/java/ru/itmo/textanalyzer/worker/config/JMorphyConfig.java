package ru.itmo.textanalyzer.worker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itmo.textanalyzer.jmorphy.MorphAnalyzer;

@Configuration
public class JMorphyConfig {

    @Bean
    public MorphAnalyzer morphAnalyzer() {
        return MorphAnalyzer.createMorphAnalyzer();
    }
}

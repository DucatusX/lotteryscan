package io.lastwill.eventscan.services;

import io.lastwill.eventscan.model.TokenInfo;
import io.lastwill.eventscan.model.TokenType;
import io.lastwill.eventscan.repositories.TokenEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Slf4j
public class DataBaseIdFiller {
    @Autowired
    RandomMd5Generator generator;
    @Autowired
    TokenEntryRepository tokenRepository;
    @Value("${io.lastwill.eventscan.8gramm-generate-value}")
    private int eightGramValue;
    @Value("${io.lastwill.eventscan.10gramm-generate-value}")
    private int tenGramValue;

    @PostConstruct
    public void fillDataBase() {
        List<TokenInfo> tokens = tokenRepository.findAll();
        if (tokens != null && !tokens.isEmpty()) {
            log.info("Token is not empty, skip filling");
            return;
        }
        log.info("Token DB is empty start filling");
        generator.generateMoreMd5Random(eightGramValue).forEach(userId -> {
            tokenRepository.save(new TokenInfo(userId, TokenType.SMALL));
        });

        generator.generateMoreMd5Random(tenGramValue).forEach(userId -> {
            tokenRepository.save(new TokenInfo(userId, TokenType.BIG));
        });
        log.info("Token DB filling completed!");
    }
}

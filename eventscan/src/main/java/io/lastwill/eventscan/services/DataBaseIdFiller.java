package io.lastwill.eventscan.services;

import io.lastwill.eventscan.model.TokenInfo;
import io.lastwill.eventscan.repositories.TokenEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@ConditionalOnBean(TokenInfo.class)
@Component
public class DataBaseIdFiller {
    @Autowired
    RandomMd5Generator generator;
    @Autowired
    TokenEntryRepository tokenRepository;
    @Value("${io.lastwill.eventscan.generate-value}")
    private int generateValue;

    @PostConstruct
    public void fillDataBase() {
        List<TokenInfo> tokens = tokenRepository.findAll();
        if (tokens != null && !tokens.isEmpty()) {
            return;
        }
        generator.generateMoreMd5Random(generateValue).forEach(userId -> {
            tokenRepository.save(new TokenInfo(userId));
        });
    }
}

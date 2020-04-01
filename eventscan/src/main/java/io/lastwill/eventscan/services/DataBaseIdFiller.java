package io.lastwill.eventscan.services;

import io.lastwill.eventscan.model.TokenInfo;
import io.lastwill.eventscan.model.TokenType;
import io.lastwill.eventscan.repositories.TokenEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    @Value("${io.lastwill.eventscan.8gramm-file-name}")
    private String eightGramFileName;
    @Value("${io.lastwill.eventscan.10gramm-file-name}")
    private String tenGramFileName;


    @PostConstruct
    public void fillDataBase() throws IOException {
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
        writeOnFile();

    }

    private void writeOnFile() throws IOException {
        String tmpDir = System.getProperty("java.io.tmpdir");
        File smallFile = new File(tmpDir.concat(eightGramFileName));
        smallFile.delete();
        if (smallFile.createNewFile()) {
            try (FileWriter writer = new FileWriter(smallFile, true)) {
                List<String> allTokens = tokenRepository.findAllIdByTokenType(TokenType.SMALL.getName());
                allTokens.forEach(id -> {
                    try {
                        writer.write(id);
                        writer.write(System.lineSeparator());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException ignored) {

            }
        }
        log.debug("Save small tokens to {}", smallFile.getAbsolutePath());

        File bigFile = new File(tmpDir.concat(tenGramFileName));
        bigFile.delete();
        if (bigFile.createNewFile()) {
            try (FileWriter writer = new FileWriter(bigFile, true)) {
                List<String> allTokens = tokenRepository.findAllIdByTokenType(TokenType.BIG.getName());
                allTokens.forEach(id -> {
                    try {
                        writer.write(id);
                        writer.write(System.lineSeparator());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException ignored) {

            }
        }
        log.debug("Save small tokens to {}", bigFile.getAbsolutePath());
    }
}


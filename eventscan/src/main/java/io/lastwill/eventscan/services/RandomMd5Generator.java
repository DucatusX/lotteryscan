package io.lastwill.eventscan.services;

import org.apache.commons.codec.digest.DigestUtils;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

public class RandomMd5Generator {
    private static int MAX_TRY_GENERATE = 100;

    /**
     * Generate a certain value of random MD5
     *
     * @param generateValue - value of MD5
     * @return Set of MD5
     */
    public Set<String> generateMoreMd5Random(int generateValue) {
        return this.generateMoreMd5Random(generateValue, 0);
    }

    /**
     * Generate one MD5 from random string.
     * String building from alphabet(char array).
     * String length = count iterations in cycle.
     *
     * @return String random MD5.
     */
    public String generateMd5Random() {
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        int random;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            random = (int) (Math.random() * alphabet.length);
            builder.append(alphabet[random]);
        }
        return DigestUtils.md5Hex(builder.toString());
    }

    private Set<String> generateMoreMd5Random(int generateValue, int countGenerate) {
        Set<String> result = new HashSet<>();
        if (generateValue >= 0) {
            for (int i = 0; i < generateValue; i++) {
                result.add(this.generateMd5Random());
            }
            if (result.size() < generateValue) {
                if (countGenerate >= MAX_TRY_GENERATE) {
                    result.clear();
                } else {
                    result = this.generateMoreMd5Random(generateValue, ++countGenerate);
                }
            }
        }
        return result;
    }

    @PostConstruct
    private void fillDataBase() {

    }
}
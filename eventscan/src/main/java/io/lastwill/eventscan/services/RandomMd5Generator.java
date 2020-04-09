package io.lastwill.eventscan.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class RandomMd5Generator {
    private static int MAX_TRY_GENERATE = 100;
    private final int multiplicity = 4;

    /**
     * Generate a certain value of random MD5
     *
     * @param generateValue - value of MD5
     * @return Set of MD5
     */
    public Map<String, String> generateMoreMd5Random(int generateValue) {
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
        String result = "";
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        int random;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            random = (int) (Math.random() * alphabet.length);
            builder.append(alphabet[random]);
        }
        result = DigestUtils.md5Hex(builder.toString());
        result.toUpperCase();
        return result;
    }

    public String generatePublic(String secret) {
        String md5 = DigestUtils.md5Hex(secret);
        String result = "";
        char[] array = md5.toCharArray();
        array[0] = 'p';
        array[1] = 'u';
        array[2] = 'b';
        result = String.valueOf(array);
        result.toUpperCase();
        return result;
    }

    private Map<String, String> generateMoreMd5Random(int generateValue, int countGenerate) {
        Map<String, String> result = new HashMap<>();
        if (generateValue >= 0) {
            for (int i = 0; i < generateValue; i++) {
                String secretCode = this.generateMd5Random();
                result.put(this.generateMd5Random(), generatePublic(secretCode));
            }
            Set<String> pubCodes = new HashSet<>(result.values());
            if (result.size() < generateValue || pubCodes.size() != result.size()) {
                if (countGenerate >= MAX_TRY_GENERATE) {
                    log.info("Try generate id more than {}. Can't generate with unique", MAX_TRY_GENERATE);
                    result.clear();
                } else {
                    log.info("Can't generate unique id. from {} generate {} unique. Try {} times", generateValue, result.size(), countGenerate + 1);
                    result = this.generateMoreMd5Random(generateValue, ++countGenerate);
                }
            }
        }
        return result;
    }

    public String convertToUserFriendlyFormat(String prototype) {
        int start = 0;
        int iter = 0;
        int stop = multiplicity;
        char[] arrayPrototype = prototype.toCharArray();
        if (arrayPrototype.length % multiplicity != 0) {
            throw new RuntimeException("Code length = " + arrayPrototype.length);
        }

        StringBuilder builder = new StringBuilder();
        while (builder.length() - iter < arrayPrototype.length - 1) {
            builder.append(Arrays.copyOfRange(arrayPrototype, start, stop));
            iter++;
            start = stop;
            stop += multiplicity;
            if (builder.length() - iter != arrayPrototype.length - 1) {
                builder.append("-");
            }
        }
        String result = builder.toString();
        result = result.toUpperCase();
        return result;
    }

    public String convertToDBFormat(String prototype) {
        String result = prototype.toUpperCase();
        result = result.replace("-", "");
        return result;
    }

}

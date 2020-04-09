package io.lastwill.eventscan.service;

import io.lastwill.eventscan.services.RandomMd5Generator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class RandomMd5GeneratorTest {
    RandomMd5Generator randomMd5Generator = new RandomMd5Generator();

    @Test
    public void whenGenerateRandomThenReturnString() {
        String result = randomMd5Generator.generateMd5Random();
        Assert.assertNotNull(result);
        Assert.assertNotEquals(0, result.length());
    }

    @Test
    public void whenGenerateRandomThenStringLengthIs32() {
        String result = randomMd5Generator.generateMd5Random();
        Assert.assertEquals(32, result.length());
    }

    @Test
    public void whenGenerateMoreRandomThenReturnMoreMap() {
        int size = 100;
        Map<String, String> randoms = randomMd5Generator.generateMoreMd5Random(size);
        Assert.assertEquals(randoms.size(), size);
        randoms.values().forEach(e -> Assert.assertEquals("pub", e.substring(0, 3)));
    }

    @Test
    public void whenConvertUserFriendlyFormatThenUpperCase() {
        String random = randomMd5Generator.generateMd5Random();
        String userFriendly = randomMd5Generator.convertToUserFriendlyFormat(random);

        Assert.assertNotEquals(random, userFriendly);

        String withoutDelimeter = userFriendly.replace("-", "");
        Assert.assertNotEquals(random, withoutDelimeter);

        Assert.assertTrue(random.equalsIgnoreCase(withoutDelimeter));
    }

    @Test
    public void whenConverFromUserFormatToDBFormatThenOnlyChangeRegister() {
        Map<String, String> randoms = randomMd5Generator.generateMoreMd5Random(1);
        String secretCode = randoms.keySet().iterator().next();
        String publicCode = randoms.values().iterator().next();

        Assert.assertNotEquals(secretCode, publicCode);

        String userFriendlySecret = randomMd5Generator.convertToUserFriendlyFormat(secretCode);
        String userFriendlyPublic = randomMd5Generator.convertToUserFriendlyFormat(publicCode);

        Assert.assertNotEquals(secretCode, userFriendlySecret);
        Assert.assertNotEquals(publicCode, userFriendlyPublic);

        String dbSecret = randomMd5Generator.convertToDBFormat(userFriendlySecret);
        String dbPublic = randomMd5Generator.convertToDBFormat(userFriendlyPublic);

        Assert.assertNotEquals(secretCode, dbSecret);
        Assert.assertNotEquals(publicCode, dbPublic);

        Assert.assertEquals(secretCode.toUpperCase(), dbSecret);
        Assert.assertEquals(publicCode.toUpperCase(), dbPublic);
    }
}

package com.cagan.library.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.security.SecureRandom;

@UtilityClass
public final class RandomUtil {
    private static final int DEF_COUNT = 20;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generateRandomAlphanumericString() {
        return RandomStringUtils.random(20, 0, 0, true, true, (char[])null, SECURE_RANDOM);
    }

    public static String generatePassword() {
        return generateRandomAlphanumericString();
    }

    public static String generateActivationKey() {
        return generateRandomAlphanumericString();
    }

    static {
        SECURE_RANDOM.nextBytes(new byte[64]);
    }
}

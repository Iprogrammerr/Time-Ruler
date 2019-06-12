package com.iprogrammerr.time.ruler.tool;

import java.util.Random;

public class RandomStrings {

    private static final int MAX_RANDOM_SIZE = 10_000;
    private static final int MAX_ASCII = 256;
    private static final int ALPHABETIC_LOWER_CASE_MIN = 97;
    private static final int ALPHABETIC_LOWER_CASE_MAX = 122;
    private static final int ALPHABETIC_UPPER_CASE_MIN = 65;
    private static final int ASCII_DIGIT_MIN = 48;
    private static final int ASCII_DIGIT_MAX = 57;
    private static final int MIN_NAME_SIZE = 3;
    private static final int MAX_NAME_SIZE = 25;
    private static final char AT = '@';
    private static final char DOT = '.';
    private final Random random;

    public RandomStrings(Random random) {
        this.random = random;
    }

    public RandomStrings() {
        this(new Random());
    }

    public String random(int size) {
        int range = 1 + MAX_ASCII;
        char[] chars = new char[size];
        for (int i = 0; i < size; i++) {
            chars[i] = (char) (random.nextInt(range));
        }
        return new String(chars);
    }

    public String random() {
        return random(random.nextInt(MAX_RANDOM_SIZE));
    }

    public String nonAlphabetic(int size) {
        int upperStart = 1 + ALPHABETIC_LOWER_CASE_MAX;
        char[] chars = new char[size];
        for (int i = 0; i < size; i++) {
            char c;
            if (random.nextBoolean()) {
                c = (char) random.nextInt(ALPHABETIC_LOWER_CASE_MIN);
            } else {
                c = (char) (upperStart + (random.nextInt(MAX_ASCII - ALPHABETIC_LOWER_CASE_MAX)));
            }
            chars[i] = c;
        }
        return new String(chars);
    }

    public String nonAlphabetic() {
        return nonAlphabetic(1 + random.nextInt(MAX_RANDOM_SIZE));
    }

    public String alphabetic(int size) {
        int range = 1 + ALPHABETIC_LOWER_CASE_MAX - ALPHABETIC_LOWER_CASE_MIN;
        char[] chars = new char[size];
        for (int i = 0; i < size; i++) {
            int min = random.nextBoolean() ? ALPHABETIC_UPPER_CASE_MIN : ALPHABETIC_LOWER_CASE_MIN;
            chars[i] = (char) (min + random.nextInt(range));
        }
        return new String(chars);
    }

    public String alphabetic() {
        return alphabetic(1 + random.nextInt(MAX_RANDOM_SIZE));
    }

    public String alphanumeric(int size) {
        int alphaRange = 1 + ALPHABETIC_LOWER_CASE_MAX - ALPHABETIC_LOWER_CASE_MIN;
        int digitRange = 1 + ASCII_DIGIT_MAX - ASCII_DIGIT_MIN;
        char[] chars = new char[size];
        for (int i = 0; i < size; i++) {
            int min;
            int range;
            if (random.nextBoolean()) {
                min = random.nextBoolean() ? ALPHABETIC_UPPER_CASE_MIN : ALPHABETIC_LOWER_CASE_MIN;
                range = alphaRange;
            } else {
                min = ASCII_DIGIT_MIN;
                range = digitRange;
            }
            chars[i] = (char) (min + random.nextInt(range));
        }
        return new String(chars);
    }

    public String alphanumeric() {
        return alphanumeric(1 + random.nextInt(MAX_RANDOM_SIZE));
    }

    public String limitedAlphanumeric(int limit) {
        return alphanumeric(1 + random.nextInt(limit));
    }

    public String name() {
        return alphabetic(MIN_NAME_SIZE + random.nextInt(MAX_NAME_SIZE - MIN_NAME_SIZE));
    }

    public String email() {
        return new StringBuilder()
            .append(alphabetic(3 + random.nextInt(10)))
            .append(AT).append(alphanumeric(2 + random.nextInt(5)))
            .append(DOT).append(alphabetic(3))
            .toString();
    }
}

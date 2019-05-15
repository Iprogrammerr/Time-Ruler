package com.iprogrammerr.time.ruler.tool;

import com.iprogrammerr.time.ruler.model.user.User;

import java.util.Random;

public class RandomUsers {

    private static final int MAX_NAME_LENGTH = 25;
    private static final int MAX_PASSWORD_LENGTH = 50;
    private final RandomStrings randomStrings;
    private final Random random;

    public RandomUsers(RandomStrings randomStrings, Random random) {
        this.randomStrings = randomStrings;
        this.random = random;
    }

    public RandomUsers(Random random) {
        this(new RandomStrings(random), random);
    }

    public RandomUsers() {
        this(new Random());
    }

    public User user() {
        return user(random.nextLong());
    }

    public User inactive() {
        return user(random.nextLong(), false);
    }

    public User user(long id) {
        return user(id, random.nextBoolean());
    }

    private User user(long id, boolean active) {
        return new User(id, randomStrings.alphabetic(1 + random.nextInt(MAX_NAME_LENGTH)), randomStrings.email(),
            randomStrings.alphanumeric(1 + MAX_PASSWORD_LENGTH),
            active);
    }
}

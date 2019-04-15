package com.iprogrammerr.time.ruler.mock;

import com.iprogrammerr.time.ruler.model.activity.Activity;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RandomActivities {

    private static final int DAY_SECONDS = (int) TimeUnit.DAYS.toSeconds(1);
    private static final int MAX_NAME_LENGTH = 50;
    private final RandomStrings strings;
    private final Random random;

    public RandomActivities(RandomStrings strings, Random random) {
        this.strings = strings;
        this.random = random;
    }

    public RandomActivities(Random random) {
        this(new RandomStrings(random), random);
    }

    public RandomActivities() {
        this(new Random());
    }

    public Activity activity(long id, long dayId, boolean done) {
        String name = strings.alphabetic(1 + random.nextInt(MAX_NAME_LENGTH));
        int end = 1 + random.nextInt(DAY_SECONDS);
        int start = random.nextInt(end);
        return new Activity(id, name, dayId, start, end, done);
    }

    public Activity activity(long dayId, boolean done) {
        return activity(0, dayId, done);
    }

    public Activity activity(long dayId) {
        return activity(dayId, random.nextBoolean());
    }

    public Activity activity(boolean done) {
        return activity(0, done);
    }

    public Activity activity() {
        return activity(random.nextBoolean());
    }
}

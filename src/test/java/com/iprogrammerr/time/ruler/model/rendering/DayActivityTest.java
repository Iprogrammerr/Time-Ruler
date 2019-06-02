package com.iprogrammerr.time.ruler.model.rendering;

import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.tool.RandomActivities;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.Instant;
import java.util.Random;
import java.util.function.Function;

public class DayActivityTest {

    private static final int MAX_OFFSET_VALUE = 3600;

    @Test
    public void appliesTimeTransformation() {
        int offset = new Random().nextInt(MAX_OFFSET_VALUE);
        RandomActivities activities = new RandomActivities();
        DateTimeFormatting formatting = new DateTimeFormatting();
        Function<Long, Instant> timeTransformation = secs -> Instant.ofEpochSecond(secs)
            .plusSeconds(offset);
        Activity activity = activities.activity();
        String start = formatting.time(timeTransformation.apply(activity.startDate));
        String end = formatting.time(timeTransformation.apply(activity.endDate));
        MatcherAssert.assertThat("Does not apply transformation",
            new DayActivity(activity.id, activity.name, start, end, activity.done),
            Matchers.equalTo(new DayActivity(activity, formatting, timeTransformation)));
    }
}

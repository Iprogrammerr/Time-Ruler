package com.iprogrammerr.time.ruler.model.rendering;

import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.tool.RandomActivities;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class FoundActivityTest {

    private static final int MAX_OFFSET_VALUE = (int) TimeUnit.DAYS.toSeconds(1);

    @Test
    public void appliesDateTransformation() {
        int offset = new Random().nextInt(MAX_OFFSET_VALUE);
        RandomActivities activities = new RandomActivities();
        DateTimeFormatting formatting = new DateTimeFormatting();
        Function<Long, Instant> dateTransformation = secs -> Instant.ofEpochSecond(secs)
            .plusSeconds(offset);
        Activity activity = activities.activity();
        String date = formatting.dateTimeRange(dateTransformation.apply(activity.startDate),
            dateTransformation.apply(activity.endDate));
        MatcherAssert.assertThat("Does not apply transformation",
            new FoundActivity(activity.id, date, activity.name),
            Matchers.equalTo(new FoundActivity(activity, formatting, dateTransformation)));
    }
}

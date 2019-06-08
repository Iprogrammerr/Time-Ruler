package com.iprogrammerr.time.ruler.view;

import com.iprogrammerr.time.ruler.matcher.rendering.DayPlanViewsMatcher;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.setup.TestTemplatesSetup;
import com.iprogrammerr.time.ruler.tool.RandomActivities;
import com.iprogrammerr.time.ruler.view.rendering.DayPlanViews;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class DayPlanViewsTest {

    private static final int MAX_TIME_TRANSFORMATION = (int) TimeUnit.DAYS.toSeconds(20);
    private static final int MAX_ACTIVITIES = 30;
    private final DateTimeFormatting formatting = new DateTimeFormatting();
    private final ViewsTemplates templates = new TestTemplatesSetup().templates();
    private final DayPlanViews views = new DayPlanViews(templates, formatting);


    @Test
    public void returnsView() {
        Random random = new Random();
        RandomActivities randomActivities = new RandomActivities();
        List<Activity> activities = new ArrayList<>();
        for (int i = 0; i < random.nextInt(MAX_ACTIVITIES); i++) {
            activities.add(randomActivities.activity());
        }
        int timeOffset = random.nextInt(MAX_TIME_TRANSFORMATION);
        Function<Long, Instant> timeTransformation = t -> Instant.ofEpochSecond(t)
            .plusSeconds(timeOffset);
        MatcherAssert.assertThat("Does not return properly rendered view", views,
            new DayPlanViewsMatcher(templates, formatting, Instant.now(), timeTransformation, activities));
    }
}

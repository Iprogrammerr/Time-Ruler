package com.iprogrammerr.time.ruler.view.rendering;

import com.iprogrammerr.time.ruler.matcher.rendering.DayPlanExecutionViewsMatcher;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.setup.TestTemplatesSetup;
import com.iprogrammerr.time.ruler.tool.RandomActivities;
import com.iprogrammerr.time.ruler.view.ViewsTemplates;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class DayPlanExecutionViewsTest {

    private static final int MAX_TIME_TRANSFORMATION = (int) TimeUnit.DAYS.toSeconds(10);
    private static final int MAX_ACTIVITIES = 10;
    private final DateTimeFormatting formatting = new DateTimeFormatting();
    private final ViewsTemplates templates = new TestTemplatesSetup().templates();
    private final DayPlanExecutionViews views = new DayPlanExecutionViews(templates, formatting);


    @Test
    public void returnsView() {
        Random random = new Random();
        boolean history = random.nextBoolean();
        RandomActivities randomActivities = new RandomActivities();
        List<Activity> activities = new ArrayList<>();
        for (int i = 0; i < random.nextInt(MAX_ACTIVITIES); i++) {
            activities.add(randomActivities.activity());
        }
        int timeOffset = random.nextInt(MAX_TIME_TRANSFORMATION);
        Function<Long, Instant> timeTransformation = t -> Instant.ofEpochSecond(t)
            .plusSeconds(timeOffset);
        MatcherAssert.assertThat("Does not return properly rendered view", views,
            new DayPlanExecutionViewsMatcher(templates, formatting, Instant.now(), history,
                activities, timeTransformation));
    }
}

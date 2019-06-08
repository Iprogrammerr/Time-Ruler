package com.iprogrammerr.time.ruler.view;

import com.iprogrammerr.time.ruler.matcher.rendering.ActivityErrorsViewMatcher;
import com.iprogrammerr.time.ruler.matcher.rendering.EmptyActivityViewMatcher;
import com.iprogrammerr.time.ruler.matcher.rendering.FilledActivityViewMatcher;
import com.iprogrammerr.time.ruler.model.activity.DescribedActivity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.setup.TestTemplatesSetup;
import com.iprogrammerr.time.ruler.tool.RandomActivities;
import com.iprogrammerr.time.ruler.tool.RandomStrings;
import com.iprogrammerr.time.ruler.validation.ValidateableName;
import com.iprogrammerr.time.ruler.validation.ValidateableTime;
import com.iprogrammerr.time.ruler.view.rendering.ActivityViews;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.function.Function;

public class ActivityViewsTest {

    private final DateTimeFormatting formatting = new DateTimeFormatting();
    private ViewsTemplates templates;
    private ActivityViews views;

    @Before
    public void setup() {
        templates = new TestTemplatesSetup().templates();
        views = new ActivityViews(templates, formatting);
    }

    @Test
    public void returnsEmptyView() {
        ZonedDateTime time = ZonedDateTime.now();
        boolean plan = new Random().nextBoolean();
        MatcherAssert.assertThat("Does not return empty rendering", views, new EmptyActivityViewMatcher(templates,
            formatting, time, plan));
    }

    @Test
    public void returnsInvalidTimeErrorView() {
        returnsErrorView(false);
    }

    @Test
    public void returnsInvalidNameErrorView() {
        returnsErrorView(true);
    }

    private void returnsErrorView(boolean invalidName) {
        Random random = new Random();
        ZonedDateTime time = ZonedDateTime.now();
        boolean plan = random.nextBoolean();
        RandomStrings strings = new RandomStrings(random);
        ValidateableName name;
        if (invalidName) {
            name = new ValidateableName(strings.alphanumeric());
        } else {
            name = new ValidateableName(strings.name());
        }
        ValidateableTime startTime;
        ValidateableTime endTime;
        if (invalidName) {
            startTime = new ValidateableTime(formatting.time(Instant.ofEpochMilli(random.nextLong())));
            endTime = new ValidateableTime(formatting.time(Instant.ofEpochMilli(random.nextLong())));
        } else {
            startTime = new ValidateableTime(strings.alphanumeric());
            endTime = new ValidateableTime(strings.alphanumeric());
        }
        String description = strings.alphanumeric();
        String reason = invalidName ? "Does not return error view with invalid name" :
            "Does not return error view with invalid time";
        MatcherAssert.assertThat(reason, views, new ActivityErrorsViewMatcher(templates, formatting,
            time, plan, name, startTime, endTime, description));
    }

    @Test
    public void returnsFilledView() {
        DescribedActivity activity = new DescribedActivity(new RandomActivities().activity(),
            new RandomStrings().alphanumeric());
        Function<Long, ZonedDateTime> timeTransformation = t -> ZonedDateTime.ofInstant(Instant.ofEpochSecond(t),
            ZoneOffset.UTC);
        MatcherAssert.assertThat("Does not return filled view", views,
            new FilledActivityViewMatcher(templates, formatting, activity, timeTransformation));
    }
}

package com.iprogrammerr.time.ruler.view;

import com.iprogrammerr.time.ruler.matcher.rendering.ActivitiesViewsMatcher;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import com.iprogrammerr.time.ruler.model.date.DateTimeFormatting;
import com.iprogrammerr.time.ruler.model.rendering.Page;
import com.iprogrammerr.time.ruler.setup.TestTemplatesSetup;
import com.iprogrammerr.time.ruler.tool.RandomActivities;
import com.iprogrammerr.time.ruler.tool.RandomStrings;
import com.iprogrammerr.time.ruler.view.rendering.ActivitiesViews;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ActivitiesViewsTest {

    private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    private static final int MAX_PAGES_NUMBER = 100;
    private static final int MAX_ACTIVITIES_NUMBER = 100;
    private static final int MAX_URL_SIZE = 50;
    private final DateTimeFormatting formatting = new DateTimeFormatting(DATE_TIME_FORMAT, TIME_FORMAT,
        DATE_TIME_FORMAT);
    private ViewsTemplates templates;
    private ActivitiesViews views;

    @Before
    public void setup() {
        templates = new TestTemplatesSetup().templates();
        views = new ActivitiesViews(templates, formatting);
    }

    @Test
    public void returnsPlannedView() {
        returnsView(true);
    }

    private void returnsView(boolean plan) {
        Random random = new Random();
        RandomStrings randomStrings = new RandomStrings(random);
        String pattern = randomStrings.alphanumeric();
        int currentPage = 1 + random.nextInt(MAX_PAGES_NUMBER);
        List<Page> pages = new ArrayList<>(MAX_PAGES_NUMBER);
        for (int i = 0; i < MAX_PAGES_NUMBER; i++) {
            String url = randomStrings.alphanumeric(MAX_URL_SIZE);
            pages.add(new Page(url, i + 1));
        }
        RandomActivities randomActivities = new RandomActivities(random);
        int activitiesNumber = 1 + random.nextInt(MAX_ACTIVITIES_NUMBER);
        List<Activity> activities = new ArrayList<>(activitiesNumber);
        for (int i = 0; i < activitiesNumber; i++) {
            activities.add(randomActivities.activity());
        }
        MatcherAssert.assertThat("Should render rendering based on given parameters", views,
            new ActivitiesViewsMatcher(templates, plan, pattern, currentPage, pages, activities, formatting));
    }

    @Test
    public void returnsUnplannedView() {
        returnsView(false);
    }

    @Test
    public void returnsEmptyView() {
        Random random = new Random();
        MatcherAssert.assertThat("Should render empty rendering", views,
            new ActivitiesViewsMatcher(templates, random.nextBoolean(), "", random.nextInt(),
                new ArrayList<>(), new ArrayList<>(), formatting));
    }
}

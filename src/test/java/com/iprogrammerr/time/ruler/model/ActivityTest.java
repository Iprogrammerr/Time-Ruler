package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.mock.RandomActivities;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Random;

public class ActivityTest {

    @Test
    public void longerIntersectsWithShorter() {
        Activity longer = new RandomActivities().activity();
        Activity shorter = withNewTimes(longer, 1, -1);
        MatcherAssert.assertThat("Longer should intersect with shorter", longer.intersects(shorter),
            Matchers.equalTo(true));
    }

    private Activity withNewTimes(Activity activity, long startOffset, long endOffset) {
        return new Activity(activity.userId, activity.name, activity.startDate + startOffset,
            activity.endDate + endOffset, activity.done);
    }

    @Test
    public void shorterIntersectsWithLonger() {
        Activity shorter = new RandomActivities().activity();
        Activity longer = withNewTimes(shorter, -1, 1);
        MatcherAssert.assertThat("Shorter should intersect with longer", shorter.intersects(longer),
            Matchers.equalTo(true));
    }

    @Test
    public void shiftedIntersectsWithOriginal() {
        Random random = new Random();
        Activity original = new RandomActivities(random).activity();
        int shift = random.nextBoolean()? 1 : -1;
        Activity shifted = withNewTimes(original, shift, shift);
        MatcherAssert.assertThat("Original should intersect with shifted", original.intersects(shifted));
    }

    @Test
    public void intersectsWithItself() {
        Activity activity = new RandomActivities().activity();
        MatcherAssert.assertThat("Should intersect with itself", activity.intersects(activity),
            Matchers.equalTo(true));
    }

    @Test
    public void doesNotIntersectWithScheduledBefore() {
        Activity activity = new RandomActivities().activity();
        Activity before = withNewTimes(activity, -activity.startDate, -activity.startDate);
        MatcherAssert.assertThat("Should not intersect with scheduled before", activity.intersects(before),
            Matchers.equalTo(false));
    }

    @Test
    public void doesNotIntersectWithScheduledAfter() {
        Activity activity = new RandomActivities().activity();
        long offset = activity.endDate - activity.startDate;
        Activity after = withNewTimes(activity, offset, offset);
        MatcherAssert.assertThat("Should not intersect with scheduled after", activity.intersects(after),
            Matchers.equalTo(false));
    }
}

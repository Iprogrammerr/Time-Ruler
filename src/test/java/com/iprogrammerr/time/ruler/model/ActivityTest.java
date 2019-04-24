package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.mock.RandomActivities;
import com.iprogrammerr.time.ruler.model.activity.Activity;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

public class ActivityTest {

    @Test
    public void longerIntersectsWithShorter() {
        Activity longer = new RandomActivities().activity();
        Activity shorter = withNewTimes(longer, 1, -1);
        MatcherAssert.assertThat("Longer should intersect with shorter", longer.intersects(shorter),
            Matchers.equalTo(true));
    }

    private Activity withNewTimes(Activity activity, int startOffset, int endOffset) {
        return new Activity(activity.name, activity.dayId, activity.startTime + startOffset,
            activity.endTime + endOffset, activity.done);
    }

    @Test
    public void shorterIntersectsWithLonger() {
        Activity shorter = new RandomActivities().activity();
        Activity longer = withNewTimes(shorter, -1, 1);
        MatcherAssert.assertThat("Shorter should intersect with longer", shorter.intersects(longer),
            Matchers.equalTo(true));
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
        Activity before = withNewTimes(activity, -activity.startTime, -activity.startTime);
        MatcherAssert.assertThat("Should not intersect with scheduled before", activity.intersects(before),
            Matchers.equalTo(false));
    }

    @Test
    public void doesNotIntersectWithScheduledAfter() {
        Activity activity = new RandomActivities().activity();
        int offset = activity.endTime - activity.startTime;
        Activity after = withNewTimes(activity, offset, offset);
        MatcherAssert.assertThat("Should not intersect with scheduled after", activity.intersects(after),
            Matchers.equalTo(false));
    }
}

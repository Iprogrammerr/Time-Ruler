package com.iprogrammerr.time.ruler.model.activity;

import java.util.Objects;

public class DescribedActivity {

    public final Activity activity;
    public final String description;

    public DescribedActivity(Activity activity, String description) {
        this.activity = activity;
        this.description = description;
    }

    @Override
    public boolean equals(Object other) {
        boolean equal = false;
        if (this == other) {
            equal = true;
        } else if (other != null && other.getClass().isAssignableFrom(DescribedActivity.class)) {
            DescribedActivity otherActivity = (DescribedActivity) other;
            equal = activity.equals(otherActivity.activity) && description.equals(otherActivity.description);
        }
        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(activity, description);
    }
}

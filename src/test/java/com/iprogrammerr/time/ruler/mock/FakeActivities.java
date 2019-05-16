package com.iprogrammerr.time.ruler.mock;

import com.iprogrammerr.time.ruler.model.activity.Activities;
import com.iprogrammerr.time.ruler.model.activity.Activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeActivities implements Activities {

    private final Map<Long, Activity> source;
    private long nextId;

    public FakeActivities() {
        source = new HashMap<>();
        nextId = 1;
    }

    @Override
    public long create(Activity activity) {
        source.put(nextId, activity);
        nextId++;
        return nextId - 1;
    }

    @Override
    public void update(Activity activity) {
        if (source.containsKey(activity.id)) {
            source.put(activity.id, activity);
        }
    }

    @Override
    public void delete(long id) {
        source.remove(id);
    }

    @Override
    public Optional<Activity> activity(long id) {
        return Optional.ofNullable(source.get(id));
    }

    @Override
    public boolean belongsToUser(long userId, long activityId) {
        Optional<Activity> activity = activity(activityId);
        return activity.isPresent() && activity.get().userId == userId;
    }

    @Override
    public void setDone(long id, boolean done) {
        Optional<Activity> activity = activity(id);
        activity.ifPresent(a ->
            source.put(id, new Activity(a.id, a.userId, a.name, a.startDate, a.endDate, done))
        );
    }
}

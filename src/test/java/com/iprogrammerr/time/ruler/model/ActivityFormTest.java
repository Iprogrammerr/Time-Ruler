package com.iprogrammerr.time.ruler.model;

import com.iprogrammerr.time.ruler.model.form.ActivityForm;
import com.iprogrammerr.time.ruler.model.form.FormParams;
import com.iprogrammerr.time.ruler.tool.ConvertableMap;
import com.iprogrammerr.time.ruler.tool.RandomStrings;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Random;

public class ActivityFormTest {

    @Test
    public void readsForm() {
        Random random = new Random();
        RandomStrings randomStrings = new RandomStrings(random);
        ActivityForm form = new ActivityForm(randomStrings.name(), randomStrings.alphanumeric(),
            randomStrings.alphanumeric(), randomStrings.alphanumeric(), random.nextBoolean());
        ConvertableMap formMap = new ConvertableMap()
            .put(FormParams.NAME, form.name).put(FormParams.START_TIME, form.startTime)
            .put(FormParams.END_TIME, form.endTime).put(FormParams.DESCRIPTION, form.description)
            .put(FormParams.DONE, form.done);
        MatcherAssert.assertThat("Should be the same", form,
            Matchers.equalTo(ActivityForm.parsed(formMap.getConverted())));
    }

}

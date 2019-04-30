package com.iprogrammerr.time.ruler;

import com.iprogrammerr.time.ruler.database.QueryTemplates;
import org.junit.Test;

public class ExperimentalTest {

    @Test
    public void experimentalTest() {
        QueryTemplates templates = new QueryTemplates();
        System.out.println(templates.query("SELECT * FROM activity WHERE id = ? AND done = ?", 1, false));
    }
}

package com.iprogrammerr.time.ruler;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ExperimentalTest {

    @Test
    public void experimentalTest() {
        DateFormat format = new SimpleDateFormat("EEEE, dd.MM.yyyy", Locale.US);
        System.out.println(format.format(System.currentTimeMillis()));
    }
}

package com.iprogrammerr.time.ruler.model.date;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ServerClientDatesTest {

    private static final int MAX_OFFSET_VALUE = (int) TimeUnit.HOURS.toSeconds(18);
    private HttpServletRequest request;

    public void setup(int offsetValue) {
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getCookies())
            .thenReturn(new Cookie[]{new Cookie("utcOffset", String.valueOf(offsetValue))});
    }

    private int randomOffset() {
        return -MAX_OFFSET_VALUE + new Random().nextInt(2 * MAX_OFFSET_VALUE);
    }

    @Test
    public void returnsServerInstant() {
        int offset = randomOffset();
        setup(offset);
        ServerClientDates dates = new ServerClientDates();
        Instant clientDate = Instant.now();
        MatcherAssert.assertThat("Does not return server instant", dates.serverDate(request, clientDate),
            Matchers.equalTo(clientDate.plusSeconds(offset)));


    }

    @Test
    public void returnsClientInstant() {
        int offset = randomOffset();
        setup(offset);
        ServerClientDates dates = new ServerClientDates();
        Instant serverDate = Instant.now();
        MatcherAssert.assertThat("Does not return client instant", dates.clientDate(request, serverDate),
            Matchers.equalTo(serverDate.minusSeconds(offset)));
    }
}

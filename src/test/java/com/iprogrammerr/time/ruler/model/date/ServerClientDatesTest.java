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

    private static final int DEFAULT_OFFSET = 0;
    private static final int MAX_OFFSET_VALUE = (int) TimeUnit.HOURS.toSeconds(18);
    private ServerClientDates dates;
    private HttpServletRequest request;

    private void setup(int offsetValue) {
        setup(new Cookie("utcOffset", String.valueOf(offsetValue)));
    }

    private void setup(Cookie... cookies) {
        dates = new ServerClientDates();
        request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getCookies()).thenReturn(cookies);
    }

    private void emptySetup() {
        setup();
    }

    private int randomOffset() {
        return -MAX_OFFSET_VALUE + new Random().nextInt(2 * MAX_OFFSET_VALUE);
    }

    @Test
    public void returnsServerInstant() {
        int offset = randomOffset();
        setup(offset);
        Instant clientDate = Instant.now();
        MatcherAssert.assertThat("Does not return server instant", dates.serverDate(request, clientDate),
            Matchers.equalTo(clientDate.plusSeconds(offset)));
    }

    @Test
    public void returnsClientInstant() {
        int offset = randomOffset();
        setup(offset);
        Instant serverDate = Instant.now();
        MatcherAssert.assertThat("Does not return client instant", dates.clientDate(request, serverDate),
            Matchers.equalTo(serverDate.minusSeconds(offset)));
    }

    @Test
    public void returnsDefaultOffset() {
        emptySetup();
        MatcherAssert.assertThat("Does not return default offset", dates.clientUtcOffset(request),
            Matchers.equalTo(DEFAULT_OFFSET));
    }

    @Test
    public void returnsZonedClientDateFromInstant() {
        int offset = randomOffset();
        setup(offset);
        Instant date = Instant.now();
        MatcherAssert.assertThat("Does not return client zoned date from instant",
            dates.zonedClientDate(request, date).toInstant(), Matchers.equalTo(date.minusSeconds(offset)));
    }

    @Test
    public void returnsZonedClientDateFromSeconds() {
        int offset = randomOffset();
        setup(offset);
        Instant date = Instant.now();
        MatcherAssert.assertThat("Does not return client zoned date from seconds",
            dates.zonedClientDate(request, date.getEpochSecond()).toEpochSecond(),
            Matchers.equalTo(date.minusSeconds(offset).getEpochSecond()));
    }

    @Test
    public void returnsZonedClientDateFromNow() {
        int offset = randomOffset();
        setup(offset);
        MatcherAssert.assertThat("Does not return client zoned date from now",
            dates.zonedClientDate(request).toEpochSecond(),
            Matchers.equalTo(Instant.now().minusSeconds(offset).getEpochSecond()));
    }
}

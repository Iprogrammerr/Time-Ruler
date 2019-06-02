package com.iprogrammerr.time.ruler.model.date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ServerClientDates {

    private final String offsetKey;

    public ServerClientDates(String offsetKey) {
        this.offsetKey = offsetKey;
    }

    public ServerClientDates() {
        this("utcOffset");
    }

    public Instant serverDate(HttpServletRequest request, Instant clientDate) {
        return clientDate.plusSeconds(clientUtcOffset(request));
    }

    public int clientUtcOffset(HttpServletRequest request) {
        int offset = 0;
        for (Cookie c : request.getCookies()) {
            if (offsetKey.equals(c.getName())) {
                try {
                    offset = Integer.parseInt(c.getValue());
                    break;
                } catch (Exception e) {
                    offset = 0;
                }
            }
        }
        return offset;
    }

    public Instant clientDate(HttpServletRequest request, Instant serverDate) {
        return serverDate.minusSeconds(clientUtcOffset(request));
    }

    public ZonedDateTime zonedClientDate(HttpServletRequest request, Instant serverDate) {
        return inUtc(clientDate(request, serverDate));
    }

    private ZonedDateTime inUtc(Instant instant) {
        return ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public Instant clientDate(HttpServletRequest request, long serverSeconds) {
        return clientDate(request, Instant.ofEpochSecond(serverSeconds));
    }

    public ZonedDateTime zonedClientDate(HttpServletRequest request, long serverSeconds) {
        return inUtc(clientDate(request, serverSeconds));
    }

    public Instant clientDate(HttpServletRequest request) {
        return clientDate(request, Instant.now(Clock.systemUTC()));
    }

    public ZonedDateTime zonedClientDate(HttpServletRequest request) {
        return inUtc(clientDate(request));
    }
}

package com.iprogrammerr.time.ruler.model.date;

public class UTCTimeConversion implements TimeConversion {

    private final int utcOffset;

    public UTCTimeConversion(int utcOffset) {
        this.utcOffset = utcOffset;
    }

    @Override
    public int value(int origin) {

        return 0;
    }
}

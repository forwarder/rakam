package org.rakam.util;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.rakam.util.DateUtil.UTCTime;

/**
 * Created by buremba on 21/12/13.
 */

public class SpanTime implements com.hazelcast.nio.serialization.DataSerializable {
    private final static Pattern parser = Pattern.compile("^(?:([0-9]+)(month)s?)? ?(?:([0-9]+)(week)s?)? ?(?:([0-9]+)(day)s?)? ?(?:([0-9]+)(hour)s?)? ?(?:([0-9]+)(minute)s?)?$");

    public int period;
    private transient int cursor = -1;


    public SpanTime(int p) {
        this.period = p;
    }

    public SpanTime(int p, int cursor) {
        this.period = p;
        this.cursor = cursor;
    }

    public int current() {
        return cursor;
    }

    public long untilTimeFrame(int now) {
        return (now - cursor) / period;
    }

    public static SpanTime fromString(String str) throws IllegalArgumentException {
        try {
            return new SpanTime(Integer.parseInt(str));
        } catch (NumberFormatException e) {}

        Matcher match = parser.matcher(str);
        int p = 0;
        if (match.find())
            for (int i = 1; i < 10; i+=2) {
                String num_str = match.group(i);
                if (num_str != null) {
                    int num = Integer.parseInt(num_str);
                    String period = match.group(i + 1);
                    if (period.startsWith("month")) {
                        p += 60 * 60 * 24 * 7 * 30 * num;
                    } else if (period.startsWith("week")) {
                        p += 60 * 60 * 24 * 7 * num;
                    } else if (period.startsWith("day")) {
                        p += 60 * 60 * 24 * num;
                    } else if (period.startsWith("hour")) {
                        p += 60 * 60 * num;
                    } else if (period.startsWith("minute")) {
                        p += 60 * num;
                    } else if (period.startsWith("second")) {
                        p += num;
                    }
                }
            }
        if (p == 0)
                throw new IllegalArgumentException("couldn't parse interval string. usage [*month, *week, *day, *hour, *minute, *second], ");
        return new SpanTime(p);
    }

    public SpanTime previous() {
        if (cursor == -1)
            throw new IllegalStateException("you must set cursor timestamp before using this method");
        cursor -= period;
        return this;
    }

    public SpanTime copy() {
        return new SpanTime(period, cursor);
    }

    public SpanTime next() {
        if (cursor == -1)
            throw new IllegalStateException("you must set cursor timestamp before using this method");
        cursor += period;
        return this;
    }

    public String toString() {
        return Long.toString(period);
    }


    public SpanTime spanCurrent() {
        return span(UTCTime());
    }

    public SpanTime span(int now) {
        cursor = (now / period) * period;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpanTime)) return false;

        SpanTime spanTime = (SpanTime) o;

        if (period != spanTime.period) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return period;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(period);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        period = in.readInt();
    }
}

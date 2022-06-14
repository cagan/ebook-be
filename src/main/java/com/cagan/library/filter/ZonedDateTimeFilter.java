package com.cagan.library.filter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.List;

public class ZonedDateTimeFilter extends RangeFilter<ZonedDateTime> {
    private static final long serialVersionUID = 1L;

    public ZonedDateTimeFilter() {
    }

    public ZonedDateTimeFilter(ZonedDateTimeFilter filter) {
        super(filter);
    }

    public ZonedDateTimeFilter copy() {
        return new ZonedDateTimeFilter(this);
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public ZonedDateTimeFilter setEquals(ZonedDateTime equals) {
        super.setEquals(equals);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public ZonedDateTimeFilter setNotEquals(ZonedDateTime equals) {
        super.setNotEquals(equals);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public ZonedDateTimeFilter setIn(List<ZonedDateTime> in) {
        super.setIn(in);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public ZonedDateTimeFilter setNotIn(List<ZonedDateTime> notIn) {
        super.setNotIn(notIn);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public ZonedDateTimeFilter setGreaterThan(ZonedDateTime equals) {
        super.setGreaterThan(equals);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public ZonedDateTimeFilter setLessThan(ZonedDateTime equals) {
        super.setLessThan(equals);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public ZonedDateTimeFilter setGreaterThanOrEqual(ZonedDateTime equals) {
        super.setGreaterThanOrEqual(equals);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public ZonedDateTimeFilter setLessThanOrEqual(ZonedDateTime equals) {
        super.setLessThanOrEqual(equals);
        return this;
    }
}

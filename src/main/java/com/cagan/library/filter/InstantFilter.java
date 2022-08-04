package com.cagan.library.filter;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.time.Instant;
import java.util.List;

public class InstantFilter extends RangeFilter<Instant> {
    @Serial
    private static final long serialVersionUID = 1L;

    public InstantFilter() {
    }

    public InstantFilter(InstantFilter filter) {
        super(filter);
    }

    public InstantFilter copy() {
        return new InstantFilter(this);
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setEquals(Instant equals) {
        super.setEquals(equals);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setNotEquals(Instant equals) {
        super.setNotEquals(equals);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setIn(List<Instant> in) {
        super.setIn(in);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setNotIn(List<Instant> notIn) {
        super.setNotIn(notIn);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setGreaterThan(Instant equals) {
        super.setGreaterThan(equals);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setLessThan(Instant equals) {
        super.setLessThan(equals);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setGreaterThanOrEqual(Instant equals) {
        super.setGreaterThanOrEqual(equals);
        return this;
    }

    @Override
    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    public InstantFilter setLessThanOrEqual(Instant equals) {
        super.setLessThanOrEqual(equals);
        return this;
    }
}

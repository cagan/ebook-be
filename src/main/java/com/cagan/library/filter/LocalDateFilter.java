package com.cagan.library.filter;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.time.LocalDate;
import java.util.List;

public class LocalDateFilter extends RangeFilter<LocalDate> {
    @Serial
    private static final long serialVersionUID = 1L;

    public LocalDateFilter() {
    }

    public LocalDateFilter(LocalDateFilter filter) {
        super(filter);
    }

    public LocalDateFilter copy() {
        return new LocalDateFilter(this);
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE
    )
    public LocalDateFilter setEquals(LocalDate equals) {
        super.setEquals(equals);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE
    )
    public LocalDateFilter setNotEquals(LocalDate equals) {
        super.setNotEquals(equals);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE
    )
    public LocalDateFilter setIn(List<LocalDate> in) {
        super.setIn(in);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE
    )
    public LocalDateFilter setNotIn(List<LocalDate> notIn) {
        super.setNotIn(notIn);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE
    )
    public LocalDateFilter setGreaterThan(LocalDate equals) {
        super.setGreaterThan(equals);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE
    )
    public LocalDateFilter setLessThan(LocalDate equals) {
        super.setLessThan(equals);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE
    )
    public LocalDateFilter setGreaterThanOrEqual(LocalDate equals) {
        super.setGreaterThanOrEqual(equals);
        return this;
    }

    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE
    )
    public LocalDateFilter setLessThanOrEqual(LocalDate equals) {
        super.setLessThanOrEqual(equals);
        return this;
    }
}
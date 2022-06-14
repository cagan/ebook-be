package com.cagan.library.filter;

import lombok.*;

import java.util.Objects;

@NoArgsConstructor
public class RangeFilter<FIELD_TYPE extends Comparable<? super FIELD_TYPE>> extends Filter<FIELD_TYPE> {
    private static final long serialVersionUID = 1L;
    private FIELD_TYPE greaterThan;
    private FIELD_TYPE lessThan;
    private FIELD_TYPE greaterThanOrEqual;
    private FIELD_TYPE lessThanOrEqual;

    public RangeFilter(RangeFilter<FIELD_TYPE> filter) {
        super(filter);
        this.greaterThan = filter.greaterThan;
        this.lessThan = filter.lessThan;
        this.greaterThanOrEqual = filter.greaterThanOrEqual;
        this.lessThanOrEqual = filter.lessThanOrEqual;
    }

    public RangeFilter<FIELD_TYPE> copy() {
        return new RangeFilter<>(this);
    }

    public RangeFilter<FIELD_TYPE> setGreaterThan(FIELD_TYPE greaterThan) {
        this.greaterThan = greaterThan;
        return this;
    }

    public FIELD_TYPE getGreaterThan() {
        return this.greaterThan;
    }

    public RangeFilter<FIELD_TYPE> setLessThan(FIELD_TYPE lessThan) {
        this.lessThan = lessThan;
        return this;
    }

    public FIELD_TYPE getLessThan() {
        return this.lessThan;
    }

    public RangeFilter<FIELD_TYPE> setGreaterThanOrEqual(FIELD_TYPE greaterThanOrEqual) {
        this.greaterThanOrEqual = greaterThanOrEqual;
        return this;
    }

    public FIELD_TYPE getGreaterThanOrEqual() {
        return this.greaterThanOrEqual;
    }

    public RangeFilter<FIELD_TYPE> setLessThanOrEqual(FIELD_TYPE lessThanOrEqual) {
        this.lessThanOrEqual = lessThanOrEqual;
        return this;
    }

    public FIELD_TYPE getLessThanOrEqual() {
        return this.lessThanOrEqual;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RangeFilter)) return false;
        if (!super.equals(o)) return false;
        RangeFilter<?> that = (RangeFilter<?>) o;
        return Objects.equals(greaterThan, that.greaterThan) && Objects.equals(lessThan, that.lessThan) && Objects.equals(greaterThanOrEqual, that.greaterThanOrEqual) && Objects.equals(lessThanOrEqual, that.lessThanOrEqual);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), greaterThan, lessThan, greaterThanOrEqual, lessThanOrEqual);
    }
}

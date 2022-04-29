package com.cagan.library.filter;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Data
public class Filter<FIELD_TYPE> implements Serializable {
    private static final long SerialVersionUID = 1L;
    private FIELD_TYPE equals;
    private FIELD_TYPE notEquals;
    private Boolean specified;
    private List<FIELD_TYPE> in;
    private List<FIELD_TYPE> notIn;

    public Filter(Filter<FIELD_TYPE> filter) {
        this.equals = filter.equals;
        this.notEquals = filter.notEquals;
        this.specified = filter.specified;
        this.in = filter.in == null ? null : new ArrayList<>(filter.in);
        this.notIn = filter.notIn == null ? null : new ArrayList<>(filter.notIn);
    }

    public Filter<FIELD_TYPE> copy() {
        return new Filter<>(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Filter<?> filter = (Filter)o;
            return Objects.equals(this.equals, filter.equals) && Objects.equals(this.notEquals, filter.notEquals) && Objects.equals(this.specified, filter.specified) && Objects.equals(this.in, filter.in) && Objects.equals(this.notIn, filter.notIn);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(this.equals, this.notEquals, this.specified, this.in, this.notIn);
    }

    public String getFilterName() {
        return this.getClass().getSimpleName();
    }

    public String toString() {
        return this.getFilterName() + " [" + (this.getEquals() != null ? "equals=" + this.getEquals() + ", " : "") +
                (this.getNotEquals() != null ? "notEquals=" + this.getNotEquals() + ", " : "") +
                (this.getSpecified() != null ? "specified=" + this.getSpecified() + ", " : "") +
                (this.getIn() != null ? "in=" + this.getIn() + ", " : "") +
                (this.getNotIn() != null ? "notIn=" + this.getNotIn() : "") + "]";
    }
}

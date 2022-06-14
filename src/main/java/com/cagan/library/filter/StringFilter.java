package com.cagan.library.filter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
public class StringFilter extends Filter<String> {
    private static final long serialVersionUID = 1L;
    private String contains;
    private String doesNotContains;

    public StringFilter(StringFilter filter) {
        super(filter);
        this.contains = filter.contains;
        this.doesNotContains = filter.doesNotContains;
    }

    public StringFilter copy() {
        return new StringFilter(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            if (!super.equals(o)) {
                return false;
            } else {
                StringFilter that = (StringFilter) o;
                return Objects.equals(this.contains, that.contains) && Objects.equals(this.doesNotContains, that.doesNotContains);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), this.contains, this.doesNotContains);
    }

    public String toString() {
        return this.getFilterName() + " [" + (this.getEquals() != null ? "equals=" + (String) this.getEquals() + ", " : "") + (this.getNotEquals() != null ? "notEquals=" + (String) this.getNotEquals() + ", " : "") + (this.getSpecified() != null ? "specified=" + this.getSpecified() + ", " : "") + (this.getIn() != null ? "in=" + this.getIn() + ", " : "") + (this.getNotIn() != null ? "notIn=" + this.getNotIn() + ", " : "") + (this.getContains() != null ? "contains=" + this.getContains() + ", " : "") + (this.getDoesNotContains() != null ? "doesNotContain=" + this.getDoesNotContains() : "") + "]";
    }
}
package com.cagan.library.filter;

public class FloatFilter extends RangeFilter<Float> {
    private static final long serialVersionUID = 1L;

    public FloatFilter() {
    }

    public FloatFilter(FloatFilter filter) {
        super(filter);
    }

    public FloatFilter copy() {
        return new FloatFilter(this);
    }
}

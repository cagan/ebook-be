package com.cagan.library.filter;

import java.io.Serial;

public class FloatFilter extends RangeFilter<Float> {
    @Serial
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

package com.cagan.library.filter;

public class ShortFilter extends RangeFilter<Short> {
    private static final long serialVersionUID = 1L;

    public ShortFilter() {
    }

    public ShortFilter(ShortFilter filter) {
        super(filter);
    }

    public ShortFilter copy() {
        return new ShortFilter(this);
    }
}

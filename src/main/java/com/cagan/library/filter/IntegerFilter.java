package com.cagan.library.filter;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IntegerFilter extends RangeFilter<Integer> {
    private static final long serialVersionUID = 1L;

    public IntegerFilter(IntegerFilter filter) {
        super(filter);
    }

    public IntegerFilter copy() {
        return new IntegerFilter(this);
    }
}

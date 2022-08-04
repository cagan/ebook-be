package com.cagan.library.filter;

import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor
public class IntegerFilter extends RangeFilter<Integer> {
    @Serial
    private static final long serialVersionUID = 1L;

    public IntegerFilter(IntegerFilter filter) {
        super(filter);
    }

    public IntegerFilter copy() {
        return new IntegerFilter(this);
    }
}

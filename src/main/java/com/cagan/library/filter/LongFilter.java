package com.cagan.library.filter;

import lombok.NoArgsConstructor;

import java.io.Serial;

@NoArgsConstructor
public class LongFilter extends RangeFilter<Long> {
    @Serial
    private static final long serialVersionUID = 1L;

    public LongFilter(LongFilter filter) {
        super(filter);
    }

    public LongFilter copy() {
        return new LongFilter(this);
    }
}

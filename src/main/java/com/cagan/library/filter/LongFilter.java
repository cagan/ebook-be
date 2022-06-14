package com.cagan.library.filter;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LongFilter extends RangeFilter<Long> {
    private static final long serialVersionUID = 1L;

    public LongFilter(LongFilter filter) {
        super(filter);
    }

    public LongFilter copy() {
        return new LongFilter(this);
    }
}

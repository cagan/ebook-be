package com.cagan.library.book;

import com.opencsv.bean.CsvToBeanFilter;

import java.util.Arrays;

public class BookFileFilter implements CsvToBeanFilter {
    @Override
    public boolean allowLine(String[] strings) {

        // Title
        if (strings[0].isEmpty()) {
            return false;
        }

        if (strings[1].isEmpty()) {
            return false;
        }

        if (strings[2].isEmpty()) {
            return false;
        }

        return true;
    }
}

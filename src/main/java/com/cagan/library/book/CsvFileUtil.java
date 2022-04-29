package com.cagan.library.book;

import com.opencsv.bean.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class CsvFileUtil {

    public static <T extends CsvToBean<?>> List<T> extractToList(Class<T> clazz, InputStream stream, CsvToBeanFilter filter) {
        MappingStrategy<T> ms = new ColumnPositionMappingStrategy<>();
        ms.setType(clazz);

        var builder = new CsvToBeanBuilder<T>(new InputStreamReader(stream))
                .withType(clazz)
                .withSkipLines(1)
                .withMappingStrategy(ms)
                .build();

        builder.setFilter(filter);

        return builder.parse();
    }
}

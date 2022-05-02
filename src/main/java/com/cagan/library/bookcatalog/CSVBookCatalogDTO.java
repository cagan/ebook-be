package com.cagan.library.bookcatalog;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvToBean;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CSVBookCatalogDTO extends CsvToBean<BookCatalog> {
    @CsvBindByPosition(position = 0)
    private String title;

    @NotNull
    @CsvBindByPosition(position = 1)
    private String author;

    @NotNull
    @CsvBindByPosition(position = 2)
    private String genre;

    @NotNull
    @CsvBindByPosition(position = 3)
    private Integer height;

    @CsvBindByPosition(position = 4)
    private String publisher;
}

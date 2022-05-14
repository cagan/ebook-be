package com.cagan.library.domain;

import java.util.stream.Stream;

public enum ProductCategory {
    BOOK("BOOK"),
    MAGAZINE("MAGAZINE"),
    COMIC("COMIC"),
    NEWSPAPER("NEWSPAPER");

    public String label;

    ProductCategory(String label) {
        this.label = label;
    }

    public static ProductCategory findByLabel(String label) {
        return Stream.of(ProductCategory.values())
                .filter(value -> value.label.equalsIgnoreCase(label))
                .findFirst()
                .orElse(null);
    }

    public String getLabel() {
        return label;
    }
}

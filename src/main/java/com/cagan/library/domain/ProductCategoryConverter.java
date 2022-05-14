package com.cagan.library.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ProductCategoryConverter implements AttributeConverter<ProductCategory, String> {

    @Override
    public String convertToDatabaseColumn(ProductCategory productCategory) {
        return productCategory.getLabel();
    }

    @Override
    public ProductCategory convertToEntityAttribute(String s) {
        return ProductCategory.findByLabel(s);
    }
}

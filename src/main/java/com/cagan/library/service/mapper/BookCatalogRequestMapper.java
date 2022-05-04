package com.cagan.library.service.mapper;

import com.cagan.library.service.dto.request.BookCatalogRequest;
import com.cagan.library.domain.BookCatalog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookCatalogRequestMapper extends EntityMapper<BookCatalogRequest, BookCatalog> {
}

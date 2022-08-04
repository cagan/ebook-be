package com.cagan.library.service.mapper;

import com.cagan.library.service.dto.view.BookCatalogView;
import com.cagan.library.domain.BookCatalog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookCatalogViewMapper extends EntityMapper<BookCatalogView, BookCatalog> { }

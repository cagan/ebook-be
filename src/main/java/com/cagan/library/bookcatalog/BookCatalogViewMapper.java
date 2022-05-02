package com.cagan.library.bookcatalog;

import com.cagan.library.common.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookCatalogViewMapper extends EntityMapper<BookCatalogView, BookCatalog> { }

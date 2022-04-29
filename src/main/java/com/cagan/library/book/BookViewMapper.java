package com.cagan.library.book;

import com.cagan.library.common.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookViewMapper extends EntityMapper<BookView, Book> { }

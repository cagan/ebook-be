package com.cagan.library.service.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.TimeZone;

@Mapper(componentModel = "spring")
public interface LocalDateTimeMapper {
    @Mapping(source = "createdDate", target = "createdDate", dateFormat = "dd.MM.yyyy")
    default LocalDateTime fromInstant(Instant instant, @Context TimeZone timeZone) {
        return instant == null ? null: LocalDateTime.ofInstant(instant, timeZone.toZoneId());
    }

    @Mapping(source = "createdDate", target = "createdDate", dateFormat = "dd.MM.yyyyy")
    default Instant toInstant(LocalDateTime localDateTime, @Context TimeZone timeZone) {
        return localDateTime == null ? null : LocalDateTime.now().toInstant(ZoneOffset.UTC);
    }
}

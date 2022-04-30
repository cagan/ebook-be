package com.cagan.library.common;


import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Set;

// TODO: Convert entity data type to Instant, figure out how to map Instant to LocalDateTime
public interface EntityMapper<D, E> {

    D toDto(E entity);

    E toEntity(D dto);

    List<D> toDto(List<E> entityList);

    List<E> toEntity(List<D> dtoList);

    Set<D> toDto(Set<E> entityList);

    Set<E> toEntity(Set<D> dtoList);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget E entity, D dto);
//
//    @Mapping(target = "createdDate", source = "createdDate")
//    @Mapping(target = "lastModifiedDate", source = "lastModifiedDate")
//    LocalDateTime map(Instant instant, @Context TimeZone timeZone);
//
//    default LocalDateTime fromInstant(Instant instant, @Context TimeZone timeZone) {
//        return instant == null ? null : LocalDateTime.ofInstant(instant, timeZone.toZoneId());
//    }
}

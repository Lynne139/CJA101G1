package com.resto.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class RestoOrderSourceConverter implements AttributeConverter<RestoOrderSource, Integer> {

    @Override
    public Integer convertToDatabaseColumn(RestoOrderSource source) {
        return source != null ? source.getCode() : null;
    }

    @Override
    public RestoOrderSource convertToEntityAttribute(Integer dbData) {
        return dbData != null ? RestoOrderSource.fromCode(dbData) : null;
    }
}

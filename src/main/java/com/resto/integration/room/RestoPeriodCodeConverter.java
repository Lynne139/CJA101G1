package com.resto.integration.room;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class RestoPeriodCodeConverter implements AttributeConverter<RestoPeriodCode, Integer> {

    @Override
    public Integer convertToDatabaseColumn(RestoPeriodCode source) {
        return source != null ? source.getCode() : null;
    }

    @Override
    public RestoPeriodCode convertToEntityAttribute(Integer dbData) {
        return dbData != null ? RestoPeriodCode.fromCode(dbData) : null;
    }
}

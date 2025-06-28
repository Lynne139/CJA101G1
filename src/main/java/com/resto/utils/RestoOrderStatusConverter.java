package com.resto.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class RestoOrderStatusConverter implements AttributeConverter<RestoOrderStatus, Integer> {
    @Override
    public Integer convertToDatabaseColumn(RestoOrderStatus status) {
        return status != null ? status.getValue() : null;
    }

    @Override
    public RestoOrderStatus convertToEntityAttribute(Integer dbData) {
        return dbData != null ? RestoOrderStatus.fromValue(dbData) : null;
    }
}
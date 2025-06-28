package com.resto.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true) // autoApply=所有RestoOrderStatus都套用
public class RestoOrderStatusConverter implements AttributeConverter<RestoOrderStatus, Integer> {

	@Override
    public Integer convertToDatabaseColumn(RestoOrderStatus status) {
        return status == null ? null : status.getCode();
    }

    @Override
    public RestoOrderStatus convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : RestoOrderStatus.fromCode(dbData);
    }
    
}

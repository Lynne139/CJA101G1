package com.resto.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true) // autoApply=所有RestoOrderStatus都套用
public class RestoOrderSourceConverter implements AttributeConverter<RestoOrderSource, Integer> {

	@Override
    public Integer convertToDatabaseColumn(RestoOrderSource status) {
        return status == null ? null : status.getCode();
    }

    @Override
    public RestoOrderSource convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : RestoOrderSource.fromCode(dbData);
    }
    
}

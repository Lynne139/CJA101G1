package com.coupon.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class OrderTypeConverter implements AttributeConverter<OrderType, Integer> {

	// JPA 會自動在 Entity 讀取資料時呼叫 convertToEntityAttribute，
	// 反之寫入資料時呼叫 convertToDatabaseColumn
	
    @Override
    public Integer convertToDatabaseColumn(OrderType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue(); // Enum -> DB 整數
    }

    @Override
    public OrderType convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return OrderType.fromValue(dbData); // 整數 -> Enum
    }
}

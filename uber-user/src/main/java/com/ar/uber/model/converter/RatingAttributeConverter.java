package com.ar.uber.model.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.ar.uber.model.DriverRating;

@Converter
public class RatingAttributeConverter implements AttributeConverter<DriverRating, Integer> {

    @Override
    public Integer convertToDatabaseColumn(DriverRating attribute) {
        if (attribute == null)
            return null;

        switch (attribute) {
            case ONE:
                return 1;
            case TWO:
                return 2;
            case THREE:
                return 3;
            case FOUR:
                return 4;
            case FIVE:
                return 5;
            default:
                throw new IllegalArgumentException(attribute + " not supported.");
        }
    }

    @Override
    public DriverRating convertToEntityAttribute(Integer dbData) {
        if (dbData == null)
            return null;

        switch (dbData) {
            case 1:
                return DriverRating.ONE;
            case 2:
                return DriverRating.TWO;
            case 3:
                return DriverRating.THREE;
            case 4:
                return DriverRating.FOUR;
            case 5:
                return DriverRating.FIVE;
            default:
                throw new IllegalArgumentException(dbData + " not supported.");
        }
    }
}

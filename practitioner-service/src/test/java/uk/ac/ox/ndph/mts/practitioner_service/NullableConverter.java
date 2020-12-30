package uk.ac.ox.ndph.mts.practitioner_service;

import org.junit.jupiter.params.converter.DefaultArgumentConverter;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.converter.ArgumentConversionException;

public final class NullableConverter extends SimpleArgumentConverter {
   @Override
   protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
       if ("null".equals(source)) {
           return null;
       }
       return DefaultArgumentConverter.INSTANCE.convert(source, targetType);
   }
}
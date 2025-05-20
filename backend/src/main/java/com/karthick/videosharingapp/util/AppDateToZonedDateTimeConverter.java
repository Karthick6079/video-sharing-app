package com.karthick.videosharingapp.util;

import com.karthick.videosharingapp.domain.ZoneContext;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@ReadingConverter
public class AppDateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime> {

    private final ZoneContext zoneContext;

    public AppDateToZonedDateTimeConverter(ZoneContext zoneContext) {
        this.zoneContext = zoneContext;
    }


    @Override
    public ZonedDateTime convert(Date source) {
        return source != null ? source.toInstant().atZone(zoneContext.getZoneId()) : null ;
    }
}

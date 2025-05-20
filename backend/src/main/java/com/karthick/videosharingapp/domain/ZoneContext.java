package com.karthick.videosharingapp.domain;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.ZoneId;

@Component
@RequestScope
public class ZoneContext {

    ZoneId zoneId = ZoneId.systemDefault();

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }
}

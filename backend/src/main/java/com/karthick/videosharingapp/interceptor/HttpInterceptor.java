package com.karthick.videosharingapp.interceptor;

import com.karthick.videosharingapp.domain.ZoneContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.ZoneId;

@Component
public class HttpInterceptor implements HandlerInterceptor {

    private final ZoneContext zoneContext;

    public HttpInterceptor(ZoneContext zoneContext) {
        this.zoneContext = zoneContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        String header = request.getHeader("User-TZ");
        try {
            ZoneId zone = header != null ? ZoneId.of(header) : ZoneId.systemDefault();
            zoneContext.setZoneId(zone);
        } catch (Exception e) {
            zoneContext.setZoneId(ZoneId.systemDefault());
        }
        return  true;
    }
}

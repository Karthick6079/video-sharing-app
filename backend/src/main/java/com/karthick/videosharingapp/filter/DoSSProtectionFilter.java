package com.karthick.videosharingapp.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class DoSSProtectionFilter extends OncePerRequestFilter {


    private final RedisTemplate<String, Integer> redisTemplate;
    private final int MAX_REQUESTS = 30;
    private final int TIME_WINDOW_SECONDS = 60;

    public DoSSProtectionFilter(RedisTemplate<String, Integer> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = getClientIP(request);
        String key = "req_count:" + ip;

        Integer count = redisTemplate.opsForValue().get(key);

        if (count == null) {
            redisTemplate.opsForValue().set(key, 1, Duration.ofSeconds(TIME_WINDOW_SECONDS));
        } else if (count >= MAX_REQUESTS) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests. Try again later.");
            return;
        } else {
            redisTemplate.opsForValue().increment(key);
        }

        filterChain.doFilter(request, response);
    }

    public String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            // X-Forwarded-For: client, proxy1, proxy2 => take first
            return xfHeader.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isEmpty()) {
            return realIp;
        }

        return request.getRemoteAddr(); // fallback
    }
}

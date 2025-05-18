package com.karthick.videosharingapp.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class MDCLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Set Session ID
            String sessionId = request.getSession(false) != null
                    ? request.getSession(false).getId()
                    : "NO_SESSION";
            MDC.put("sessionId", sessionId);

            // Set Client IP Address
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = request.getRemoteAddr();
            }
            MDC.put("ipAddress", ip);

            // Set User ID (assuming Spring Security is in use)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                MDC.put("userId", auth.getName());
            } else {
                MDC.put("userId", "ANONYMOUS");
            }

            // Generate Correlation ID (or reuse if passed from header)
            String correlationId = request.getHeader("X-Correlation-ID");
            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = UUID.randomUUID().toString();
            }
            MDC.put("correlationId", correlationId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}


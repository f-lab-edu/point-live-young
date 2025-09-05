package com.pointliveyoung.forliveyoung.common.filter;

import jakarta.servlet.*;
import org.jboss.logging.MDC;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        String traceId = UUID.randomUUID().toString();

        MDC.put("traceId", traceId);
        filterChain.doFilter(servletRequest, servletResponse);
        MDC.clear();
    }
}

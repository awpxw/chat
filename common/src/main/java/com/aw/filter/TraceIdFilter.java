package com.aw.filter;

import com.aw.trace.TraceIdUtil;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String traceId = UUID.randomUUID().toString().replace("-", "");
        TraceIdUtil.setTraceId(traceId);
        try {
            chain.doFilter(request, response);
        } finally {
            TraceIdUtil.clear();
        }
    }
}
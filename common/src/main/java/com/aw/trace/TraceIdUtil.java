package com.aw.trace;

public  class TraceIdUtil {
    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    private TraceIdUtil() {}

    public static void setTraceId(String traceId) {
        TRACE_ID.set(traceId);
    }

    public static String getTraceId() {
        String traceId = TRACE_ID.get();
        return traceId == null ? "unknown" : traceId;
    }

    public static void clear() {
        TRACE_ID.remove();
    }

}
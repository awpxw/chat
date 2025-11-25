package com.aw.snowflake;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdWorker {
    private final SnowflakeIdWorker snowflake;

    public long nextId() {
        return snowflake.nextId();
    }

    public String nextIdStr() {
        return String.valueOf(snowflake.nextId());
    }
}
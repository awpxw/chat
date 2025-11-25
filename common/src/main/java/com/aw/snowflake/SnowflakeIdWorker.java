package com.aw.snowflake;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Twitter Snowflake 算法（64bit）
 * 1bit 符号位 + 41bit 时间戳 + 10bit 机器ID（5bit机房+5bit机器） + 12bit 序列号
 */
@Slf4j
@Component
public class SnowflakeIdWorker {

    // ============================== Fields =====================================
    private final long twepoch = 1288834974657L; // 起始时间戳 2010-11-04

    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long maxWorkerId = ~(-1L << workerIdBits);        // 31
    private final long maxDatacenterId = ~(-1L << datacenterIdBits); // 31
    private final long sequenceBits = 12L;

    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = ~(-1L << sequenceBits);

    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    @PostConstruct
    public void init() {
        this.datacenterId = getDatacenterId();
        this.workerId = getWorkerId();
        log.info("SnowflakeIdWorker 初始化成功 => datacenterId={}, workerId={}", datacenterId, workerId);
    }

    // ============================== 生成ID =====================================
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 时钟回拨处理（生产必备！）
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    Thread.sleep(offset << 1);
                    timestamp = timeGen();
                    if (timestamp < lastTimestamp) {
                        throw new RuntimeException("时钟回拨严重，拒绝生成ID");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("时钟回拨中断");
                }
            } else {
                throw new RuntimeException("时钟回拨超过5ms，拒绝生成ID");
            }
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) |
               (datacenterId << datacenterIdShift) |
               (workerId << workerIdShift) |
               sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    // ============================== 机器ID生成（自动） ===========================
    private long getDatacenterId() {
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = ((0x000000FF & (long) mac[mac.length - 2]) |
                          (0x0000FF00 & (((long) mac[mac.length - 1]) << 8))) >> 6;
                    id = id % (maxDatacenterId + 1);
                }
            }
        } catch (Exception e) {
            log.warn("获取 datacenterId 失败，使用随机值", e);
            id = (long) (Math.random() * (maxDatacenterId + 1));
        }
        return id;
    }

    private long getWorkerId() {
        long pid = 1L;
        try {
            pid = Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        } catch (Exception ignored) {}
        return (pid % (maxWorkerId + 1));
    }
}
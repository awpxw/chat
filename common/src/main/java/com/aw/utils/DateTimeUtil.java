// src/main/java/com/aw/common/utils/DateTimeUtil.java
package com.aw.utils;

import org.springframework.util.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * LocalDateTime 终极工具类
 * 再也不写：.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
 * 再也不写：LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
 * 直接：DateTimeUtil.now()、DateTimeUtil.format(date)、DateTimeUtil.parse("2025-01-01")
 */
public final class DateTimeUtil {

    /** 默认时区：中国上海 */
    public static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    public static final ZoneOffset OFFSET = ZoneOffset.ofHours(8);

    // ====================== 常用格式化器（直接调用）======================
    public static final DateTimeFormatter DF_YMDHMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DF_YMDHM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter DF_YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DF_HMS = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter DF_YMDHMS_SSS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private DateTimeUtil() {}

    // ====================== 当前时间（最常用）======================
    public static LocalDateTime now() {
        return LocalDateTime.now(ZONE);
    }

    public static LocalDate today() {
        return LocalDate.now(ZONE);
    }

    public static String nowStr() {
        return now().format(DF_YMDHMS);
    }

    // ====================== String → LocalDateTime ======================
    public static LocalDateTime parse(String text) {
        if (!StringUtils.hasText(text)) return null;
        text = text.trim();

        try {
            if (text.length() == 10) return LocalDate.parse(text, DF_YMD).atStartOfDay();
            if (text.length() == 16) return LocalDateTime.parse(text, DF_YMDHM);
            if (text.length() == 19) return LocalDateTime.parse(text, DF_YMDHMS);
            if (text.length() == 23) return LocalDateTime.parse(text, DF_YMDHMS_SSS);
            if (text.contains("T")) return LocalDateTime.parse(text);
            return LocalDateTime.parse(text, DF_YMDHMS);
        } catch (Exception e) {
            throw new IllegalArgumentException("时间格式错误: " + text);
        }
    }

    public static LocalDate parseDate(String text) {
        if (!StringUtils.hasText(text)) return null;
        return LocalDate.parse(text.trim(), text.length() <= 10 ? DF_YMD : DF_YMDHMS);
    }

    // ====================== LocalDateTime → String ======================
    public static String format(LocalDateTime date) {
        return date == null ? null : date.format(DF_YMDHMS);
    }

    public static String formatDate(LocalDate date) {
        return date == null ? null : date.format(DF_YMD);
    }

    public static String format(LocalDateTime date, DateTimeFormatter formatter) {
        return date == null ? null : date.format(formatter);
    }

    // ====================== LocalDateTime ↔ Date ↔ Long ======================
    public static Date toDate(LocalDateTime dateTime) {
        return dateTime == null ? null : Date.from(dateTime.atZone(ZONE).toInstant());
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return date == null ? null : LocalDateTime.ofInstant(date.toInstant(), ZONE);
    }

    public static long toEpochMilli(LocalDateTime dateTime) {
        return dateTime == null ? 0L : dateTime.toInstant(OFFSET).toEpochMilli();
    }

    public static LocalDateTime ofEpochMilli(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZONE);
    }

    // ====================== 常用时间点 ======================
    public static LocalDateTime startOfDay() {
        return today().atStartOfDay();
    }

    public static LocalDateTime endOfDay() {
        return today().atTime(23, 59, 59, 999999999);
    }

    public static LocalDateTime startOfToday() { return startOfDay(); }
    public static LocalDateTime endOfToday() { return endOfDay(); }

    public static LocalDateTime yesterday() {
        return today().minusDays(1).atStartOfDay();
    }

    public static LocalDateTime tomorrow() {
        return today().plusDays(1).atStartOfDay();
    }
}
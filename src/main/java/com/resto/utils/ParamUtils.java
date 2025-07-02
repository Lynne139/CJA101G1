package com.resto.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * 各種安全型別轉換工具：
 * - 任何轉型錯誤都回傳 null / false，而不丟Exception
 * - 用於複合查詢時防止使用者輸入格式錯誤
 */
public final class ParamUtils {

    private ParamUtils() { /* Utility class */ }

    // ---------- 基本數值 ----------
    public static Integer parseIntSafe(String[] arr) {
        if (arr == null || arr.length == 0) return null;
        try { return Integer.valueOf(arr[0]); }
        catch (NumberFormatException e) { return null; }
    }

    public static Long parseLongSafe(String[] arr) {
        if (arr == null || arr.length == 0) return null;
        try { return Long.valueOf(arr[0]); }
        catch (NumberFormatException e) { return null; }
    }

    // ---------- 布林 ----------
    public static Boolean parseBooleanSafe(String[] arr) {
        if (arr == null || arr.length == 0) return false;
        return Boolean.parseBoolean(arr[0]);
    }

    // ---------- Enum ----------
    public static <E extends Enum<E>> E parseEnumSafe(String[] arr, Class<E> enumCls) {
        if (arr == null || arr.length == 0) return null;
        try { return Enum.valueOf(enumCls, arr[0]); }
        catch (IllegalArgumentException e) { return null; }
    }

    // ---------- 日期 ----------
    public static LocalDate parseLocalDateSafe(String[] arr) {
        if (arr == null || arr.length == 0) return null;
        try { return LocalDate.parse(arr[0]); }
        catch (DateTimeParseException e) { return null; }
    }

    public static LocalDateTime parseLocalDateTimeSafe(String[] arr) {
        if (arr == null || arr.length == 0) return null;
        try { return LocalDateTime.parse(arr[0]); }
        catch (DateTimeParseException e) { return null; }
    }

    // ---------- 字串 ----------
    public static String parseString(String[] arr) {
        if (arr == null || arr.length == 0) return null;
        String s = arr[0].trim();
        return s.isEmpty() ? null : s;
    }
}

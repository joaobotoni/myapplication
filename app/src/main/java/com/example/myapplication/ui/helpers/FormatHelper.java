package com.example.myapplication.ui.helpers;

import static com.example.myapplication.utils.DecimalUtil.createCurrencyFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

public final class FormatHelper {

    private FormatHelper() {
        throw new AssertionError("FormatHelper is a utility class and must not be instantiated.");
    }

    @NonNull
    public static Integer parseInt(@Nullable String value) {
        String trimmed = trimOrNull(value);
        if (trimmed == null) return 0;
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    @NonNull
    public static Float parseFloat(@Nullable String value) {
        String trimmed = trimOrNull(value);
        if (trimmed == null) return 0.0f;
        try {
            return Float.parseFloat(trimmed);
        } catch (NumberFormatException ignored) {
            return 0.0f;
        }
    }

    @NonNull
    public static Double parseDouble(@Nullable String value) {
        String trimmed = trimOrNull(value);
        if (trimmed == null) return 0.0;
        try {
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException ignored) {
            return 0.0;
        }
    }

    @NonNull
    public static BigDecimal parseDecimal(@Nullable String value) {
        String trimmed = trimOrNull(value);
        if (trimmed == null) return BigDecimal.ZERO;
        try {
            DecimalFormat format = createCurrencyFormat();
            format.setParseBigDecimal(true);
            Number parsed = format.parse(trimmed);
            return (parsed instanceof BigDecimal) ? (BigDecimal) parsed : BigDecimal.ZERO;
        } catch (ParseException ignored) {
            return BigDecimal.ZERO;
        }
    }

    @NonNull
    public static String formatCurrency(@Nullable BigDecimal value) {
        if (value == null) return "0,00";
        return createCurrencyFormat().format(value);
    }

    @NonNull
    public static String formatInteger(@Nullable Integer value) {
        if (value == null) return "0";
        return Integer.toString(value);
    }

    @NonNull
    public static String formatDouble(@Nullable Double value) {
        if (value == null) return String.format(Locale.getDefault(), "%.2f", 0.0);
        return String.format(Locale.getDefault(), "%.2f", value);
    }

    @NonNull
    public static String formatFloat(@Nullable Float value) {
        if (value == null) return String.format(Locale.getDefault(), "%.2f", 0.0f);
        return String.format(Locale.getDefault(), "%.2f", value);
    }

    @Nullable
    private static String trimOrNull(@Nullable String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
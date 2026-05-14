package com.example.myapplication.ui.helpers;


import static com.example.myapplication.utils.DecimalUtil.CURRENCY_FORMAT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

public class FormatHelper {
    @NonNull
    public static Integer getInt(@Nullable String value) {
        if (value == null || value.trim().isEmpty()) return 0;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @NonNull
    public static Float getFloat(@Nullable String value) {
        if (value == null || value.trim().isEmpty()) return 0.0f;
        try {
            return Float.parseFloat(value.trim());
        } catch (NumberFormatException e) {
            return 0.0f;
        }
    }

    @NonNull
    public static Double getDouble(@Nullable String value) {
        if (value == null || value.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @NonNull
    public static BigDecimal getDecimal(@Nullable String value) {
        if (value == null || value.trim().isEmpty()) return BigDecimal.ZERO;
        try {
            CURRENCY_FORMAT.setParseBigDecimal(true);
            Number number = CURRENCY_FORMAT.parse(value.trim());
            if (number == null) return BigDecimal.ZERO;
            return new BigDecimal(number.toString());
        } catch (ParseException e) {
            return BigDecimal.ZERO;
        }
    }


    @NonNull
    public static String formatCurrency(@Nullable BigDecimal value) {
        if (value == null) return "0,00";
        return CURRENCY_FORMAT.format(value);
    }

    @NonNull
    public static String formatInteger(@Nullable Integer value) {
        if (value == null) return "0";
        return String.valueOf(value);
    }

    @NonNull
    public static String formatDouble(@Nullable Double value) {
        if (value == null) return "0";
        return String.format(Locale.getDefault(), "%.2f", value);
    }
}
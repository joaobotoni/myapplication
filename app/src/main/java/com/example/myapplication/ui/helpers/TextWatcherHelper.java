package com.example.myapplication.ui.helpers;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Objects;

public final class TextWatcherHelper {

    private TextWatcherHelper() {
        throw new AssertionError("TextWatcherHelper is a utility class and must not be instantiated.");
    }

    public interface TextFormatter {
        @NonNull String format(@NonNull String input);
    }

    public abstract static class BaseTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) {}
    }

    public static class SimpleTextWatcher extends BaseTextWatcher {
        private final Runnable onChanged;

        public SimpleTextWatcher(@NonNull Runnable onChanged) {
            Objects.requireNonNull(onChanged, "onChanged must not be null");

            this.onChanged = onChanged;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onChanged.run();
        }
    }

    public static class SearchTextWatcher extends BaseTextWatcher {
        private final Runnable onChanged;
        private final int minLength;

        public SearchTextWatcher(int minLength, @NonNull Runnable onChanged) {
            if (minLength < 1) throw new IllegalArgumentException("minLength must be >= 1");
            Objects.requireNonNull(onChanged, "onChanged must not be null");

            this.minLength = minLength;
            this.onChanged = onChanged;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s != null && s.length() >= minLength) onChanged.run();
        }
    }

    public static class FormattingTextWatcher extends BaseTextWatcher {
        private final TextFormatter formatter;
        private final Runnable onChanged;
        private boolean isUpdating = false;

        public FormattingTextWatcher(@NonNull TextFormatter formatter, @NonNull Runnable onChanged) {
            Objects.requireNonNull(formatter, "formatter must not be null");
            Objects.requireNonNull(onChanged, "onChanged must not be null");

            this.formatter = formatter;
            this.onChanged = onChanged;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isUpdating) return;
            String current = s.toString();
            String formatted = formatter.format(current);
            if (!current.equals(formatted)) {
                isUpdating = true;
                s.replace(0, s.length(), formatted);
                isUpdating = false;
            }
            onChanged.run();
        }
    }

    public static class CurrencyFormatter implements TextFormatter {
        private final double maxValue;
        private final NumberFormat format;

        public CurrencyFormatter(double maxValue, @NonNull NumberFormat format) {
            Objects.requireNonNull(format, "format must not be null");

            this.maxValue = maxValue;
            this.format = format;
        }

        @NonNull
        @Override
        public String format(@NonNull String input) {
            String digits = input.replaceAll("[^\\d]", "");
            if (digits.isEmpty()) return "";
            BigDecimal value = new BigDecimal(digits).movePointLeft(2);
            return value.compareTo(BigDecimal.valueOf(maxValue)) > 0.0 ? input : format.format(value);
        }
    }

    @NonNull
    public static TextWatcher simpleTextWatcher(@NonNull Runnable onChanged) {
        return new SimpleTextWatcher(onChanged);
    }

    @NonNull
    public static TextWatcher searchTextWatcher(int minLength, @NonNull Runnable onChanged) {
        return new SearchTextWatcher(minLength, onChanged);
    }

    @NonNull
    public static TextWatcher moneyTextWatcher(double maxValue, @NonNull NumberFormat format, @NonNull Runnable onChanged) {
        return new FormattingTextWatcher(new CurrencyFormatter(maxValue, format), onChanged);
    }
}
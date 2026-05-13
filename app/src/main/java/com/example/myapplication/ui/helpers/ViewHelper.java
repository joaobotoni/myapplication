package com.example.myapplication.ui.helpers;

import android.content.Context;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

public class ViewHelper {

    @NonNull
    public static String requireText(@Nullable TextView view) {
        if (view == null || view.getText() == null) return "";
        return view.getText().toString().trim();
    }

    @NonNull
    public static Integer getInt(@Nullable EditText view) {
        return FormatHelper.getInt(requireText(view));
    }

    @NonNull
    public static Float getFloat(@Nullable EditText view) {
        return FormatHelper.getFloat(requireText(view));
    }

    @NonNull
    public static Double getDouble(@Nullable EditText view) {
        return FormatHelper.getDouble(requireText(view));
    }

    @NonNull
    public static BigDecimal getBigDecimal(@Nullable EditText view) {
        return FormatHelper.getDecimal(requireText(view));
    }

    public static <T> boolean isEmpty(@Nullable T value) {
        if (value == null) return true;
        if (value instanceof String) return ((String) value).trim().isEmpty();
        if (value instanceof TextView) return requireText((TextView) value).isEmpty();
        if (value instanceof Collection) return ((Collection<?>) value).isEmpty();
        if (value instanceof Object[]) return ((Object[]) value).length == 0;
        if (value instanceof Integer) return ((Integer) value) == 0;
        if (value instanceof Double) return ((Double) value) == 0.0;
        if (value instanceof BigDecimal) return ((BigDecimal) value).compareTo(BigDecimal.ZERO) == 0;
        return false;
    }

    public static <T> boolean isNotEmpty(@Nullable T value) {
        return !isEmpty(value);
    }

    @SafeVarargs
    public static <T> boolean anyEmpty(@Nullable T... values) {
        if (values == null) return true;
        for (T value : values) {
            if (isEmpty(value)) return true;
        }
        return false;
    }

    @SafeVarargs
    public static <T> boolean noneEmpty(@Nullable T... values) {
        if (values == null) return false;
        for (T value : values) {
            if (isEmpty(value)) return false;
        }
        return true;
    }

    @NonNull
    public static <T> T orElse(@Nullable T value, @NonNull T fallback) {
        return value != null ? value : fallback;
    }

    public static void setText(@NonNull TextView textView, @Nullable String text) {
        textView.setText(text != null ? text.trim() : "");
    }

    public static void setHelperText(@NonNull TextInputLayout textInputLayout, @Nullable String text) {
        textInputLayout.setHelperText(text != null ? text.trim() : "");
    }

    public static void setPluralText(@NonNull TextView textView, @NonNull Context context,
                                     @PluralsRes int resId, @Nullable Integer quantity) {
        if (quantity == null) {
            textView.setText("");
            return;
        }
        textView.setText(context.getResources().getQuantityString(resId, quantity, quantity));
    }

    @SafeVarargs
    public static <T> void setText(@NonNull TextView textView, @NonNull Context context, @StringRes int resId, T... args) {
        for (T arg : args) {
            if (arg == null) {
                textView.setText("");
                return;
            }
        }
        textView.setText(context.getString(resId, Arrays.asList(args).toArray()));
    }

    public static void setTextSafely(@NonNull EditText field, @NonNull String value, @NonNull TextWatcher watcher) {
        if (field.hasFocus()) return;
        field.removeTextChangedListener(watcher);
        field.setText(value);
        field.addTextChangedListener(watcher);
    }

    public static void selectChip(@NonNull ChipGroup chipGroup, @NonNull String text) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.getText().toString().equals(text)) {
                chip.setChecked(true);
                return;
            }
        }
    }

    @Nullable
    public static String getCheckedChipText(@NonNull ChipGroup chipGroup) {
        int chipId = chipGroup.getCheckedChipId();
        Chip chip = chipGroup.findViewById(chipId);
        return chip != null ? chip.getText().toString() : null;
    }


    public static void clearText(TextView... views) {
        for (TextView view : views) {
            if (view != null) view.setText("");
        }
    }

    public static void setVisible(boolean visible, View... views) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }
}
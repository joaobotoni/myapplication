package com.example.myapplication.ui.helpers;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Map;


public final class PermissionHelper {

    private PermissionHelper() {
        throw new AssertionError("PermissionHelper is a utility class and must not be instantiated.");
    }

    public static boolean hasPermissions(@Nullable Context context, @NonNull String... permissions) {
        if (context == null) return false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @NonNull
    public static ActivityResultLauncher<String[]> register(@NonNull Fragment fragment, @NonNull OnPermissionResultListener listener) {
        return fragment.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    if (result != null) listener.onResult(!result.containsValue(false), result);
                }
        );
    }

    public static void request(@Nullable Context context, @Nullable ActivityResultLauncher<String[]> launcher, @NonNull String... permissions) {
        if (launcher == null || hasPermissions(context, permissions)) return;
        launcher.launch(permissions);
    }

    public interface OnPermissionResultListener {
        void onResult(boolean granted, @NonNull Map<String, Boolean> result);
    }
}
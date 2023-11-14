package com.mycompany.plugins.example;

import androidx.activity.result.ActivityResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.biometric.BiometricManager;

import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

public class BiometricLock {
    private final BiometricLockPlugin plugin;
    private BiometricLockConfiguration config;

    public static final String MAX_ATTEMPTS = "androidMaxAttempts";
    public static final int DEFAULT_MAX_ATTEMPTS = 3;

    public static final String DEVICE_CREDENTIAL = "allowDeviceCredential";

    public BiometricLock(BiometricLockPlugin plugin) {
        this.plugin = plugin;

        var potentialConfig = BiometricLockConfiguration.load(plugin.getContext());
        this.config = potentialConfig != null ? potentialConfig : new BiometricLockConfiguration();

        if (config.enabled) {
            addFlags();
        }
    }

    public void onStop() {
//        if (privacyProtectionWindow != null) {
//
//        }
    }

    public void onResume() {
//        this.privacyProtectionWindow = new PrivacyProtectionWindow(plugin);
//        this.privacyProtectionWindow.show();
        authenticate();
    }

    public void showPrivacyProtectionWindowIfNeeded() {

    }

    public void hidePrivacyProtectionWindow() {

    }

    public void authenticate() {
        // The result of an intent is supposed to have the package name as a prefix
        Intent intent = new Intent(plugin.getContext(), AuthActivity.class);

        // Pass the options to the activity
//        intent.putExtra(
//                TITLE,
//                call.getString(TITLE, biometryNameMap.get(biometryTypes.get(0)))
//        );
//        intent.putExtra(SUBTITLE, call.getString(SUBTITLE));
//        intent.putExtra(REASON, call.getString(REASON));
//        intent.putExtra(CANCEL_TITLE, call.getString(CANCEL_TITLE));
        intent.putExtra(
                DEVICE_CREDENTIAL,
                true
        );

//        if (call.hasOption(CONFIRMATION_REQUIRED)) {
//            intent.putExtra(
//                    CONFIRMATION_REQUIRED,
//                    call.getBoolean(CONFIRMATION_REQUIRED, true)
//            );
//        }

        // Just in case the developer does something dumb like using a number < 1...
        intent.putExtra(MAX_ATTEMPTS, DEFAULT_MAX_ATTEMPTS);

        AuthActivity.plugin = plugin;
        plugin.getActivity().startActivity(intent);

    }

    public void configure(BiometricLockConfiguration options) {
        this.config = options;
        options.save(plugin.getContext());

        if (options.enabled) {
            enableSecureScreen();
        } else {
            disableSecureScreen();
        }
    }

    public BiometricLockConfiguration getConfiguration() {
        return config;
    }

    public int getBiometricMethod() {
        BiometricManager manager = BiometricManager.from(plugin.getContext());
        int biometryResult;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            biometryResult =
                    manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK);
        } else {
            biometryResult = manager.canAuthenticate();
        }

        if (biometryResult != BiometricManager.BIOMETRIC_SUCCESS) {
            return BiometryType.NONE.getType();
        }

        var biometryTypes = getDeviceBiometryTypes();

        return biometryTypes.get(0).getType();
    }

    private ArrayList<BiometryType> getDeviceBiometryTypes() {
        ArrayList<BiometryType> types = new ArrayList<>();
        PackageManager manager = plugin.getContext().getPackageManager();

        if (manager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            types.add(BiometryType.FINGERPRINT);
        }

        if (manager.hasSystemFeature(PackageManager.FEATURE_FACE)) {
            types.add(BiometryType.FACE);
        }

        if (manager.hasSystemFeature(PackageManager.FEATURE_IRIS)) {
            types.add(BiometryType.IRIS);
        }

        if (types.size() == 0) {
            types.add(BiometryType.NONE);
        }

        return types;
    }


    public void enableSecureScreen() {
        plugin
                .getBridge()
                .executeOnMainThread(
                        () -> {
                            addFlags();
                        }
                );
    }

    public void disableSecureScreen() {
        plugin
                .getBridge()
                .executeOnMainThread(
                        () -> {
                            clearFlags();
                        }
                );
    }

    private void addFlags() {
        Window window = plugin.getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    private void clearFlags() {
        Window window = plugin.getActivity().getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }
}

enum BiometryType {
    NONE(0),
    FINGERPRINT(3),
    FACE(4),
    IRIS(5);

    private final int type;

    BiometryType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
package com.mycompany.plugins.example;

import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.biometric.BiometricManager;

import android.os.Build;

import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

public class BiometricLock {
    private final BiometricLockPlugin plugin;
    private BiometricLockConfiguration config;

    public BiometricLock(BiometricLockPlugin plugin) {
        this.plugin = plugin;

        var potentialConfig = BiometricLockConfiguration.load(plugin.getContext());
        this.config = potentialConfig != null ? potentialConfig : new BiometricLockConfiguration();

        if (config.enabled && getBiometricMethod(plugin) != BiometricLockPlugin.BiometryType.NONE.getType()) {
            addFlags();
        } else {
            clearFlags();
        }
    }

    public void onResume() {
        authenticateIfNeeded();
    }

    public void authenticateIfNeeded() {
        if (!config.enabled) return;
        if (getBiometricMethod(plugin) == BiometricLockPlugin.BiometryType.NONE.getType()) return;

        Intent intent = new Intent(plugin.getContext(), AuthActivity.class);

//        intent.putExtra(RETRY_BUTTON_COLOR, config.retryButtonColor);
//        intent.putExtra(APP_NAME, config.appName);

        AuthActivity.plugin = plugin;
        plugin.getActivity().startActivity(intent);
    }

    public void configure(BiometricLockConfiguration options) {
        this.config = options;
        options.save(plugin.getContext());

        if (options.enabled && getBiometricMethod(plugin) != BiometricLockPlugin.BiometryType.NONE.getType()) {
            enableSecureScreen();
        } else {
            disableSecureScreen();
        }
    }

    public BiometricLockConfiguration getConfiguration() {
        return config;
    }

    static public int getBiometricMethod(BiometricLockPlugin plugin) {
        BiometricManager manager = BiometricManager.from(plugin.getContext());
        int biometryResult;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            biometryResult =
                    manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK);
        } else {
            biometryResult = manager.canAuthenticate();
        }

        if (biometryResult != BiometricManager.BIOMETRIC_SUCCESS) {
            return BiometricLockPlugin.BiometryType.NONE.getType();
        }

        var biometryTypes = getDeviceBiometryTypes(plugin);


        return biometryTypes.get(0).getType();
    }

    static private ArrayList<BiometricLockPlugin.BiometryType> getDeviceBiometryTypes(BiometricLockPlugin plugin) {
        ArrayList<BiometricLockPlugin.BiometryType> types = new ArrayList<>();
        PackageManager manager = plugin.getContext().getPackageManager();

        if (manager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            types.add(BiometricLockPlugin.BiometryType.FINGERPRINT);
        }

        if (manager.hasSystemFeature(PackageManager.FEATURE_FACE)) {
            types.add(BiometricLockPlugin.BiometryType.FACE);
        }

        if (manager.hasSystemFeature(PackageManager.FEATURE_IRIS)) {
            types.add(BiometricLockPlugin.BiometryType.IRIS);
        }

        if (types.size() == 0) {
            types.add(BiometricLockPlugin.BiometryType.NONE);
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

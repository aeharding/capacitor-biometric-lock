package com.mycompany.plugins.example;

import android.content.pm.PackageManager;
import androidx.biometric.BiometricManager;
import android.os.Build;
import android.util.Log;

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

        if (config.enabled) {
            addFlags();
        }
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
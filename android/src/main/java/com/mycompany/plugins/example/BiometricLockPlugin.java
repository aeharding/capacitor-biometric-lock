package com.mycompany.plugins.example;


import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

import android.app.ActivityManager;
import android.os.Handler;
import android.util.Log;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebView;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.WebViewListener;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "BiometricLock")
public class BiometricLockPlugin extends Plugin {

    private BiometricLock implementation;
    private boolean webViewOpen;


    public void load() {
        implementation = new BiometricLock(this);

        getBridge().getWebView().evaluateJavascript();
        getBridge().getWebView().event
    }

    @Override
    protected void handleOnStart() {
        super.handleOnStart();

        if (!webViewOpen) {
            implementation.onResume();
        }
    }


    @Override
    protected void handleOnStop() {

        super.handleOnStop();
    }

    @PluginMethod
    public void configure(PluginCall call) {
        BiometricLockConfiguration config = new BiometricLockConfiguration();

        // Set configuration values based on plugin call parameters
        config.timeoutInSeconds = call.getInt("timeoutInSeconds", config.timeoutInSeconds);
        config.enabled = call.getBoolean("enabled", config.enabled);
        config.appName = call.getString("appName", config.appName);
        config.retryButtonColor = call.getString("retryButtonColor", config.retryButtonColor);

        implementation.configure(config);

        getConfiguration(call);
    }

    @PluginMethod
    public void getConfiguration(PluginCall call) {
        BiometricLockConfiguration config = implementation != null ? implementation.getConfiguration() : new BiometricLockConfiguration();

        JSObject result = new JSObject();
        result.put("enabled", config.enabled);
        result.put("timeoutInSeconds", config.timeoutInSeconds);
        result.put("appName", config.appName);
        result.put("retryButtonColor", config.retryButtonColor);

        call.resolve(result);
    }

    @PluginMethod
    public void getBiometricMethod(PluginCall call) {
        int method = implementation != null ? implementation.getBiometricMethod(this) : 0;

        JSObject result = new JSObject();
        result.put("biometricMethod", method);

        call.resolve(result);
    }

    public enum BiometryType {
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
}

package com.mycompany.plugins.example;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "BiometricLock")
public class BiometricLockPlugin extends Plugin {

    private BiometricLock implementation;

    public void load() {
         implementation = new BiometricLock(this);
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
        int method = implementation != null ? implementation.getBiometricMethod() : 0;

        JSObject result = new JSObject();
        result.put("biometricMethod", method);

        call.resolve(result);
    }
}

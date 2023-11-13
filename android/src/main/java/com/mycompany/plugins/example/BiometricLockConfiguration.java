package com.mycompany.plugins.example;
import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONException;
import org.json.JSONObject;

public class BiometricLockConfiguration {

    private static final String BIOMETRIC_CONFIG_KEY = "__capacitor_biometricLockConfiguration";

    public boolean enabled = false;
    public int timeoutInSeconds = 0;
    public String appName = "";
    public String retryButtonColor = "000000";

    // Constructor
    public BiometricLockConfiguration() {
        // Default constructor
    }

    // Function to save the BiometricLockConfig to SharedPreferences
    public void save(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("enabled", enabled);
            jsonObject.put("timeoutInSeconds", timeoutInSeconds);
            jsonObject.put("appName", appName);
            jsonObject.put("retryButtonColor", retryButtonColor);

            editor.putString(BIOMETRIC_CONFIG_KEY, jsonObject.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Function to retrieve the BiometricLockConfig from SharedPreferences
    public static BiometricLockConfiguration load(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(BIOMETRIC_CONFIG_KEY, "");

        if (json.isEmpty()) {
            return null;
        }

        BiometricLockConfiguration configuration = new BiometricLockConfiguration();
        try {
            JSONObject jsonObject = new JSONObject(json);
            configuration.enabled = jsonObject.optBoolean("enabled", false);
            configuration.timeoutInSeconds = jsonObject.optInt("timeoutInSeconds", 0);
            configuration.appName = jsonObject.optString("appName", "");
            configuration.retryButtonColor = jsonObject.optString("retryButtonColor", "000000");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return configuration;
    }
}

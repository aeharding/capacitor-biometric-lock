package com.mycompany.plugins.example;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.hardware.biometrics.BiometricManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import java.util.concurrent.Executor;

public class AuthActivity extends AppCompatActivity {
    public static BiometricLockPlugin plugin;
    static boolean allowDeviceCredential;

    boolean authenticated = false;

    private Button retryButton;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        retryButton = findViewById(R.id.retryButton);

        applyBlurEffect();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Execute your function after a delay (e.g., after 1000 milliseconds = 1 second)
                authenticate();
            }
        }, 100);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticate();
            }
        });
    }

    void authenticate() {
        hideRetryButton();

        Executor executor;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            executor = this.getMainExecutor();
        } else {
            executor = command -> new Handler(this.getMainLooper()).post(command);
        }

        BiometricPrompt.PromptInfo.Builder builder =
                new BiometricPrompt.PromptInfo.Builder();
        Intent intent = getIntent();
        String title = "Hi";
        String subtitle = "Sub";
        String description = "Reeason";
        allowDeviceCredential = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android docs say we should check if the device is secure before enabling device credential fallback
            KeyguardManager manager = (KeyguardManager) getSystemService(
                    KEYGUARD_SERVICE
            );

            if (manager.isDeviceSecure()) {
                allowDeviceCredential = true;
            }
        }

        // The title must be non-null and non-empty
        if (title == null || title.isEmpty()) {
            title = "Authenticate";
        }

        builder.setTitle(title).setSubtitle(subtitle).setDescription(description);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int authenticators = BiometricManager.Authenticators.BIOMETRIC_WEAK;

            if (allowDeviceCredential) {
                authenticators |= BiometricManager.Authenticators.DEVICE_CREDENTIAL;
            }

            builder.setAllowedAuthenticators(authenticators);
        } else {
            builder.setDeviceCredentialAllowed(allowDeviceCredential);
        }

        // Android docs say that negative button text should not be set if device credential is allowed
        if (!allowDeviceCredential) {
//            String negativeButtonText = intent.getStringExtra(
//                    BiometricAuthNative.CANCEL_TITLE
//            );
//            builder.setNegativeButtonText(
//                    negativeButtonText == null || negativeButtonText.isEmpty()
//                            ? "Cancel"
//                            : negativeButtonText
//            );
        }

//        builder.setConfirmationRequired(
//                intent.getBooleanExtra(BiometricAuthNative.CONFIRMATION_REQUIRED, true)
//        );

        BiometricPrompt.PromptInfo promptInfo = builder.build();
        BiometricPrompt prompt = new BiometricPrompt(
                this,
                executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(
                            int errorCode,
                            @NonNull CharSequence errorMessage
                    ) {
                        super.onAuthenticationError(errorCode, errorMessage);

                        showRetryButton();
                    }

                    @Override
                    public void onAuthenticationSucceeded(
                            @NonNull BiometricPrompt.AuthenticationResult result
                    ) {
                        super.onAuthenticationSucceeded(result);
                        finishActivity();
                    }
                }
        );

        prompt.authenticate(promptInfo);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (authenticated) return;

        finishAffinity();
    }

    void finishActivity() {
        undoBlurEffect();
        authenticated = true;
        finish();
    }

    static public void applyBlurEffect() {
        // Check if the device supports the blur effect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Apply blur effect
            plugin.getBridge().getWebView().setRenderEffect(
                    RenderEffect.createBlurEffect(50, 50, Shader.TileMode.CLAMP)
            );
        } else {
            // If the blur effect is not supported, set background color to grey
            plugin.getBridge().getWebView().setBackgroundColor(plugin.getContext().getResources().getColor(android.R.color.darker_gray));
        }
    }

    static public void undoBlurEffect() {
        // Clear render effect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            plugin.getBridge().getWebView().setRenderEffect(null);
        }

        // Reset background color to default or any other color you prefer
        plugin.getBridge().getWebView().setBackgroundColor(Color.TRANSPARENT);
    }

    // Example method to show the retry button
    private void showRetryButton() {
        retryButton.setVisibility(View.VISIBLE);
    }

    // Example method to hide the retry button
    private void hideRetryButton() {
        retryButton.setVisibility(View.GONE);
    }
}

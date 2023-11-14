package com.mycompany.plugins.example;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
public class PrivacyProtectionWindow {
    private final BiometricLockPlugin plugin;
    private ViewGroup overlayView;

    public PrivacyProtectionWindow(BiometricLockPlugin plugin) {
        this.plugin = plugin;
        initOverlayView();
    }

    private void initOverlayView() {
        // Use a FrameLayout as the root layout
        overlayView = new FrameLayout(plugin.getContext());

        // Set layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        // Add the overlay view to the window manager
        ViewGroup rootView = plugin.getActivity().findViewById(android.R.id.content);
        rootView.addView(overlayView);

        // Inflate the layout with a centered button
        LayoutInflater.from(plugin.getContext()).inflate(R.layout.privacy_protection_layout, overlayView, true);

        // Set up the button click listener
        Button centerButton = overlayView.findViewById(R.id.retryButton);

        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
            }
        });
    }

    public void show() {
        if (overlayView != null) {
            overlayView.setVisibility(View.VISIBLE);
        }
    }

    public void hide() {
        if (overlayView != null) {
            overlayView.setVisibility(View.GONE);
        }
    }
}

package com.anubhav.edgeviewer;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextureView textureView;
    private GLSurfaceView glSurfaceView;
    private GLRenderer glRenderer;
    private EdgeViewer edgeViewer;
    private Button toggleButton;
    private Button filterButton;
    private Button effectButton;
    private TextView fpsText;
    private boolean isProcessed = true;
    private int currentFilter = 0;
    private int currentEffect = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        textureView = findViewById(R.id.textureView);
        glSurfaceView = findViewById(R.id.glSurfaceView);
        toggleButton = findViewById(R.id.toggleButton);
        filterButton = findViewById(R.id.filterButton);
        effectButton = findViewById(R.id.effectButton);
        fpsText = findViewById(R.id.fpsText);
        
        glRenderer = new GLRenderer();
        glSurfaceView.setRenderer(glRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        edgeViewer = new EdgeViewer(textureView, glRenderer);
        edgeViewer.startCamera(this);

        toggleButton.setOnClickListener(v -> {
            isProcessed = !isProcessed;
            edgeViewer.setProcessed(isProcessed);
            toggleButton.setText(isProcessed ? "Show Raw" : "Show Processed");
        });

        filterButton.setOnClickListener(v -> {
            if (isProcessed) {
                currentFilter = (currentFilter + 1) % 3;
                edgeViewer.setFilterType(currentFilter);
                String[] filterNames = {"Canny", "Gaussian", "Threshold"};
                filterButton.setText("Filter: " + filterNames[currentFilter]);
            }
        });

        effectButton.setOnClickListener(v -> {
            currentEffect = (currentEffect + 1) % 3;
            glRenderer.setEffectType(currentEffect);
            String[] effectNames = {"Normal", "Grayscale", "Invert"};
            effectButton.setText("Effect: " + effectNames[currentEffect]);
        });

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    runOnUiThread(() -> {
                        float fps = edgeViewer.getCurrentFps();
                        fpsText.setText(String.format("FPS: %.1f", fps));
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }
} 
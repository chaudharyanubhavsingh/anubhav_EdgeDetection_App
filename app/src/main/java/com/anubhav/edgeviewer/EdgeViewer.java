package com.anubhav.edgeviewer;

import android.graphics.SurfaceTexture;
import android.media.Image;
import android.view.Surface;
import android.view.TextureView;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EdgeViewer {
    private TextureView textureView;
    private ExecutorService cameraExecutor;
    private ProcessCameraProvider cameraProvider;
    private GLRenderer glRenderer;
    private boolean isProcessed = true;

    public EdgeViewer(TextureView textureView, GLRenderer glRenderer) {
        this.textureView = textureView;
        this.glRenderer = glRenderer;
        this.cameraExecutor = Executors.newSingleThreadExecutor();
    }

    public void startCamera(LifecycleOwner lifecycleOwner) {
        ProcessCameraProvider.getInstance(ContextCompat.getMainExecutor(textureView.getContext()))
            .addListener(() -> {
                try {
                    cameraProvider = ProcessCameraProvider.getInstance(textureView.getContext()).get();
                    bindPreview(lifecycleOwner);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, ContextCompat.getMainExecutor(textureView.getContext()));
    }

    private void bindPreview(LifecycleOwner lifecycleOwner) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(surface -> {
            SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
            if (surfaceTexture != null) {
                surfaceTexture.setDefaultBufferSize(640, 480);
                surface.release(new Surface(surfaceTexture));
            }
        });

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build();

        imageAnalysis.setAnalyzer(cameraExecutor, image -> {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            int width = image.getWidth();
            int height = image.getHeight();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            if (isProcessed) {
                int[] processedData = processFrame(data, width, height);
                glRenderer.updateTexture(processedData, width, height);
            } else {
              
                int[] rawData = new int[width * height];
                for (int i = 0; i < rawData.length; i++) {
                    rawData[i] = (data[i * 4] & 0xff) | ((data[i * 4 + 1] & 0xff) << 8) | ((data[i * 4 + 2] & 0xff) << 16) | ((data[i * 4 + 3] & 0xff) << 24);
                }
                glRenderer.updateTexture(rawData, width, height);
            }
            image.close();
        });

        cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis);
    }

    public void setProcessed(boolean processed) {
        this.isProcessed = processed;
    }

    private native int[] processFrame(byte[] input, int width, int height);

    static {
        System.loadLibrary("edgeviewer");
    }
} 
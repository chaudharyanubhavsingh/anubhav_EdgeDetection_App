package com.anubhav.edgeviewer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLRenderer implements GLSurfaceView.Renderer {
    private float[] mvpMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private int[] textures = new int[1];
    private int[] processedData;
    private int width, height;
    private int mProgram;
    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;
    private int mPositionHandle;
    private int mTexCoordHandle;
    private int mMVPMatrixHandle;
    private int mTextureHandle;
    private int mEffectTypeHandle;
    private float currentFps = 0;
    private int effectType = 0; 

    private static final float[] VERTEX_COORDS = {
        -1.0f, -1.0f, 0.0f,
         1.0f, -1.0f, 0.0f,
        -1.0f,  1.0f, 0.0f,
         1.0f,  1.0f, 0.0f
    };

    private static final float[] TEX_COORDS = {
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f
    };

    private static final String VERTEX_SHADER =
        "uniform mat4 uMVPMatrix;" +
        "attribute vec4 aPosition;" +
        "attribute vec2 aTexCoord;" +
        "varying vec2 vTexCoord;" +
        "void main() {" +
        "  gl_Position = uMVPMatrix * aPosition;" +
        "  vTexCoord = aTexCoord;" +
        "}";

    private static final String FRAGMENT_SHADER =
        "precision mediump float;" +
        "varying vec2 vTexCoord;" +
        "uniform sampler2D sTexture;" +
        "uniform int uEffectType;" +
        "void main() {" +
        "  vec4 color = texture2D(sTexture, vTexCoord);" +
        "  if (uEffectType == 1) {" + 
        "    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));" +
        "    gl_FragColor = vec4(vec3(gray), color.a);" +
        "  } else if (uEffectType == 2) {" + 
        "    gl_FragColor = vec4(1.0 - color.rgb, color.a);" +
        "  } else {" + 
        "    gl_FragColor = color;" +
        "  }" +
        "}";

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        vertexBuffer = ByteBuffer.allocateDirect(VERTEX_COORDS.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(VERTEX_COORDS).position(0);

        texCoordBuffer = ByteBuffer.allocateDirect(TEX_COORDS.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texCoordBuffer.put(TEX_COORDS).position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if (processedData != null) {
            GLES20.glUseProgram(mProgram);
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
            mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            mTextureHandle = GLES20.glGetUniformLocation(mProgram, "sTexture");
            mEffectTypeHandle = GLES20.glGetUniformLocation(mProgram, "uEffectType");

            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

            GLES20.glEnableVertexAttribArray(mTexCoordHandle);
            GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer);

            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
            GLES20.glUniform1i(mEffectTypeHandle, effectType);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, ByteBuffer.wrap(ByteBuffer.allocate(processedData.length * 4).putInt(processedData).array()));
            GLES20.glUniform1i(mTextureHandle, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            GLES20.glDisableVertexAttribArray(mPositionHandle);
            GLES20.glDisableVertexAttribArray(mTexCoordHandle);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    public void updateTexture(int[] data, int w, int h) {
        this.processedData = data;
        this.width = w;
        this.height = h;
    }

    public void updateFps(float fps) {
        this.currentFps = fps;
    }

    public void setEffectType(int type) {
        this.effectType = type;
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
} 
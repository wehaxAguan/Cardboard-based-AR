package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.google.vrtoolkit.cardboard.ScreenParams;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.utils.GlHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

/**
 * Created by mayuhan on 15/6/11.
 */
public class FovBackground {

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    private static final float[] mVerticesData = {
            // X, Y, Z, U, V
            -1f, -1f, -40f, 0.f, 0.f,   //lb
            1f, -1f, -40f, 1.f, 0.f,    //rb
            -1f, 1f, -40f, 0.f, 1.f,    //lt
            1f, 1f, -40f, 1.f, 1.f,     //rt
    };
    private float[] mTriangleVerticesData;

    private FloatBuffer mTriangleVertices;


    private final String mVertexShader =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uSTMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * aPosition;\n" +
                    "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                    "}\n";

    private final String mFragmentShader =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
//                    "uniform vec4 u_Color;\n" +
                    "void main() {\n" +
//                    "  gl_FragColor = u_Color;\n" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}\n";

    private float[] mMVPMatrix = new float[16];
    private float[] mSTMatrix = new float[16];
    private float[] mModelMatrix = new float[16];

    private int mProgram;
    private int mTextureID;
    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;
    private int maPositionHandle;
    private int maTextureHandle;
    private int msTextureHandle;

    private float scaleX;
    private float scaleY;
    private ScreenParams screenParams;


//    private int uColorLocation;

    private SurfaceTexture mSurface;

    private static int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

    private Camera fovCamera;


    /**
     * 构造场景中的模型,在onSurfaceCreated中调用
     */
    public void bind() {

        mTriangleVertices = ByteBuffer.allocateDirect(
                mTriangleVerticesData.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangleVertices.put(mTriangleVerticesData).position(0);
        Matrix.setIdentityM(mSTMatrix, 0);


        mProgram = GlHelper.createProgram(mVertexShader, mFragmentShader);
        if (mProgram == 0) {
            return;
        }
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GlHelper.checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        GlHelper.checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GlHelper.checkGlError("glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
        GlHelper.checkGlError("glGetUniformLocation uSTMatrix");
        if (muSTMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
        GlHelper.checkGlError("glBindTexture mTextureID");

        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        msTextureHandle = GLES20.glGetUniformLocation(mProgram, "sTexture");
        GlHelper.checkGlError("glGetUniformLocation sTexture");
        if (msTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for sTexture");
        }

        mSurface = new SurfaceTexture(mTextureID);

        float width = screenParams.getWidthMeters();
        float height = screenParams.getHeightMeters();

        scaleX = width / 0.1f * 40.1f;
        scaleY = height / 0.1f * 40.1f;

    }

    public void setOnFrameAvailableListener(SurfaceTexture.OnFrameAvailableListener listener) {
        mSurface.setOnFrameAvailableListener(listener);
    }

    public void start(ScreenParams screenParams) {
        try {
            if (fovCamera == null) {
                fovCamera = Camera.open();
            }


            this.screenParams = screenParams;
            updateScreenAdaptive();

            fovCamera.setPreviewTexture(mSurface);
            Camera.Parameters camParam = fovCamera.getParameters();
            if (camParam.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                camParam.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                fovCamera.setParameters(camParam);
            }
            fovCamera.startPreview();
//            fovCamera.autoFocus();


        } catch (Exception ioe) {
            ioe.printStackTrace();
            Log.w("MainActivity", "CAM LAUNCH FAILED");
        }
    }

    public void stop() {
        fovCamera.stopPreview();
        fovCamera.release();
        fovCamera = null;
    }

    public void updateTexImage() {
    }

    public void draw(float[] perspective) {

        synchronized (this) {
//            if (updateSurface) {
            mSurface.updateTexImage();
            mSurface.getTransformMatrix(mSTMatrix);
//                updateSurface = false;
//            }
        }

//        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
//        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(mProgram);
        GlHelper.checkGlError("glUseProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
        GLES20.glUniform1i(msTextureHandle, 0);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        GlHelper.checkGlError("glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GlHelper.checkGlError("glEnableVertexAttribArray maPositionHandle");

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(maTextureHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        GlHelper.checkGlError("glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        GlHelper.checkGlError("glEnableVertexAttribArray maTextureHandle");

        Matrix.setIdentityM(mMVPMatrix, 0);
//        float temp[] = new float[16];

        //TODO 貌似可以通过ClipMatrix优化

        Matrix.multiplyMM(mMVPMatrix, 0, perspective, 0, mMVPMatrix, 0);
        Matrix.scaleM(mMVPMatrix, 0, mMVPMatrix, 0, scaleX, scaleY, 1f);
//        Matrix.multiplyMM(mMVPMatrix, 0, scale, 0, mMVPMatrix, 0);

//        GLES20.glUniform4f(uColorLocation, 1f, 0f, 0f, 1f);
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);


        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GlHelper.checkGlError("glDrawArrays");

    }

    private void updateScreenAdaptive() {

        Camera.Parameters camParam = fovCamera.getParameters();
        List<Camera.Size> supportSizes = camParam.getSupportedPreviewSizes();
        Camera.Size size = supportSizes.get(GlHelper.maxPreviewSizeIndex(camParam));
        camParam.setPreviewSize(size.width, size.height);
        fovCamera.setParameters(camParam);

        final float trimOffset;
        final float screenWidth, screenHeight;
        screenWidth = screenParams.getWidthMeters();
        screenHeight = screenParams.getHeightMeters();
        final float texAspect = (float) size.width / (float) size.height;
        final float screenAspect = screenWidth / screenHeight;
        final float scale;
        mTriangleVerticesData = mVerticesData.clone();
        if (texAspect > screenAspect) {
            scale = size.height / screenHeight;

            trimOffset = (size.width - screenWidth * scale) / 2 / size.height;
            //lb
            mTriangleVerticesData[3] += trimOffset;
            //lt
            mTriangleVerticesData[13] += trimOffset;
            //rb
            mTriangleVerticesData[8] -= trimOffset;
            //rt
            mTriangleVerticesData[18] -= trimOffset;
        } else {
            scale = size.width / screenWidth;
            trimOffset = (size.height - screenHeight * scale) / 2 / size.width;
            //lb
            mTriangleVerticesData[4] += trimOffset;
            //lt
            mTriangleVerticesData[14] -= trimOffset;
            //rb
            mTriangleVerticesData[9] += trimOffset;
            //rt
            mTriangleVerticesData[19] -= trimOffset;
        }
    }


}

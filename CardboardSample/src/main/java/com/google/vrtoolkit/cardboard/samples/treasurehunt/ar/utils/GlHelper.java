package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.utils;

import android.hardware.Camera;
import android.opengl.GLES20;
import android.util.Log;

import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker.Spirit;

import java.util.List;

/**
 * Created by mayuhan on 15/6/11.
 */
public class GlHelper {

    private final static String TAG = "GlHelper";

    /**
     * 检查GL内部报错
     *
     * @param action
     */
    public static void checkGlError(String action) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, action + ": glError " + error);
            throw new RuntimeException(action + ": glError " + error);
        }
    }

    public static void checkObjShaderError(Spirit sprit, String action) {
        checkGlError(action + " ready to draw");
    }

    public static void checkShaderError(String action, int shaderHandle) {
        Log.d(action, GLES20.glGetShaderInfoLog(shaderHandle));
    }


    /**
     * 新建一个管线程序对象,并链接传入的顶点和片段着色器
     *
     * @param vertexSource
     * @param fragmentSource
     * @return 管线程序id
     */
    public static int createProgram(String vertexSource, String fragmentSource) {

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        if (program != 0) {

            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");

            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");

            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }


    private static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }


    /**
     * 获得摄像头支持的最高预览分辨率index
     *
     * @param parameters
     * @return index
     */
    public static int maxPreviewSizeIndex(Camera.Parameters parameters) {
        int maxIndex = 0;
        int quality = 0;
        Camera.Size curSize;
        List<Camera.Size> supportSize = parameters.getSupportedPictureSizes();
        for (int i = 0; i < supportSize.size(); i++) {
            curSize = supportSize.get(i);
            if (curSize.width * curSize.height > quality) {
                quality = curSize.width * curSize.height;
                maxIndex = i;
            }
        }
        return maxIndex;
    }

}

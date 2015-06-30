package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.exception.CompileGLSLException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mayuhan on 15/6/23.
 */
public class ShaderManager {
    private static String TAG = "ShaderManager";
    private static Map<String, Integer> shaderCache = new HashMap<String, Integer>();


    public static int loadGLShader(Context ctx, int type, int resId) {
        String code = readRawTextFile(ctx, resId);
        return loadGLShader(type, code);
    }

    /**
     * 读取并编译一段GLSL
     *
     * @param type
     * @param shaderCodeString
     * @return 着色器对象id
     */
    public static int loadGLShader(int type, String shaderCodeString) {

        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCodeString);
        GLES20.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            String infoLog = GLES20.glGetShaderInfoLog(shader);
            Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            throw new CompileGLSLException(infoLog);
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;

    }

    public static String readRawTextFile(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

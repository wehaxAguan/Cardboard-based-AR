package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.utils.GlHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by mayuhan on 15/6/24.
 */
public class ViewObject {

    /**
     * 绘制图形时，每个顶点xyz三分坐标
     */
    public static int COORDS_PER_VERTEX_3D = 3;

    /**
     * 纹理映射时，每个顶点对应贴图的uv两分坐标
     */
    public static int COORDS_PER_VERTEX_UV = 2;

    /**
     * 每个ViewObj都视为一个空间中的正方行，所以顶点数量为4
     */
    public static int VERTEX_CONT = 4;

    private static String TAG = "ViewObject";

    int program;
    int positionHandle;
    int textureCoordHandle;
    int textureHandle;
    int mvpMatrixHandle;

    private float[] mvpMatrix = new float[16];
    private float[] model = new float[16];

    FloatBuffer vertexBuffer;
    FloatBuffer textureBuffer;


    public ViewObject(int vertexShader, int fragmentShader) {
        this.program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);
        GlHelper.checkGlError(this.getClass().getName());
        textureCoordHandle = GLES20.glGetAttribLocation(program, "aTextureCoord");
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");

        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, 0, 0, -10);
    }


    public void putVertexData(float[] vertexData) {
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertexData);
        vertexBuffer.position(0);
    }

    public void putTexture(int texture, float[] textureCoordData) {
        this.textureHandle = texture;
        textureBuffer = ByteBuffer.allocateDirect(textureCoordData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        textureBuffer.put(textureCoordData);
        textureBuffer.position(0);
    }


    public void draw(float[] perspective, float[] view) {

        GLES20.glUseProgram(program);
        GlHelper.checkGlError(TAG + ":glUserProgram");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
        GLES20.glUniform1i(textureHandle, 0);

        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX_3D, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(textureCoordHandle, COORDS_PER_VERTEX_UV, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(textureCoordHandle);


        float[] modelView = new float[16];

        Matrix.multiplyMM(modelView, 0, view, 0, model, 0);

        Matrix.multiplyMM(mvpMatrix, 0, perspective, 0, modelView, 0);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_CONT);

    }

}

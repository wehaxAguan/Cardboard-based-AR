package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.programs.TextureShaderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by mayuhan on 15/6/24.
 */
public class Sprit {

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
    private float[] model = new float[16];

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private int textureHandle;

    public void putVertexData(float[] vertexData) {
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertexData);
        vertexBuffer.position(0);
    }

    public void putTexture(int textureHandle, float[] textureCoordData) {
        this.textureHandle = textureHandle;
        textureBuffer = ByteBuffer.allocateDirect(textureCoordData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        textureBuffer.put(textureCoordData);
        textureBuffer.position(0);
    }

    public void moveTo(float x, float y, float z) {
        Matrix.setIdentityM(model, 0);
        Matrix.translateM(model, 0, x, y, z);
    }


    public void draw(float[] perspective, float[] view, TextureShaderProgram program) {

        program.setTexture(textureHandle);

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(program.getVertexPositionHandle(), COORDS_PER_VERTEX_3D, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(program.getVertexPositionHandle());

        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(program.getTextureCoordHandle(), COORDS_PER_VERTEX_UV, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(program.getTextureCoordHandle());

        final float[] modelView = new float[16];

        Matrix.multiplyMM(modelView, 0, view, 0, model, 0);

        final float[] mvpMatrix = new float[16];

        Matrix.multiplyMM(mvpMatrix, 0, perspective, 0, modelView, 0);

        GLES20.glUniformMatrix4fv(program.getMVPMatrixHandle(), 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX_CONT);
    }

}

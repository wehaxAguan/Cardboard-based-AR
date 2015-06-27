package com.google.vrtoolkit.cardboard.samples.treasurehunt.markers;

import android.opengl.GLES20;

import com.google.vrtoolkit.cardboard.samples.treasurehunt.utils.GlHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by mayuhan on 15/6/24.
 */
public class ViewObject {

    /**
     * 绘制图形时，每个顶点xyz三个坐标
     */
    public static int COORDS_PER_VERTEX_3D = 3;

    /**
     * 纹理映射时，每个顶点对应贴图的uv两个坐标
     */
    public static int COORDS_PER_VERTEX_UV = 2;

    private static String TAG = "ViewObject";

    private int vertexCount = 0;

    float[] mvpMatrix = new float[16];

    int program;
    int positionLocation;
    int textureCoord;
    int texture;
    int uTexture;

    FloatBuffer vertexBuffer;
    FloatBuffer textureBuffer;


    public ViewObject(int vertexShader, int fragmentShader) {
        this.program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);
        GlHelper.checkGlError(this.getClass().getName());
        GLES20.glGetAttribLocation(textureCoord, "aTextureCoord");
        GLES20.glGetAttribLocation(uTexture, "uTexture");
    }


    public void putVertexData(float[] vertexData) {
        this.vertexCount = vertexData.length;
        vertexBuffer = ByteBuffer.allocate(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertexData);
        vertexBuffer.position(0);
    }

    public void putTexture(int texture, float[] textureCoordData) {
        this.texture = texture;
        textureBuffer = ByteBuffer.allocate(textureCoordData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        textureBuffer.put(textureCoordData);
        textureBuffer.position(0);
    }


    public void draw() {
        if (texture == 0) {
            return;
        }

        GLES20.glUseProgram(program);
        GlHelper.checkGlError(TAG + ":glUserProgram");

        GLES20.glActiveTexture(this.texture);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);

        GLES20.glUniform1i(uTexture, 0);

        GLES20.glVertexAttribPointer(positionLocation, COORDS_PER_VERTEX_3D, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionLocation);

        GLES20.glVertexAttribPointer(textureCoord, COORDS_PER_VERTEX_UV, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(textureCoord);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

    }
}

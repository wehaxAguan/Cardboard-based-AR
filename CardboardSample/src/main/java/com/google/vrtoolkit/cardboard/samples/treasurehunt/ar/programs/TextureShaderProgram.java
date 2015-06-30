package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.google.vrtoolkit.cardboard.samples.treasurehunt.R;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.ShaderManager;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.utils.GlHelper;

/**
 * Created by mayuhan on 15/6/29.
 */
public class TextureShaderProgram {

    private static final String U_MVPMATRIX = "uMVPMatrix";
    private static final String U_TEXTURE = "uTexture";

    private static final String A_POSTION = "aPosition";
    private static final String A_TEXTURECOORD = "aTextureCoord";

    private final int uMVPMatrix;
    private final int aPosition;
    private final int aTextureCoord;
    private final int uTexture;

    private final int program;

    public TextureShaderProgram(Context context) {
        program = GlHelper.createProgram(ShaderManager.readRawTextFile(context, R.raw.texture_vertex),
                ShaderManager.readRawTextFile(context, R.raw.texture_fragment));
        aPosition = GLES20.glGetAttribLocation(program, A_POSTION);
        aTextureCoord = GLES20.glGetAttribLocation(program, A_TEXTURECOORD);
        uMVPMatrix = GLES20.glGetUniformLocation(program, U_MVPMATRIX);
        uTexture = GLES20.glGetUniformLocation(program, U_TEXTURE);
    }

    public void setTexture(int textureId) {


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glUniform1i(uTexture, 0);

        checkInfo("setTexture");

    }


    public int getVertexPositionHandle() {
        return aPosition;
    }

    public int getTextureCoordHandle() {
        return aTextureCoord;
    }

    public int getMVPMatrixHandle() {
        return uMVPMatrix;
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
        checkInfo("useProgram");
    }

    private void checkInfo(String action) {
        GlHelper.checkGlError(action);
    }
}

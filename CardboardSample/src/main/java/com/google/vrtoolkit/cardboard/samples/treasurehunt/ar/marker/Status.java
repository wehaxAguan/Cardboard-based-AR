package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker;

import android.content.Context;

import com.google.vrtoolkit.cardboard.samples.treasurehunt.R;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.ModelDataManager;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.TextureDataManager;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker.base.Spirit;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker.base.ViewObject;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.programs.TextureShaderProgram;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.utils.TextureLoader;

/**
 * Created by mayuhan on 15/6/23.
 */
public class Status extends ViewObject {

    private Spirit mBorder;
    private Spirit mThumbnail;
    private final static float LEVEL_RANGE = 0.1f;

    public void bind(Context context) {

        mBorder = new Spirit();
        mThumbnail = new Spirit();

        mBorder.putTextureData(TextureLoader.load(context, R.drawable.avatar_border_male), TextureDataManager.getAvatarBorderData());
        mBorder.putVertexData(ModelDataManager.getSpiritVertexData());

        mThumbnail.putTextureData(TextureLoader.load(context, R.drawable.ic_launcher), TextureDataManager.getAvatarBorderData());
        mThumbnail.putVertexData(ModelDataManager.getSpiritVertexData());

    }

    public void draw(float[] perspective, float[] view, TextureShaderProgram program) {
        mThumbnail.draw(perspective, view, program);
        mBorder.draw(perspective, view, program);
    }

    public void moveTo(float x, float y, float z) {
        mThumbnail.moveTo(x, y, z);
        mBorder.moveTo(x, y, z + LEVEL_RANGE);
    }
}

package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker;

import android.content.Context;
import android.util.Log;

import com.google.vrtoolkit.cardboard.samples.treasurehunt.R;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.ModelDataManager;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.TextureDataManager;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.programs.TextureShaderProgram;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.utils.TextureLoader;

/**
 * Created by mayuhan on 15/6/23.
 */
public class Avatar {

    private final static String TAG = "Avatar";
    private Spirit mBorder;
    private Spirit mAvatar;

    public void bind(Context context) {
        mBorder = new Spirit();
        mAvatar = new Spirit();

        mBorder.putTexture(TextureLoader.load(context, R.drawable.avatar_border_male), TextureDataManager.getAvatarBorderData());
        mBorder.putVertexData(ModelDataManager.getSpiritVertexData());

        mAvatar.putTexture(TextureLoader.load(context, R.drawable.ic_launcher), TextureDataManager.getAvatarBorderData());
        mAvatar.putVertexData(ModelDataManager.getSpiritVertexData());

    }

    public void draw(float[] perspective, float[] view, TextureShaderProgram program) {
        if (mBorder == null || mAvatar == null) {
            Log.e(TAG, "Avatar need bind onSurfaceCreated");
            return;
        }
        mAvatar.draw(perspective, view, program);
        mBorder.draw(perspective, view, program);
    }

    public void moveTo(float x, float y, float z) {
        if (mBorder == null || mAvatar == null) {
            Log.e(TAG, "Avatar need bind onSurfaceCreated");
            return;
        }
        mAvatar.moveTo(x, y, z);
        mBorder.moveTo(x, y, z+0.1f);

    }

}

package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker;

import android.content.Context;
import android.util.Log;

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
public class Avatar extends ViewObject {

    private final static String TAG = "Avatar";
    private final static float LEVEL_RANGE = 0.1f;
    private Spirit mBorder;
    private Spirit mAvatar;

    public void bind(Context context) {
        mBorder = new Spirit();
        mAvatar = new Spirit();

        mBorder.putTextureData(TextureLoader.load(context, R.drawable.avatar_border_male), TextureDataManager.getAvatarBorderData());
        mBorder.putVertexData(ModelDataManager.getSpiritVertexData());


        mAvatar.putTextureData(0, TextureDataManager.getAvatarBorderData());
        mAvatar.putVertexData(ModelDataManager.getSpiritVertexData());

    }

    public void draw(float[] perspective, float[] view, TextureShaderProgram program) {
        if (mBorder == null || mAvatar == null) {
            Log.e(TAG, "Avatar need bind onSurfaceCreated");
            return;
        }
        //希望在底层的先绘制
        mAvatar.draw(perspective, view, program);
        mBorder.draw(perspective, view, program);
    }

    public void moveTo(float x, float y, float z) {
        if (mBorder == null || mAvatar == null) {
            Log.e(TAG, "Avatar need bind onSurfaceCreated");
            return;
        }
        mAvatar.moveTo(x, y, z);
        //有个距离差防止两个Spirit绘制到同一位置互相冲突
        mBorder.moveTo(x, y, z + LEVEL_RANGE);

    }

    public void setAvatarTexture(int avatarThumbHandle) {
        mAvatar.putTexture(avatarThumbHandle);
    }

}

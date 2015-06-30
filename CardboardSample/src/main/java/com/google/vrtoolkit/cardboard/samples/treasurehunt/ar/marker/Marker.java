package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker;

import android.content.Context;

import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker.base.ViewObject;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.programs.TextureShaderProgram;

/**
 * Created by mayuhan on 15/6/30.
 */
public class Marker extends ViewObject {

    private Avatar mAvatar;
    private Status mStatus;
    private final static float LEVEL_RANGE = 0.5f;

    public void bind(Context context) {
        mAvatar = new Avatar();
        mStatus = new Status();
        mAvatar.bind(context);
        mStatus.bind(context);
    }

    public void moveTo(float x, float y, float z) {
        mAvatar.moveTo(x, y, z);
        mStatus.moveTo(x, y + 2, z + LEVEL_RANGE);
    }

    public void draw(float[] perspective, float[] view, TextureShaderProgram program) {

        mAvatar.draw(perspective, view, program);
        mStatus.draw(perspective, view, program);

    }

    public void setAvatarTexture(int textureHandle) {
        mAvatar.setAvatarTexture(textureHandle);
    }

}

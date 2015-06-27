package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar;

/**
 * Created by mayuhan on 15/6/27.
 */
public class TextureDataManager {
    private static final float[] mAvatarBorderData = {
            //U ,V
            0f, 0f,         //lb
            1f, 0f,         //rb
            0f, 1f,         //lt
            1f, 1f          //rt
    };

    public static float[] getAvatarBorderData() {
        return mAvatarBorderData.clone();
    }

}

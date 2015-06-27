package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar;

/**
 * Created by mayuhan on 15/6/27.
 */
public class TextureDataManager {
    private static final float[] mAvatarBorderData = {
            //U ,V
            0f, 0f,                 //lt
            0f, 1f,                 //lb
            1f, 0f,                 //rt
            1f, 1f,                 //rb


    };

    /**
     * @return 头像边框strip顺序倒置纹理映射
     */
    public static float[] getAvatarBorderData() {
        return mAvatarBorderData.clone();
    }

}

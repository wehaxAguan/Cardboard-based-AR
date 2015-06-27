package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar;

/**
 * Created by mayuhan on 15/6/27.
 */
public class ModelDataManager {

    private static final float[] mViewObjSquare = {
            // X, Y, Z,
            -1f, -1f, 1f,       //lb
            1f, -1f, 1f,        //rb
            -1f, 1f, 1f,        //lt
            1f, 1f, 1f          //rt
    };

    public static float[] getViewObjectData() {
        return mViewObjSquare.clone();
    }
}

package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.util.HashMap;

/**
 * Created by mayuhan on 15/6/24.
 */
public class TextureLoader {

    private static HashMap<Integer, Integer> textureHandleCache = new HashMap<Integer, Integer>();

    /**
     * 加载一个纹理
     *
     * @param context
     * @param resourceId
     * @return 纹理id
     */
    public static int load(Context context, int resourceId) {
        if (textureHandleCache.containsKey(resourceId)) {
            return textureHandleCache.get(resourceId);
        } else {
            final int[] textureHandle = new int[1];
            GLES20.glGenTextures(1, textureHandle, 0);

            if (textureHandle[0] != 0) {

                final BitmapFactory.Options options = new BitmapFactory.Options();

                options.inScaled = false;

                final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                        GLES20.GL_LINEAR);

                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                        GLES20.GL_LINEAR);

                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

                bitmap.recycle();
            }
            if (textureHandle[0] == 0) {
                throw new RuntimeException("Error loading texture.");
            }

            textureHandleCache.put(resourceId, textureHandle[0]);

            return textureHandle[0];
        }

    }

    public static void clearTextureCache() {
        //TODO 清理掉已经加载过的texture
    }
}

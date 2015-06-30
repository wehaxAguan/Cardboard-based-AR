package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker;

import android.content.Context;

import com.google.vrtoolkit.cardboard.samples.treasurehunt.R;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.ModelDataManager;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.TextureDataManager;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.utils.GlHelper;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.utils.TextureLoader;

/**
 * Created by mayuhan on 15/6/23.
 */
public class Avatar {
    TextureObject border;
    TextureObject avatar;

    public void bind(Context context) {

//        border = new TextureObject(context);
//        avatar = new TextureObject(vertexShader, fragmentShader);

//        border.putVertexData(ModelDataManager.getTextureObjectData());
//        border.putTexture(TextureLoader.load(context, R.drawable.avatar_border_male), TextureDataManager.getAvatarBorderData());

//        avatar.putVertexData(ModelDataManager.getTextureObjectData());
//        avatar.putTexture(TextureLoader.load(context, R.drawable.ic_launcher), TextureDataManager.getAvatarBorderData());

        GlHelper.checkObjShaderError(border, "border");
    }

    public void draw(float[] perspective, float[] view) {
//        border.draw(perspective, view);
//        avatar.draw(perspective, view);
    }

    public void checkSelf() {


//        GlHelper.checkObjShaderError(avatar, "avatar");
    }
}

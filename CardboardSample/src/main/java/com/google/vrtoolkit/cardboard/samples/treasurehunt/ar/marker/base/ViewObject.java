package com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker.base;

import android.content.Context;

import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.programs.TextureShaderProgram;

/**
 * Created by mayuhan on 15/6/30.
 */
public abstract class ViewObject {

    public abstract void draw(float[] pespective, float[] view, TextureShaderProgram program);

    public abstract void bind(Context context);

    public abstract void moveTo(float x, float y, float z);
}

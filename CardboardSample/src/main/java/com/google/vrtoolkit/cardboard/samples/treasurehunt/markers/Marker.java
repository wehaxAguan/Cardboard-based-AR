package com.google.vrtoolkit.cardboard.samples.treasurehunt.markers;

/**
 * Created by mayuhan on 15/6/23.
 */
public class Marker extends ViewObject {
    private int program;
    private int positionLocation;
    private int colorLocation;
    private int normalLocation;

    Marker(int vertexShader, int fragmentShader) {
        super(vertexShader, fragmentShader);
    }

}

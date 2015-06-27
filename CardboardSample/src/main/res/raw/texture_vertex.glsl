#version 120
uniform mat4 uMVPMatrix; //带投影

attribute vec2 aTextureCoord;
varying vec2 vTextureCoord;

void main() {
    vTextureCoord = aTextureCoord;
    gl_Position = uMVPMatrix * aPosition;
}
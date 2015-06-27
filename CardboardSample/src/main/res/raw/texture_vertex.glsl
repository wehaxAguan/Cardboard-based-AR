uniform mat4 uMVPMatrix;

attribute vec2 aTextureCoord;
attribute vec4 aPosition;
varying vec2 vTextureCoord;

void main() {
    vTextureCoord = aTextureCoord;
    gl_Position = uMVPMatrix * aPosition;

}
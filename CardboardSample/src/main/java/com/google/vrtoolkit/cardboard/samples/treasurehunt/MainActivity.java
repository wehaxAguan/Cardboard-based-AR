/*
 * Copyright 2014 Google Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.vrtoolkit.cardboard.samples.treasurehunt;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.FovBackground;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.ModelDataManager;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.ShaderManager;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.TextureDataManager;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker.Avatar;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker.Marker;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker.Status;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.marker.base.Spirit;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.programs.TextureShaderProgram;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.utils.GlHelper;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.ar.utils.TextureLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * A Cardboard sample application.
 */
public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer, SurfaceTexture.OnFrameAvailableListener, View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final float Z_NEAR = 0.1f;
    private static final float Z_FAR = 300.0f;

    private static final float CAMERA_Z = 1f;
    private static final float TIME_DELTA = 0.3f;

    private static final float YAW_LIMIT = 0.12f;
    private static final float PITCH_LIMIT = 0.12f;

    private static final int COORDS_PER_VERTEX = 3;

    // We keep the light always position just above the user.
    private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[]{0.0f, 2.0f, 0.0f, 1.0f};

    private final float[] lightPosInEyeSpace = new float[4];

    private FloatBuffer floorVertices;
    private FloatBuffer floorColors;
    private FloatBuffer floorNormals;

    private FloatBuffer cubeVertices;
    private FloatBuffer cubeColors;
    private FloatBuffer cubeFoundColors;
    private FloatBuffer cubeNormals;

    private int cubeProgram;
    private int floorProgram;

    private int cubePositionParam;
    private int cubeNormalParam;
    private int cubeColorParam;
    private int cubeModelParam;
    private int cubeModelViewParam;
    private int cubeModelViewProjectionParam;
    private int cubeLightPosParam;

    private int floorPositionParam;
    private int floorNormalParam;
    private int floorColorParam;
    private int floorModelParam;
    private int floorModelViewParam;
    private int floorModelViewProjectionParam;
    private int floorLightPosParam;

    private float[] modelCube;
    private float[] camera;
    private float[] view;
    private float[] headView;
    private float[] modelViewProjection;
    private float[] modelView;
    private float[] modelFloor;

    private int score = 0;
    private float objectDistance = 12f;
    private float floorDepth = 20f;

    private TextView currText;

    private Vibrator vibrator;
    private CardboardOverlayView overlayView;

    private FovBackground mFovBg;
    private float[] orthoM = new float[16];
    private int viewObjVertexShader;
    private int viewObjFragmentShader;
    private Spirit mViewObj;
    private Spirit mViewObj2;

    private TextureShaderProgram textureShaderProgram;
    private int avatarBorder;
    private int avatar;
    private Avatar mAvatar;
    private Status mStatus;
    private Marker mMarker;
    private Button loadAvatarBtn;
    private Button changeTexBtn;


    @Override
    protected void onPause() {
        super.onPause();
        mFovBg.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFovBg.start(cardboardView.getScreenParams());
    }

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
     *
     * @param label Label to report in case of error.
     */
    private static void checkGlError(String label) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, label + ": glError " + error);
            throw new RuntimeException(label + ": glError " + error);
        }
    }

    private CardboardView cardboardView;


    /**
     * Sets the view to our CardboardView and initializes the transformation matrices we will use
     * to render our scene.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_ui);
        cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        loadAvatarBtn = (Button) findViewById(R.id.load_texture_btn);
        loadAvatarBtn.setOnClickListener(this);
        changeTexBtn = (Button) findViewById(R.id.change_texture_btn);
        changeTexBtn.setOnClickListener(this);
        currText = (TextView) findViewById(R.id.current_data);

        cardboardView.setVRModeEnabled(false);
        cardboardView.setRenderer(this);
        setCardboardView(cardboardView);

        modelCube = new float[16];
        camera = new float[16];
        view = new float[16];
        modelViewProjection = new float[16];
        modelView = new float[16];
        modelFloor = new float[16];
        headView = new float[16];
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        currText = (TextView) findViewById(R.id.current_data);

        overlayView = (CardboardOverlayView) findViewById(R.id.overlay);
        overlayView.show3DToast("Pull the magnet when you find an object.");
        mFovBg = new FovBackground();
//        mAvatar = new Avatar();

    }

    @Override
    public void onRendererShutdown() {
        Log.i(TAG, "onRendererShutdown");
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged");
        final float aspectRatio = width > height ? (float) width / (float) height : (float) height / (float) width;
        if (width > height) {
            Matrix.orthoM(orthoM, 0, -aspectRatio, aspectRatio, -1f, 1f, -50f, 50f);
        } else {
            Matrix.orthoM(orthoM, 0, -1f, 1f, -aspectRatio, aspectRatio, -50f, 50f);
        }

    }


    private FloatBuffer envVertices, evnTex;

    /**
     * Creates the buffers we use to store information about the 3D world.
     * <p/>
     * <p>OpenGL doesn't use Java arrays, but rather needs data in a format it can understand.
     * Hence we use ByteBuffers.
     *
     * @param config The EGL configuration used when creating the surface.
     */
    @Override
    public void onSurfaceCreated(EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");
        GLES20.glClearColor(0f, 0f, 0f, 0f); // Dark background so text shows up well.

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);


        ByteBuffer bbVertices = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_COORDS.length * 4);
        bbVertices.order(ByteOrder.nativeOrder());
        cubeVertices = bbVertices.asFloatBuffer();
        cubeVertices.put(WorldLayoutData.CUBE_COORDS);
        cubeVertices.position(0);

        ByteBuffer bbColors = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_COLORS.length * 4);
        bbColors.order(ByteOrder.nativeOrder());
        cubeColors = bbColors.asFloatBuffer();
        cubeColors.put(WorldLayoutData.CUBE_COLORS);
        cubeColors.position(0);

        ByteBuffer bbFoundColors = ByteBuffer.allocateDirect(
                WorldLayoutData.CUBE_FOUND_COLORS.length * 4);
        bbFoundColors.order(ByteOrder.nativeOrder());
        cubeFoundColors = bbFoundColors.asFloatBuffer();
        cubeFoundColors.put(WorldLayoutData.CUBE_FOUND_COLORS);
        cubeFoundColors.position(0);

        ByteBuffer bbNormals = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_NORMALS.length * 4);
        bbNormals.order(ByteOrder.nativeOrder());
        cubeNormals = bbNormals.asFloatBuffer();
        cubeNormals.put(WorldLayoutData.CUBE_NORMALS);
        cubeNormals.position(0);

        // make a floor
        ByteBuffer bbFloorVertices = ByteBuffer.allocateDirect(WorldLayoutData.FLOOR_COORDS.length * 4);
        bbFloorVertices.order(ByteOrder.nativeOrder());
        floorVertices = bbFloorVertices.asFloatBuffer();
        floorVertices.put(WorldLayoutData.FLOOR_COORDS);
        floorVertices.position(0);

        ByteBuffer bbFloorNormals = ByteBuffer.allocateDirect(WorldLayoutData.FLOOR_NORMALS.length * 4);
        bbFloorNormals.order(ByteOrder.nativeOrder());
        floorNormals = bbFloorNormals.asFloatBuffer();
        floorNormals.put(WorldLayoutData.FLOOR_NORMALS);
        floorNormals.position(0);

        ByteBuffer bbFloorColors = ByteBuffer.allocateDirect(WorldLayoutData.FLOOR_COLORS.length * 4);
        bbFloorColors.order(ByteOrder.nativeOrder());
        floorColors = bbFloorColors.asFloatBuffer();
        floorColors.put(WorldLayoutData.FLOOR_COLORS);
        floorColors.position(0);


        ByteBuffer bbEnvVertices = ByteBuffer.allocateDirect(WorldLayoutData.ENV_COORDS.length * 4);
        bbEnvVertices.order(ByteOrder.nativeOrder());
        envVertices = bbEnvVertices.asFloatBuffer();
        envVertices.put(WorldLayoutData.ENV_COORDS);
        envVertices.position(0);

        ByteBuffer bbEnvTex = ByteBuffer.allocateDirect(WorldLayoutData.ENV_TEX_COORDS.length * 4);
        bbEnvTex.order(ByteOrder.nativeOrder());
        evnTex = bbEnvTex.asFloatBuffer();
        evnTex.put(WorldLayoutData.ENV_TEX_COORDS);
        evnTex.position(0);

        int vertexShader = ShaderManager.loadGLShader(this, GLES20.GL_VERTEX_SHADER, R.raw.light_vertex);
        int gridShader = ShaderManager.loadGLShader(this, GLES20.GL_FRAGMENT_SHADER, R.raw.grid_fragment);
        int passthroughShader = ShaderManager.loadGLShader(this, GLES20.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment);

        cubeProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(cubeProgram, vertexShader);
        GLES20.glAttachShader(cubeProgram, passthroughShader);
        GLES20.glLinkProgram(cubeProgram);
        GLES20.glUseProgram(cubeProgram);

        checkGlError("Cube program");

        cubePositionParam = GLES20.glGetAttribLocation(cubeProgram, "a_Position");
        cubeNormalParam = GLES20.glGetAttribLocation(cubeProgram, "a_Normal");
        cubeColorParam = GLES20.glGetAttribLocation(cubeProgram, "a_Color");

        cubeModelParam = GLES20.glGetUniformLocation(cubeProgram, "u_Model");
        cubeModelViewParam = GLES20.glGetUniformLocation(cubeProgram, "u_MVMatrix");
        cubeModelViewProjectionParam = GLES20.glGetUniformLocation(cubeProgram, "u_MVP");
        cubeLightPosParam = GLES20.glGetUniformLocation(cubeProgram, "u_LightPos");

        GLES20.glEnableVertexAttribArray(cubePositionParam);
        GLES20.glEnableVertexAttribArray(cubeNormalParam);
        GLES20.glEnableVertexAttribArray(cubeColorParam);

        checkGlError("Cube program params");

        floorProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(floorProgram, vertexShader);
        GLES20.glAttachShader(floorProgram, gridShader);
        GLES20.glLinkProgram(floorProgram);
        GLES20.glUseProgram(floorProgram);

        checkGlError("Floor program");

        floorModelParam = GLES20.glGetUniformLocation(floorProgram, "u_Model");
        floorModelViewParam = GLES20.glGetUniformLocation(floorProgram, "u_MVMatrix");
        floorModelViewProjectionParam = GLES20.glGetUniformLocation(floorProgram, "u_MVP");
        floorLightPosParam = GLES20.glGetUniformLocation(floorProgram, "u_LightPos");

        floorPositionParam = GLES20.glGetAttribLocation(floorProgram, "a_Position");
        floorNormalParam = GLES20.glGetAttribLocation(floorProgram, "a_Normal");
        floorColorParam = GLES20.glGetAttribLocation(floorProgram, "a_Color");

        GLES20.glEnableVertexAttribArray(floorPositionParam);
        GLES20.glEnableVertexAttribArray(floorNormalParam);
        GLES20.glEnableVertexAttribArray(floorColorParam);

        checkGlError("Floor program params");

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

//        viewObjVertexShader = ShaderManager.loadGLShader(this, GLES20.GL_VERTEX_SHADER, R.raw.texture_vertex);
//        viewObjFragmentShader = ShaderManager.loadGLShader(this, GLES20.GL_FRAGMENT_SHADER, R.raw.texture_fragment);

        avatarBorder = TextureLoader.load(this, R.drawable.avatar_border_male);
        avatar = TextureLoader.load(this, R.drawable.ic_launcher);

        textureShaderProgram = new TextureShaderProgram(this);

        mViewObj = new Spirit();
        mViewObj.putVertexData(ModelDataManager.getSpiritVertexData());
        mViewObj.putTextureData(TextureLoader.load(this, R.drawable.avatar_border_male), TextureDataManager.getAvatarBorderData());
        mViewObj.moveTo(0, 0, -12);
//
        mViewObj2 = new Spirit();
        mViewObj2.putVertexData(ModelDataManager.getSpiritVertexData());
        mViewObj2.putTextureData(TextureLoader.load(this, R.drawable.ic_launcher), TextureDataManager.getAvatarBorderData());
        mViewObj2.moveTo(0, 0, -12.01f);

        mAvatar = new Avatar();
        mAvatar.bind(this);
        mAvatar.moveTo(2, 0, -10.1f);

        mStatus = new Status();
        mStatus.bind(this);
        mStatus.moveTo(3, 1, -9);

        mMarker = new Marker();
        mMarker.bind(this);
        mMarker.moveTo(2, 1, -10);

        mFovBg.bind();
        mFovBg.setOnFrameAvailableListener(this);
        mFovBg.start(cardboardView.getScreenParams());

        GLES20.glDepthMask(true);
        // Object first appears directly in front of user.
//        Matrix.setIdentityM(modelCube, 0);
//        Matrix.translateM(modelCube, 0, 0, 0, -objectDistance);
//
//        Matrix.setIdentityM(modelFloor, 0);
//        Matrix.translateM(modelFloor, 0, 0, -floorDepth, 0); // Floor appears below user.

    }

    /**
     * Prepares OpenGL ES before we draw a frame.
     *
     * @param headTransform The head transformation in the new frame.
     */
    @Override
    public void onNewFrame(HeadTransform headTransform) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        // Build the Model part of the ModelView matrix.
        Matrix.rotateM(modelCube, 0, TIME_DELTA, 0.5f, 0.5f, 1.0f);

        // Build the camera matrix and apply it to the ModelView.
        Matrix.setLookAtM(camera, 0, 0.0f, 0.0f, CAMERA_Z, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);


        headTransform.getHeadView(headView, 0);

//        mAvatar.checkSelf();

        GlHelper.checkGlError("onReadyToDraw");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Draws a frame for an eye.
     *
     * @param eye The eye to render. Includes all required transformations.
     */


    @Override
    public void onDrawEye(Eye eye) {


        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        checkGlError("colorParam");

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        // Build the ModelView and ModelViewProjection matrices
        // for calculating cube position and light.
        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);

        mFovBg.draw(perspective);


        textureShaderProgram.useProgram();
//        textureShaderProgram.setTexture(avatar);
//        mViewObj2.draw(perspective, view, textureShaderProgram);

//        textureShaderProgram.setTexture(avatarBorder);

//        mViewObj.
//        mViewObj.draw(perspective, view, textureShaderProgram);

//        mAvatar.draw(perspective, view, textureShaderProgram);
//
//        mStatus.draw(perspective, view, textureShaderProgram);

        mMarker.draw(perspective, view, textureShaderProgram);

        updateCurrentData();
    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }


    /**
     * Draw the cube.
     * <p/>
     * <p>We've set all of our transformation matrices. Now we simply pass them into the shader.
     */
    public void drawCube() {


        GLES20.glUseProgram(cubeProgram);

        GlHelper.checkGlError("Texture obj on draw");

        GLES20.glUniform3fv(cubeLightPosParam, 1, lightPosInEyeSpace, 0);

        // Set the Model in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(cubeModelParam, 1, false, modelCube, 0);

        // Set the ModelView in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(cubeModelViewParam, 1, false, modelView, 0);

        // Set the position of the cube
        GLES20.glVertexAttribPointer(cubePositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, 0, cubeVertices);

//        Log.e(TAG, "cubePositionParam:" + cubePositionParam);

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(cubeModelViewProjectionParam, 1, false, modelViewProjection, 0);

        // Set the normal positions of the cube, again for shading
        GLES20.glVertexAttribPointer(cubeNormalParam, 3, GLES20.GL_FLOAT, false, 0, cubeNormals);
        GLES20.glVertexAttribPointer(cubeColorParam, 4, GLES20.GL_FLOAT, false, 0,
                isLookingAtObject() ? cubeFoundColors : cubeColors);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
        checkGlError("Drawing cube");

    }

    /**
     * Draw the floor.
     * <p/>
     * <p>This feeds in data for the floor into the shader. Note that this doesn't feed in data about
     * position of the light, so if we rewrite our code to draw the floor first, the lighting might
     * look strange.
     */
    public void drawFloor() {
        GLES20.glUseProgram(floorProgram);

        // Set ModelView, MVP, position, normals, and color.
        GLES20.glUniform3fv(floorLightPosParam, 1, lightPosInEyeSpace, 0);
        GLES20.glUniformMatrix4fv(floorModelParam, 1, false, modelFloor, 0);
        GLES20.glUniformMatrix4fv(floorModelViewParam, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(floorModelViewProjectionParam, 1, false,
                modelViewProjection, 0);
        GLES20.glVertexAttribPointer(floorPositionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
                false, 0, floorVertices);
        GLES20.glVertexAttribPointer(floorColorParam, 4, GLES20.GL_FLOAT, false, 0, floorColors);
//
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        checkGlError("drawing floor");
    }

    /**
     * Called when the Cardboard trigger is pulled.
     */
    @Override
    public void onCardboardTrigger() {
        Log.i(TAG, "onCardboardTrigger");

        if (isLookingAtObject()) {
            score++;
            overlayView.show3DToast("Found it! Look around for another one.\nScore = " + score);
            hideObject();
        } else {
            overlayView.show3DToast("Look around to find the object!");
        }

        // Always give user feedback.
        vibrator.vibrate(50);
    }

    /**
     * Find a new random position for the object.
     * <p/>
     * <p>We'll rotate it around the Y-axis so it's out of sight, and then up or down by a little bit.
     */
    private void hideObject() {
        float[] rotationMatrix = new float[16];
        float[] posVec = new float[4];

        // First rotate in XZ plane, between 90 and 270 deg away, and scale so that we vary
        // the object's distance from the user.
        float angleXZ = (float) Math.random() * 180 + 90;
        Matrix.setRotateM(rotationMatrix, 0, angleXZ, 0f, 1f, 0f);
        float oldObjectDistance = objectDistance;
        objectDistance = (float) Math.random() * 15 + 5;
        float objectScalingFactor = objectDistance / oldObjectDistance;
        Matrix.scaleM(rotationMatrix, 0, objectScalingFactor, objectScalingFactor,
                objectScalingFactor);
        Matrix.multiplyMV(posVec, 0, rotationMatrix, 0, modelCube, 12);

        // Now get the up or down angle, between -20 and 20 degrees.
        float angleY = (float) Math.random() * 80 - 40; // Angle in Y plane, between -40 and 40.
        angleY = (float) Math.toRadians(angleY);
        float newY = (float) Math.tan(angleY) * objectDistance;

        Matrix.setIdentityM(modelCube, 0);
        Matrix.translateM(modelCube, 0, posVec[0], newY, posVec[2]);
    }

    /**
     * Check if user is looking at object by calculating where the object is in eye-space.
     *
     * @return true if the user is looking at the object.
     */
    private boolean isLookingAtObject() {
        float[] initVec = {0, 0, 0, 1.0f};
        float[] objPositionVec = new float[4];

        // Convert object space to camera space. Use the headView from onNewFrame.
        Matrix.multiplyMM(modelView, 0, headView, 0, modelCube, 0);
        Matrix.multiplyMV(objPositionVec, 0, modelView, 0, initVec, 0);

        float pitch = (float) Math.atan2(objPositionVec[1], -objPositionVec[2]);
        float yaw = (float) Math.atan2(objPositionVec[0], -objPositionVec[2]);

        return Math.abs(pitch) < PITCH_LIMIT && Math.abs(yaw) < YAW_LIMIT;
    }

    private void updateCurrentData() {

        float[] initVec = {0, 0, 0, 1.0f};
        float[] objPositionVec = new float[4];

        // Convert object space to camera space. Use the headView from onNewFrame.
        Matrix.multiplyMM(modelView, 0, headView, 0, modelCube, 0);
        Matrix.multiplyMV(objPositionVec, 0, modelView, 0, initVec, 0);

        final float pitch = (float) Math.atan2(objPositionVec[1], -objPositionVec[2]);
        final float yaw = (float) Math.atan2(objPositionVec[0], -objPositionVec[2]);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currText.setText("Pitch:" + pitch + ",Yaw:" + yaw);
            }
        });

    }


    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.cardboardView.requestRender();
        mFovBg.updateTexImage();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.load_texture_btn:
                cardboardView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mMarker.setAvatarTexture(TextureLoader.load(MainActivity.this, R.drawable.ic_launcher));
                    }
                });

                break;
            case R.id.change_texture_btn:
                cardboardView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mMarker.setAvatarTexture(TextureLoader.load(MainActivity.this, R.drawable.status_border));
                    }
                });
                break;
        }
    }
}
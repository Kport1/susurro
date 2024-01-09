/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kport.susurro;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import com.kport.susurro.ui.scene.DefaultScene;
import com.kport.susurro.ui.scene.Scene;

import java.util.ArrayList;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Scene activeScene = null;
    private ArrayList<Scene> scenes = new ArrayList<>();

    private int width, height;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.2f, 1.0f);

        scenes.add(new DefaultScene());
        switchScene("default");
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glViewport(0, (height - width) / 2, width, width);
        activeScene.render();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean onTouchEvent(MotionEvent e){
        float xnd = 2f * e.getX() / width - 1f;
        float ynd = (2f * e.getY() / height - 1f) * -((float) height / width);

        e.setLocation(xnd, ynd);

        return activeScene.onTouchEvent(e);
    }

    public void switchScene(String id){
        for (Scene scene : scenes) {
            if(scene.id().equals(id)){
                activeScene = scene;
                return;
            }
        }
        Log.e("RENDERER", "Couldn't find scene id: \"" + id + "\"");
    }

}
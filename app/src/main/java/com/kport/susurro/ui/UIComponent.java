package com.kport.susurro.ui;

import android.util.Pair;
import android.view.MotionEvent;

import com.kport.susurro.ui.scene.SceneObject;

public interface UIComponent extends SceneObject {
    Pair<Float, Float> getSize();
    Pair<Float, Float> getCorner();
    boolean onTouchEvent(MotionEvent event);

}

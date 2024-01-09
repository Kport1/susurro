package com.kport.susurro.ui.scene;

import android.view.MotionEvent;

public interface Scene {
    String id();
    void render();
    boolean onTouchEvent(MotionEvent e);
}

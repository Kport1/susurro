package com.kport.susurro.ui.scene;

import android.view.MotionEvent;

import com.kport.susurro.ui.Button;
import com.kport.susurro.ui.UIComponent;

import java.util.ArrayList;

public class DefaultScene implements Scene {
    final private ArrayList<SceneObject> objects = new ArrayList<>();

    public DefaultScene(){
        objects.add(new Button(-1.0f, -1.0f, 0.2f, 0.2f, null));
        objects.add(new Button(-0.1f, -0.1f, 0.2f, 0.2f, null));
        objects.add(new Button(0.8f, 0.8f, 0.2f, 0.2f, null));
    }

    @Override
    public String id() {
        return "default";
    }

    @Override
    public void render() {
        for (SceneObject object : objects) {
            object.render();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean handled = false;
        for(SceneObject obj : objects){
            if(obj instanceof UIComponent uiComponent){
                float dx = e.getX() - uiComponent.getCorner().first;
                float dy = e.getY() - uiComponent.getCorner().second;
                if( dx > 0 && dx < uiComponent.getSize().first &&
                        dy > 0 && dy < uiComponent.getSize().second)
                {
                    handled |= uiComponent.onTouchEvent(e);
                }
            }
        }

        return handled;
    }
}

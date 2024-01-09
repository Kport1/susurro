package com.kport.susurro.ui;

import android.opengl.GLES20;
import android.os.SystemClock;
import android.util.Pair;
import android.view.MotionEvent;

import com.kport.susurro.Shader;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.function.Consumer;

public class Button implements UIComponent{

    private static final float animationSpeed = 1f;
    private static Shader shader = null;

    private final float x, y, sx, sy;
    private long timeLastClick = SystemClock.elapsedRealtimeNanos() - 5_000_000_000L;
    private final Consumer<State> callback;
    private final int[] vbo = new int[1];

    public Button(float x, float y, float sx, float sy, Consumer<State> callback){
        this.x = x;
        this.y = y;
        this.sx = sx;
        this.sy = sy;
        this.callback = callback == null? state -> {} : callback;

        float[] vertices = new float[]{
                x, y,
                x + sx, y,
                x + sx, y + sy,
                x, y,
                x + sx, y + sy,
                x, y + sy
        };

        GLES20.glGenBuffers(1, vbo, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices.length * 4, FloatBuffer.wrap(vertices), GLES20.GL_STATIC_DRAW);

        if(shader == null) {
            shader = new Shader(
                    """
                            #version 300 es
                                                    
                            in vec2 attrPos;
                                    
                            void main(){
                                gl_Position = vec4(attrPos, 0.0, 1.0);
                            }
                            """,
                    """
                            #version 300 es
                                                    
                            uniform mediump vec3 col;
                                                    
                            out mediump vec4 outCol;
                                                            
                            void main(){
                                outCol = vec4(col, 1.0);
                            }
                            """);
        }
    }

    @Override
    public Pair<Float, Float> getSize() {
        return Pair.create(sx, sy);
    }

    @Override
    public Pair<Float, Float> getCorner() {
        return Pair.create(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float dx = event.getX() - x;
        float dy = event.getY() - y;

        if( dx > 0 && dx < sx &&
            dy > 0 && dy < sy)
        {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN -> {
                    timeLastClick = SystemClock.elapsedRealtimeNanos();
                    System.out.println("click");
                    callback.accept(State.PRESS);
                }
                case MotionEvent.ACTION_UP -> {
                    callback.accept(State.RELEASE);
                }
                default -> {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void render() {
        shader.use();
        shader.updateUniforms();

        float clickDecayAnim = (float)Math.pow(0.9, (SystemClock.elapsedRealtimeNanos() - timeLastClick) / 1_000_000_000f * 100.0 * animationSpeed);
        shader.setUniform3f("col", clickDecayAnim, clickDecayAnim, clickDecayAnim);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 8, 0);
        GLES20.glEnableVertexAttribArray(0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
    }

    public enum State {
        PRESS,
        RELEASE,
        HOLD
    }
}

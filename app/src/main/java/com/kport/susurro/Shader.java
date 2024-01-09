package com.kport.susurro;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.os.SystemClock;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Shader {
    public int program;

    private final Map<String, Integer> uniforms = new HashMap<>();

    public Shader(String vsh, String fsh){
        int[] status = new int[1];

        program = GLES20.glCreateProgram();


        int vsh_ = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vsh_, vsh);
        GLES20.glCompileShader(vsh_);

        GLES20.glGetShaderiv(vsh_, GLES20.GL_COMPILE_STATUS, status, 0);
        if(status[0] == 0)
            Log.e("SHADER", GLES20.glGetShaderInfoLog(vsh_));

        GLES20.glAttachShader(program, vsh_);


        int fsh_ = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fsh_, fsh);
        GLES20.glCompileShader(fsh_);

        GLES20.glGetShaderiv(fsh_, GLES20.GL_COMPILE_STATUS, status, 0);
        if(status[0] == 0)
            Log.e("SHADER", GLES20.glGetShaderInfoLog(fsh_));

        GLES20.glAttachShader(program, fsh_);


        GLES20.glLinkProgram(program);

        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if(status[0] == 0)
            Log.e("SHADER", GLES20.glGetProgramInfoLog(program));

        GLES20.glDeleteShader(vsh_);
        GLES20.glDeleteShader(fsh_);
    }

    public void use(){
        GLES20.glUseProgram(program);
    }

    public void updateUniforms(){
        setUniform1f("sus_Time", (SystemClock.elapsedRealtimeNanos() / 1_000_000_000f));
        setUniform2f("sus_ScreenSize",
                Resources.getSystem().getDisplayMetrics().widthPixels,
                Resources.getSystem().getDisplayMetrics().heightPixels
        );
    }

    private int getUniformLocationCached(String uniform){
        return uniforms.computeIfAbsent(uniform, u -> {
            int loc = GLES20.glGetUniformLocation(program, u);
            if(loc == -1)
                Log.e("SHADER", "Unknown uniform: \"" + u + "\"");
            return loc;
        });
    }

    public void setUniform1f(String uniform, float val){
        int prevProg = getActiveProgram();
        use();
        GLES20.glUniform1f(getUniformLocationCached(uniform), val);
        GLES20.glUseProgram(prevProg);
    }

    public void setUniform2f(String uniform, float val1, float val2){
        int prevProg = getActiveProgram();
        use();
        GLES20.glUniform2f(getUniformLocationCached(uniform), val1, val2);
        GLES20.glUseProgram(prevProg);
    }

    public void setUniform3f(String uniform, float val1, float val2, float val3){
        int prevProg = getActiveProgram();
        use();
        GLES20.glUniform3f(getUniformLocationCached(uniform), val1, val2, val3);
        GLES20.glUseProgram(prevProg);
    }

    public float getUniform1f(String uniform){
        float[] f = new float[1];
        GLES20.glGetUniformfv(program, getUniformLocationCached(uniform), f, 0);
        return f[0];
    }


    @Override
    protected void finalize(){
        GLES20.glDeleteProgram(program);
    }

    public static int getActiveProgram(){
        int[] i = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_CURRENT_PROGRAM, i, 0);
        return i[0];
    }
}

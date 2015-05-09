package com.example.denis.mapnav;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;

/**
 * Created by denis on 08.04.15.
 */


public class Triangle {
    private Paint mPaint;
    private Path mPath;
    private Region triangle=null;
    private float sizeTriangle=30;
    private double part=0;
    private float partFloat=0;

    public Triangle(float firstX, float firstY) {   //firstX и firstY- значения, в которые делается мув ту в pathView
         part = Math.sqrt(sizeTriangle*sizeTriangle-(sizeTriangle/2)*(sizeTriangle/2));
         partFloat=(float)part;
        mPath = new Path();
    }

    public Path drawTriangle(float X, float Y){  //новые координаты
        mPath.moveTo(X-sizeTriangle/2,Y+partFloat); //подумать над этим
        mPath.lineTo(X,Y-2*sizeTriangle);
        mPath.lineTo(X+sizeTriangle/2, Y+partFloat);
        mPath.lineTo(X-sizeTriangle/2,Y+partFloat);
        return mPath;
    }

}

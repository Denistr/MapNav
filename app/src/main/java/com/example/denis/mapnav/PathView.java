package com.example.denis.mapnav;
/**
 * Created by denis on 24.03.15.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.nfc.TagLostException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.Collections;

public class PathView extends View {

    private Paint mPaint;
    private Path mPath;
    private float x, y;
    private MapsActivity myActivity = null;
    private float sizeX;
    private float sizeY;
    private float sizeRoomX = (float)0.03;
    private float sizeRoomY = (float)0.05;
    private float XS, YS=0;


    public PathView(Context context) {
        super(context);
         myActivity = (MapsActivity) getContext();
        // TODO Auto-generated constructor stub
        init();
    }

    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
         myActivity = (MapsActivity) getContext();
        init();
    }

    public PathView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        myActivity = (MapsActivity) getContext();
        init();
    }

    private void init() {
     mPaint = new Paint();
     mPaint.setStyle(Paint.Style.STROKE);
     mPath = new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        XS=myActivity.outSize.centerX();
        YS=myActivity.outSize.centerY();

        if(myActivity==null) return;

        sizeX=myActivity.outSize.width()/sizeRoomX;
        sizeY=myActivity.outSize.height()/sizeRoomY;


        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(3);


        if (myActivity.dataAcelGyroList.size()==0) {
            mPath.reset();
            mPath.moveTo(XS, YS);
        }else {
            AccData ad=myActivity.dataAcelGyroList.get(myActivity.dataAcelGyroList.size()-1);
            if(ad==null) return;
                try {
                    mPath.lineTo((ad.getsX() * sizeX) + XS, (ad.getsY() * sizeY) + YS);
                } catch (Exception e){
                    int z=0;
                }
            }
        try {
            canvas.drawPath(mPath, mPaint);
        } catch (Exception e){
            int z=0;
        }

    }

}
package com.example.denis.mapnav;
/**
 * Created by denis on 24.03.15.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.nfc.TagLostException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.Collections;

public class PathView extends View {

    private Paint mPaint, paintTri=null;
    private Path mPath, pathTri=null;
    private float x, y;
    private MapsActivity myActivity = null;
    private float sizeX;
    private float sizeY;
    private float sizeRoomX = (float)0.03;
    private float sizeRoomY = (float)0.05;
    private float XS, YS=0;
    private Triangle triangle=null;
    private Matrix matrix=null;
    private boolean pathTriOk=false;

    public PathView(Context context) {
        super(context);
        myActivity = (MapsActivity) getContext();
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
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(3);

        paintTri = new Paint();
        paintTri.setStyle(Paint.Style.STROKE);
        paintTri.setAntiAlias(true);
        paintTri.setColor(Color.GREEN);
        paintTri.setStrokeWidth(3);
    mPath = new Path();
    pathTri=new Path();
    matrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        if(myActivity==null) return;

        if (myActivity.dataAcelGyroList.size()==0) {
            mPath.reset();
            matrix.reset();
            XS=myActivity.outSize.centerX();
            YS=myActivity.outSize.centerY();
            sizeX=myActivity.outSize.width()/sizeRoomX;
            sizeY=myActivity.outSize.height()/sizeRoomY;
            mPath.moveTo(XS, YS);

            triangle=new Triangle(XS, YS);
        }else {
            AccData ad=myActivity.dataAcelGyroList.get(myActivity.dataAcelGyroList.size()-1);
            if(ad==null) return;
                try {
                    if (pathTriOk) {
                        pathTri.reset();

                    }
                    mPath.lineTo((ad.getsX() * sizeX) + XS, (ad.getsY() * sizeY) + YS);
                    pathTri = triangle.drawTriangle((ad.getsX() * sizeX) + XS, (ad.getsY() * sizeY) + YS);//проблема, скорее всего, здесь. Возвращается что-то странное
                    matrix.setRotate(ad.getCorner(), (ad.getsX() * sizeX) + XS, (ad.getsY() * sizeY) + YS); //применяем матрицу односильно точки, коорд кот. указаны во 2 и 3 парам.
                    pathTri.transform(matrix);
                    pathTriOk=true;

                } catch (Exception e){
                    int z=0;
                }
            }
        try {
            canvas.drawPath(mPath, mPaint);
            canvas.drawPath(pathTri,paintTri);
        } catch (Exception e){
            int z=0;
        }

    }

}
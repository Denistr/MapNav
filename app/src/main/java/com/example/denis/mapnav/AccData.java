package com.example.denis.mapnav;

/**
 * Created by denis on 02.03.15.
 */

public class AccData  {
    private float corner, sX, sY;


    public AccData()
    {
        corner=0;
        sX=0;
        sY=0;
    }


    public AccData(float a, float x, float y){

        corner=a;
        sX=x;
        sY=y;
    }


    public float getCorner(){
        return corner;
    }
    public float getsX() { return sX; }
    public float getsY() { return sY; }
}

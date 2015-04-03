package com.example.denis.mapnav;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class MapsActivity extends Activity {


    DataTreatment data;

    public Rect outSize;
    Display display=null;
    private View vCanvas;
    ArrayList<AccData> dataAcelGyroList= new ArrayList<>(); //создаем список из 100 эелементов
    Button btn;
    private static final String TAG = "BLUESOCKET";
    private boolean buttonClickStart=false;
    DataTreatment myTask=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        outSize=new Rect();
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (display==null)
                return;
        display.getRectSize(outSize);
        btn = (Button)findViewById(R.id.btnStart);
        btn.setBackgroundColor(android.R.color.transparent);
        vCanvas = this.findViewById(R.id.pathView);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myTask!=null)
            myTask.killApp();

    }

    public void drawPath(AccData ad){
    if (vCanvas==null)
        return;
     dataAcelGyroList.add(ad);
     vCanvas.invalidate();
    }



    public void onClickStart(View view) throws IOException {
        if (!buttonClickStart) {
            btn.setText("Stop");
            buttonClickStart = true;
            try {
                myTask = new DataTreatment();
                myTask.execute(this);
            } catch (Exception e) {
                Log.e(TAG, "fkfk");
            }
        } else {
            //TODO:уничтожаем сокет
            btn.setText("Start");
            if (myTask!=null) {
                myTask.stopDraw();
                dataAcelGyroList.clear();
            }
            buttonClickStart=false;


        }

    }

}

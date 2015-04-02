package com.example.denis.mapnav;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Rect;
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


    public TextView textView;

    private BluetoothAdapter btAdapter=null;
    private static final String TAG = "BLUESOCKET";

    private static String MacAdress = "00:15:83:3D:0A:57";

    private static final boolean D = true;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;

    public ArrayList<AccData> dataAcelGyroList= new ArrayList<>(100); //создаем список из 100 эелементов
    public String outmessage;
    float corner=0;
    private float x, maxX=0;
    private float y, maxY=0;
    AccData myAccData=new AccData();
    public boolean  isDataReady=false;
    private boolean xReady, yReady, cornerReady=false;
    public Rect outSize;
    Display display=null;
    private String lastV="";
    private int lastJ = 0;

    private String residue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //setContentView(new PathView(this));
        outSize=new Rect();
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (display==null)
                return;
       display.getRectSize(outSize);

        textView = (TextView)findViewById(R.id.text1);
        if (D)
            Log.e(TAG, "+++ ON CREATE +++");

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(this,
                    "Bluetooth is not available.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!btAdapter.isEnabled()) {
            Toast.makeText(this,
                    "Please enable your BT and re-run this program.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        BluetoothDevice device = btAdapter.getRemoteDevice(MacAdress);
        btSocket = connectUsingHack(device,1);

    }


    private BluetoothSocket connectUsingHack(BluetoothDevice device,int channel){
        BluetoothSocket socket=null;

        try {
            Method m=device.getClass().getMethod("createRfcommSocket",new Class[] {int.class});
            socket=(BluetoothSocket)m.invoke(device,Integer.valueOf(channel));
            socket.connect();
            return socket;
        }
        catch (  NoSuchMethodException e) {
            Log.e(TAG,e.getMessage());
        }
        catch (  IOException e) {
            Log.e(TAG,e.getMessage());
        }
        catch (  Exception ignore) {
            Log.e(TAG,ignore.getMessage());
        }
        return socket;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {

            String message = "q";
            byte[] msgBuffer = message.getBytes();

            outStream = btSocket.getOutputStream();
            outStream.write(msgBuffer);
            outStream.flush();


            if (inStream != null) {
                inStream.close();
            }
            if (outStream != null) {
                outStream.close();
            }

            if (btSocket!=null){
                btSocket.close();
            }
        }
        catch (  IOException e) {
            Log.e(TAG,e.getMessage());
        }

        if (D)
            Log.e(TAG, "--- ON DESTROY ---");
    }



    private void ParseReadStrEx(String inStr) {

        boolean bready = false;
        String[] promStr = inStr.split("\n");
        try {
            for (int i = 0; i < promStr.length; i++) {
                if (!promStr[i].trim().isEmpty()) {
                    String[] tempStr = promStr[i].split(" ");
                    if (lastJ + tempStr.length<3) {
                       lastJ = lastJ + 1;
                    }
                    for (int j = lastJ; j < lastJ+tempStr.length; j++) {
                        if (j == 0)
                            corner = Float.parseFloat(lastV + tempStr[j-lastJ]);
                        else if (j == 1)
                            x = Float.parseFloat(lastV + tempStr[j-lastJ]);
                        else if (j == 2)
                            y = Float.parseFloat(lastV + tempStr[j-lastJ]);
                        lastV = "";

                        bready = (j == 2);
                    }
                    if (bready) {
                        try {
                            myAccData = new AccData(corner, x, y);
                            dataAcelGyroList.add(myAccData);
                            lastJ = 0;
                        } catch (Exception e) {
                            Log.e(TAG, "add in list");
                        }
                    }

                    if (i == promStr.length - 1) {

                        lastJ = tempStr.length - 1;
                        lastV = tempStr[lastJ];
                    }
                }
            }
        } catch (Exception e)
        {
            Log.e(TAG,"ddd");
        }
    }


    public void sendData()
    {
        // Create a data stream so we can talk to server.

        if (btSocket==null) {return;}

        String inputMessage = "s";
        byte[] msgBuffer = inputMessage.getBytes();
        byte[] msgOutBuffer= new byte[100];

        try {
            outStream = btSocket.getOutputStream();
            outStream.write(msgBuffer);
            outStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }



            for (int j=0;j<100;j++) //сделать while true. по кнопке сбрасывать флажок и выходить из цикла (сделать 1 кнопку старт-стоп, которая меняет название) (перставать принимать данные) лист сделать безразмерным.
            {
                try {
                    inStream = btSocket.getInputStream();
                    int btread = inStream.read(msgOutBuffer);
                    if(btread>0)
                    {
                        outmessage = new String(msgOutBuffer);//Записываем полученную строку в outmessage
                            ParseReadStrEx(outmessage);

                        textView.setText(outmessage);

                    }
                } catch (Exception e)
                {
                    Log.e(TAG, "ON sendData: ololo", e);
                }
                if (inputMessage != "s"){break;}
            }
            isDataReady=true;
            dataAcelGyroList.size(); //для проверки значений

    }



    public void onClickStart(View view) throws IOException {
            sendData();
        try {
            View vCanvas = this.findViewById(R.id.pathView);
            vCanvas.invalidate();
        } catch (Exception e){
            Log.e(TAG,"fsfs");}
    }

    public float getMaxX() {return maxX;}
    public float getMaxY() {return maxY;}
}

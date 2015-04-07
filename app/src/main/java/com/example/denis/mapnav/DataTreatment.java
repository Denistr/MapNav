package com.example.denis.mapnav;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by denis on 02.04.15.
 */
public class DataTreatment extends AsyncTask<Context, AccData, Void> {

    private static final boolean D = true;
    private String lastV="";
    private int lastJ = 0;
    public String outmessage;
    float corner=0;
    private float x, maxX=0;
    private float y, maxY=0;
    private static String MacAdress = "00:15:83:3D:0A:57";
    private BluetoothSocket btSocket = null;
    private BluetoothAdapter btAdapter=null;

    private OutputStream outStream = null;
    private InputStream inStream = null;

    public boolean  isDataReady=false;
    private boolean needBreak = false;

    private static final String TAG = "BLUESOCKET";

    private MapsActivity myActivity=null;
    DrawHelper myDraw;
    public boolean noSocket=true;


    private BluetoothSocket connectUsingHack(BluetoothDevice device,int channel){
        BluetoothSocket socket=null;

        try {
            Method m=device.getClass().getMethod("createRfcommSocket",new Class[] {int.class});
            socket=(BluetoothSocket)m.invoke(device,Integer.valueOf(channel));
            socket.connect();
            noSocket=false;
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



    private class DrawHelper {

        public DrawHelper(Context context) {
            myActivity=(MapsActivity)context;
        }

        public void progressUpdate(AccData... ads) {
            for (AccData ad:ads){
                myActivity.drawPath(ad);
            }

        }
    }
            void killApp () {

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

                    if (btSocket != null) {
                        btSocket.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }

                if (D)
                    Log.e(TAG, "--- ON DESTROY ---");
            }



    private ArrayList<AccData> ParseReadStrEx(String inStr) {

        ArrayList<AccData> tempArr= null;
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
                            if(tempArr==null){tempArr = new ArrayList<AccData>();}
                            tempArr.add(new AccData(corner, x, y));
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
        return tempArr;
    }
    private void sendData()
    {

        isDataReady=true;


    }

    public void stopDraw() throws IOException {

        needBreak=false;
        String inputMessage = "p";
        byte[] msgBuffer = inputMessage.getBytes();

        try {
            outStream = btSocket.getOutputStream();
            outStream.write(msgBuffer);
            outStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //btSocket.close();
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        needBreak=true;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = btAdapter.getRemoteDevice(MacAdress);
        try {
            btSocket = connectUsingHack(device, 1);
        } catch (Exception e)
        {
            Log.e(TAG,"fjfj");
        }
        myDraw = new DrawHelper(myActivity);
        String inputMessage = "s";
        byte[] msgBuffer = inputMessage.getBytes();

        try {
            outStream = btSocket.getOutputStream();
            outStream.write(msgBuffer);
            outStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    protected Void doInBackground(Context... params) {
        myActivity=(MapsActivity)params[0];
        while (true) {
            if (!needBreak) {
                return null;
            }

            if (btSocket == null) {
                return null;
            }
            if (myActivity.dataAcelGyroList.size()>0)
            {
                AccData ad = myActivity.dataAcelGyroList.get(myActivity.dataAcelGyroList.size()-1);
                AccData newad = new AccData(ad.getCorner(),ad.getsX(),ad.getsY());
                myActivity.dataAcelGyroList.clear();
                myActivity.dataAcelGyroList.add(newad);
            }

            byte[] msgOutBuffer = new byte[100];
            try {
                inStream = btSocket.getInputStream();
                int btread = inStream.read(msgOutBuffer);
                if (btread > 0) {
                    outmessage = new String(msgOutBuffer);//Записываем полученную строку в outmessage
                    ArrayList<AccData> arr = ParseReadStrEx(outmessage);
                    if (arr!=null) {
                        for (AccData ad: arr) {
                            publishProgress(ad);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "ON sendData: ololo", e);
            }

        }
    }


    @Override
    protected void onProgressUpdate(AccData...  values) {
        super.onProgressUpdate(values);

       myDraw.progressUpdate(values);
    }
    public BluetoothSocket getBtSocket(){
        return btSocket;
    }
}

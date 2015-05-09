package com.example.denis.mapnav;

import android.os.AsyncTask;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by denis on 09.05.15.
 */

public class FixAngle extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
         OutputStream outStream = null;
         InputStream inStream = null;
        DataTreatment socket=new DataTreatment();
        String inputMessage = "f";
        byte[] msgBuffer = inputMessage.getBytes();

        try {
            outStream = socket.getBtSocket().getOutputStream();
            outStream.write(msgBuffer);
            outStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.example.smart;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class Recording extends Service {
    private MP3Recorder mRecorder;
    public boolean recordStart;
    private File file;
    private static final String MEDIA_PATH = new String(
            Environment.getExternalStorageDirectory() + "/AudioRecorder/");

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        file = new File(MEDIA_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
        Date date = new Date();
        CharSequence c = DateFormat.format("hh-mm-ss",date.getTime());
        mRecorder = new MP3Recorder(file.getAbsolutePath()+"/"+c+"rec.mp3");
         //call
        TelephonyManager telephonyManager =(TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        // 5

        telephonyManager.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                //stop Record
                if (TelephonyManager.CALL_STATE_IDLE == state && mRecorder == null){
                    mRecorder.stop();
                    recordStart = false;
                    stopSelf();

                }
                //start Record
                else if (TelephonyManager.CALL_STATE_OFFHOOK == state){
                    try {
                        mRecorder.start();
                        recordStart = true;
                        Toast.makeText(Recording.this, "start Recording", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        },PhoneStateListener.LISTEN_CALL_STATE);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mRecorder.stop();
    }
}


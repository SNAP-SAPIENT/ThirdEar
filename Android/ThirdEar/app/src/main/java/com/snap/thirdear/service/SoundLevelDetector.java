package com.snap.thirdear.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hrajal on 5/30/2016.
 */
public class SoundLevelDetector extends Service {
    private static final String TAG = SoundLevelDetector.class.getName();
    private MediaRecorder mRecorder;
    Thread recordSound;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        recordSound = new Thread(){
            @Override
            public void run() {
                getSound();
                while (!this.isInterrupted()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    checkNoiseLevel();
                }
            }
        };

        recordSound.start();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        recordSound.interrupt();
    }


    private void getSound(){
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        mRecorder.start();
    }


    private void checkNoiseLevel(){
        double amp = getAmplitude();
        Log.d(TAG, "checkNoiseLevel: amp: " +  amp);
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return   20 * Math.log10(mRecorder.getMaxAmplitude() / 2700.0);
        else
            return 0;

    }

}

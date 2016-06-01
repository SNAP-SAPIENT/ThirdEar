package com.snap.thirdear.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
    import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.snap.thirdear.AlertActivity;
import com.snap.thirdear.R;

import java.io.File;
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
    File audiofile = null;

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
        destroyThread();
    }

    private void destroyThread() {
//        recordSound.notify();
        recordSound.interrupt();
    }


    private void getSound(){
        File dir = Environment.getExternalStorageDirectory();
        try {
            audiofile = File.createTempFile("sound", ".3gp", dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(audiofile.getAbsolutePath());
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
    }


    private void checkNoiseLevel(){
        double amp = getAmplitude();
        showAlertScreen(amp);
        Log.d(TAG, "checkNoiseLevel: amp: " +  amp);
    }

    private void showAlertScreen(double amp) {
        Double limit = new Double(15);
        Double ampDouble = new Double(amp);
        if(ampDouble.isInfinite() || ampDouble.isNaN()){
            Log.d(TAG, "showAlertScreen: not valid values");
        }else if(ampDouble.compareTo(limit) >= 0 ){
            destroyThread();
            Intent intent = new Intent(getApplicationContext(), AlertActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(getString(R.string.intent_group), "NoiseLevel");
            intent.putExtra(getString(R.string.intent_img), "alert_emergency");
            String ampString = Double.toString(amp);
            Log.d(TAG, "showAlertScreen: ampString: " + ampString);
            if (ampString.contains(".")) {
                String ampSplit = ampString.substring(0,ampString.indexOf("."));
                Log.d(TAG, "showAlertScreen: ampSplit: " + ampSplit);
                intent.putExtra(getString(R.string.intent_amp), "Sound level : " + ampSplit);
            }else{
                intent.putExtra(getString(R.string.intent_amp), ampString);
            }
            intent.putExtra(getString(R.string.alert_for_keywords), getString(R.string.alert_for_noiseLevel));
            startActivity(intent);
        }
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return   20 * Math.log10(mRecorder.getMaxAmplitude() / 2700.0);
        else
            return 0;

    }

}

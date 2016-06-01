package com.snap.thirdear.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.snap.thirdear.AlertActivity;
import com.snap.thirdear.R;

import java.io.File;
import java.io.IOException;


public class SoundLevelDetector extends Service {
    private static final String TAG = SoundLevelDetector.class.getName();
    private MediaRecorder mRecorder;
    Thread recordSound;
    File audioFile = null;
    private SharedPreferences sharedPref;
    private boolean isBreak;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        recordSound = new Thread(){
            @Override
            public void run() {
                String filePath = getSound();
                while (!this.interrupted()) {
                    try {
                        this.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    checkNoiseLevel(filePath);
                    if(isBreak()){
                        break;
                    }
                }
            }
        };

        recordSound.start();

    }


    public synchronized boolean isBreak() {
        return isBreak;
    }

    public synchronized void setBreak(boolean aBreak) {
        isBreak = aBreak;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyThread();
    }

    private void destroyThread() {
        recordSound.interrupt();
        setBreak(Boolean.TRUE);
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }


    private String getSound(){
        File dir =  new File(Environment.getExternalStorageDirectory() + "/com.snap.thirdear");
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            audioFile = File.createTempFile("sound", ".3gp", dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(audioFile.getAbsolutePath());
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
        Log.d(TAG, "getSound: path: " + audioFile.getAbsolutePath());
        return audioFile.getAbsolutePath();
    }


    private void checkNoiseLevel(String filePath){
        double amp = getAmplitude();
        showAlertScreen(amp, filePath);
        Log.d(TAG, "checkNoiseLevel: amp: " +  amp);
    }

    private void showAlertScreen(double amp, String filePath) {
        String defaultLimit = sharedPref.getString("pref_monitor_default", "40.0");
        String limitString = sharedPref.getString("pref_noiseLevel", defaultLimit);
        Double limit = new Double(limitString);

        Double ampDouble = new Double(amp);
        if(ampDouble.isInfinite() || ampDouble.isNaN()){
            Log.d(TAG, "showAlertScreen: not valid values");
        }else if(ampDouble.compareTo(limit) >= 0 ){
            destroyThread();
            Intent intent = new Intent(getApplicationContext(), AlertActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(getString(R.string.intent_group), "NoiseLevel");
            intent.putExtra(getString(R.string.intent_img), "alert_emergency");
            intent.putExtra(getString(R.string.sound_filePath), filePath);
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
        double amp = 0;
        if (mRecorder != null) {
            amp = 20 * Math.log10(mRecorder.getMaxAmplitude() / 2700.0);
            Log.d(TAG, "getAmplitude: before abd: " + amp);
            amp = Math.abs(amp);
            Log.d(TAG, "getAmplitude: after abd: " + amp);
        }
        return amp;

    }

}

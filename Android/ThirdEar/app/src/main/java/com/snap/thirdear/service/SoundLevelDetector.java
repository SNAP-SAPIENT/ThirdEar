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
    String defaultAndroidProfile;
    public static int onOff = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        defaultAndroidProfile = getString(R.string.pref_selecProfile_default);
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
        SoundLevelDetector.onOff = 2;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SoundLevelDetector.onOff = 1;
        destroyThread();
    }

    private void destroyThread() {
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
        int amp = getAmplitude();
        showAlertScreen(amp, filePath);
        Log.d(TAG, "checkNoiseLevel: amp: " +  amp);
    }

    private void showAlertScreen(int amp, String filePath) {
        Log.d(TAG, "showAlertScreen: amp: " + amp);
        String defaultLimit = sharedPref.getString("pref_noiseLevel_default", "40000");
        Log.d(TAG, "showAlertScreen: defaultLimit: " + defaultLimit);
        String selectedLimit = sharedPref.getString("pref_noiseLevel", defaultLimit);
        int limit = Integer.parseInt(selectedLimit);
        Log.d(TAG, "showAlertScreen: limit: " + limit);
        String voiceProfile = sharedPref.getString("pref_selectProfile", defaultAndroidProfile);
        if(amp >= limit){
            destroyThread();
            Intent intent = new Intent(getApplicationContext(), AlertActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(getString(R.string.intent_group), "Noise Level");
            intent.putExtra(getString(R.string.intent_img), "alert_emergency");
            intent.putExtra(getString(R.string.sound_filePath), filePath);
            intent.putExtra(getString(R.string.intent_amp), Integer.toString(amp));
            intent.putExtra(getString(R.string.voice_profile),voiceProfile);
            intent.putExtra(getString(R.string.alert_for_keywords), getString(R.string.alert_for_noiseLevel));
            startActivity(intent);
        }
    }

    public int getAmplitude() {
        int amp = 0;
        if (mRecorder != null) {
            /*amp = 20 * Math.log10(mRecorder.getMaxAmplitude() / 2700.0);
            Log.d(TAG, "getAmplitude: before abd: " + amp);
            amp = Math.abs(amp);*/
            amp = mRecorder.getMaxAmplitude();
            Log.d(TAG, "getAmplitude: after abd: " + amp);
        }
        return amp;

    }

}

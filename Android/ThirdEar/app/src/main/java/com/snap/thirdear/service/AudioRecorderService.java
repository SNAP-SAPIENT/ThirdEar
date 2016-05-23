package com.snap.thirdear.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/**
 * Created by hrajal on 5/17/2016.
 */
public class AudioRecorderService extends Service{


    private MediaRecorder myAudioRecorder;
    private String outputFile = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ThirdEar//recording.3gp";
        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        record();
        return START_STICKY;
    }

    private void record() {
        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IOException e) {
            Log.e("AudioRecorderService","Error while recording: ", e);
        }
    }

    private void stopRecording() {
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder  = null;
    }

    private void play() {
        MediaPlayer m = new MediaPlayer();

        try {
            m.setDataSource(outputFile);
        }

        catch (IOException e) {
            Log.e("AudioRecorderService","Error setting source for playing: ", e);
        }

        try {
            m.prepare();
        }

        catch (IOException e) {
            Log.e("AudioRecorderService","Error while prepare for playing: ", e);
        }

        m.start();
    }

    @Override
    public void onDestroy() {
        stopRecording();
    }
}

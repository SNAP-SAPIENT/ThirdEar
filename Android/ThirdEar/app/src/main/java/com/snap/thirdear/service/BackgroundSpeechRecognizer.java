package com.snap.thirdear.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.snap.thirdear.AlertActivity;
import com.snap.thirdear.R;
import com.snap.thirdear.db.DataBaseHelper;
import com.snap.thirdear.db.Groups;
import com.snap.thirdear.db.Trigger;

import java.util.HashMap;
import java.util.Locale;

// import com.ventrific.speechtest.command.*;

public class BackgroundSpeechRecognizer extends Service implements RecognitionListener, TextToSpeech.OnInitListener {
    public static final String INPUT = "input";
    public static final String RESULT = "result";
    public static final String NOTIFICATION = "com.ventrific.receiver";
    public boolean triggerWordSpoken = false;
    private BluetoothService bluetoothService;
    private DataBaseHelper dataBaseHelper;
    SQLiteDatabase db;

    SpeechRecognizer speechRecognizer;
    AudioManager audioManager;
    Intent speechRecognizerIntent;
    TextToSpeech tts;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        //cleanup
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }

        if (audioManager == null)
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audioManager.setSpeakerphoneOn(true);
        Log.d(getPackageName(), "Current mic vol:" + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(this);
            speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, (new Long(10000)));
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        }

        if (tts == null) {
            tts = new TextToSpeech(this, this);
        }

        if (speechRecognizer != null)
            speechRecognizer.startListening(speechRecognizerIntent);
        bluetoothService = new BluetoothService();
        dataBaseHelper = new DataBaseHelper(this);
        db = dataBaseHelper.getReadableDatabase();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (db != null){
            db.close();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(getClass().getName(), "TTS - Language is not supported");
            } else {
                Log.d(getClass().getName(), "TTS Legit???");
            }
        } else {
            Log.e(getClass().getName(), "Initilization Failed!");
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d(getClass().getName(), "onReadyForSpeech(..) called.");
        //TODO notify activity?
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(getClass().getName(), "onBeginningOfSpeech(..) called.");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(getClass().getName(), "onEndOfSpeech(..) called.");
    }

    @Override
    public void onError(int error) {
        Log.d(getClass().getName(), "onError(..) called (" + error + ")");
        if (error != 5) {
            //Toast.makeText(getApplicationContext(), "Error!", 1000).show();
            restartSpeechRecognizer();
       }
        /*else{
             Handler mHandler = new Handler();
             mHandler.postDelayed(new Runnable() {
                public void run() {
                    restartSpeechRecognizer();
                }
            }, 1000);
        }*/
    }

    private void restartSpeechRecognizer() {
        speechRecognizer.cancel();
        speechRecognizer.stopListening();
        speechRecognizer.startListening(speechRecognizerIntent);
    }

    @Override
    public void onResults(Bundle results) {
        Log.d(getClass().getName(), "onResults(..) called.");
        speechRecognizer.cancel();
        speechRecognizer.stopListening();
        Log.d(getClass().getName(), results.toString());
        java.util.ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

        if (data.size() > 0) {
            Log.d(getClass().getName(), "results: " + String.valueOf(data.size()));
            String command = null;
            String text = "First match was: "  + data.get(0);
            Log.d(getClass().getName(), "TTS: " + text);

            String mostProbableSentence = (String)data.get(0);

            Trigger trigger = dataBaseHelper.getTriggerByText(mostProbableSentence);
            Groups group = null;
            if(null != trigger){
                group = dataBaseHelper.getGroup(trigger.getGroupsId());
                if(null != group){
                    if( 1 == group.getPhoneVibrate())
                        alertVibrate(1);
                    if( 1 == group.getBtReceiver())
                        sendCmdToBluetoohDevices(group.getName());
                    if(1 == group.getPhoneLight())
                        falshPhoneLight(Color.argb(255, 0, 255, 0));
                    speak(group.getAlertText(), map);
                    showAlertScreen(trigger.getMatchingWord(), group);
                }
            }
            //speechRecognizer.startListening(speechRecognizerIntent);

        } else {
            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }

    @NonNull
    private void showAlertScreen(String matchingText, Groups group) {
        Intent intent = new Intent(getApplicationContext(),AlertActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(getString(R.string.intent_trigger),matchingText);
        intent.putExtra(getString(R.string.intent_group),group.getName());
        startActivity(intent);
    }

    private void sendCmdToBluetoohDevices(String command) {
        if(null != BluetoothService.mmOutStream) {
            bluetoothService.write(command, BluetoothService.mmOutStream);
        }
    }

    private void speak(String greeting, HashMap<String, String> map) {
        tts.speak(greeting, TextToSpeech.QUEUE_FLUSH, map);
    }


    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d(getClass().getName(), "onPartialResults(..) called.");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d(getClass().getName(), "onEvent(..) called.");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void alertVibrate(int level) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(level == 1) {
            // Vibrate for 500 milliseconds
            v.vibrate(500);
        }else if(level == 2){
            v.vibrate(1000);
        }
    }
    private void falshPhoneLight(int color) {
        /*NotificationManager nm = ( NotificationManager ) getSystemService( NOTIFICATION_SERVICE );
        Notification notif = new Notification();
        notif.ledARGB = 0xFFff0000;
        notif.flags = Notification.FLAG_SHOW_LIGHTS;
        notif.ledOnMS = 2000;
        notif.ledOffMS = 2000;
        nm.notify(1234, notif);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                NotificationManager nm = ( NotificationManager ) getSystemService( NOTIFICATION_SERVICE );
                nm.cancel(1234);
            }
        }, 5000);*/
    }
}
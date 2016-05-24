package com.snap.thirdear.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
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
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
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
        Log.d(getClass().getName(), results.toString());
        java.util.ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String str = new String();
        if (data.size() > 0) {
            speechRecognizer.stopListening();

            Log.d(getClass().getName(), "results: " + String.valueOf(data.size()));
            String command = null;
            String text = "First match was: "  + data.get(0);
            Log.d(getClass().getName(), "TTS: " + text);

            String word = (String)data.get(0);

            command = checkForKeywords(word);
            String group = command;

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

            if(command != null && command.equals("GREETING")) {
                showAlertScreen(word, group);
                speak("greeting to you too", map);
                alertVibrate(1);
                sendCmdToBluetoohDevices(command);
            }if(command != null && command.equals("FIRE_ALERT")) {
                showAlertScreen(word, group);
                speak("RUN", map);
                alertVibrate(2);
                sendCmdToBluetoohDevices(command);
            }if(command != null && command.equals("OK")){
                showAlertScreen(word, group);
                speak("All is well", map);
                alertVibrate(1);
                sendCmdToBluetoohDevices(command);
            }
            speechRecognizer.startListening(speechRecognizerIntent);

        } else {
            speechRecognizer.stopListening();
            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }

    @NonNull
    private void showAlertScreen(String word, String group) {
        Intent intent = new Intent(getApplicationContext(),AlertActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(getString(R.string.intent_trigger),word);
        intent.putExtra(getString(R.string.intent_group),group);
        startActivity(intent);
    }

    private String checkForKeywords(String word) {
        //Cursor c = db.query(DataBaseHelper.TABLE_ONE_NAME, "");

        String command = null;
        if(word.contains("hello")){
            command = "GREETING";
        }else if(word.contains("fire")){
            command = "FIRE_ALERT";
        }else if(word.contains("okay")){
            command = "OK";
        }
        return command;
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
}
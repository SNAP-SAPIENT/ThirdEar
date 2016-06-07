package com.snap.thirdear;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.snap.thirdear.service.BackgroundSpeechRecognizer;
import com.snap.thirdear.service.SoundLevelDetector;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AlertActivity extends AppCompatActivity {


    private TextView alertText;
    private ImageView alertImg;
    private String TAG = AlertActivity.class.getName();
    private String alertFor;
    private String filePath;
    private ImageButton palyBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        alertText = (TextView) findViewById(R.id.alertMsg);
        alertImg = (ImageView) findViewById(R.id.alert_image);
        palyBackBtn = (ImageButton) findViewById(R.id.alert_palyBack_btn);
        Intent intent = getIntent();
        palyBackBtn.setImageResource(R.drawable.playback_grey);
        String voiceProfile = intent.getStringExtra(getString(R.string.voice_profile));
        alertFor= intent.getStringExtra(getString(R.string.alert_for_keywords));
        if(alertFor.equals(getString(R.string.alert_for_keywords))) {
            String group = intent.getStringExtra(getString(R.string.intent_group));
            String trigger = intent.getStringExtra(getString(R.string.intent_trigger));
            String imageName = intent.getStringExtra(getString(R.string.intent_img));

            alertText.setText(trigger);
            int id = getResources().getIdentifier(imageName, "drawable", this.getPackageName());
            Log.d(TAG, "onCreate: img id" + id);
            alertImg.setImageResource(id);
            Log.d(TAG, "onCreate: Voice profile: " + voiceProfile);
            if (!voiceProfile.equalsIgnoreCase(getString(R.string.pref_selecProfile_default)))
                playSound(voiceProfile, group);
        }else if (alertFor.equals(getString(R.string.alert_for_noiseLevel))){
            String imageName = intent.getStringExtra(getString(R.string.intent_img));
            String amp = intent.getStringExtra(getString(R.string.intent_amp));
            filePath= intent.getStringExtra(getString(R.string.sound_filePath));
            if(null != filePath){
                palyBackBtn.setImageResource(R.drawable.playback);
            }
            alertText.setText("Noise detected...");
            //alertText.setText(amp);
            int id = getResources().getIdentifier(imageName, "drawable", this.getPackageName());
            Log.d(TAG, "onCreate: img id" + id);
            alertImg.setImageResource(id);
            if (!voiceProfile.equalsIgnoreCase(getString(R.string.pref_selecProfile_default)))
                playSound(voiceProfile, "Noise Level");
        }
        hide();
    }

    private void playSound(String voiceProfile, String group) {
        group = group.toLowerCase();
        group = group.replace(" ", "");
        String sound = voiceProfile+ "_" + group;
        Log.d(TAG, "playSound: sound: " + sound);
        int id = getResources().getIdentifier(sound, "raw", this.getPackageName());
        Log.d(TAG, "playSound: sound id: " + id);
        if( id > 0) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, id);
            mediaPlayer.start();
        }
    }


    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void closeAlert(View view){
        if(alertFor.equals(getString(R.string.alert_for_keywords))) {
            stopService(new Intent(getBaseContext(), BackgroundSpeechRecognizer.class));
            startService(new Intent(getBaseContext(), BackgroundSpeechRecognizer.class));
        }else{
            stopService(new Intent(getBaseContext(), SoundLevelDetector.class));
            startService(new Intent(getBaseContext(), SoundLevelDetector.class));
        }
        finish();
    }

    public void playBack(View view){
        palyBackBtn.setImageResource(R.drawable.playback_grey);
        if(null != filePath){
            Log.d(TAG, "playBack: filePath: " + filePath);
            MediaPlayer mediaPlayer = MediaPlayer.create(this, Uri.parse(filePath));
            mediaPlayer.start();
            palyBackBtn.setImageResource(R.drawable.playback);
        }
    }

}

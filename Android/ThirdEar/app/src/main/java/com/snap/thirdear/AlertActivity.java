package com.snap.thirdear;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.snap.thirdear.service.BackgroundSpeechRecognizer;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class AlertActivity extends AppCompatActivity {


    private TextView alertText;
    private ImageView alertImg;
    private String TAG = AlertActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        alertText = (TextView) findViewById(R.id.alertMsg);
        alertImg = (ImageView) findViewById(R.id.alert_image);
        Intent intent = getIntent();
        String group = intent.getStringExtra(getString(R.string.intent_group));
        String trigger = intent.getStringExtra(getString(R.string.intent_trigger));
        String imageName = intent.getStringExtra(getString(R.string.intent_img));
        alertText.setText(trigger);
        int id = getResources().getIdentifier(imageName, "drawable", this.getPackageName());
        Log.d(TAG, "onCreate: img id" + id);
        alertImg.setImageResource(id);
        hide();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void closeAlert(View view){
        stopService(new Intent(getBaseContext(), BackgroundSpeechRecognizer.class));
        startService(new Intent(getBaseContext(), BackgroundSpeechRecognizer.class));
        finish();
    }

}

package com.snap.thirdear;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.snap.thirdear.db.DataBaseHelper;
import com.snap.thirdear.db.Groups;

import java.util.ArrayList;

/**
 * Created by hrajal on 5/25/2016.
 */
public class AlertPreferencesActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private HorizontalScrollView horizontalScrollView;
    private DataBaseHelper dataBaseHelper;
    private ArrayList<Groups> groupsList;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_preferences);
        dataBaseHelper = new DataBaseHelper(this);
        dynamicallyAddControlsForGroups();

    }

    private void dynamicallyAddControlsForGroups() {
        groupsList = dataBaseHelper.getAllGroups();

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.alert_horizontalScrollView);
        linearLayout = (LinearLayout) horizontalScrollView.findViewById(R.id.alert_linear);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int counter = 1;
        for (Groups group : groupsList) {
            View view = layoutInflater.inflate(R.layout.alert_pref_relative, linearLayout, false);

            RelativeLayout relLayout = (RelativeLayout) view.findViewById(R.id.alert_rel);
            TextView groupName = (TextView) view.findViewById(R.id.group_name_text);
            Button Button = (Button) view.findViewById(R.id.select_all_btn);
            Switch phoneSpeaker = (Switch) view.findViewById(R.id.switch_phone_speaker);
            Switch phoneFlashLight = (Switch) view.findViewById(R.id.switch_phone_flash_light);
            Switch phoneVibrate = (Switch) view.findViewById(R.id.switch_phone_vibrate);
            Switch light = (Switch) view.findViewById(R.id.switch_light);
            Switch smartWatch = (Switch) view.findViewById(R.id.switch_watch);
            Switch hearingAid = (Switch) view.findViewById(R.id.switch_hearing_aid);

            groupName.setText(group.getName());
            if (isEnabled(group.getPhoneAudio()))
                phoneSpeaker.setChecked(true);
            if (isEnabled(group.getPhoneLight()))
                phoneFlashLight.setChecked(true);
            if (isEnabled(group.getPhoneVibrate()))
                phoneVibrate.setChecked(true);
            if (isEnabled(group.getLight()))
                light.setChecked(true);
            if (isEnabled(group.getWearableDevice()))
                smartWatch.setChecked(true);
            if (isEnabled(group.getBtReceiver()))
                hearingAid.setChecked(true);


            /*if (counter % 2 == 0) {
                relLayout.setBackgroundColor(0x24000000);
            }*/
            linearLayout.addView(view);
            counter++;
        }
    }

    private boolean isEnabled(int flag) {
        return flag == 1;
    }

}

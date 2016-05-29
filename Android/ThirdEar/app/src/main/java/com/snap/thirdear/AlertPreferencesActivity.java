package com.snap.thirdear;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
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
            String groupId = String.valueOf(group.get_id());
            View view = layoutInflater.inflate(R.layout.alert_pref_relative, linearLayout, false);
            RelativeLayout relLayout = (RelativeLayout) view.findViewById(R.id.alert_rel);
            TextView groupName = (TextView) view.findViewById(R.id.group_name_text);
            Button selectAllButton = (Button) view.findViewById(R.id.select_all_btn);
            selectAllButton.setTag(groupId);
            Switch phoneSpeaker = (Switch) view.findViewById(R.id.switch_phone_speaker);
            phoneSpeaker.setTag(groupId);
            Switch phoneFlashLight = (Switch) view.findViewById(R.id.switch_phone_flash_light);
            phoneFlashLight.setTag(groupId);
            Switch phoneVibrate = (Switch) view.findViewById(R.id.switch_phone_vibrate);
            phoneVibrate.setTag(groupId);
            Switch light = (Switch) view.findViewById(R.id.switch_light);
            light.setTag(groupId);
            Switch smartWatch = (Switch) view.findViewById(R.id.switch_watch);
            smartWatch.setTag(groupId);
            Switch hearingAid = (Switch) view.findViewById(R.id.switch_hearing_aid);
            hearingAid.setTag(groupId);

            groupName.setText(group.getName());

            if (isEnabled(group.getPhoneAudio()))
                phoneSpeaker.setChecked(true);
            else
                phoneSpeaker.setChecked(false);
            if (isEnabled(group.getPhoneLight()))
                phoneFlashLight.setChecked(true);
            else
                phoneFlashLight.setChecked(false);
            if (isEnabled(group.getPhoneVibrate()))
                phoneVibrate.setChecked(true);
            else
                phoneVibrate.setChecked(false);
            if (isEnabled(group.getLight()))
                light.setChecked(true);
            else
                light.setChecked(false);
            if (isEnabled(group.getWearableDevice()))
                smartWatch.setChecked(true);
            else
                smartWatch.setChecked(false);
            if (isEnabled(group.getBtReceiver()))
                hearingAid.setChecked(true);
            else
                hearingAid.setChecked(false);


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


    public void onClickSelectAll(View view) {
        Button selectAllButton = (Button) view.findViewById(R.id.select_all_btn);
        String groupId = (String) selectAllButton.getTag();
        Log.d("onClickSelectAll", "groupId " + groupId);
        dataBaseHelper.toggleAlertMode(groupId);
        finish();
        startActivity(getIntent());
       /* Switch phoneSpeaker = (Switch) view.findViewById(R.id.switch_phone_speaker);
        phoneSpeaker.setChecked(true);
        Switch phoneFlashLight = (Switch) view.findViewById(R.id.switch_phone_flash_light);
        phoneFlashLight.setChecked(true);
        Switch phoneVibrate = (Switch) view.findViewById(R.id.switch_phone_vibrate);
        phoneVibrate.setChecked(true);
        Switch light = (Switch) view.findViewById(R.id.switch_light);
        light.setChecked(true);
        Switch smartWatch = (Switch) view.findViewById(R.id.switch_watch);
        smartWatch.setChecked(true);
        Switch hearingAid = (Switch) view.findViewById(R.id.switch_hearing_aid);
        hearingAid.setChecked(true);*/
    }

    public void onClickPhoneSpeaker(View view) {
        Switch phoneSpeaker = (Switch) view.findViewById(R.id.switch_phone_speaker);
        String groupId = (String) phoneSpeaker.getTag();
        boolean checked = phoneSpeaker.isChecked();
        Log.d("onClickPhoneSpeaker", "groupId " + groupId);
        if (checked) {
            Log.d("onClickPhoneSpeaker", "turn on " + checked);
            dataBaseHelper.toggleAlertMode(DataBaseHelper.TABLE_ONE_COL_6, DataBaseHelper.DB_TRUE, groupId);
        } else {
            Log.d("onClickPhoneSpeaker", "turn off " + checked);
            dataBaseHelper.toggleAlertMode(DataBaseHelper.TABLE_ONE_COL_6, DataBaseHelper.DB_FALSE, groupId);
        }
    }

    public void onClickPhoneFlashLight(View view) {
        Switch phoneFlashLight = (Switch) view.findViewById(R.id.switch_phone_flash_light);
        String groupId = (String) phoneFlashLight.getTag();
        boolean checked = phoneFlashLight.isChecked();
        Log.d("onClickPhoneFlashLight", "groupId " + groupId);
        if (checked) {
            Log.d("onClickPhoneFlashLight", "turn on " + checked);
            dataBaseHelper.toggleAlertMode(DataBaseHelper.TABLE_ONE_COL_5, DataBaseHelper.DB_TRUE, groupId);
        } else {
            Log.d("onClickPhoneFlashLight", "turn off " + checked);
            dataBaseHelper.toggleAlertMode(DataBaseHelper.TABLE_ONE_COL_5, DataBaseHelper.DB_FALSE, groupId);
        }
    }

    public void onClickPhoneVibrate(View view) {
        Switch phoneVibrate = (Switch) view.findViewById(R.id.switch_phone_vibrate);
        String groupId = (String) phoneVibrate.getTag();
        boolean checked = phoneVibrate.isChecked();
        Log.d("onClickPhoneVibrate", "groupId " + groupId);
        if (checked) {
            Log.d("onClickPhoneVibrate", "turn on " + checked);
            dataBaseHelper.toggleAlertMode(DataBaseHelper.TABLE_ONE_COL_4, DataBaseHelper.DB_TRUE, groupId);
        } else {
            Log.d("onClickPhoneVibrate", "turn off " + checked);
            dataBaseHelper.toggleAlertMode(DataBaseHelper.TABLE_ONE_COL_4, DataBaseHelper.DB_FALSE, groupId);
        }
    }

    public void onClickLight(View view) {
        Switch light = (Switch) view.findViewById(R.id.switch_light);
        String groupId = (String) light.getTag();
        boolean checked = light.isChecked();
        Log.d("onClickLight", "groupId " + groupId);
        if (checked) {
            Log.d("onClickLight", "turn on " + checked);
            dataBaseHelper.toggleAlertMode(DataBaseHelper.TABLE_ONE_COL_7, DataBaseHelper.DB_TRUE, groupId);
        } else {
            Log.d("onClickLight", "turn off " + checked);
            dataBaseHelper.toggleAlertMode(DataBaseHelper.TABLE_ONE_COL_7, DataBaseHelper.DB_FALSE, groupId);
        }
    }

    public void onClickWatch(View view) {
        Switch light = (Switch) view.findViewById(R.id.switch_watch);
        String groupId = (String) light.getTag();
        boolean checked = light.isChecked();
        Log.d("onClickWatch", "groupId " + groupId);
        if (checked) {
            Log.d("onClickWatch", "turn on " + checked);
            dataBaseHelper.toggleAlertMode(DataBaseHelper.TABLE_ONE_COL_9, DataBaseHelper.DB_TRUE, groupId);
        } else {
            Log.d("onClickWatch", "turn off " + checked);
            dataBaseHelper.toggleAlertMode(DataBaseHelper.TABLE_ONE_COL_9, DataBaseHelper.DB_FALSE, groupId);
        }
    }

    public void onClickHearingAid(View view) {
        Switch hearingAid = (Switch) view.findViewById(R.id.switch_hearing_aid);
        String groupId = (String) hearingAid.getTag();
        boolean checked = hearingAid.isChecked();
        Log.d("onClickHearingAid", "groupId " + groupId);
        if (checked) {
            Log.d("onClickHearingAid", "turn on " + checked);
            dataBaseHelper.toggleAlertMode(DataBaseHelper.TABLE_ONE_COL_8, DataBaseHelper.DB_TRUE, groupId);
        } else {
            Log.d("onClickHearingAid", "turn off " + checked);
            dataBaseHelper.toggleAlertMode(DataBaseHelper.TABLE_ONE_COL_8, DataBaseHelper.DB_FALSE, groupId);
        }
    }
}

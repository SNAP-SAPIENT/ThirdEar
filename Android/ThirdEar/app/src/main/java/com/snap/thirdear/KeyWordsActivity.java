package com.snap.thirdear;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;

import com.snap.thirdear.adapter.ExpandableListAdapter;
import com.snap.thirdear.db.DataBaseHelper;
import com.snap.thirdear.db.Groups;
import com.snap.thirdear.db.Trigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KeyWordsActivity extends AppCompatActivity {


    private DataBaseHelper dataBaseHelper;
    private ExpandableListView expListView;
    private ExpandableListAdapter listAdapter;;
    private ArrayList<Groups> groupsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_words);
        expListView = (ExpandableListView) findViewById(R.id.expandableListView);
        dataBaseHelper = new DataBaseHelper(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_keyword);
        showGroupsAndTriggers();
    }

    private void showGroupsAndTriggers() {
        groupsList = dataBaseHelper.getAllGroups();
        if (groupsList.isEmpty()) {
            //show no keyword available Dialog
            keywordUnavailableDialog();
        } else {
            List<String> groupNameList = new ArrayList<>();
            HashMap<String, List<String>> triggersForEachGroup = new HashMap<>();
            for (Groups group : groupsList) {
                groupNameList.add(group.getName());
                ArrayList<Trigger> triggerList = dataBaseHelper.getAllTriggersForGroup(group.get_id());
                List<String> triggerTextList = new ArrayList<>();
                for (Trigger trigger : triggerList) {
                    triggerTextList.add(trigger.getTriggerText());
                }
                triggersForEachGroup.put(group.getName(), triggerTextList);
            }
            listAdapter = new ExpandableListAdapter(this, groupNameList, triggersForEachGroup);
            expListView.setAdapter(listAdapter);
            //for(int i=0; i < listAdapter.getGroupCount(); i++)
            expListView.expandGroup(0);
        }
    }

    private void keywordUnavailableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_key_words)
                .setMessage(R.string.keywords_unavailable);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addKeyword(View view) {
        Intent intent = new Intent(this,AddKeyWordActivity.class);
        finish();
        startActivity(intent);
    }


}

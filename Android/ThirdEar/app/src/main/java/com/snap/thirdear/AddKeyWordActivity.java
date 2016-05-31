package com.snap.thirdear;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.snap.thirdear.adapter.ExpandableListAdapter;
import com.snap.thirdear.db.DataBaseHelper;
import com.snap.thirdear.db.Groups;

import java.util.ArrayList;
import java.util.List;

public class AddKeyWordActivity extends AppCompatActivity {


    private Spinner groupsDropDownList;
    private DataBaseHelper dataBaseHelper;
    private ArrayList<Groups> groupsList;
    private Groups selectedGroup;
    private EditText userTrigger;
    private Button addButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_keyword_activity);

        groupsDropDownList = (Spinner) findViewById(R.id.groups_dropdown);
        userTrigger = (EditText) findViewById(R.id.trigger_text);
        addButton = (Button) findViewById(R.id.add_keyword);
        cancelButton = (Button) findViewById(R.id.cancel);

        dataBaseHelper = new DataBaseHelper(this);
        selectedGroup = null;
        populateSpinnerWithGroups();

    }

    private void populateSpinnerWithGroups() {
        groupsList = dataBaseHelper.getAllGroups();
        List<String> groupNameList = new ArrayList<>();
        if(groupsList.isEmpty()){
            Toast.makeText(AddKeyWordActivity.this, R.string.add_keyword_group_empty_error, Toast.LENGTH_LONG).show();
        }else{
            for (Groups group : groupsList) {
                groupNameList.add(group.getName());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, groupNameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupsDropDownList.setAdapter(adapter);
        // ListView Item Click Listener
        groupsDropDownList.setOnItemSelectedListener(new SpinnerActivity());
    }

    private class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
            Log.d("SpinnerActivity", "index" + pos);
            Log.d("SpinnerActivity", groupsList.get(pos).toString());
            selectedGroup = groupsList.get(pos);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    public void addTrigger(View view){
        String text = userTrigger.getText().toString();
        Log.d("addTrigger:", text);
        Log.d("addTrigger", "length:" + text.length());
        text = text.trim();
        Log.d("addTrigger", "trim:" + text.length());
        if(text.length() > 0 ){
            dataBaseHelper.insertTrigger(selectedGroup.get_id(),text);
            Intent i = new Intent(this, KeyWordsActivity.class);
            finish();
            startActivity(i);
        }else{
            Toast.makeText(AddKeyWordActivity.this, R.string.add_keyword_empty_text_error, Toast.LENGTH_LONG).show();
        }

    }

    public void closeActivity(View view){
        Intent i = new Intent(this, KeyWordsActivity.class);
        finish();
        startActivity(i);
    }

}

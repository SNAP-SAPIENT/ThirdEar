package com.snap.thirdear;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.snap.thirdear.service.BackgroundSpeechRecognizer;
import com.snap.thirdear.service.BluetoothService;
import com.snap.thirdear.service.SoundLevelDetector;


public class HomeFragment extends Fragment {

    private ImageButton startStopBtn;
    private TextView startStopText;
    private ImageView waveImg;
    private SharedPreferences sharedPref;
    private String defaultListenFor;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  view = inflater.inflate(R.layout.fragment_home, container, false);
        startStopBtn = (ImageButton) view.findViewById(R.id.mainButton);
        startStopBtn.setImageResource(R.drawable.start);
        startStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appControl(v);
            }
        });
        startStopText = (TextView)  view.findViewById(R.id.statusTextView);
        waveImg = (ImageView)  view.findViewById(R.id.waveImageView);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        defaultListenFor = getString(R.string.pref_monitor_default);
        return view;
    }

    public void appControl(View view) {
        if ( 0 == BackgroundSpeechRecognizer.onOff || 1 == BackgroundSpeechRecognizer.onOff){
            startListening();
        }else{
            stopListening();
        }
    }

    public void startListening() {
        startStopBtn.setImageResource(R.drawable.stop);
        startStopText.setText(R.string.started_msg);
        waveImg.setImageResource(R.drawable.soundwave_listening);
        String listenFor  = sharedPref.getString("pref_monitor", defaultListenFor);
        if(listenFor.equals(getString(R.string.alert_for_keywords)))
            getActivity().startService(new Intent( getActivity(), BackgroundSpeechRecognizer.class));
        else if(listenFor.equals(getString(R.string.alert_for_noiseLevel)))
            getActivity().startService(new Intent(getActivity(), SoundLevelDetector.class));
        getActivity().startService(new Intent(getActivity(), BluetoothService.class));
        // startService(new Intent(getBaseContext(), AudioRecorderService.class));
    }


    public void stopListening() {
        startStopText.setText(R.string.stopped_msg);
        startStopBtn.setImageResource(R.drawable.start);
        waveImg.setImageResource(R.drawable.soundwave_quiet);
        String listenFor  = sharedPref.getString("pref_monitor", defaultListenFor);
        if(listenFor.equals(getString(R.string.alert_for_keywords)))
            getActivity().stopService(new Intent(getActivity(), BackgroundSpeechRecognizer.class));
        else if(listenFor.equals(getString(R.string.alert_for_noiseLevel)))
            getActivity().stopService(new Intent(getActivity(), SoundLevelDetector.class));
        getActivity().stopService(new Intent(getActivity(), BluetoothService.class));
        // stopService(new Intent(getBaseContext(), AudioRecorderService.class));
    }
}

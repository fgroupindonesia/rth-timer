package com.rth.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import com.rth.timer.dbops.InternalDatabase;

public class SettingsActivity extends AppCompatActivity {

    Switch switchPer10, switchPer5;

    InternalDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchPer10  = (Switch) findViewById(R.id.switchPer10);
        switchPer5 = (Switch) findViewById(R.id.switchPer5);

        db = new InternalDatabase(this);

        if(db.getTimeAudioNarrator().isEmpty()){
            switchPer5.setChecked(true);
            switchPer10.setChecked(false);
        }else if(db.getTimeAudioNarrator().equalsIgnoreCase("5min")){
            switchPer5.setChecked(true);
            switchPer10.setChecked(false);
        }else{
            switchPer5.setChecked(false);
            switchPer10.setChecked(true);
        }

    }

    public void per10Menit(View v){

        switchPer5.setChecked(!switchPer10.isChecked());
        applySaving();
    }

    public void per5Menit(View v){
        switchPer10.setChecked(!switchPer5.isChecked());
        applySaving();
    }

    private void applySaving(){
        if(switchPer5.isChecked()) {
            db.setTimeAudioNarrator("5min");
        } else {
            db.setTimeAudioNarrator("10min");
        }
    }
}
package com.rth.timer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import com.rth.timer.dbops.InternalDatabase;
import com.rth.timer.helper.UIHelper;

public class SettingsActivity extends AppCompatActivity {

    Switch switchPer10, switchPer5, switchPassProtection;
    EditText editText;
    String passSettings;

    InternalDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchPassProtection = (Switch) findViewById(R.id.switchPassProtection);
        switchPer10  = (Switch) findViewById(R.id.switchPer10);
        switchPer5 = (Switch) findViewById(R.id.switchPer5);

        db = new InternalDatabase(this);

        if(db.getPass() != null){
            switchPassProtection.setChecked(true);
        }else{
            switchPassProtection.setChecked(false);
        }

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

     centerTitle();

    }

    private void centerTitle(){
        UIHelper.centerTitle(this);
    }


    public void lockPasswordProtection(View v){

        if(switchPassProtection.isChecked()){
            displayPopupPass();
        }else {
            // delete the pass
            db.setPass(null);
        }

    }

    private void displayPopupPass(){

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Password");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        editText = new EditText(this);
        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());

        builder.setView(editText);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                passSettings = editText.getText().toString();
                updatePassword();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updatePassword();
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updatePassword(){
        // if the passSettings has no value
        db.setPass(passSettings);

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
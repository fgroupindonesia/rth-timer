package com.rth.timer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.rth.timer.dbops.InternalDatabase;
import com.rth.timer.helper.APermissionHelper;
import com.rth.timer.helper.AudioPlayer;
import com.rth.timer.helper.DownloaderHelper;
import com.rth.timer.helper.ShowDialog;
import com.rth.timer.helper.UIHelper;

public class SettingsActivity extends AppCompatActivity {

    Switch switchPer10, switchPer5, switchPassProtection;
    EditText editText;
    String passSettings;

    InternalDatabase db;
    ProgressBar progressBarDownload;

    Spinner spinnerAudioMp3;

    DownloaderHelper downloader;

    ImageButton playButton, pauseButton;

    AudioPlayer audioPlayerMp3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        audioPlayerMp3 = new AudioPlayer(this);

        playButton = (ImageButton) findViewById(R.id.buttonPlay);
        pauseButton = (ImageButton) findViewById(R.id.buttonPause);
        // default is not shown
        playButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.GONE);

        spinnerAudioMp3 = (Spinner) findViewById(R.id.spinnerAudioMp3);
        setOnItemSelected(spinnerAudioMp3);

        progressBarDownload = (ProgressBar) findViewById(R.id.progressBarDownload);
        // kita hide dr sini aja
        progressBarDownload.setVisibility(View.GONE);

        switchPassProtection = (Switch) findViewById(R.id.switchPassProtection);
        switchPer10  = (Switch) findViewById(R.id.switchPer10);
        switchPer5 = (Switch) findViewById(R.id.switchPer5);

        db = new InternalDatabase(this);

        if(db.getMp3() != null){
            setSpinner(spinnerAudioMp3, db.getMp3());
        }

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

        requestPermissions();

        downloader = new DownloaderHelper(this);
        downloader.setPlayButton(playButton);
        downloader.setProgressBar(progressBarDownload);


    }

    private void setSpinner(Spinner spinner, String val){
           for (int i = 0; i < spinner.getCount(); i++) {
                if (spinner.getItemAtPosition(i).equals(val)) {
                    spinner.setSelection(i);
                    break;
                }
            }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downloader.close();
    }


    private boolean checkFileTersedia(String opsi){

        boolean tersedia = false;
        String urlna = null;

        if(opsi.equalsIgnoreCase("running")){
            urlna = getString(R.string.link_running);
        }else if(opsi.equalsIgnoreCase("futuristic")){
            urlna = getString(R.string.link_futuristic);
        }else if(opsi.equalsIgnoreCase("empowering")){
            urlna = getString(R.string.link_empowering);
        }else if(opsi.equalsIgnoreCase("spirit")){
            urlna = getString(R.string.link_spirit);
        }

        tersedia = downloader.isFileDownloaded(urlna);

        return tersedia;

    }

    public void pauseMp3(View v){
        String urlAslina = null;
        String lokasiLokal = null;

        if(mp3Terpilih != null){
            if(!mp3Terpilih.equalsIgnoreCase("-")){
                urlAslina = getURLAudio(mp3Terpilih);

                lokasiLokal = downloader.getSavedFilePath(urlAslina);
                audioPlayerMp3.pause();

                pauseButton.setVisibility(View.GONE);
                playButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void playMp3(View v){

        String urlAslina = null;
        String lokasiLokal = null;

        if(mp3Terpilih != null){
            if(!mp3Terpilih.equalsIgnoreCase("-")){
                urlAslina = getURLAudio(mp3Terpilih);

                lokasiLokal = downloader.getSavedFilePath(urlAslina);
                audioPlayerMp3.test(lokasiLokal);

                pauseButton.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.GONE);
            }
        }

    }

    String mp3Terpilih;

    public void saveAudioMp3Config(){
        db.setMp3(mp3Terpilih);
    }

    private void setOnItemSelected(Spinner spin){
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mp3Terpilih = parent.getItemAtPosition(position).toString();

                if(!mp3Terpilih.equalsIgnoreCase("-")){
                    if(!checkFileTersedia(mp3Terpilih)){
                        displayContinueDownload(mp3Terpilih);

                        // when the audio is not exist
                        playButton.setVisibility(View.GONE);
                        pauseButton.setVisibility(View.GONE);

                    }else{
                        // when the audio file is exist
                        playButton.setVisibility(View.VISIBLE);
                        pauseButton.setVisibility(View.GONE);
                    }
                }else{
                    playButton.setVisibility(View.GONE);
                    pauseButton.setVisibility(View.GONE);
                }

                audioPlayerMp3.pause();
                // save it locally for the config
                saveAudioMp3Config();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    private void requestPermissions(){

        APermissionHelper permissionRequestor = new APermissionHelper();


    }

    private String getURLAudio(String nama){
        String urlna = null;

        if(nama.equalsIgnoreCase("spirit")){
            urlna = getString(R.string.link_spirit);
        }else if(nama.equalsIgnoreCase("empowering")){
            urlna =  getString(R.string.link_empowering);
        }else if(nama.equalsIgnoreCase("futuristic")){
            urlna =  getString(R.string.link_futuristic);
        }else if(nama.equalsIgnoreCase("running")){
            urlna =  getString(R.string.link_running);
        }

        return urlna;
    }


    private void displayContinueDownload(String nama){

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Download Audio Mp3 : " + nama);

        //TextView txt = new TextView(this);
        //txt.setText(nama);
        //builder.setView(txt);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
               // continue download
                progressBarDownload.setVisibility(View.VISIBLE);

                String nameTarget = getURLAudio(nama);

                downloader.download(nameTarget);
                ShowDialog.shortMessage(SettingsActivity.this, "downloading...");
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ShowDialog.shortMessage(SettingsActivity.this, "cancelling...");
                progressBarDownload.setVisibility(View.GONE);
                dialog.cancel();
            }
        });

        builder.show();
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
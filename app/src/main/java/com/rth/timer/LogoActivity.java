package com.rth.timer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.rth.timer.helper.UIHelper;

import java.util.Timer;
import java.util.TimerTask;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        centerTitle();
        playWelcomeMessage();
        nextActivity();
    }

    private void centerTitle(){
        UIHelper.centerTitle(this);
    }

    private void nextActivity(){

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }, 4000);


    }

    private void playWelcomeMessage(){
        MediaPlayer ring = MediaPlayer.create(this, R.raw.selamat_datang_di_rth_timer); ring.start();
    }
}
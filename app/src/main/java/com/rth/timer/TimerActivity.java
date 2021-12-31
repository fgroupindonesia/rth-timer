package com.rth.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TimerActivity extends AppCompatActivity {

    Button buttonPlayPause;
    Handler handler =new Handler();
    Runnable r;
    TextView textViewTimer;
    MediaPlayer mplayer;
    ImageView imageViewSound;

    int hour, min, sec;
    String hourText, minText, secText,
    satuanWaktuAfter = null;

    boolean pauseTimeNow, mutedSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        imageViewSound = (ImageView) findViewById(R.id.imageViewSound);
        buttonPlayPause = (Button) findViewById(R.id.buttonPlayPause);
        textViewTimer = (TextView) findViewById(R.id.textViewTimer);

        runTimerTick();

    }

    MediaPlayer.OnCompletionListener completeListener = new MediaPlayer.OnCompletionListener() {

        public void onCompletion(MediaPlayer mp) {
            mp.release();

            if(satuanWaktuAfter!=null) {
                if (satuanWaktuAfter.equalsIgnoreCase("minute")) {
                    mp = MediaPlayer.create(TimerActivity.this, R.raw.menit);
                    mp.setOnCompletionListener(completeListener);
                    satuanWaktuAfter = null;
                    mp.start();

                } else if (satuanWaktuAfter.equalsIgnoreCase("hour")) {
                    mp = MediaPlayer.create(TimerActivity.this, R.raw.jam);
                    mp.start();
                    numberToVoice(min, "minute");
                }
            }else {
                mp = MediaPlayer.create(TimerActivity.this, R.raw.telah_berlalu);
                mp.start();
            }
        }
    };

    public void muteSound(View v){
        mutedSound = !mutedSound;
        if(mutedSound==true){
            imageViewSound.setImageResource(R.drawable.mute_icon_24);
        }else{
            imageViewSound.setImageResource(R.drawable.unmute_icon_24);
        }

    }

    private String getDigitNumber(int val){
        String v  = null;

        if(val<10){
            v = "0" + val;
        }else{
            v = "" + val;
        }

        return v;
    }

    private void updateTiming(){

        sec++;

        if(sec==60){
            sec=0;
            min++;
            playTimerVoice();
        }

        if(min==60){
            min=0;
            hour++;
        }

        getAllDigitNumbers();
        textViewTimer.setText(getTimerText());
    }

    private void getAllDigitNumbers(){
        hourText = getDigitNumber(hour);
        minText = getDigitNumber(min);
        secText = getDigitNumber(sec);
    }

    private String getTimerText(){
        return hourText+":"+minText+":"+secText;
    }

    public void pauseTimer(View v){
        if(pauseTimeNow==false){
            buttonPlayPause.setText(getResources().getString(R.string.tombol_play));
            buttonPlayPause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.play_icon_24, 0, 0, 0);
        }else{
            buttonPlayPause.setText(getResources().getString(R.string.tombol_pause));
            buttonPlayPause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pause_icon_24, 0, 0, 0);
        }
        pauseTimeNow = !pauseTimeNow;
    }

    private void runTimerTick(){

        r = new Runnable() {
            public void run() {
                if(!pauseTimeNow) {
                    updateTiming();
                }
                handler.postDelayed(this, 200);
            }
        };

        handler.postDelayed(r, 200);

    }




    public void stopTimer(View v){

        resetTimer();
        pauseTimeNow = true;

        textViewTimer.setText(getTimerText());
    }

    private void resetTimer(){
        sec = 0;
        min = 0;
        hour = 0;

        getAllDigitNumbers();
    }

    private void numberToVoice(int val, String satuanWaktu){

        if(val==1){
            mplayer = MediaPlayer.create(this, R.raw._1);
        }else if(val==2) {
            mplayer = MediaPlayer.create(this, R.raw._2);
        }else if(val==3) {
            mplayer = MediaPlayer.create(this, R.raw._3);
        }else if(val==4) {
            mplayer = MediaPlayer.create(this, R.raw._4);
        }else if(val==5) {
            mplayer = MediaPlayer.create(this, R.raw._5);
        }else if(val==6) {
            mplayer = MediaPlayer.create(this, R.raw._6);
        }else if(val==7) {
            mplayer = MediaPlayer.create(this, R.raw._7);
        }else if(val==8) {
            mplayer = MediaPlayer.create(this, R.raw._8);
        }else if(val==9) {
            mplayer = MediaPlayer.create(this, R.raw._9);
        }else if(val==10) {
            mplayer = MediaPlayer.create(this, R.raw._10);
        }else if(val==15) {
            mplayer = MediaPlayer.create(this, R.raw._15);
        }else if(val==20) {
            mplayer = MediaPlayer.create(this, R.raw._20);
        }else if(val==25) {
            mplayer = MediaPlayer.create(this, R.raw._25);
        }else if(val==30) {
            mplayer = MediaPlayer.create(this, R.raw._30);
        }else if(val==45) {
            mplayer = MediaPlayer.create(this, R.raw._45);
        }else if(val==50) {
            mplayer = MediaPlayer.create(this, R.raw._50);
        }else {
            mplayer=null;
        }

        if(mplayer!=null) {
            satuanWaktuAfter = satuanWaktu;
            mplayer.setOnCompletionListener(completeListener);
            mplayer.start();
        }

    }

    private void playTimerVoice(){

        if(hour==0){
            numberToVoice(min, "minute");
        }else {
            numberToVoice(hour, "hour");
        }



    }

}
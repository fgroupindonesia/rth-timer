package com.rth.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rth.timer.dbops.DataRiwayat;
import com.rth.timer.dbops.InternalDatabase;
import com.rth.timer.reference.Mode;

import java.util.Timer;
import java.util.TimerTask;

public class TimerActivity extends AppCompatActivity {

    Button buttonPlayPause;
    Handler handler =new Handler(), handler2 = new Handler();
    Runnable r, r2;
    TextView textViewTimer, textViewTimerMili;
    MediaPlayer mplayer;
    ImageView imageViewSound;

    int hour, min, sec, mill;
    String hourText, minText, secText,
    satuanWaktuAfter = null;

    int angkaStop;
    String satuanStop;

    boolean pauseTimeNow, mutedSound, timeFinished;
    InternalDatabase db;

    int aksiSaatIni;

    DataRiwayat dataNya;

    final long startTime = SystemClock.uptimeMillis();
    long timeInMilliseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        imageViewSound = (ImageView) findViewById(R.id.imageViewSound);
        buttonPlayPause = (Button) findViewById(R.id.buttonPlayPause);
        textViewTimer = (TextView) findViewById(R.id.textViewTimer);
        textViewTimerMili = (TextView) findViewById(R.id.textViewTimerMili);
        runTimerTick();
        keepOn();

        db = new InternalDatabase(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            satuanStop = bundle.getString("satuanStop");
            angkaStop = bundle.getInt("angkaStop");
            aksiSaatIni = bundle.getInt("MODE");
        }

        }

    private void keepOn(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    MediaPlayer.OnCompletionListener completeListener = new MediaPlayer.OnCompletionListener() {

        public void onCompletion(MediaPlayer mp) {
            mp.release();

            if(satuanWaktuAfter!=null) {
                if (satuanWaktuAfter.equalsIgnoreCase("minute")) {
                    mp = MediaPlayer.create(TimerActivity.this, R.raw.menit);
                    satuanWaktuAfter = null;
                    mp.setOnCompletionListener(completeListener);
                    mp.start();

                } else if (satuanWaktuAfter.equalsIgnoreCase("hour")) {
                    mp = MediaPlayer.create(TimerActivity.this, R.raw.jam);
                    mp.start();

                    // play after 1.5 seconds
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            numberToVoice(min, "minute");
                        }
                    }, 1500);

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

    private void popupNextTreatment(){

        // questioning whether want to continue
        // for next treatment ?
        AlertDialog.Builder builder = new AlertDialog.Builder(TimerActivity.this);

        // Set a title for alert dialog
        builder.setTitle("Lanjutkan ke Therapy?");

        // Ask the final question
        builder.setMessage("Pasien "+ db.getUser() + " ini akan berlanjut ke tahapan Therapy.");

        // Set the alert dialog yes button click listener
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // new intent to setOption
                finish();

                Intent i = new Intent(TimerActivity.this, SetOptionsActivity.class);
                i.putExtra("layout", "therapy");
                startActivity(i);
            }
        });

        // Set the alert dialog no button click listener
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();


    }

    private void saveDataToRiwayat(){
        dataNya = new DataRiwayat();

        if(aksiSaatIni==Mode.MODE_DIAGNOSA) {
            dataNya.setMode("diagnosa & konsultasi");
        }else{
            dataNya.setMode("therapy");
            dataNya.setTherapy(db.getTreatment());
        }

        dataNya.setNamaPasien(db.getUser());
        if(timeFinished) {
            if(satuanStop.equalsIgnoreCase("minute")){
                satuanStop = "menit";
            }else{
                satuanStop = "jam";
            }

            dataNya.setDurasi(angkaStop + " " + satuanStop);
        } else {
            // time is not yet finished but stopped
            String pesan = null;
            if(hour>0){
                pesan = hour + " jam " + min + " menit";
            }else{
                pesan = min + " menit";
            }

            dataNya.setDurasi(pesan);

        }


        db.addHistory(dataNya);
    }

    private void checkIfTimeOut(){
        // if the time is matched
        // we will stop every process and show the popup
        if(satuanStop!=null) {
            if (satuanStop.equalsIgnoreCase("minute")) {
                if (min >= angkaStop) {
                    pauseTimeNow = true;
                    timeFinished = true;
                }


            } else if (satuanStop.equalsIgnoreCase("hour")) {
                if (hour >= angkaStop) {
                    pauseTimeNow = true;
                    timeFinished = true;
                }


            }

            if(timeFinished){
                playFinishVoice();

                // save to db
                saveDataToRiwayat();

                if(aksiSaatIni==Mode.MODE_DIAGNOSA ) {
                    popupNextTreatment();
                } else{
                    // we dont offer anything just
                   jumpToRiwayatActivity();
                }
            }

        }
    }

    private void updateTiming(){

        timeInMilliseconds = SystemClock.uptimeMillis() - startTime ; //Start time is 1.000/s
        timeInMilliseconds = (timeInMilliseconds ) + 1000;

        mill = (int) (timeInMilliseconds % 1000);

        sec = (int) (timeInMilliseconds / 1000);

        min = sec / 60;

        sec = sec % 60;
        hour = min / 60;
        /*
        if(mill>=1000) {
            mill = 0;
            sec++;
        }*/



        /*if(min==60){
            min=0;
            hour++;
        }*/

        getAllDigitNumbers();
        textViewTimerMili.setText(""+mill);
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

       // here is for audio minute speech
        // read on each minute

        r2 = new Runnable() {
            public void run() {
                if(!pauseTimeNow) {
                    playTimerVoice();
                    checkIfTimeOut();
                }
                handler2.postDelayed(this, 60000);

            }
        };

        handler2.postDelayed(r2, 60000);

        // below is for timing animation
        r = new Runnable() {
            public void run() {
                if(!pauseTimeNow) {
                    updateTiming();
                }
                handler.postDelayed(this, 10);

            }
        };

        handler.postDelayed(r, 10);

    }

    public void stopTimer(View v){

        // save first
        saveDataToRiwayat();

        resetTimer();
        pauseTimeNow = true;
       // timeFinished = true;
        playFinishVoice();

        textViewTimer.setText(getTimerText());

        if(aksiSaatIni==Mode.MODE_DIAGNOSA) {
            popupNextTreatment();
        }else{
            jumpToRiwayatActivity();
        }
    }

    private void jumpToRiwayatActivity(){
        finish();
        // then opening the riwayat activity
        Intent i = new Intent(TimerActivity.this, RiwayatActivity.class);
        startActivity(i);
    }
    private void resetTimer(){
        sec = 0;
        min = 0;
        hour = 0;

        getAllDigitNumbers();
    }

    private void numberToVoice(int val, String satuanWaktu){

        if(mplayer!=null){
            mplayer.release();
        }

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

    private boolean isMuted(){
        return mutedSound;
    }

    private void playFinishVoice(){
        if(mplayer != null){
            mplayer.release();
        }

        //if(timeFinished){
            if(aksiSaatIni== Mode.MODE_DIAGNOSA) {
                mplayer = MediaPlayer.create(this, R.raw.diagnosa_konsultasi_telah_usai);
            } else if(aksiSaatIni==Mode.MODE_LANGSUNG_THERAPY){
                mplayer = MediaPlayer.create(this, R.raw.therapy_telah_usai);
            }
        //}

        if(mplayer!=null){
            mplayer.start();
        }

    }

    private void playTimerVoice(){

        if(isMinuteReadable() && !isMuted()) {
            if (hour == 0) {
                numberToVoice(min, "minute");
            } else {
                numberToVoice(hour, "hour");
            }
        }

    }

    private boolean isMinuteReadable(){

        // only certain minutes we will read into voice,
        // such as 1,2,3,4,5,6,7,8,9,10
        // 15, 20, 25, 30, 45, 50
        boolean valid = false;
        int validNumbers[] = {1,2,3,4,5,6,7,8,9,10,15,20,25,30,45,50};

        for(int n : validNumbers){

            if(min == n){
                valid=true;
                break;
            }

        }


        return valid;

    }

}
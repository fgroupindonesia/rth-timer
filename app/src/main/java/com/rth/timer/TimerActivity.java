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
    Handler handler = new Handler(), handler2 = new Handler();
    Runnable r, r2;
    TextView textViewTimer, textViewTimerMili;
    MediaPlayer mplayer;
    ImageView imageViewSound;

    final int equalToMilSec = 1000;
    int highSpeedTime = equalToMilSec;
    int normalSpeedTime = 60 * highSpeedTime;

    int hour, min, sec, mill;
    String hourText, minText, secText,
            satuanWaktuAfter = null;

    int angkaStop;
    String satuanStop;

    boolean pauseTimeNow, mutedSound, timeFinished;
    InternalDatabase db;

    int aksiSaatIni;

    DataRiwayat dataNya;

    long startTime = SystemClock.uptimeMillis();
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

    private void keepOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    MediaPlayer.OnCompletionListener completeListener = new MediaPlayer.OnCompletionListener() {

        public void onCompletion(MediaPlayer mp) {
            mp.release();

            if (satuanWaktuAfter != null) {
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
            } else {
                mp = MediaPlayer.create(TimerActivity.this, R.raw.telah_berlalu);
                mp.start();
            }
        }
    };

    public void muteSound(View v) {
        mutedSound = !mutedSound;
        if (mutedSound == true) {
            imageViewSound.setImageResource(R.drawable.mute_icon_24);
        } else {
            imageViewSound.setImageResource(R.drawable.unmute_icon_24);
        }

    }

    private String getDigitNumber(int val) {
        String v = null;

        if (val < 10) {
            v = "0" + val;
        } else {
            v = "" + val;
        }

        return v;
    }

    private void popupNextTreatment() {

        // questioning whether want to continue
        // for next treatment ?
        AlertDialog.Builder builder = new AlertDialog.Builder(TimerActivity.this);

        // Set a title for alert dialog
        builder.setTitle("Lanjutkan ke Therapy?");

        // Ask the final question
        builder.setMessage("Pasien " + db.getUser() + " ini akan berlanjut ke tahapan Therapy.");

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

    private void saveDataToRiwayat() {
        dataNya = new DataRiwayat();

        if (aksiSaatIni == Mode.MODE_DIAGNOSA) {
            dataNya.setMode("diagnosa & konsultasi");
        } else {
            dataNya.setMode("therapy");
            dataNya.setTherapy(db.getTreatment());
        }

        dataNya.setNamaPasien(db.getUser());
        if (timeFinished) {
            if (satuanStop.equalsIgnoreCase("minute")) {
                satuanStop = "menit";
            } else {
                satuanStop = "jam";
            }

            dataNya.setDurasi(angkaStop + " " + satuanStop);
        } else {
            // time is not yet finished but stopped
            String pesan = null;
            if (hour > 0) {
                pesan = hour + " jam " + min + " menit";
            } else {
                pesan = min + " menit";
            }

            dataNya.setDurasi(pesan);

        }


        db.addHistory(dataNya);
    }

    private void checkIfTimeOut() {
        // if the time is matched
        // we will stop every process and show the popup
        if (satuanStop != null) {
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

            if (timeFinished) {
                playFinishVoice();

                // save to db
                saveDataToRiwayat();

                if (aksiSaatIni == Mode.MODE_DIAGNOSA) {
                    popupNextTreatment();
                } else {
                    // we dont offer anything just
                    jumpToRiwayatActivity();
                }
            }

        }
    }


    long currTimeMilis = 0;
    boolean fromResume = false;
    long timeMiliStopFrom;

    private void updateTiming() {

        // turn this off if you want to test the high speed
        // NORMAL SPEED STARTED
        if(fromResume==false) {
            currTimeMilis = SystemClock.uptimeMillis();
        }else {
            // if coming from resume
            currTimeMilis = timeMiliStopFrom+ INTERVAL_MILIS_WAIT;
        }

        timeInMilliseconds = currTimeMilis - startTime; //Start time is 1.000/s

        // used for later usage resuming after pausing

        timeMiliStopFrom = currTimeMilis;

        timeInMilliseconds = (timeInMilliseconds) + equalToMilSec;


        mill = (int) (timeInMilliseconds % equalToMilSec);

        sec = (int) (timeInMilliseconds / equalToMilSec);

        min = sec / 60;
        sec = sec % 60;
        hour = min / 60;

        if (mill >= 1000) {
            mill = 0;
            sec++;
        }
        // NORMAL SPEED ENDED

        /*
        // HIGH SPEED STARTED
        // turn this off if you want to be normal speed

        sec++;

        if(sec==60){
            sec = 0;
            min++;
        }

        if(min==60){
            min=0;
            hour++;
        }

        if(hour>=24){
            sec=0;
            min=0;
            hour=0;
        }

        // HIGH SPEED ENDED
        */

        getAllDigitNumbers();
        textViewTimerMili.setText("" + mill);
        textViewTimer.setText(getTimerText());

    }

    private void getAllDigitNumbers() {
        hourText = getDigitNumber(hour);
        minText = getDigitNumber(min);
        secText = getDigitNumber(sec);
    }

    private String getTimerText() {
        return hourText + ":" + minText + ":" + secText;
    }

    public void pauseTimer(View v) {
        if (pauseTimeNow == false) {
            // here we continue the startTime from the started pause button pressed
            fromResume = false;
            buttonPlayPause.setText(getResources().getString(R.string.tombol_play));
            buttonPlayPause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.play_icon_24, 0, 0, 0);
        } else {
            fromResume = true;
            buttonPlayPause.setText(getResources().getString(R.string.tombol_pause));
            buttonPlayPause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pause_icon_24, 0, 0, 0);
        }
        pauseTimeNow = !pauseTimeNow;
    }

    final int INTERVAL_MILIS_WAIT = 10;
    private void runTimerTick() {

        // here is for audio minute speech
        // read on each minute


        r2 = new Runnable() {
            public void run() {
                if (!pauseTimeNow) {
                    playTimerVoice();
                    checkIfTimeOut();
                }
                handler2.postDelayed(this, normalSpeedTime);

            }
        };

        handler2.postDelayed(r2, normalSpeedTime);

        // below is for timing animation
        r = new Runnable() {
            public void run() {
                if (!pauseTimeNow) {
                    updateTiming();
                }
                handler.postDelayed(this, INTERVAL_MILIS_WAIT);

            }
        };

        handler.postDelayed(r, INTERVAL_MILIS_WAIT);

    }

    public void stopTimer(View v) {

        // save first
        saveDataToRiwayat();

        resetTimer();
        pauseTimeNow = true;
        // timeFinished = true;
        playFinishVoice();

        textViewTimer.setText(getTimerText());

        if (aksiSaatIni == Mode.MODE_DIAGNOSA) {
            popupNextTreatment();
        } else {
            jumpToRiwayatActivity();
        }
    }

    private void jumpToRiwayatActivity() {
        finish();
        // then opening the riwayat activity
        Intent i = new Intent(TimerActivity.this, RiwayatActivity.class);
        startActivity(i);
    }

    private void resetTimer() {
        sec = 0;
        min = 0;
        hour = 0;

        getAllDigitNumbers();
    }

    private void numberToVoice(int val, String satuanWaktu) {

        if (mplayer != null) {
            mplayer.release();
        }

        if (val == 1) {
            mplayer = MediaPlayer.create(this, R.raw._1);
        } else if (val == 2) {
            mplayer = MediaPlayer.create(this, R.raw._2);
        } else if (val == 3) {
            mplayer = MediaPlayer.create(this, R.raw._3);
        } else if (val == 4) {
            mplayer = MediaPlayer.create(this, R.raw._4);
        } else if (val == 5) {
            mplayer = MediaPlayer.create(this, R.raw._5);
        } else if (val == 6) {
            mplayer = MediaPlayer.create(this, R.raw._6);
        } else if (val == 7) {
            mplayer = MediaPlayer.create(this, R.raw._7);
        } else if (val == 8) {
            mplayer = MediaPlayer.create(this, R.raw._8);
        } else if (val == 9) {
            mplayer = MediaPlayer.create(this, R.raw._9);
        } else if (val == 10) {
            mplayer = MediaPlayer.create(this, R.raw._10);
        } else if (val == 15) {
            mplayer = MediaPlayer.create(this, R.raw._15);
        } else if (val == 20) {
            mplayer = MediaPlayer.create(this, R.raw._20);
        } else if (val == 25) {
            mplayer = MediaPlayer.create(this, R.raw._25);
        } else if (val == 30) {
            mplayer = MediaPlayer.create(this, R.raw._30);
        } else if (val == 45) {
            mplayer = MediaPlayer.create(this, R.raw._45);
        } else if (val == 50) {
            mplayer = MediaPlayer.create(this, R.raw._50);
        } else {
            mplayer = null;
        }

        if (mplayer != null) {
            satuanWaktuAfter = satuanWaktu;
            mplayer.setOnCompletionListener(completeListener);
            mplayer.start();
        }

    }

    private boolean isMuted() {
        return mutedSound;
    }

    private void playFinishVoice() {
        if (mplayer != null) {
            mplayer.release();
        }

        //if(timeFinished){
        if (aksiSaatIni == Mode.MODE_DIAGNOSA) {
            mplayer = MediaPlayer.create(this, R.raw.diagnosa_konsultasi_telah_usai);
        } else if (aksiSaatIni == Mode.MODE_LANGSUNG_THERAPY) {
            mplayer = MediaPlayer.create(this, R.raw.therapy_telah_usai);
        }
        //}

        if (mplayer != null) {
            mplayer.start();
        }

    }

    private void playTimerVoice() {

        if (isMinuteReadable() && !isMuted()) {
            if (hour == 0) {
                numberToVoice(min, "minute");
            } else {
                numberToVoice(hour, "hour");
            }
            // when the time hour is matched exactly
        } else if (!isMuted() && hour >= 1 && min == 0) {
            numberToVoice(hour, "hour");
        }

    }

    private int[] getMinuteAsSettings() {

        // we get the minute as the time interval from settings

        int min10[] = {10, 20, 30, 40, 50};
        int min5[] = {5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55};

        if (db.getTimeAudioNarrator().isEmpty()) {
            return min5;
        } else if (db.getTimeAudioNarrator().equalsIgnoreCase("5min")) {
            return min5;
        }

        return min10;

    }

    private boolean isMinuteReadable() {

        // only certain minutes we will read into voice,
        // such as 1,2,3,4,5,6,7,8,9,10
        // 15, 20, 25, 30, 45, 50
        boolean valid = false;
        //int validNumbers[] = {1,2,3,4,5,6,7,8,9,10,15,20,25,30,45,50};

        int validNumbers[] = getMinuteAsSettings();

        for (int n : validNumbers) {

            if (min == n) {
                valid = true;
                break;
            }

        }

        return valid;

    }

}
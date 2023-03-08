package com.rth.timer.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

public class AudioPlayer {

    MediaPlayer mplayer;
    Context appContext;

    public AudioPlayer(AppCompatActivity app){
        appContext = app;
    }

    String localPertama;

    public void test(String pathLocal){

        // play from locally
        if(localPertama==null){
            localPertama = pathLocal;
        }

        if(mplayer != null && !isPaused()){
            mplayer.stop();
        }

            if(mplayer == null || !localPertama.equalsIgnoreCase(pathLocal)){
                mplayer = MediaPlayer.create(appContext, Uri.parse(pathLocal));
                mplayer.setLooping(false);
            }


        mplayer.start();


    }

    private boolean isPaused(){
        return !mplayer.isPlaying() && mplayer.getCurrentPosition() > 1;
    }

    public void pause(){
        if(mplayer != null)
            mplayer.pause();

    }


}

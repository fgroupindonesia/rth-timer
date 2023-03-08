package com.rth.timer.dbops;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class InternalDatabase {

    public static final String KEY_PASIEN ="pasien", KEY_DATA_RIWAYAT = "history",
    KEY_TREATMENT = "treatment", KEY_TIME_AUDIO_NARRATOR = "waktu",
    KEY_PASSWORD = "password", KEY_AUDIO_MP3  = "mp3";

    Context appContext;
    SharedPreferences sharedPreferences;

    public InternalDatabase(Context app){
        sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(app);
        appContext = app;
    }

    public void setMp3(String terpilih){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AUDIO_MP3, terpilih);
        editor.commit();
    }

    public void setPass(String data){
        // either 5min or 10min
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PASSWORD, data);
        editor.commit();
    }

    public void setTimeAudioNarrator(String name){
        // either 5min or 10min
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TIME_AUDIO_NARRATOR, name);
        editor.commit();
    }

    public void setTreatment(String name){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TREATMENT, name);
        editor.commit();
    }

    public void setUser(String name){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PASIEN, name);
        editor.commit();
    }

    public void clearDataHistory(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DATA_RIWAYAT, "");
        editor.commit();
    }

    public String getMp3(){
        return sharedPreferences.getString(KEY_AUDIO_MP3,null);
    }


    public String getPass(){
        return sharedPreferences.getString(KEY_PASSWORD,null);
    }

    public String getTimeAudioNarrator(){
        return sharedPreferences.getString(KEY_TIME_AUDIO_NARRATOR,"");
    }


    public String getTreatment(){
        return sharedPreferences.getString(KEY_TREATMENT,"");
    }

    public String getUser(){
        return sharedPreferences.getString(KEY_PASIEN,"");
    }

    public DataRiwayat[] getDataHistory(){
       String val = sharedPreferences.getString(KEY_DATA_RIWAYAT,"");

       DataRiwayat [] entry = null;

        try {
            Gson gson = new Gson();
            entry = gson.fromJson(val, DataRiwayat[].class);

        } catch (Exception e) {
            e.printStackTrace();
            // if error occured
            // so the data is empty
        }

        return entry;

    }

    public void addHistory(DataRiwayat dataIn){

        Gson gson = new Gson();
        // String json = gson.toJson(dataIn);

        String jsonAll = null;
        DataRiwayat [] dataAll = null, dataBaru = null;

        if(this.getDataHistory()==null){
            dataAll = new DataRiwayat[1];
            dataAll[0] = dataIn;
            jsonAll = gson.toJson(dataAll);
        }else{
            // if any
            dataAll = this.getDataHistory();
            // make a new copy
            dataBaru = new DataRiwayat[dataAll.length+1];
            for(int x = 0 ; x<dataAll.length; x++){
                dataBaru[x] = dataAll[x];
            }
            // last post
            dataBaru[dataAll.length] = dataIn;
            jsonAll = gson.toJson(dataBaru);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DATA_RIWAYAT, jsonAll);
        editor.commit();
    }

}

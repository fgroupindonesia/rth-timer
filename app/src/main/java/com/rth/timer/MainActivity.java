package com.rth.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rth.timer.helper.UIHelper;
import com.rth.timer.reference.Mode;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        centerTitle();
    }

    private void centerTitle(){
        UIHelper.centerTitle(this);
    }

    public void openMulaiDiagnosa(View v){
        openSetOptions(Mode.MODE_DIAGNOSA);
    }

    public void openLangsungTherapy(View v){
        openSetOptions(Mode.MODE_LANGSUNG_THERAPY);
    }

    private void openSetOptions(int modeNa){

        Intent i = new Intent(this, SetOptionsActivity.class);
        if(modeNa == Mode.MODE_DIAGNOSA) {
            i.putExtra("layout", "diagnosa");
        } else {
            i.putExtra("layout", "therapy");
        }
        startActivity(i);
    }

    public void openRiwayat(View v){
        Intent i = new Intent(this, RiwayatActivity.class);
        startActivity(i);
    }

    public void openSettingan(View v){
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }
}
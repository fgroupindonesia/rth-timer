package com.rth.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    final int MODE_DIAGNOSA = 1, MODE_LANGSUNG_THERAPY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openMulaiDiagnosa(View v){
        openSetOptions(MODE_DIAGNOSA);
    }

    public void openLangsungTherapy(View v){
        openSetOptions(MODE_LANGSUNG_THERAPY);
    }

    private void openSetOptions(int modeNa){

        Intent i = new Intent(this, SetOptionsActivity.class);
        if(modeNa == MODE_DIAGNOSA) {
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
}
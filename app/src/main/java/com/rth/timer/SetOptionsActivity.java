package com.rth.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class SetOptionsActivity extends AppCompatActivity {

    LinearLayout linearLayoutLanjutTherapy, linearLayoutTherapy;
    TextView textViewJudulSetOption;
    Spinner spinnerDurasiAngka, spinnerSatuan, spinnerTherapy;
    EditText editTextNamaPasien;
    CheckBox checkBoxLanjutTherapy;
    Button buttonMulai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_options);

        buttonMulai = (Button) findViewById(R.id.buttonMulai);
        checkBoxLanjutTherapy = (CheckBox) findViewById(R.id.checkBoxLanjutTherapy);
        spinnerTherapy = (Spinner)findViewById(R.id.spinnerTherapy);
        spinnerSatuan = (Spinner) findViewById(R.id.spinnerSatuan);
        spinnerDurasiAngka = (Spinner) findViewById(R.id.spinnerDurasiAngka);
        editTextNamaPasien = (EditText) findViewById(R.id.editTextNamaPasien);
        textViewJudulSetOption = (TextView) findViewById(R.id.textViewJudulSetOption);

        linearLayoutLanjutTherapy = (LinearLayout) findViewById(R.id.linearLayoutLanjutTherapy);
        linearLayoutTherapy = (LinearLayout) findViewById(R.id.linearLayoutTherapy);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
           String jenisLayout = bundle.getString("layout");

           if(jenisLayout.equalsIgnoreCase("diagnosa")){
               // diagnosa
               textViewJudulSetOption.setText(getResources().getString(R.string.judul_diagnosa_konsultasi));
               linearLayoutLanjutTherapy.setVisibility(View.VISIBLE);
               linearLayoutTherapy.setVisibility(View.GONE);
           }else{
               // therapy
               textViewJudulSetOption.setText(getResources().getString(R.string.judul_langsung_therapy));
               linearLayoutLanjutTherapy.setVisibility(View.GONE);
               linearLayoutTherapy.setVisibility(View.VISIBLE);
           }

        }

    }

    public void mulaiTimer(View v){
        Intent i = new Intent(this, TimerActivity.class);
        startActivity(i);
    }
}
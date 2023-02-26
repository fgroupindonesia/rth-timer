package com.rth.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.rth.timer.dbops.InternalDatabase;
import com.rth.timer.helper.UIHelper;
import com.rth.timer.reference.Mode;

public class SetOptionsActivity extends AppCompatActivity {

    LinearLayout  linearLayoutTherapy;
    TextView textViewJudulSetOption;
    Spinner spinnerDurasiAngka, spinnerSatuan, spinnerTherapy;
    EditText editTextNamaPasien;
    Button buttonMulai;

    int angkaStop;
    String satuanStop, namaUser;
    int aksiSaatIni;

    InternalDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_options);

        buttonMulai = (Button) findViewById(R.id.buttonMulai);
        spinnerTherapy = (Spinner)findViewById(R.id.spinnerTherapy);
        spinnerSatuan = (Spinner) findViewById(R.id.spinnerSatuan);
        spinnerDurasiAngka = (Spinner) findViewById(R.id.spinnerDurasiAngka);
        editTextNamaPasien = (EditText) findViewById(R.id.editTextNamaPasien);
        textViewJudulSetOption = (TextView) findViewById(R.id.textViewJudulSetOption);

         linearLayoutTherapy = (LinearLayout) findViewById(R.id.linearLayoutTherapy);

         centerTitle();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
           String jenisLayout = bundle.getString("layout");

           if(jenisLayout.equalsIgnoreCase("diagnosa")){
               // diagnosa
               textViewJudulSetOption.setText(getResources().getString(R.string.judul_diagnosa_konsultasi));
               linearLayoutTherapy.setVisibility(View.GONE);
               aksiSaatIni = Mode.MODE_DIAGNOSA;
           }else{
               // therapy
               textViewJudulSetOption.setText(getResources().getString(R.string.judul_langsung_therapy));
                linearLayoutTherapy.setVisibility(View.VISIBLE);
               aksiSaatIni = Mode.MODE_LANGSUNG_THERAPY;
           }

        }

        db = new InternalDatabase(this);

        if(!db.getUser().isEmpty()){
            editTextNamaPasien.setText(db.getUser());
        }

        hidingKeyboardOnTherapySpinner();

    }

    private void centerTitle(){
        UIHelper.centerTitle(this);
    }

    private void hidingKeyboardOnTherapySpinner(){
        spinnerTherapy.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextNamaPasien.getWindowToken(), 0);
                return false;
            }
        }) ;
    }

    public void mulaiTimer(View v){

        angkaStop = Integer.parseInt(spinnerDurasiAngka.getSelectedItem().toString());
        satuanStop = spinnerSatuan.getSelectedItem().toString().toLowerCase();

        if(satuanStop.contains("menit")){
            satuanStop = "minute";
        }else{
            satuanStop = "hour";
        }

        db = new InternalDatabase(this);

        namaUser = editTextNamaPasien.getText().toString();
        db.setUser(namaUser);

        if(aksiSaatIni==Mode.MODE_LANGSUNG_THERAPY) {
            db.setTreatment(spinnerTherapy.getSelectedItem().toString());
        }else{

            db.setTreatment("");
        }

        Intent i = new Intent(this, TimerActivity.class);
        i.putExtra("angkaStop", angkaStop);
        i.putExtra("satuanStop", satuanStop);
        i.putExtra("MODE", aksiSaatIni);
        startActivity(i);
    }



}
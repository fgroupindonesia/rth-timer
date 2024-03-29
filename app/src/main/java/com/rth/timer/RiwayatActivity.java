package com.rth.timer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.rth.timer.dbops.DataRiwayat;
import com.rth.timer.dbops.InternalDatabase;
import com.rth.timer.helper.ShowDialog;
import com.rth.timer.helper.UIHelper;

public class RiwayatActivity extends AppCompatActivity {

    InternalDatabase db;
   EditText editTextTextRiwayatTimer;
   Button buttonClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        buttonClear = (Button) findViewById(R.id.buttonClear);
        editTextTextRiwayatTimer = (EditText) findViewById(R.id.editTextTextRiwayatTimer);

        db=  new InternalDatabase(this);

        if(db.getDataHistory() != null){
            // we render it into the editText
            String jsonAll = new Gson().toJson(db.getDataHistory());
            renderData(jsonAll);
        }else{
            buttonClear.setEnabled(false);
            editTextTextRiwayatTimer.setText("no data riwayat from timer yet!");
        }

        centerTitle();

    }

    private void centerTitle(){
        UIHelper.centerTitle(this);
    }

    EditText editText;
    String passEntered;

   private void displayPopupPassRequest(){


           AlertDialog.Builder builder = new AlertDialog.Builder(RiwayatActivity.this);
           builder.setTitle("Password");

           editText = new EditText(this);
           editText.setTransformationMethod(PasswordTransformationMethod.getInstance());

           builder.setView(editText);

           // Set up the buttons
           builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   dialog.dismiss();
                   passEntered = editText.getText().toString();

                   if(db.getPass()!=null){
                       if(db.getPass().equals(passEntered)){
                           allowClear = true;
                           confirmClear();
                       }else{
                           ShowDialog.message(RiwayatActivity.this, "Password missmatch!");
                       }
                   }

               }
           });
           builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
               }
           });

           builder.show();
       }

    boolean allowClear = false;

    public void clearAll(View v){

        if(db.getPass() == null){
            allowClear = true;
        }else{
            // showing the input pass request
            displayPopupPassRequest();
        }

      confirmClear();

    }

    private void confirmClear(){
        if(allowClear) {
            if (db.getDataHistory().length > 0) {

                showDialogYesNoClear();

            }
        }
    }

    private void showDialogYesNoClear(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah anda yakin ingin membersihkan seluruh data riwayat?");

        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                clearAllDataHistory();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    private void clearAllDataHistory(){

            buttonClear.setEnabled(false);
            db.clearDataHistory();
            editTextTextRiwayatTimer.setText("no data riwayat from timer yet!");

    }

    private String humanDate(String computerDate){
        String res = null;

        // computerdate is
        // yyyy-MM-dd HH:mm
        String dataAll [] = computerDate.split(" ");
        String dataTgl [] = dataAll[0].split("-");
        String dataWaktu [] = dataAll[1].split(":");

        res = dataTgl[2] + "/" + dataTgl[1] + "/" + dataTgl[0] + " " + dataAll[1];

        return res;

    }

    private void renderData(String dataJSON){

        // data JSON is in array format
        Gson g = new Gson();
       DataRiwayat dr[] =  g.fromJson(dataJSON, DataRiwayat[].class);

       StringBuffer sbf = new StringBuffer();
       for(int x=dr.length-1; x>-1; x--){

           sbf.append("- " + humanDate(dr[x].getTanggal()) );
           sbf.append("\n");
           if(dr[x].getMode().equalsIgnoreCase("therapy")){
               sbf.append(dr[x].getMode() + " " + dr[x].getTherapy());
           }else{
               sbf.append(dr[x].getMode());
           }

           sbf.append("\n");
           sbf.append("Pasien " + dr[x].getNamaPasien() + " selama " + dr[x].getDurasi());
           sbf.append("\n\n");

       }


        editTextTextRiwayatTimer.setText(sbf.toString());

    }

}
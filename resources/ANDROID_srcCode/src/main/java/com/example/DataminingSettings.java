package com.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.io.File;

/**Datamining Settings page class
 * This activity is called when the user navigates to the Dataming page
 */

public class DataminingSettings extends Activity {

    private String username;

    /*Creates clear data button and toggle data mining button listeners*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datamining_page);
        infoDialog();
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggBtn);
        Boolean status = ((MyApplication) this.getApplication()).getDataminingStatus();
        username = ((MyApplication) this.getApplication()).getUsername();
        toggle.setChecked(status);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setDataminingStatus(true);
                } else {
                    setDataminingStatus(false);
                }
            }
        });
        Button resetData = (Button) findViewById(R.id.resetData);
        resetData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                confirmDialog();
            }
        });
    }

    /*Displays some info about the datamining feature*/
    private void infoDialog(){
        final AlertDialog alertDialog = new AlertDialog.Builder(DataminingSettings.this).create();
        alertDialog.setTitle("Data Mining (BETA)");
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Turning this function on will lead to automated heating and cooling based of classifications found from previous user behaviour and other variables such as the temperature outside. ");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        if(!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }


    /*Shows confirm dialog when delete data clicked*/
    private void confirmDialog(){
        final AlertDialog alertDialog = new AlertDialog.Builder(DataminingSettings.this).create();
        alertDialog.setTitle("Confirm action");
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Are you sure you want to delete data?");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                        deleteArff();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        if(!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    /*Deletes arff file containing saved instances from other sessions*/
    private void deleteArff(){
        File x = new File(getApplicationContext().getFilesDir() + "/"+username+"_trainingSet.arff");
        if (x.exists()) {
            DataMining.GET().initARFF_file(username);
        }
    }

    /*Setter for dataminig status global variable*/
    private void setDataminingStatus(Boolean status){
        ((MyApplication)this.getApplication()).setDataminingStatus(status);
    }

}
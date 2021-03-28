package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.io.File;

public class DataminingSettings extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datamining_page);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggBtn);
        Boolean status = ((MyApplication) this.getApplication()).getDataminingStatus();
        String username = ((MyApplication) this.getApplication()).getUsername();
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
                File x = new File(getApplicationContext().getFilesDir() + "/"+username+"_trainingSet.arff");
                if (x.exists()) {
                    DataMining.GET().initARFF_file(username);
                }
            }
        });
    }

    private void setDataminingStatus(Boolean status){
        ((MyApplication)this.getApplication()).setDataminingStatus(status);
    }

}
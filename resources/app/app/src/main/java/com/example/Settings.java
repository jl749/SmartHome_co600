package com.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Settings extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);
        Button button1 = (Button) findViewById(R.id.deeplearning);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Deeplearning.class);
                startActivity(i);
            }
        });
        Button button2 = (Button) findViewById(R.id.profile);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Profile.class);
                startActivity(i);
            }
        });
        Button button3 = (Button) findViewById(R.id.security);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), SecuritySettings.class);
                startActivity(i);
            }
        });
        Button button4 = (Button) findViewById(R.id.notifications);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Notifications.class);
                startActivity(i);
            }
        });
        Button button5 = (Button) findViewById(R.id.device);
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Device.class);
                startActivity(i);
            }
        });
    }
}

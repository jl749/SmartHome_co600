package com.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class FunctionMenu extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.function_menu);
        ImageButton button1 = (ImageButton) findViewById(R.id.button4);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), TempControl.class);
                startActivity(i);
            }
        });
        ImageButton button2 = (ImageButton) findViewById(R.id.button1);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Security.class);
                startActivity(i);
            }
        });
        ImageButton button3 = (ImageButton) findViewById(R.id.button2);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), SoundControl.class);
                startActivity(i);
            }
        });
        ImageButton button4 = (ImageButton) findViewById(R.id.button7);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Settings.class);
                startActivity(i);
            }
        });
    }
}

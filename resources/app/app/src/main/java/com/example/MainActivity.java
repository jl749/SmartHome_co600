package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private static String pin = "2222";

    final Handler handler = new Handler(Looper.getMainLooper());
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_secreen);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.pin_page);
                pinFunctionality();
            }
        }, 3000);
    }


    public void pinFunctionality() {
        final EditText text1 = (EditText) findViewById(R.id.digit1);
        text1.requestFocus();
        text1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 4){
                    if(text1.getText().toString().equals(pin)){
                        Intent i = new Intent(MainActivity.this, FunctionMenu.class);
                        startActivity(i);
                    }
                    else{
                        text1.setText("");
                        TextView textView1 = (TextView) findViewById(R.id.textView1);
                        textView1.setText("Incorrect pin, please try again");
                    }
                }
            }
        });
    }
}



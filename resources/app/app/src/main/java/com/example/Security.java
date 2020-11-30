package com.example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;

import org.w3c.dom.Text;


public class Security extends Activity {

    float x1,x2,y1,y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_page);
        ImageButton lockButton = (ImageButton) findViewById(R.id.lockbutton);

        lockButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView state = (TextView) findViewById(R.id.currentState1);
                if(state.getText().equals("Currently Unlocked")){
                    ImageView image = (ImageView) findViewById(R.id.currentState);
                    image.setImageResource(R.drawable.locked);
                    state.setText("Currently Locked");
                }

            }
        });

        ImageButton unlockButton = (ImageButton) findViewById(R.id.unlockbutton);

        unlockButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView state = (TextView) findViewById(R.id.currentState1);
                if(state.getText().equals("Currently Locked")){
                    ImageView image = (ImageView) findViewById(R.id.currentState);
                    image.setImageResource(R.drawable.unlocked);
                    state.setText("Currently Unlocked");
                }
            }
        });
    }
    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 > x2) {
                    Intent i = new Intent(Security.this, Security1.class);
                    startActivity(i);

                }
                break;

        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(Security.this, FunctionMenu.class);
        startActivity(i);
    }
}

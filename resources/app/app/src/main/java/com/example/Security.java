package com.example;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;


public class Security extends Activity {

    float x1,x2,y1,y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_page);
        SwitchCompat aSwitch = findViewById(R.id.toggleLight1);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) {
                    TextView state = (TextView) findViewById(R.id.light1Status);
                    state.setText("Light On");
                    setLightOn();
                }
                else {
                    TextView state = (TextView) findViewById(R.id.light1Status);
                    state.setText("Light Off");
                    setLightOff();
                }
            }
        });
        ImageButton lockButton = (ImageButton) findViewById(R.id.lockbutton);

        lockButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView state = (TextView) findViewById(R.id.currentState1);
                if(state.getText().equals("Currently Unlocked")){
                    ImageView image = (ImageView) findViewById(R.id.currentState);
                    image.setImageResource(R.drawable.locked);
                    state.setText("Currently Locked");
                    setClose();
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
                    setOpen();
                }
            }
        });


        if(((MyApplication) this.getApplication()).getLock1().equals("True")){
            TextView state = (TextView) findViewById(R.id.currentState1);
            ImageView image = (ImageView) findViewById(R.id.currentState);
            image.setImageResource(R.drawable.locked);
            state.setText("Currently Locked");
        }
        else{
            TextView state = (TextView) findViewById(R.id.currentState1);
            ImageView image = (ImageView) findViewById(R.id.currentState);
            image.setImageResource(R.drawable.unlocked);
            state.setText("Currently Unlocked");
        }

        if(((MyApplication) this.getApplication()).getLight1().equals("True")){
            aSwitch.setChecked(true);
            TextView state = (TextView) findViewById(R.id.light1Status);
            state.setText("Light On");
        }
        else {
            aSwitch.setChecked(false);
            TextView state = (TextView) findViewById(R.id.light1Status);
            state.setText("Light Off");
        }


    }

    public void setOpen(){
        ((MyApplication) this.getApplication()).setLock1("False");
    }
    public void setClose(){
        ((MyApplication) this.getApplication()).setLock1("True");
    }
    public void setLightOn(){
        ((MyApplication) this.getApplication()).setLight1("True");
    }
    public void setLightOff(){
        ((MyApplication) this.getApplication()).setLight1("False");
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

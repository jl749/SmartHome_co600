package com.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SecuritySettings extends Activity {

    private String m_Text = "";
    private final String currentPin = ((MyApplication)this.getApplication()).getCurrentPin();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_settings);
        Button setPostcode = (Button) findViewById(R.id.changePin);
        setPostcode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                inputCurrentPinDialog("Input Current Pin");
            }
        });
    }

    private void inputCurrentPinDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(message);

        // Set up the input
        final EditText input = new EditText(this);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(4);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        input.setFilters(FilterArray);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                if(!m_Text.isEmpty()) {
                    if(m_Text.equals(currentPin)){
                        inputNewPinDialog();
                    }
                    else{
                        inputCurrentPinDialog("Incorrect Pin, Please Try Again");
                    }
                }
                else{
                    inputCurrentPinDialog("Incorrect Pin, Please Try Again");
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void inputNewPinDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Input New Pin");

        // Set up the input
        final EditText input = new EditText(this);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(4);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        input.setFilters(FilterArray);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                if(!m_Text.isEmpty()) {
                    setNewPin(m_Text);
                }
                else{
                    inputNewPinDialog();
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void setNewPin(String pin){
        ((MyApplication)this.getApplication()).setCurrentPin(pin);
        Context context = getApplicationContext();
        CharSequence text = "Pin Has Been Changed Successfully";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
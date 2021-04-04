package com.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

/**Settings page class
 * This activity is called when the user navigates to the settings page
 */
public class Settings extends Activity {

    private String m_Text = "";
    private String m_Text1 = "";
    private String n_Text = "";
    private  String currentPin;


    /*Sets listeners for each button*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);
        Button button1 = (Button) findViewById(R.id.deeplearning);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), DataminingSettings.class);
                startActivity(i);
            }
        });
        Button button2 = (Button) findViewById(R.id.setPostcode);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                inputDialog();
            }
        });
        Button button3 = (Button) findViewById(R.id.changePin);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                inputCurrentPinDialog("Input Current Pin");
            }
        });
        Button button6 = (Button) findViewById(R.id.logout);
        button6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File x = new File(getApplicationContext().getFilesDir() + "/John98_trainingSet.arff");
                File f = new File(getApplicationContext().getFilesDir() + "/BINARY_DIR.DAT");
                if(f.exists()) {
                    if(x.exists()){
                        System.out.println(x.delete());
                    }
                    boolean deleted = f.delete();
                    Intent i = getBaseContext().getPackageManager().
                            getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(i);
                }
                else{
                    Intent i = getBaseContext().getPackageManager().
                            getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    finish();
                    startActivity(i);
                }
            }
        });
    }

    /*Shows input dialog when user clicks on the change postcode button*/
    private void inputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Input postcode");

        // Set up the input
        final EditText input = new EditText(this);
        input.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                if(!m_Text.isEmpty()) {
                    updatePostCode(m_Text);
                    updateWeather(m_Text);
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

    /*Updates the users postcode in the database*/
    public void updatePostCode(String postCode){
        ((MyApplication)this.getApplication()).setPostCode(postCode);
        String houseID = ((MyApplication)this.getApplication()).getCurrentHouse();
        UpdatePostcode upc = new UpdatePostcode();
        upc.run(houseID,postCode);
        Context context = getApplicationContext();
        CharSequence text = "Postcode Has Been Changed Successfully";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /*Updates the weather api for the new postcode*/
    public void updateWeather(String postCode){
        WeatherAPI wapi = new WeatherAPI();
        wapi.run(((MyApplication) this.getApplication()),postCode);
    }

    /*Shows first input box to confirm current pin when user wants to change pin*/
    private void inputCurrentPinDialog(String message){
        currentPin = ((MyApplication)this.getApplication()).getCurrentPin();
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
                m_Text1 = input.getText().toString();
                if(!m_Text1.isEmpty()) {
                    if(m_Text1.equals(currentPin)){
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

    /*Shows input dialog that is used to set the new pin */
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
                n_Text = input.getText().toString();
                if(n_Text.length() == 4) {
                    setNewPin(n_Text);
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

    /*Saves new pin in binary file*/
    private void setNewPin(String pin){
        System.out.println(pin);
        ((MyApplication)this.getApplication()).setCurrentPin(pin);
        Context context = getApplicationContext();
        CharSequence text = "Pin Has Been Changed Successfully";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        PinFile.writeBinaryOBJ(pin,getApplicationContext());
        toast.show();
    }
}


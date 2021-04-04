package com.example;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PinFile {


    /*Writes a binary file containing users pin.
     * Only when custom pin is created
     */
    public  static void writeBinaryOBJ(String pin, Context c) {
        PinInfo userpin = new PinInfo(pin);
        ObjectOutputStream objOUT=null;
        FileOutputStream fos=null;
        ObjectOutputStream os = null;
        try{
            fos = c.openFileOutput("BINARY_PIN.DAT", Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(userpin);
            os.flush();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{os.close();}catch(IOException e){e.printStackTrace();}
            try{fos.close();}catch(IOException e){e.printStackTrace();}
        }
    }

    public static PinInfo readBinaryOBJ(Context c){
        PinInfo userpin = new PinInfo();
        FileInputStream fis = null;
        ObjectInputStream is = null;
        try{
            fis = c.openFileInput("BINARY_PIN.DAT");
            is = new ObjectInputStream(fis);
            userpin = (PinInfo) is.readObject();
        }catch(Exception e) {
            e.printStackTrace();
        }finally{
            try{is.close();}catch(IOException e){e.printStackTrace();}
            try{fis.close();}catch(IOException e){e.printStackTrace();}
        }
        return userpin;
    }

}

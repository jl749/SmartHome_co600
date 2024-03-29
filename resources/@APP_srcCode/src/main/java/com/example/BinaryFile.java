package com.example;


import android.content.Context;

import java.io.*;



public class BinaryFile{

    /*Writes a binary file containing users info (Username and Password)
    * for ease of login
    */
    public  static void writeBinaryOBJ(String id, String pass, Context c) {
        UserInfo record=new UserInfo(id,pass);
        FileOutputStream fos=null;
        ObjectOutputStream os = null;
        try{
            fos = c.openFileOutput("BINARY_DIR.DAT", Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(record);
            os.flush();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{os.close();}catch(IOException e){e.printStackTrace();}
            try{fos.close();}catch(IOException e){e.printStackTrace();}
        }
    }

    /*Reads the binary file when user logs in*/
    public static UserInfo readBinaryOBJ(Context c){
        UserInfo record=new UserInfo();
        FileInputStream fis = null;
        ObjectInputStream is = null;
        try{
            fis = c.openFileInput("BINARY_DIR.DAT");
            is = new ObjectInputStream(fis);
            record = (UserInfo) is.readObject();
        }catch(Exception e) {
            e.printStackTrace();
        }finally{
            try{is.close();}catch(IOException e){e.printStackTrace();}
            try{fis.close();}catch(IOException e){e.printStackTrace();}
        }
        return record;
    }
}


package com.example;

import java.io.Serializable;

/*Class that sets and gets the pin file binary object*/
public class PinInfo implements Serializable {
    private static final long serialVersionUID=1L;
    private String pin;

    public PinInfo(String pin){
        this.pin=pin;

    }
    public PinInfo(){}

    public String getPin(){return this.pin;}
    public void setPin(String pin){this.pin=pin;}

}

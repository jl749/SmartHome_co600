package com.example;

import java.io.Serializable;

/*Class that sets and gets the user info file binary object*/
public class UserInfo implements Serializable {
    private static final long serialVersionUID=1L;
    private String id;
    private String pass;

    //constructor
    public UserInfo(String id,String pass){
        this.id=id;
        this.pass=pass;
    }
    public UserInfo(){}

    //getter
    public String getID(){return this.id;}
    public String getPass(){return this.pass;}
    //setter
    public void setID(String id){this.id=id;}
    public void setPass(String pass){this.pass=pass;}

    @Override
    public String toString() {
        return "UserInfo [id=" + this.id + ", pass=" + this.pass + "]";
    }
}

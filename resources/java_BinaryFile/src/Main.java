import java.io.*;

public class Main {
    private static final String BINARY_DIR = "./raw/binaryLogin.dat";

    public static void writeBinaryOBJ(String id, String pass) {
        UserInfo record=new UserInfo(id,pass);
        ObjectOutputStream objOUT=null;
        try{
            new File("./raw").mkdir();
            objOUT=new ObjectOutputStream(new FileOutputStream(BINARY_DIR));
            objOUT.writeObject(record);
            objOUT.flush();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{objOUT.close();}catch(IOException e){e.printStackTrace();}
        }
    }
    public static UserInfo readBinaryOBJ(){
        UserInfo record=new UserInfo();
        ObjectInputStream objINPUT=null;
        try{
            objINPUT = new ObjectInputStream(new FileInputStream(BINARY_DIR));
            record = (UserInfo) objINPUT.readObject();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try{objINPUT.close();}catch(IOException e){e.printStackTrace();}
        }
        return record;
    }
    public static void main(String[] args) {
        writeBinaryOBJ("a","b");
        UserInfo x=readBinaryOBJ();
        System.out.println(x.getID());
        System.out.println(x.getPass());
    }
}

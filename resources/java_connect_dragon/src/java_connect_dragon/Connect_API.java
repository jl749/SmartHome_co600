package java_connect_dragon;

import java.util.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.sql.*;

public class Connect_API {
	final String url = "jdbc:mysql://dragon.kent.ac.uk:3306/jl749"; //dragon.kent.ac.uk
    final String user = "jl749"; 
    final String pass = "p@ssword1";
    
    Connection con=null;
    
	public Connect_API() {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver()); 
            
            con=DriverManager.getConnection(url,user,pass);
            System.out.println("[mysql connection]....");

            System.out.println("Connection successful");
        }catch(Exception e){
        	closeCon();
            System.err.println(e);
        }
	}
	
	
	
	public void closeCon() {
		try{con.close();}catch(SQLException e){System.err.println(e);}
	}

	private byte[] getSHA(String input) throws NoSuchAlgorithmException 
    {  
        // Static getInstance method is called with hashing SHA  
        MessageDigest md = MessageDigest.getInstance("SHA-256");  
  
        // digest() method called  
        // to calculate message digest of an input  
        // and return array of byte 
        return md.digest(input.getBytes(StandardCharsets.UTF_8));  
    } 
    private String toHexString(byte[] hash) 
    { 
        // Convert byte array into signum representation  
        BigInteger number = new BigInteger(1, hash);  
  
        // Convert message digest into hex value  
        StringBuilder hexString = new StringBuilder(number.toString(16));  
  
        // Pad with leading zeros 
        while (hexString.length() < 32)  
        {  
            hexString.insert(0, '0');  
        }  
  
        return hexString.toString();  
    } 
    
    public void fixIntegrity(String[][] arr) {
    	List<List<Integer>> result=new ArrayList<>();
    	for(int i=0;i<arr.length;i++) 
    		for(int j=1;j<arr.length;j++) 
    			if(arr[i][0].equals(arr[(i+j)%arr.length][1])) {
    				List<Integer> tmp=new ArrayList<>();
    				tmp.add(i);
    				tmp.add((i+j)%arr.length);
    				result.add(tmp);
        		}
    	//NOW CONNECT RESULTS
    }
    public boolean integrityCheck() {
    	int linkCount=0;
    	int size=0;
    	try {
    		PreparedStatement pstmt = con.prepareStatement("select * from User_Info");
            ResultSet rs=pstmt.executeQuery();
            
            if (rs!=null) {
              rs.last();    // moves cursor to the last row
              size=rs.getRow(); // get row id 
            }
            
            int index=0;
            String[][] arr=new String[size][2]; //[0]=instance hashvalue,	[1]=prevHash
            while(rs.next()) {
            	String tmp="";
            	tmp+=rs.getString("Username");
            	tmp+=rs.getString("Password");
            	tmp+=rs.getString("FirstName");
            	tmp+=rs.getString("Surname");
            	tmp+=rs.getString("RegTime");
            	
            	arr[index][0]=toHexString(getSHA(tmp));
            	arr[index++][1]=rs.getString("PrevHash");
            }
            
            List<Integer> dupIndex=new ArrayList<>();
            for(int i=0;i<size;i++) {
            	int matchCount=0;
            	for(int j=0;j<size;j++) {
            		if(i==j || dupIndex.contains(j)) //skip itself and already connected nodes
            			continue;
            		if(arr[i][0].equals(arr[j][1])) {
            			dupIndex.add(i);
            			linkCount++;
            			matchCount++;
            		}
            	}
            	if(matchCount>1) //if node linked more than once
            		return false;
            }
    	}catch(Exception e){
    		closeCon();
    		System.err.println(e);
    	}
    	if(linkCount==size-1)
    		return true;
    	return false;
    }
}


//import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class Main {
	private static final String raptor="https://raptor.kent.ac.uk/~jl749/";
	
	public static boolean chkLogin(String id, String pass) {
		StringBuilder result=new StringBuilder();
		
		HttpsURLConnection https=null;
		OutputStream out=null;
		InputStreamReader in=null;
		BufferedReader reader=null;
		try {
			URL url = new URL(raptor+"chkLogin.php");
			String msg="id="+id+"&pass="+pass;
			byte[] postDataBytes=msg.getBytes("UTF-8");
			
			URLConnection con = url.openConnection();
			https = (HttpsURLConnection)con;
			https.setRequestMethod("POST");
			https.setConnectTimeout(500);
			https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			https.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			https.setDoOutput(true);
			out=https.getOutputStream();
			out.write(postDataBytes);
			
			in=new InputStreamReader(https.getInputStream(),"UTF-8");
			reader=new BufferedReader(in);
			result.append(reader.readLine());
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try{reader.close();}catch(Exception e) {e.printStackTrace();}
			try{in.close();}catch(Exception e) {e.printStackTrace();}
			try{out.close();}catch(Exception e) {e.printStackTrace();}
			try{https.disconnect();}catch(Exception e) {e.printStackTrace();}
		}
		System.out.println(result.toString()+"!");
		if(result.toString().equals("True")) 
			return true;
		else 
			return false;
	}
	
	public static void updateTMP(double val,int houseID) {
		HttpsURLConnection https=null;
		OutputStream out=null;
		InputStreamReader in=null;
		BufferedReader reader=null;
		try {
			URL url = new URL(raptor+"updateThreshold.php");
			String msg="tmp="+val+"&houseID="+houseID;
			byte[] postDataBytes=msg.getBytes("UTF-8");
			
			URLConnection con = url.openConnection();
			https = (HttpsURLConnection)con;
			https.setRequestMethod("POST");
			https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			https.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			https.setDoOutput(true);
			out=https.getOutputStream();
			out.write(postDataBytes);
			
			in=new InputStreamReader(https.getInputStream(),"UTF-8");
			reader=new BufferedReader(in);
			System.out.println(reader.readLine()+"!!!!");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try{reader.close();}catch(Exception e) {e.printStackTrace();}
			try{in.close();}catch(Exception e) {e.printStackTrace();}
			try{out.close();}catch(Exception e) {e.printStackTrace();}
			try{https.disconnect();}catch(Exception e) {e.printStackTrace();}
		}
	}
	
	public static void updateIntruder(boolean b,int houseID) {
		HttpsURLConnection https=null;
		OutputStream out=null;
		InputStreamReader in=null;
		BufferedReader reader=null;
		try {
			URL url = new URL(raptor+"updateThreshold.php");
			int val=(b) ? 1 : 0;
			String msg="intruder="+val+"&houseID="+houseID;
			byte[] postDataBytes=msg.getBytes("UTF-8");
			
			URLConnection con = url.openConnection();
			https = (HttpsURLConnection)con;
			https.setRequestMethod("POST");
			https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			https.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			https.setDoOutput(true);
			out=https.getOutputStream();
			out.write(postDataBytes);
			
			in=new InputStreamReader(https.getInputStream(),"UTF-8");
			reader=new BufferedReader(in);
			System.out.println(reader.readLine()+"!!!!");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try{reader.close();}catch(Exception e) {e.printStackTrace();}
			try{in.close();}catch(Exception e) {e.printStackTrace();}
			try{out.close();}catch(Exception e) {e.printStackTrace();}
			try{https.disconnect();}catch(Exception e) {e.printStackTrace();}
		}
	}
	
	public static List<String> getHouseReg(String username) {
		List<String> result=new ArrayList<>();
		
		HttpsURLConnection https=null;
		OutputStream out=null;
		InputStreamReader in=null;
		BufferedReader reader=null;
		try {
			URL url = new URL(raptor+"getHouseRegistered.php");
			String msg="username="+username;
			byte[] postDataBytes=msg.getBytes("UTF-8");
			
			URLConnection con = url.openConnection();
			https = (HttpsURLConnection)con;
			https.setRequestMethod("POST");
			https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			https.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			https.setDoOutput(true);
			out=https.getOutputStream();
			out.write(postDataBytes);
			
			String line;
			in=new InputStreamReader(https.getInputStream(),"UTF-8");
			reader=new BufferedReader(in);
			while((line=reader.readLine())!=null) {
				System.out.println(line+"!!");
				result.add(line);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try{reader.close();}catch(Exception e) {e.printStackTrace();}
			try{in.close();}catch(Exception e) {e.printStackTrace();}
			try{out.close();}catch(Exception e) {e.printStackTrace();}
			try{https.disconnect();}catch(Exception e) {e.printStackTrace();}
		}
		return result;
	}
	
	public static void main(String[] args) {
		chkLogin("John98","19513FDC9DA4FB72A4A05EB66917548D3C90FF94D5419E1F2363EEA89DFEE1DD");
		updateTMP(30,1234);
		updateIntruder(true,1234);
		System.out.println(getHouseReg("John98"));
	}
}

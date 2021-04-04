//import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import javax.net.ssl.HttpsURLConnection;


public class Main {
	private static final String raptor="https://raptor.kent.ac.uk/~jl749/";
	//private static final String raptor="https://www.cs.kent.ac.uk/people/staff/ds710/co600/";
	
	public static boolean chkLogin(String id, String pass) {
		StringBuilder result=new StringBuilder();
		
		HttpURLConnection http=null;
		OutputStream out=null;
		InputStreamReader in=null;
		BufferedReader reader=null;
		try {
			URL url = new URL(raptor+"chkLogin.php");
			String msg="id="+id+"&pass="+pass;
			byte[] postDataBytes=msg.getBytes("UTF-8");
			
			URLConnection con = url.openConnection();
			http = (HttpURLConnection)con;
			http.setRequestMethod("POST");
			http.setConnectTimeout(500);
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			http.setDoOutput(true);
			out=http.getOutputStream();
			out.write(postDataBytes);
			
			in=new InputStreamReader(http.getInputStream(),"UTF-8");
			reader=new BufferedReader(in);
			result.append(reader.readLine());
			//System.out.println(reader.readLine());
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try{reader.close();}catch(Exception e) {e.printStackTrace();}
			try{in.close();}catch(Exception e) {e.printStackTrace();}
			try{out.close();}catch(Exception e) {e.printStackTrace();}
			try{http.disconnect();}catch(Exception e) {e.printStackTrace();}
		}
		System.out.println(result.toString()+"!");
		if(result.toString().equals("True")) 
			return true;
		else 
			return false;
	}
	
	public static void updateTMP(double val,int houseID) {
		HttpURLConnection http=null;
		OutputStream out=null;
		InputStreamReader in=null;
		BufferedReader reader=null;
		try {
			URL url = new URL(raptor+"updateThreshold.php");
			String msg="tmp="+val+"&houseID="+houseID;
			byte[] postDataBytes=msg.getBytes("UTF-8");
			
			URLConnection con = url.openConnection();
			http = (HttpURLConnection)con;
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			http.setDoOutput(true);
			out=http.getOutputStream();
			out.write(postDataBytes);
			
			in=new InputStreamReader(http.getInputStream(),"UTF-8");
			reader=new BufferedReader(in);
			System.out.println(reader.readLine()+"!!!!");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try{reader.close();}catch(Exception e) {e.printStackTrace();}
			try{in.close();}catch(Exception e) {e.printStackTrace();}
			try{out.close();}catch(Exception e) {e.printStackTrace();}
			try{http.disconnect();}catch(Exception e) {e.printStackTrace();}
		}
	}
	
	public static void updateIntruder(boolean b,int houseID) {
		HttpURLConnection http=null;
		OutputStream out=null;
		InputStreamReader in=null;
		BufferedReader reader=null;
		try {
			URL url = new URL(raptor+"updateThreshold.php");
			int val=(b) ? 1 : 0;
			String msg="intruder="+val+"&houseID="+houseID;
			byte[] postDataBytes=msg.getBytes("UTF-8");
			
			URLConnection con = url.openConnection();
			http = (HttpURLConnection)con;
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			http.setDoOutput(true);
			out=http.getOutputStream();
			out.write(postDataBytes);
			
			in=new InputStreamReader(http.getInputStream(),"UTF-8");
			reader=new BufferedReader(in);
			System.out.println(reader.readLine()+"!!!!");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try{reader.close();}catch(Exception e) {e.printStackTrace();}
			try{in.close();}catch(Exception e) {e.printStackTrace();}
			try{out.close();}catch(Exception e) {e.printStackTrace();}
			try{http.disconnect();}catch(Exception e) {e.printStackTrace();}
		}
	}
	
	public static List<String> getHouseReg(String username) {
		List<String> result=new ArrayList<>();
		
		HttpURLConnection http=null;
		OutputStream out=null;
		InputStreamReader in=null;
		BufferedReader reader=null;
		try {
			URL url = new URL(raptor+"getHouseRegistered.php");
			String msg="username="+username;
			byte[] postDataBytes=msg.getBytes("UTF-8");
			
			URLConnection con = url.openConnection();
			http = (HttpURLConnection)con;
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			http.setDoOutput(true);
			out=http.getOutputStream();
			out.write(postDataBytes);
			
			String line;
			in=new InputStreamReader(http.getInputStream(),"UTF-8");
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
			try{http.disconnect();}catch(Exception e) {e.printStackTrace();}
		}
		return result;
	}
	
	public static Map<String, String> getThresholds(String houseID) {
		Map<String, String> result=new HashMap<>();
		
		HttpURLConnection http=null;
		OutputStream out=null;
		InputStreamReader in=null;
		BufferedReader reader=null;
		try {
			URL url = new URL(raptor+"getThreshold.php");
			String msg="houseID="+houseID;
			byte[] postDataBytes=msg.getBytes("UTF-8");
			
			URLConnection con = url.openConnection();
			http = (HttpURLConnection)con;
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			http.setDoOutput(true);
			out=http.getOutputStream();
			out.write(postDataBytes);
			
			String line;
			in=new InputStreamReader(http.getInputStream(),"UTF-8");
			reader=new BufferedReader(in);
			while((line=reader.readLine())!=null) {
				System.out.println(line+"!!");
				String tmp[]=line.split("=");
				result.put(tmp[0],tmp[1]);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try{reader.close();}catch(Exception e) {e.printStackTrace();}
			try{in.close();}catch(Exception e) {e.printStackTrace();}
			try{out.close();}catch(Exception e) {e.printStackTrace();}
			try{http.disconnect();}catch(Exception e) {e.printStackTrace();}
		}
		return result;
	}
	
	public static String getAPIKey(String houseID) {
		String apiKey=null;
		HttpURLConnection http=null;
		OutputStream out=null;
		InputStreamReader in=null;
		BufferedReader reader=null;
		try {
			URL url = new URL(raptor+"getAPIKey.php");
			String msg="houseID="+houseID;
			byte[] postDataBytes=msg.getBytes("UTF-8");
			
			URLConnection con = url.openConnection();
			http = (HttpURLConnection)con;
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			http.setDoOutput(true);
			http.setDoInput(true);
			out=http.getOutputStream();
			out.write(postDataBytes);
			
			in=new InputStreamReader(http.getInputStream(),"UTF-8");
			reader=new BufferedReader(in);
			apiKey=reader.readLine();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try{reader.close();}catch(Exception e) {e.printStackTrace();}
			try{in.close();}catch(Exception e) {e.printStackTrace();}
			try{out.close();}catch(Exception e) {e.printStackTrace();}
			try{http.disconnect();}catch(Exception e) {e.printStackTrace();}
		}
		return apiKey;
	}
	
	public static String getPOSTCODE(String houseID) {
		String postCode=null;
		HttpURLConnection http=null;
		OutputStream out=null;
		InputStreamReader in=null;
		BufferedReader reader=null;
		try {
			URL url = new URL(raptor+"getPOSTCODE.php");
			String msg="houseID="+houseID;
			byte[] postDataBytes=msg.getBytes("UTF-8");
			
			URLConnection con = url.openConnection();
			http = (HttpURLConnection)con;
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			http.setDoOutput(true);
			http.setDoInput(true);
			out=http.getOutputStream();
			out.write(postDataBytes);
			
			in=new InputStreamReader(http.getInputStream(),"UTF-8");
			reader=new BufferedReader(in);
			postCode=reader.readLine();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try{reader.close();}catch(Exception e) {e.printStackTrace();}
			try{in.close();}catch(Exception e) {e.printStackTrace();}
			try{out.close();}catch(Exception e) {e.printStackTrace();}
			try{http.disconnect();}catch(Exception e) {e.printStackTrace();}
		}
		return postCode;
	}
	
	public static void updatePOSTCODE(String houseID, String postCode) {
		HttpURLConnection http=null;
		OutputStream out=null;
		InputStreamReader in=null;
		BufferedReader reader=null;
		try {
			URL url = new URL(raptor+"updatePOSTCODE.php");
			String msg="houseID="+houseID+"&PostCode="+postCode;
			byte[] postDataBytes=msg.getBytes("UTF-8");
			
			URLConnection con = url.openConnection();
			http = (HttpURLConnection)con;
			http.setRequestMethod("POST");
			http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			http.setDoOutput(true);
			http.setDoInput(true);
			out=http.getOutputStream();
			out.write(postDataBytes);
			
			in=new InputStreamReader(http.getInputStream(),"UTF-8");
			reader=new BufferedReader(in);
			System.out.println(reader.readLine());
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try{reader.close();}catch(Exception e) {e.printStackTrace();}
			try{in.close();}catch(Exception e) {e.printStackTrace();}
			try{out.close();}catch(Exception e) {e.printStackTrace();}
			try{http.disconnect();}catch(Exception e) {e.printStackTrace();}
		}
	}
	
	public static void main(String[] args) {
		//chkLogin("John98","19513FDC9DA4FB72A4A05EB66917548D3C90FF94D5419E1F2363EEA89DFEE1DD");
		//updateTMP(30,1234);
		//updateIntruder(true,1234);
		//System.out.println(getHouseReg("John98"));
		//System.out.println(getThresholds("1234"));
		
		System.out.println(getPOSTCODE("1234"));
		updatePOSTCODE("1234","ME7 1RF");
		System.out.println(getPOSTCODE("1234"));
	}
}

package java_connect_dragon;
import java.time.LocalDateTime;

public class Node {
	LocalDateTime timeStamp;
	String name;	String pass;
	String prevHash;
	
	public Node(LocalDateTime timeStamp,String name,String pass,String prevHash){
		this.timeStamp=timeStamp;
		this.name=name;
		this.pass=pass;
		this.prevHash=prevHash;
	}
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
	public String getName() {
		return name;
	}
	public String getPrevHash() {
		return prevHash;
	}
}

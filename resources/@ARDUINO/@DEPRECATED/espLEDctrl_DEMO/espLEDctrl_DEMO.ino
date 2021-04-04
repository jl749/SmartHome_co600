#include <SoftwareSerial.h>
#define DEBUG true

#define BT_RXD 2
#define BT_TXD 3
SoftwareSerial ESP_wifi(BT_RXD,BT_TXD); //esp TX -> 2 , esp RX -> 3

void setup() {
  Serial.begin(9600);
  ESP_wifi.begin(9600);

  pinMode(11,OUTPUT); digitalWrite(11,LOW);
  pinMode(12,OUTPUT); digitalWrite(12,LOW);
  pinMode(13,OUTPUT); digitalWrite(13,LOW);

  sendData("AT+RST\r\n",2000,DEBUG); // reset module
  sendData("AT+CIOBAUD?\r\n",2000,DEBUG); // check baudrate (redundant)
  sendData("AT+CWMODE=3\r\n",1000,DEBUG); // configure as access point (working mode: AP+STA)
  sendData("AT+CWLAP\r\n",3000,DEBUG); // list available access points
  sendData("AT+CWJAP=\"AP NAME\",\"AP PASSWORD\"\r\n",5000,DEBUG); // join the access point
  sendData("AT+CIFSR\r\n",1000,DEBUG); // get ip address
  sendData("AT+CIPMUX=1\r\n",1000,DEBUG); // configure for multiple connections
  sendData("AT+CIPSERVER=1,80\r\n",1000,DEBUG); // turn on server on port 80
}

void loop() {
  if(ESP_wifi.available()){
    if(ESP_wifi.find("+IPD,")){ //move pointer hence it can get "id,len:GET /data"
      delay(1000); //serial buffer to fill up
      int connectionID=ESP_wifi.read()-48; //read char (-48 to convert to int)
      
      ESP_wifi.find("pin=");  //192.168.4.1/pin=1
      int pinN=(ESP_wifi.read()-48)*10; //tens unit
      pinN+=(ESP_wifi.read()-48);
      digitalWrite(pinN,!digitalRead(pinN)); //led switch

      /*http close*/
      String str="AT+CIPCLOSE=";
      str+=(connectionID+"\r\n");
      sendData(str,1000,DEBUG);
    }
  }
}

String sendData(String str,const int timeout,boolean debug){
  String response="";
  ESP_wifi.print(str); //send ASCII by print, write for binary data
  long int t=millis(); //current hardware clock

  while((t+timeout)>millis()){ //during timeout
    while(ESP_wifi.available()){ //while buffer not empty
      char c=ESP_wifi.read(); //read buffer
      response+=c;
    }
  }

  if(debug)
    Serial.print(response);
  return response;
}

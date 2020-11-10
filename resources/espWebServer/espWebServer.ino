#include "WiFiEsp.h"
#include <SoftwareSerial.h>

#define FV(s) ((const __FlashStringHelper*)(s))
#define TXPIN 2
#define RXPIN 3
SoftwareSerial esp01(TXPIN,RXPIN); //esp TX -> 2 , esp RX -> 3

const char ssid[]="BT-FFA25W"; //read straight from flash memory
const char pass[]="cATY9hbJUC4GQi";
int status=WL_IDLE_STATUS; //wifi radio's status

#define ledPin 13
#define digiPin 7

WiFiEspServer server(80);

#define ANLpins 3
const int ANLpinsInUse[ANLpins]={0,1,2};

void setup() {
  pinMode(ledPin,OUTPUT);
  pinMode(digiPin,OUTPUT);
  digitalWrite(ledPin,LOW);
  digitalWrite(digiPin,LOW);
  Serial.begin(9600);
  esp01.begin(9600);
  WiFi.init(&esp01); //init ESP module
  while(status!=WL_CONNECTED){
    Serial.print(F("Attempting to connect ")); //F to save SRAM
    Serial.println(ssid);
    status=WiFi.begin(ssid,pass); //connect
    IPAddress ip=WiFi.localIP();
    Serial.print("IP Address: ");
    Serial.println(ip);
  }
  server.begin();
}

void loop() {
  WiFiEspClient client=server.available(); //listen incoming clients
  if(client){ //when client detected
    while(client.connected()){
      if(client.available()){ //while someting in buffer
        /**
         * when end of line('\n') is an empty line
         * http request finished
         * send response back
        */
        String income_AP=client.readStringUntil('\n');
        client.flush();
        /*standard http response header*/
        client.println(F("HTTP/1.1 200 OK"));
        client.println(F("Content-Type: text/html"));
        client.println();

        for(int i=0;i<ANLpins;i++){
          client.print(F("analog input "));
          client.print(ANLpinsInUse[i]+": ");
          client.print(analogRead(ANLpinsInUse[i]));
          client.println(F("<br />"));
        }
      }
    }
    delay(1); //wait until web browser recieve all data
    client.stop();
    Serial.println(F("Client disconnected"));
  }
}

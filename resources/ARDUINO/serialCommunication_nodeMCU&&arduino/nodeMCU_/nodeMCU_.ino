#include <ESP8266WiFi.h>
#include <WiFiClientSecure.h>
const char* ssid = "BT-FFA25W";
const char* pass = "cATY9hbJUC4GQi";
const char* houseID = "1234";
const char* phpHost = "www.cs.kent.ac.uk/people/staff/ds710/co600/getThreshold.php";
//"api.pushbullet.com";
const char* fingerPRINT = "CA 84 CE 0F 2D 1D 24 F1 44 D7 F1 24 45 64 36 DF 29 9F 9E A7";

WiFiClient client;
WiFiClientSecure secureClient;
WiFiServer server(80);

#include <SoftwareSerial.h>
SoftwareSerial s(D6,D5);

#include <ArduinoJson.h>
StaticJsonDocument<200> doc;
int tmp,hum,air_quality;
bool led1,led2,ALARM,motion=false;

const long interval = 2000;
unsigned long previousMillis = 0;
unsigned char count=0;

void setup() {
  // Initialize Serial port
  Serial.begin(9600);
  s.begin(9600);
  while(!Serial) continue;

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, pass);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connecting to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
  server.begin();
  Serial.println("Server started");

  secureClient.setInsecure();
}

void loop() {
  unsigned long currentMillis = millis();
  if(currentMillis - previousMillis >= interval){
    previousMillis = currentMillis;
    count++;
    if(s.available()==0) getJSON();
  }
  
  if(count >= 5){
    count=0;
    String data="houseID=";
    data+=houseID;
    httpsPOST(phpHost, data);
  }
  webServer();
}

void httpsPOST(String url, String data){
  if(secureClient.connect(phpHost, 443)){
    Serial.println(data);
    secureClient.println("POST " + url + " HTTP/1.1");
    secureClient.println("Host: " + (String)phpHost);
    secureClient.println("User-Agent: ESP8266/1.0");
    secureClient.println("Connection: close");
    secureClient.println("Content-Type: application/x-www-form-urlencoded;");
    secureClient.print("Content-Length: ");
    secureClient.println(data.length());
    secureClient.println();
    secureClient.println(data);
    delay(100);
    String response = secureClient.readString();
    Serial.println(response);
  }else {
    Serial.println("ERROR");
  }
}

void getJSON(){
  s.println(F("JSON")); delay(3000);
  DeserializationError err = deserializeJson(doc, s);
  if (err == DeserializationError::Ok){
    //Print the values
    Serial.print(F("air_quality = "));
    Serial.println(air_quality=doc["air_quality"].as<double>());
    Serial.print(F("tmp = "));
    Serial.println(tmp=doc["tmp"].as<double>());
    Serial.print(F("hum = "));
    Serial.println(hum=doc["hum"].as<double>());
    Serial.print(F("LED1 = "));
    Serial.println(led1=doc["LED1"].as<bool>());
    Serial.print(F("LED2 = "));
    Serial.println(led2=doc["LED2"].as<bool>());
    Serial.println(F("---------------------xxxxx--------------------"));
  }else{
    // Print error to the "debug" serial port
    Serial.print("deserializeJson() returned ");
    Serial.println(err.c_str());
    while (s.available() > 0)
      s.read();
  }
}

void webServer(){
  client = server.available();
  if(!client) return;
 
  Serial.print(F("<new client>"));
  client.setTimeout(5000);
 
  String request = client.readStringUntil('\n');
  Serial.print(F("request: "));
  Serial.println(request);
 
  if(request.indexOf(F("LED2/1"))!=-1){
    Serial.println(F("LED2 on"));
    s.println(F("LED2/1"));
  }else if(request.indexOf(F("LED2/0"))!=-1){
    Serial.println(F("LED2 off"));
    s.println(F("LED2/0"));
  }else if(request.indexOf(F("LED1/1"))!=-1){
    Serial.println(F("LED1 on"));
    s.println(F("LED1/1"));
  }else if(request.indexOf(F("LED1/0"))!=-1){
    Serial.println(F("LED1 off"));
    s.println(F("LED1/0"));
  }
  client.flush();
 
  client.println(F("HTTP/1.1 200 OK"));
  client.println(F("Content-Type: text/html"));
  client.println();
  client.println(F("<html>"));
  client.println(F("<body>"));
  client.print(F("Temperature="));
  client.print(tmp);
  client.print(F("<br>Humidity="));
  client.print(hum);
  client.println(F("<br>LED1="));
  client.print(led1);
  client.println(F("<br>LED2="));
  client.print(led2);
  client.println(F("<br>ALARM="));
  client.print(ALARM);
  client.println(F("</body></html>"));

  delay(1000); //wait until web browser recieve all data
}

#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
const char* ssid = "BT-FFA25W";
const char* pass = "cATY9hbJUC4GQi";
const char* houseID = "1234";
String phpHost = "raptor.kent.ac.uk/~jl749/getThreshold.php";

WiFiClient client;
HTTPClient http;
WiFiServer server(80);

#include <SoftwareSerial.h>
SoftwareSerial s(D6,D5);

#include <ArduinoJson.h>
StaticJsonBuffer<1000> jsonBuffer;
double tmp,hum,air_quality;
bool led1,led2=false;

const long interval = 20000;
unsigned long previousMillis = 0;

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
}

void loop() {
  getJSON();

  unsigned long currentMillis = millis();
  if(currentMillis - previousMillis >= interval){
    previousMillis = currentMillis;
    http.begin(client, phpHost);
    http.setTimeout(1000);
    http.addHeader("Content-Type","text/plain");
    int httpCode = http.GET();
   
    if(httpCode > 0) {
      Serial.printf("GET code : %d\n\n", httpCode);
 
      if(httpCode == HTTP_CODE_OK) {
        String payload = http.getString();
        Serial.println(payload);
        //s.println("LED1/1");
      }
    } 
    else
      Serial.printf("GET failed, error: %s\n", http.errorToString(httpCode).c_str());
    
    http.end();
  }

  webServer();
}

void getJSON(){ //get sensor values from arduino
  if(s.available()>0){ //bytes in buffer
    JsonObject& root = jsonBuffer.parseObject(s);
    if (root == JsonObject::invalid())
      return;

    tmp = root["tmp"];
    hum = root["hum"];
    air_quality = root["air_quality"];
    led1 = root["LED1"];
    led2 = root["LED2"];
    Serial.println("JSON received and parsed");
    root.prettyPrintTo(Serial);
    Serial.println("---------------------xxxxx--------------------");
  }
}

void webServer(){
  client = server.available();
  if(!client) return;
 
  Serial.println("<new client>");
  client.setTimeout(5000);
 
  String request = client.readStringUntil('\n');
  Serial.println("request: ");
  Serial.println(request);
 
  if(request.indexOf(F("LED2/1"))!=-1){
    Serial.println(F("LED2 on"));
    s.println("LED2/1");
  }else if(request.indexOf(F("LED2/0"))!=-1){
    Serial.println(F("LED2 off"));
    s.println("LED2/0");
  }else if(request.indexOf(F("LED1/1"))!=-1){
    Serial.println(F("LED1 on"));
    s.println("LED1/1");
  }else if(request.indexOf(F("LED1/0"))!=-1){
    Serial.println(F("LED1 off"));
    s.println("LED1/0");
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
  client.println(F("</body></html>"));

  delay(1000); //wait until web browser recieve all data
}

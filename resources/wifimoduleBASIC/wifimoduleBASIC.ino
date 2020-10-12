#include <SoftwareSerial.h>

#define BT_RXD 2
#define BT_TXD 3
SoftwareSerial ESP_wifi(BT_RXD,BT_TXD); //esp TX -> 2 , esp RX -> 3

void setup() {
  Serial.begin(9600);
  ESP_wifi.begin(9600);
  ESP_wifi.setTimeout(5000); //5sec
  delay(1000);
}

void loop() {
  if(Serial.available()) //when input buff available send ESP
    ESP_wifi.write(Serial.read());
  if(ESP_wifi.available()) //when available write on serial port
    Serial.write(ESP_wifi.read());
}

void(* resetFunc) (void) = 0;


#include <ESP8266WiFi.h>
#include "DHTesp.h"
DHTesp dht;
#ifndef STASSID
#define STASSID "Akira"
#define STAPSK "Cubin159753"
#endif

const char* ssid = STASSID;
const char* password = STAPSK;
int time_delay;
const char* host = "192.168.1.9";
const uint16_t port = 81;
WiFiClient client;

void setup() {
  Serial.begin(115200);

  // We start by connecting to a WiFi network

  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  /* Explicitly set the ESP8266 to be a WiFi-client, otherwise, it by default,
     would try to act as both a client and an access-point and could cause
     network-issues with your other WiFi-devices on your WiFi-network. */
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  Serial.begin(115200);
  bool connect_failed = false;
  dht.setup(D2, DHTesp::DHT22);
   if (!client.connect(host, port)) {
    Serial.println("connection failed");
    connect_failed=true;
    return;
  }
  client.isKeepAliveEnabled();
  client.setTimeout(10000);
  Serial.print("Set time out: ");
  Serial.println(client.getTimeout());
  time_delay=1000;


}

void loop() {
  if (!client.connected()){
    Serial.println("Lost connection, try reconnect ");
    client.connect(host,port);
    delay(5000);
    time_delay = 1000;
    return;
  }
  float temp = dht.getTemperature();
  float humidity = dht.getHumidity();
  // Serial.print("\nhumidity: ");
  // Serial.println(humidity);
  // Serial.print("temperature: ");
  // Serial.println(temp);
  // Serial.print("\n");

  byte* bytePointer = (byte*) &temp;

  for (int i = 0; i <=3; i++) {
    client.write(bytePointer[i]);
  }
  bytePointer = (byte*) &humidity;
  for(int i = 0 ; i<=3;i++){
    client.write(bytePointer[i]);
  }

    unsigned char *buffer = new uint8[1];

    client.readBytes(buffer,1);

    bool is_make_changes = *((bool*) buffer);

    if(is_make_changes){
      unsigned char *buffer2 = new byte[4];
      client.readBytes(buffer2,2);
      int new_time_delay = *((int*) buffer2);
      if (new_time_delay == 0 || new_time_delay>99999999){
        Serial.println("reset call");
        resetFunc();
      }
      int status_code = 200;

      unsigned char *status_pointer = (uint8*) &status_code;
      client.write(status_pointer,2);
      
      Serial.print("new time delay: ");
      time_delay = new_time_delay;
      Serial.println(new_time_delay);

    }

  delay(time_delay);
  client.flush();
}




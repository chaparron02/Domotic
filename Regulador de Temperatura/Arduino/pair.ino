#include <FirebaseESP32.h>
#include  <WiFi.h>
#include "DHT.h"

#define FIREBASE_HOST "https://********.firebaseio.com/" 
#define WIFI_SSID "***********" // Change the name of your WIFI
#define WIFI_PASSWORD "*******" // Change the password of your WIFI
#define FIREBASE_Authorization_key "KdX***************c3t5sm"

#define DHTPIN 2    

#define DHTTYPE DHT11   
DHT dht(DHTPIN, DHTTYPE);

FirebaseData firebaseData;
FirebaseJson json;

void setup() {

 Serial.begin(115200);
  dht.begin();
   WiFi.begin (WIFI_SSID, WIFI_PASSWORD);
   Serial.print("Connecting...");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());
  Serial.println();
  Firebase.begin(FIREBASE_HOST,FIREBASE_Authorization_key);
  
}

void loop() {
 
  float hum = dht.readHumidity();
  float temp = dht.readTemperature(); 
  float hic = dht.computeHeatIndex(temp, hum, false); // sensacion termica en C° 
  
   if (isnan(hum) || isnan(temp)  ){
    Serial.println(F("Failed to read from DHT sensor!"));
    return;
  }

  Serial.print("Temperature: ");
  Serial.print(temp);
  Serial.print("°C");
  Serial.print(" Humidity: ");
  Serial.print(hum);
  Serial.print("%");
  Serial.print("HIC: ");
  Serial.print(hic);
  Serial.print("°C");
  Serial.println();
  
  Firebase.setFloat(firebaseData, "/regulators/Test/actual_temperature", temp);
  Firebase.setFloat(firebaseData, "/regulators/Test/Humedad", hum);
  Firebase.setFloat(firebaseData, "/regulators/Test/Hic", hic);

  delay(200);
}

//Actualizacion
/*
 * Ahora el calefactor es activado por una señal de tipo high - trigger
*/

#include <FirebaseESP32.h>
#include  <WiFi.h>
#include "DHT.h"

#define FIREBASE_HOST "https://***************.firebaseio.com/" 
#define WIFI_SSID "**************" // Change the name of your WIFI
#define WIFI_PASSWORD "********" // Change the password of your WIFI
#define FIREBASE_Authorization_key "KdmK********FL93t5sm"

#define DHTTYPE DHT11 // referencia del sensor
#define DHTPIN 5  // pin de comunicación con sensor de temperatura    
#define RELAY_PIN_VENT 4 //Data Pin from relay fan
#define RELAY_PIN_HEAT 18 //Data Pin from relay heater 

DHT dht(DHTPIN, DHTTYPE);

FirebaseData firebaseData;
FirebaseJson json;

void setup() {

  Serial.begin(115200);
  dht.begin();
  pinMode(RELAY_PIN_VENT , OUTPUT);
  pinMode(RELAY_PIN_HEAT , OUTPUT);
  WiFi.begin (WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting...");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
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
  
  // variables donde se almacenaran los datos recopilados de firebase
  int lower_l;
  int upper_l;
  
  // recopilacion de datos de firebase
  if(Firebase.getInt(firebaseData,"/regulators/Test/lower_limit")){
    lower_l = firebaseData.intData();
  }
  if(Firebase.getInt(firebaseData,"/regulators/Test/upper_limit")){
    upper_l = firebaseData.intData();
  }
  
  Serial.println(lower_l);
  Serial.println(upper_l);

  // si se quiere controlar desde arduino
  int lower_default = 5;
  int upper_default = 27;

  // si se quiere controlar desde firebase
  if(temp > lower_l){
    digitalWrite(RELAY_PIN_HEAT, LOW);
    digitalWrite(RELAY_PIN_VENT, LOW); 
  }else if (temp > 30){
    digitalWrite(RELAY_PIN_HEAT, LOW);
    digitalWrite(RELAY_PIN_VENT, LOW);
  }
  
}

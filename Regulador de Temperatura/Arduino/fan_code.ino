// codigo que controla ventilador con relé mediante sensor de temperatura

#include "DHT.h" // lib para mod temp
#define DHTPIN 7 // pin de comunicacion con el sensor de temperatura
#define DHTTYPE DHT11 // referencia del sensor

int relayPin = 6; //Data Pin 
DHT dht(DHTPIN,DHTTYPE); // instancia de objeto de clase DHT

void setup() {
  Serial.begin(9600);
  //Serial.println("Probando conexion con sensor de temperatura");
  dht.begin(); // inicializamos el obj
  pinMode(relayPin, OUTPUT);

}

void loop() { 
   // temperatura
   float humedad = dht.readHumidity();
   float temperatura = dht.readTemperature();
   float hic = dht.computeHeatIndex(temperatura, humedad, false); // sensacion termica en C°

   if (isnan(humedad)|| isnan(temperatura)){
    Serial.println("Error al leer del sensor");
    return;
   } 
   
   // relé
   delay(350);
   Serial.print("Humedad: ");
   Serial.print(humedad);
   Serial.print("%   ");
   Serial.print("Temperatura: ");
   Serial.print(temperatura);
   Serial.print("°C  ");
   Serial.print("Sensación termica: ");
   Serial.print(hic);
   Serial.print("°C  ");
   Serial.println();
   
   if(hic > 12){
    digitalWrite(relayPin, LOW); // Encendemos el relay 
    //Serial.println("Relay encendido");
   }else{
    digitalWrite(relayPin,HIGH); //Apagamos el relay
    //Serial.println("Relay apagado");
   }
   
}

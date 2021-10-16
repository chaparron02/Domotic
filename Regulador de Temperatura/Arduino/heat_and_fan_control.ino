// info 

// Ambos relés se activan por low trigger, es decir un 0 lógico (LOW) hará ya sea que se encienda el ventilador o el calefactor,
// y un 1 lógico hará que se apaguen (HIGH)

#include "DHT.h" // lib para mod temp
#define DHTPIN 5 // pin de comunicacion con el sensor de temperatura
#define DHTTYPE DHT11 // referencia del sensor
#define RELAY_PIN_VENT 4 //Data Pin from relay fan
#define RELAY_PIN_HEAT 18 //Data Pin from relay heater 

DHT dht(DHTPIN,DHTTYPE); // instancia de objeto de clase DHT

void setup() {
  Serial.begin(9600);
  Serial.println("Probando conexion con sensor de temperatura");
  Serial.print("Humedad, Temperatura, Sensacion termica\n");
  dht.begin(); // inicializamos el obj
  pinMode(RELAY_PIN_VENT , OUTPUT);
  pinMode(RELAY_PIN_HEAT , OUTPUT);

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
   delay(1000);
   Serial.print("Humedad, Temperatura, Sensacion termica\n");
   Serial.print(humedad);
   Serial.print(",    ");
   Serial.print(temperatura);
   Serial.print(",    ");
   Serial.print(hic);
   Serial.println();
   
   if(hic > 18 and temperatura > 15){
    digitalWrite(RELAY_PIN_VENT, LOW);
    digitalWrite(RELAY_PIN_HEAT, HIGH);
   }else if(hic < 5 and temperatura < 10){
    digitalWrite(RELAY_PIN_HEAT, LOW);
    digitalWrite(RELAY_PIN_VENT, HIGH);
   }
  };
   
  

/*
 United Electronics
 Arduino Division
 Program to read local input and sync data
 with CCServer via TCP/IP sockets and string
 commands defined in CCConstants.java
 */

#include <SPI.h>
#include <WiFi.h>
#include <LiquidCrystal.h> //include the library code for the LCD screen
#include <PinChangeInt.h>

#define sensor 0 

#define fanOutput 17 
#define heatOutput 16 
#define coolOutput 15
#define holdOutput 28
#define errorLED 29
#define DEGREE_SYMBOL 223

const int OFF=0,HEAT=1,COOL=2; //Op Mode Codes
const char* OP_MODES[] = {"Off ","Heat","Cool"};
const int AUTO=0,ON=1; //Fan mode codes
const char* FAN_MODES[] = {"Auto","On  "};
const int DEAD_SPAN = 1; //Tolerance between setpoint and point at which hvac will turn on. Used because a dead-span of 0 would result in the hvac constantly clicking on&off

int Vin;  // Variable to read the value from the Arduino's A0 pin.
float currentTemp; // Temperature variable in degrees Fahrenheit.
int currentTempRounded; // Temperature variable in degrees Fahrenheit, rounded to the nearest ones place.
const int NUM_OF_BUTTONS = 5;

volatile int buttonState[NUM_OF_BUTTONS];
volatile unsigned long lastDebounceTime[NUM_OF_BUTTONS];
volatile unsigned long currentTime;
volatile int reading[NUM_OF_BUTTONS],previousReading[NUM_OF_BUTTONS];
volatile boolean buttonDone[NUM_OF_BUTTONS];
volatile const long debounceDelay = 250;

//LiquidCrystal lcd(22,23,24,25,26,27);
LiquidCrystal lcd(22,23,24,25,26,27); 

//char ssid[] = "( o Y o )";      
//char pass[] = "Btimf!211364782";
char ssid[] = "Slave";
char pass[] = "password";
int keyIndex = 0;            // your network key Index number (needed only for WEP)

char buff[70],lcd_buff[17];
volatile int setPoint,opMode,fanMode,hold=0;
volatile boolean opModeChanged=false,fanModeChanged=false,setPointChanged=false,currentTempChanged=false,hvacStatusChanged=false,fanStatusChanged=false,holdChanged=false;

int heatVal,coolVal,fanVal,fanStatus=-1,hvacStatus=-1;

int status = WL_IDLE_STATUS;

WiFiClient client;
IPAddress server(192,168,1,127); 
//IPAddress server(54,244,159,175); 

unsigned long lastTempCheck = 0;
unsigned long lastUpdateTime = 0;           // last time you connected to the server, in milliseconds
unsigned long lastTempUpdate = 0;
const unsigned long updateInterval = 500;  // delay between updates, in milliseconds
const unsigned long tempInterval = 500;
const unsigned long TIME_OUT = 30000;
const unsigned long tempUpdateInterval = 3*1000;
unsigned long timeCheck;

void setup() 
{
  initLocalIO();
  initSettings();
  updateOutput();
  initLCD();
  Serial.begin(115200); 
  initWifi();
  displayLCDLabels();
}

void loop() 
{
  if((millis() - lastTempCheck) > tempInterval) 
  {
    readCurrentTemp();
    lastTempCheck = millis();
  }
  if((millis() - lastUpdateTime) > updateInterval) 
  {
    printStatusToSerial();
    updateOutput();
    lastUpdateTime = millis();
  }
  updateErrorLED();
  checkForPacket();
  if((millis() - lastTempUpdate) > tempUpdateInterval)
  {
    currentTempChanged = checkForLocalChange(currentTempChanged,"setCurrentTemp",currentTempRounded);
    lastTempUpdate = millis();
  }
  setPointChanged = checkForLocalChange(setPointChanged,"setSetPoint",setPoint);
  opModeChanged = checkForLocalChange(opModeChanged,"setOpMode",opMode);
  fanModeChanged = checkForLocalChange(fanModeChanged,"setFanMode",fanMode);  
  hvacStatusChanged = checkForLocalChange(hvacStatusChanged,"setHvacStatus",hvacStatus);
  fanStatusChanged = checkForLocalChange(fanStatusChanged,"setFanStatus",fanStatus);
  holdChanged = checkForLocalChange(holdChanged,"setHold",hold);
}

void updateOutput()
{//Remember: Outputs are active low 
  int newHvacStatus,newFanStatus;  
  switch(opMode)
  {
    case HEAT:
      coolVal = HIGH;
      if(heatVal == LOW) //currently running
      {
        if(currentTempRounded > setPoint)
          heatVal = HIGH;
      }
      else
      {
        if(currentTempRounded < (setPoint-DEAD_SPAN))
          heatVal = LOW; 
      }
      break;
    case COOL: 
      heatVal = HIGH;
      if(coolVal == LOW) //currently running
      {
        if(currentTempRounded < setPoint)
          coolVal = HIGH;
      }
      else
      {
        if(currentTempRounded > (setPoint+DEAD_SPAN))
          coolVal = LOW;
      }
      break;
    case OFF:
      heatVal = HIGH;
      coolVal = HIGH;
      break;
  }
  if(fanMode == ON || (fanMode == AUTO && (heatVal == LOW || coolVal == LOW)))
    fanVal = LOW;
  else
    fanVal = HIGH;

  if(fanVal == HIGH) newFanStatus = OFF;
  else newFanStatus = ON;
  
  if(heatVal == LOW) newHvacStatus = HEAT;
  else if(coolVal == LOW) newHvacStatus = COOL;
  else newHvacStatus = OFF;

  if(hvacStatus != newHvacStatus)
  {
    hvacStatus = newHvacStatus;
    hvacStatusChanged = true;
    digitalWrite(coolOutput,coolVal);
    delay(10); //ensure cool and heat are never on at the same time
    digitalWrite(heatOutput,heatVal);
  }
  
  if(fanStatus != newFanStatus)
  {
    fanStatus = newFanStatus;
    fanStatusChanged = true;
    digitalWrite(fanOutput,fanVal);
  }
}

void initLCD()
{
  lcd.begin(16,2);  // Tells the Arduino that the display is a 16x2 type
  lcd.setCursor(0,0);
  lcd.print("Booting...");
}

void initSettings()
{
  opMode = OFF;
  fanMode = AUTO;
}

void initLocalIO()
{   
  pinMode(fanOutput, OUTPUT);
  pinMode(heatOutput, OUTPUT);
  pinMode(coolOutput, OUTPUT);
  pinMode(holdOutput, OUTPUT);
  pinMode(errorLED, OUTPUT);
  updateHoldLED();
  updateErrorLED();
  
  attachInterrupt(2,upButtonISR,FALLING);
  attachInterrupt(3,downButtonISR,FALLING);
  attachInterrupt(4,opModeButtonISR,FALLING);
  
  
  pinMode(30, INPUT); 
  digitalWrite(30, HIGH);
  PCintPort::attachInterrupt(30, &upButtonISR, FALLING);
  
  pinMode(8, INPUT); 
  digitalWrite(8, HIGH);
  PCintPort::attachInterrupt(8, &downButtonISR, FALLING);
  
  pinMode(31, INPUT); 
  digitalWrite(31, HIGH);
  PCintPort::attachInterrupt(31, &opModeButtonISR, FALLING);
  
  attachInterrupt(0,fanModeButtonISR,FALLING);
  attachInterrupt(1,holdButtonISR,FALLING);
  
}

void displayLCDLabels()
{
  lcd.setCursor(0,0);
  sprintf(lcd_buff,"Set:   %cF     %cF",DEGREE_SYMBOL,DEGREE_SYMBOL);
  lcd.print(lcd_buff);
  lcd.setCursor(0, 1); // Moves the cursor to the next line.
  sprintf(lcd_buff,"       Fan: ");
  lcd.print(lcd_buff);
}

void updateLCDSetPoint()
{
  lcd.setCursor(5,0);
  sprintf(lcd_buff,"%2d",setPoint);
  lcd.print(lcd_buff);
}

void updateLCDCurrentTemp()
{
  lcd.setCursor(12,0);
  sprintf(lcd_buff,"%2d",currentTempRounded);
  lcd.print(lcd_buff);
}

void updateLCDOpMode()
{
  lcd.setCursor(0,1);
  sprintf(lcd_buff,"%s",OP_MODES[opMode]);
  lcd.print(lcd_buff);
}

void updateLCDFanMode()
{
  lcd.setCursor(12,1);
  sprintf(lcd_buff,"%s",FAN_MODES[fanMode]);
  lcd.print(lcd_buff);
}

void updateHoldLED()
{
  if(hold == 0)
    digitalWrite(holdOutput,HIGH); //output is active-low
  else
    digitalWrite(holdOutput,LOW);
}

void updateErrorLED()
{
  if(client.connected())
    digitalWrite(errorLED,HIGH); //output is active-low
  else
    digitalWrite(errorLED,LOW);
}

void flashErrorLED(int val,unsigned long duration)
{
  digitalWrite(errorLED,val);
  delay(duration);
  digitalWrite(errorLED,!val);
}

boolean checkForLocalChange(boolean b, char* cmd, int param)
{  
  if(b)
  {
    sprintf(buff,"%s-%d~",cmd,param);
    Serial.println(buff);
    client.print(buff);
    return false;
  } 
  return false;
}

void upButtonISR()
{
  currentTime = millis();
  if((currentTime - lastDebounceTime[0]) > debounceDelay)
  {
    setPoint++;
    updateLCDSetPoint();
    setPointChanged = true;
    lastDebounceTime[0] = currentTime;
  }
}

void downButtonISR()
{
  currentTime = millis();
  if((currentTime - lastDebounceTime[1]) > debounceDelay)
  {
    setPoint--;
    updateLCDSetPoint();
    setPointChanged = true;
    lastDebounceTime[1] = currentTime;
  }
}

void opModeButtonISR()
{
  currentTime = millis();
  if((currentTime - lastDebounceTime[2]) > debounceDelay)
  {
    opMode = (opMode+1)%3;
    updateLCDOpMode();
    opModeChanged = true;
    lastDebounceTime[2] = currentTime;
  }
}

void fanModeButtonISR()
{
  currentTime = millis();
  if((currentTime - lastDebounceTime[3]) > debounceDelay)
  {
    fanMode = (fanMode+1)%2;
    updateLCDFanMode();
    fanModeChanged = true;
    lastDebounceTime[3] = currentTime;
  }
}

void holdButtonISR()
{
  currentTime = millis();
  if((currentTime - lastDebounceTime[4]) > debounceDelay)
  {
    hold = (hold+1)%2;
    updateHoldLED();
    holdChanged = true;
    lastDebounceTime[4] = currentTime;
  }
}

void readCurrentTemp()
{
  int temp;
  Vin = analogRead(sensor); // Reads sensor value and stores it.
  currentTemp =  ((4.88*Vin)/10.0)/5 - 4; // Converts the voltage value into degrees Fahrenheit
  temp = round(currentTemp); // Round currentTemp to nearest ones place.
  if(temp != currentTempRounded)
  {
    currentTempRounded = temp;
    currentTempChanged = true;
    updateLCDCurrentTemp();
  }
}

void checkForPacket() 
{
  if(!client.connected())
  {
    Serial.println("No Connection to Server.");
    connectToServer();
  }
  char cmd[31];
  boolean done = true;
  done = !readResponse(cmd);
  if(!done)
  {
    if(!strcmp(cmd,"setSetPoint"))
    {
      readResponse(buff);
      setPoint = atoi(buff);
      Serial.print("checkForSocket(): set point = ");
      Serial.println(setPoint);
      updateLCDSetPoint();
      done = true;
    }
    else if(!strcmp(cmd,"setOpMode"))
    {
      readResponse(buff);
      opMode = atoi(buff);
      Serial.print("op mode = ");
      Serial.println(opMode);
      updateLCDOpMode();
      done = true;
    }
    else if(!strcmp(cmd,"setFanMode"))
    {
      readResponse(buff);
      fanMode = atoi(buff);
      Serial.print("fan mode = ");
      Serial.println(fanMode);
      updateLCDFanMode();
      done = true;
    }   
    else if(!strcmp(cmd,"setHold"))
    {
      readResponse(buff);
      hold = atoi(buff);
      Serial.print("hold = ");
      Serial.println(hold);
      updateHoldLED();
      done = true;
    }
  }
}

boolean readResponse(char* resp)
{
  int i=-1;
  while(client.available()) 
  {
    resp[++i] = client.read();
    if(resp[i] == '\n')
    {
        resp[i] = '\0';
        return true;
    }
  }
  return false;
}

void connectToServer()
{
  while(true)
  {
    Serial.println("Connnecting...");
    if(client.connect(server,1212))
    {
      client.print("Arduino");
      waitForServer();
      readResponse(buff);
      setPoint = atoi(buff);
      updateLCDSetPoint();
      waitForServer();
      readResponse(buff);
      opMode = atoi(buff);
      updateLCDOpMode();
      waitForServer();
      readResponse(buff);
      fanMode = atoi(buff);
      updateLCDFanMode();
      return;
    }
    else
    {
      Serial.println("Connection Failed. Trying again in 10 seconds...");
      flashErrorLED(HIGH,1000);
      delay(9000);
    }
  }
}

void waitForServer()
{
  timeCheck = millis();
  while(!client.available())
  {
    if((millis() - timeCheck) > TIME_OUT)
    {
      Serial.println("No Response from Server. Retrying...");
      client.stop();
      connectToServer();
    }
  }
}

void printStatusToSerial()
{
  sprintf(buff,"Conn: %d CT: %d SP: %d OM: %d FM: %d HVAC: %d FAN: %d",client.connected(),currentTempRounded,setPoint,opMode,fanMode,hvacStatus,fanStatus);
  Serial.println(buff);    
}

void initWifi()
{
  checkForWifiShield();
  connectToWifiNetwork();
  printWifiStatus();
}

void printWifiStatus() 
{
  lcd.clear();
  lcd.print("WiFi Connection");
  lcd.setCursor(0,1);
  lcd.print("Successful.");
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);
  long rssi = WiFi.RSSI();
  Serial.print("signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
}

void checkForWifiShield()
{
  while (WiFi.status() == WL_NO_SHIELD) 
  {
    lcd.setCursor(0,0);
    lcd.print("Connecting to");
    lcd.setCursor(0,1);
    lcd.print("WiFi Shield...");
    Serial.println("Connecting to WiFi Shield..."); 
    delay(200);     // don't continue:
  }
}

void connectToWifiNetwork()
{
  int attempts = 0;
  
  pinMode(4,OUTPUT);
  digitalWrite(4,HIGH); //disable SD card on shield
  pinMode(53,OUTPUT); //enable spi interface on Arduino mega -- should be ignored on Bobuino since dig pin 53 doesn't exist
  
  while ( status != WL_CONNECTED) 
  { 
    lcd.clear();
    sprintf(lcd_buff,"Conn Attempt #%1d:",++attempts);
    lcd.print(lcd_buff);
    lcd.setCursor(0,1);
    lcd.print(ssid);
    Serial.print("Attempting to connect to SSID: ");
    Serial.println(ssid);
    status = WiFi.begin(ssid, pass);     // Connect to WPA/WPA2 network. Change this line if using open or WEP network:    
    delay(10000);   // wait 10 seconds for connection:
  } 
}







/****
 * @Authors : Davy Duvivier, Martial Bailly, Andreas Muller, Théo Trafny
 * This file is the main code of the Lazarus project robot.
 * This robot allow you to map a room with manual control or with an automatic mode.
 * 
 * We use 4 motors wich are connected to 2 outputs motor controller.
 * One output for each side of the robot.
 * 
 * We use also a Bluetooth controller for the communication.
 * 
 * 3 ultrasonic sensors, one front and two on the right side.
 * 
 * 2 encoders, one on the right back motor and another on the left back motor
 * 
 * Look at the report for more details about conception choices.
 * 
 * About the angle and the movement of the robot :
 * the geometric marker is : front of the robot 0°, left 90°, right -90° 
 */

#include <Servo.h>
#include <NewPing.h>
#include <Encoder.h>
//Define pin and actionner controller

#define BT Serial3

Servo STR; //10 : right motors
Servo STL; //11 : left motors

int trigFront = 3; // Trigger of front sensor
int echoFront = 2; // Echo of front sensor

int trigR1 = 5; //Trig of right front sensor
int echoR1 = 4; //Echo of right front sensor

int trigR2 = 7; //Trig of right back sensor
int echoR2 = 6; //Echo of right back sensor

//Work variable
char mode = 'A'; //M  :Manuel mode, A : automatic mode
int powerMotorLeft = 60;
int powerMotorRight = 60;

long durationFront;
float distanceFront;
long durationR1;
float distanceR1;
long durationR2;
float distanceR2;
NewPing sonarFront(trigFront, echoFront, 200); // NewPing setup of pins and maximum distance.
NewPing sonarR1(trigR1, echoR1, 200); // NewPing setup of pins and maximum distance.
NewPing sonarR2(trigR2, echoR2, 200); // NewPing setup of pins and maximum distance.


//MAE variable
#define MF 1 // Middle front
#define FR 2 //Front right
#define BR 3 //Back right
#define P 4 //Parallel
#define C 5 //Control

#define TURNL 1 //Turn left
#define TURNR 2 //Turn right
#define FORWARD 3 //GO FORWARD
#define BACK 4 //Go backward
#define PARALLEL 5


int currentState=0;
int nextState=0;

//input
int sensorMF, sensorFR,sensorBR, correctParallel, control, parallel;
bool controlOK=false;

//output
int goForward, goBack, turn90Right, turn90Left;

//Position and speed variable
Encoder leftEncoder(8,9);
Encoder rightEncoder(12,13);

const unsigned long timePeriod = 1000;
unsigned long startTime = 0;
long startPositionLeft;
long startPositionRight = 0;

long oldPositionLeft  = -999;
long oldPositionRight = -999;

float rotationPerMinLeft;
float rotationPerMinRight;

float m = 6.0; //Robot weight in kg
float Tleft = 0.0; //rotation per minute left side motor
float Tright = 0.0; //rotation per minute left side motor
float speedTot = 0.0;

unsigned long debut=0;
unsigned long fin=0;
int duree=0;

float deltaAngle;          // incrément de rotation
float x = 0;
float y = 0;
float a = 0; // position angle
float e = 13.75; //entraxe des roues 2e = ?? cm
float distanceR1R2 = 17.5;

int defaultSpeed = 50;

char action = 'S';
float delta;

int loopTr = 0;
float angleWall;

void setup() {
          Serial.begin(115200); //115200 to console
          while(!Serial);
          delay(500);
          BT.begin(115200); //9600 to HM10
          delay(500);
          STR.attach(10, 2000, 1000); // attaches the servo on pin 10 to the servo object
          STL.attach(11, 2000, 1000); // attaches the servo on pin 11 to the servo object
          stopMotors();
         // attachInterrupt(0, safetyFirst, RISING); //attach interruption to the safetyFirst function
         delta = 0.52;
        /* while(!BT.available()){
          delay(500);
         }*/

}

void loop() {
          
          char recvChar;
          char in;
          float inAngle =0;
          if (BT.available()) {
                in = BT.read();
                Serial.print("command ");
                Serial.println(in);

                //Define speed of the robot
                if(in == 'P'){
                  while(!BT.available());
                  if (BT.available()) inAngle = BT.read();
                  powerMotorLeft = (int) inAngle;
                  powerMotorRight = powerMotorLeft ;
                }
                
                //Define mode
                if(in == 'M' || in == 'A'){
                  currentState=0;
                  stopMotors();
                  mode = in;
                }

                //Action to perform according to mode

                if(mode == 'M'){
                  if (in == 'F')
                    {
                      //Go forward
                      forwardMotors();
                
                    }
                  else if(in== 'B'){
                       backwardMotors();
                    }

                    else if (in == 'T')
                    {
                      //Go left
                      while(!BT.available());
                      if (BT.available()){
                        inAngle = 0;
                        byte abc[4];
                        //We are waiting for a float, which is code on 4 bytes
                        for(int i=0; i<4;i++){
                          abc[3-i] = BT.read();

                          while(!BT.available());
                        }
                        inAngle = *(float*)(abc);
                      }
                
                      //the geometric marker : front of the robot 0°, left 90°, right -90° 
                      if(inAngle <= 90 && inAngle > 0){
                        turnLeft(inAngle);
                      }

                      else if(inAngle>=(-90) && inAngle <0){
                          turnRight(inAngle);
                      }
                    }
                    else if (in == 'S')
                    {
                      //STOP
                      stopMotors();
                    }
                }
          }

          durationR1 = sonarR1.ping();
          durationR2 = sonarR2.ping();

          float newDistanceR1 = (durationR1 / 2) * 0.0343;
          float newDistanceR2 = (durationR2 / 2) * 0.0343;
          distanceR1 = newDistanceR1; 
          distanceR2 = newDistanceR2;

          durationFront = sonarFront.ping();
          float newDistanceFront = (durationFront / 2) * 0.0343; //AJouter la distance capteur- roue
            distanceFront = newDistanceFront;

          
          if(mode=='A'){
            automaticMode();
          }
          


          //delay(50); // Wait 50ms between pings (about 20 pings/sec). 29ms should be the shortest delay between pings.
  
          

         /* 
          if(newDistanceFront > 0.0){
            distanceFront = newDistanceFront;
          }*/


          //SAFETY FIRST => TODO : use interruption instead here
           //Safety First
          if(distanceFront<20.0 && distanceFront != 0.0){
            Serial.println("wola chui bloké");
              stopMotors();
          }


          /*if(newDistanceR1 > 0.0){
            distanceR1 = newDistanceR1;
          }

          if(newDistanceR2 <=0.0){
            distanceR2 = newDistanceR2;
          }*/

          speedPosCalc();
          angleWall = calculateAngle();
          loopTr ++;
          if(loopTr >= 20) {
            String dataToSend = "A "+String(distanceFront,3)+" "+String(distanceR1,3)+" "+String(distanceR2,3)+" "+String(x,3)+" "+String(y,3)+" "+String(a,3)+" Z";
            Serial.println(dataToSend);
            BT.print(dataToSend);
            loopTr = 0; 
          }
       

      
          

         // a = calculateAngle();
         //Serial.print("ANGLE =");
         //Serial.println(a);

}

void correctLeft(){
    action = 'L';
    STR.write(60);
    STL.write(80);
}

void correctRight(){
    action = 'R';
    STL.write(60);
    STR.write(80);
}


/***
 * This function stop robot motors
 */
void stopMotors(){
  action = 'S';
  STR.write(90);
  STL.write(90);
}

/***
 * This function allow the robot to go forward. Same power on each motors
 */
void forwardMotors(){
  /*if(action=='R'){
    powerMotorRight = powerMotorLeft;
  }
  else if(action =='L'){
    powerMotorLeft = powerMotorRight;
  }*/
  powerMotorRight = defaultSpeed;
  powerMotorLeft = defaultSpeed;
  /*Serial.print("vitesse :");
  Serial.print(powerMotorRight);
  Serial.print(" ");
  Serial.println(powerMotorLeft);*/

  action = 'F';
  STR.write(powerMotorRight);
  STL.write(powerMotorLeft);
}

void backwardMotors(){
  /*if(action=='R'){
    powerMotorRight = powerMotorLeft;
  }
  else if(action =='L'){
    powerMotorLeft = powerMotorRight;
  }*/
  powerMotorRight = defaultSpeed;
  powerMotorLeft = defaultSpeed;
  /*Serial.print("vitesse :");
  Serial.print(powerMotorRight);
  Serial.print(" ");
  Serial.println(powerMotorLeft);*/

  action = 'B';
  STR.write(180-powerMotorRight);
  STL.write(180-powerMotorLeft);
}


/***
 * This function allow the robot to turn left with a provided angle
 * If you use this function with 45° angle, the speed of the left motors will
 * be the half of the right motor (45° = 90 *0.5)
 */
void turnLeft(float angle){

  action = 'L';
  if(angle==90.0){
    Serial.print("tourne à gauche à 90");
      STR.write(powerMotorRight);
      STL.write(180-powerMotorRight); // same speed than righ side but opposite direction
  }
  else{
    Serial.print("turn left");
    Serial.println(angle);
    powerMotorLeft = angle;
    Serial.print("power gauche ");
    Serial.println(powerMotorLeft);
    STR.write(powerMotorRight);
    STL.write(powerMotorLeft);
    powerMotorLeft = defaultSpeed;
  }
}


/***
 * This function allow the robot to turn right with a provided angle
 * If you use this function with -45° angle, the speed of the left motors will
 * be the half of the right motor (45° = 90 *0.5)
 */
void turnRight(float angle){

    action = 'R';
    angle = -1*angle;
   if(angle==90.0){
        Serial.print("tourne à droite à 90");
      STR.write(180-powerMotorLeft); // same speed than left side but opposite direction
      STL.write(powerMotorLeft);
  }
  else{    
    Serial.print("turn right");
    Serial.println(angle);
    powerMotorRight = angle;
    Serial.print("power right ");
    Serial.println(powerMotorRight);
    STR.write(powerMotorRight);
    STL.write(powerMotorLeft);
    powerMotorRight = defaultSpeed;
  }
}


void left90Motors(){
  stopMotors();
  delay(50);
  action = 'L';
  STR.write(30);
  STL.write(180-30);
  delay(200);
  stopMotors();
  controlOK=true;
}

void right90Motors(){
  stopMotors();
  delay(50);
  action = 'F';
  STL.write(60);
  STR.write(60);
  delay(300);
  action = 'R';
  STR.write(180-30);
  STL.write(30);
  delay(500);
  stopMotors();

  controlOK=true;
}

void correctionParallel(){
    //Turn slowly...
    Serial.println(distanceR1,distanceR2);
    if(distanceR1<19 && distanceR2<19)
    {
       correctLeft();
    }
    else if(distanceR1>30 && distanceR2>30)
    {
      correctRight();
    }
    
    else{
      if(distanceR1 > distanceR2){
        action = 'R';
        STL.write(60);
        STR.write(85);
      }
      if(distanceR1 < distanceR2){
        action = 'L';
        STL.write(85);
        STR.write(60);      
        }
    }
}


/***
 * This function launch and contol the automatic mode of the robot
 */
void automaticMode(){
     //set input
    sensorMF= entry(MF);
    sensorFR=entry(FR);
    sensorBR=entry(BR);
    parallel=entry(P);
    control=entry(C);
    switch(currentState)
    {
      case 0: if(sensorMF) nextState=1; break;
      case 1: if(sensorFR && sensorBR && control) nextState=2; break;
      case 2: if(parallel) nextState=3; else if(sensorMF) nextState=1; else if(!sensorFR) nextState=4;    break;
      case 3: if(sensorMF) nextState=1; else if(!sensorFR) nextState=4; else if(!parallel) nextState = 2; break;
      case 4: if(sensorMF) nextState=1; else if(!sensorBR) nextState=5; break;
      case 5: if(controlOK) {nextState=6; controlOK=false;} break;
      case 6: if(sensorFR) nextState=7; else if(sensorMF) nextState=1; break;
      case 7: if(sensorFR && sensorBR) nextState=2; else if(!sensorFR) nextState=4; else if(sensorMF) nextState=1; break;
    }
    currentState=nextState;
    turn90Left = currentState==1 ;
    turn90Right = currentState==5;
    goForward= currentState==0 || currentState==3 || currentState ==4 || currentState==6 || currentState==7;
    correctParallel = currentState==2;
  
    output(TURNL,turn90Left);
    output(TURNR,turn90Right);
    output(FORWARD,goForward);
    output(PARALLEL, correctParallel);
   // Serial.println(currentState);
}



/***
 * Sortie de la MAE
 */
 void output(int value, int state)
{
  switch(value)
  {
    case 1: if(state) left90Motors(); break;
    case 2: if(state) right90Motors();break;
    case 3: if(state) {STL.write(60); STR.write(60) ;action = 'F';} break;
    case 5: if(state) correctionParallel(); break;
  }
}


/***
 * Entrée de la MAE
 */
 int entry(int value)
{
  switch(value)
  {
    case 1:  return (distanceFront<=25 && distanceFront !=0); //activate MF for distance <=25cm
    case 2:  return (distanceR1 <=(30+10) && distanceR1 !=0); //activate FR for distance <=40cm
    case 3:  return (distanceR2 <=(30+10) && distanceR2 !=0);//activate BR for distance <=40cm
    case 4:  return ((abs(distanceR1 - distanceR2)<2) && (!(distanceR1<19) && !(distanceR2<19)) && (!(distanceR1>30) && !(distanceR2>30))); //distanceR1 +- = distanceR2
    case 5:  return (controlOK);// delay to wait the robot movement (90°)
  }
  return 0;
}



/***
 * this function calculate the position and the angle of the robot
 */
void speedPosCalc(){
  Serial.println("speed pos calc");
  unsigned long now = millis();
  long newPositionLeft = leftEncoder.read();
  long newPositionRight = rightEncoder.read();

  
//RotationMinute left
  if ( now - startTime >= timePeriod ) {
    // time to calculate average encoder speed
    rotationPerMinLeft = (newPositionLeft - startPositionLeft) / (float)timePeriod;
    //Serial.print( "Avg speed is ");
   // Serial.println( rotationPerMinLeft, 4 );
    startTime = now;
    startPositionLeft = newPositionLeft;
  }
  if (newPositionLeft  != oldPositionLeft) {
    oldPositionLeft = newPositionLeft;
   // Serial.println(newPositionLeft);
  }

  
//RotationMinute right
  if ( now - startTime >= timePeriod ) {
    // time to calculate average encoder speed
    rotationPerMinRight = (newPositionRight - startPositionRight) / (float)timePeriod;
    //Serial.print( "Avg speed is ");
    //Serial.println( rotationPerMinRight, 4 );
    startTime = now;
    startPositionRight = newPositionRight;
  }
  if (newPositionRight  != oldPositionRight) {
    oldPositionRight = newPositionRight;
    //Serial.println(newPositionRight);
  }
  
  Tleft = rotationPerMinLeft * 0.35;
  Tright = rotationPerMinRight * 0.35;
  speedTot = 2*Tleft/m + 2*Tright/m; //Speed of the robot
  delta = speedTot/3.6; // km/h to m/s
  deltaAngle = delta/(2*e);

  //Mesure de la position 
  Serial.print("\n action=");
  Serial.println(action);
    if (action =='F'){
      x=x+delta*cos(a);
      y=y+delta*sin(a);
    }
    if (action =='B'){
      x=x-delta*cos(a);
      y=y-delta*sin(a);
    }
    if (action =='L'){
      x = x - e * sin (a) + e * sin (a + deltaAngle);
      y = y + e * cos (a) - e * cos (a + deltaAngle);
      a = a + deltaAngle;
    }
       if (action =='R'){
      x = x + e * sin (a) - e * sin (a - deltaAngle);
      y = y - e * cos (a) + e * cos (a - deltaAngle);
      a = a - deltaAngle; 
    }
  
  
  Serial.print("speed (m/s) = ");
  Serial.println(delta);
  Serial.print("delta angle =");
  Serial.println(deltaAngle);
  //mesuredistance();


}

/***
 * Display coordinate of the robot
 */
void displayCoordinate(){
    Serial.print("X: "); 
    Serial.println(x);
    Serial.print("Y :");
    Serial.println(y);
    Serial.print("angle");
    Serial.println(a);
}

/***
 * This function update coordinate and angle values according to the speed of the robot
 */
void measureAngle(){
  long newPositionRight = rightEncoder.read();
  if (oldPositionRight != newPositionRight) {       // s'il y a un changement d'état du capteur
    oldPositionRight = newPositionRight;            // nouvelle position
    if (action =='F'){
      x=x+delta*cos(a);
      y=y+delta*sin(a);
    }
    if (action =='B'){
      x=x-delta*cos(a);
      y=y-delta*sin(a);
    }
    if (action =='L'){
      x = x - e * sin (a) + e * sin (a + deltaAngle);
      y = y + e * cos (a) - e * cos (a + deltaAngle);
      a = a + deltaAngle;
    }
   }
  long newPositionLeft = leftEncoder.read();
  if (oldPositionLeft != newPositionLeft) {       // s'il y a un changement d'état du capteur
    oldPositionLeft = newPositionLeft;            // nouvelle position
    if (action =='R'){
      x = x + e * sin (a) - e * sin (a - deltaAngle);
      y = y - e * cos (a) + e * cos (a - deltaAngle);
      a = a - deltaAngle; 
    }
  }
}

/***
 * ABle to calculate the rotation angle of the robot 
 * with the distance from right ultrasonic sensors
 */
float calculateAngle(){
  float newAngle;
  if(distanceR1 == distanceR2){
    return 0;
  }
  if(distanceR1 > distanceR2){ //when Turn left
    newAngle = 90 + (asin((distanceR1 - distanceR2)/distanceR1R2)*(180/3.1415));
    if(isnan(newAngle)){
      return a;
    }
    /*Serial.print("_Angle :");
    Serial.println(newAngle);*/
    return newAngle;
  }
  if(distanceR2 > distanceR1){ //when  turn right
    newAngle = acos((distanceR2 - distanceR1)/distanceR1R2)*(180/3.1415);
      if(isnan(newAngle)){
        return a;
      }
     /*Serial.print("_Angle :");
     Serial.println(newAngle);*/
     return newAngle;
  }
}

int in1 = 2; 
int in2 =3; 
int in3 =4; 
int in4 =5; 
int en1 = 9; 
int en2 = 10; 

int dir = 0; 
void setup() {
  // put your setup code here, to run once: 
  Serial.begin(9600); 
  pinMode(in1, OUTPUT); 
  pinMode(in2, OUTPUT); 
  pinMode(in3, OUTPUT); 
  pinMode(in4, OUTPUT); 
  pinMode(en1, OUTPUT); 
  pinMode(en2, OUTPUT); 

  analogWrite(en1,0); 
  analogWrite(en2,0); 

  digitalWrite(in1, LOW); 
  digitalWrite(in2, LOW);
  digitalWrite(in3, LOW); 
  digitalWrite(in4, LOW);

}

void loop() {
  // put your main code here, to run repeatedly: 

  if(Serial.available() > 0){
    dir = Serial.read(); 

    if(dir ==1){
      go(); 
    }
    else if(dir ==2){
      turn();
    } 
    else{
      stopp(); 
    }
  }

} 
//if directions are wrong switch motor wiring (ground and voltage)
void go(){
  analogWrite(en1, 255);
  analogWrite(en2, 255); 

  digitalWrite(in1, HIGH); 
  digitalWrite(in2, LOW); 
  digitalWrite(in3, LOW); 
  digitalWrite(in4, HIGH); 
} 

void turn(){
  analogWrite(en1, 255); 
  analogWrite(en2,255); 

  digitalWrite(in1, HIGH); 
  digitalWrite(in2, LOW); 
  digitalWrite(in3, HIGH); 
  digitalWrite(in4, LOW);
} 

void stopp(){
analogWrite(en1,0); 
  analogWrite(en2,0); 

  digitalWrite(in1, LOW); 
  digitalWrite(in2, LOW);
  digitalWrite(in3, LOW); 
  digitalWrite(in4, LOW);
}

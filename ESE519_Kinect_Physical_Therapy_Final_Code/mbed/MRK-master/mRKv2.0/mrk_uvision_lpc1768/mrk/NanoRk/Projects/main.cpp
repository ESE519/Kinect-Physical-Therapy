//Test on adding additional taskset "task 1" into
//current tx-rx module

#include <include.h>
#include "mbed.h"

#define I2C_FREQUENCY 100000
#define RAD_TO_DEG 57.29577952f //  180/PI
#define DEG_TO_RAD 0.017453292f //   PI/180

//COM ports and digital pinout difination
Serial xbee1(p13, p14);
DigitalOut rst1(p12);
DigitalOut led(LED3);
Serial pc(USBTX, USBRX);
I2C i2c(p28,p27);
DigitalOut motor(p26);

//define global variable
const int IMU_ADDR_W = 0xD0;  // 0x68 is i2c address for IMU.
const int IMU_ADDR_R = 0xD1; 
const int ACCR_ADDR_W = 0xA6; //0x53 is I2C address for ADXL345
const int ACCR_ADDR_R = 0xA7;
const char GYRO_X[1] = {0x1D}; //address for 'GYRO_XOUT_H'
const char ACCR_X[1] = {0x23}; //address for 'ACCR_XOUT_H'
char GYRO_data[6];
char ACCR_data[6];
float _gyro[3];
float _accr[3];
float newEst[3];



void i2c_write2(int addr, char a, char b){
	char cmd[2];
	cmd[0] = a;
	cmd[1] = b;
	i2c.write(addr, cmd ,2);
	wait(0.1);
}

float squared(float x){
	return x*x;
}

float normalize3Dvector(float*vector){
  float R = sqrt(vector[0]*vector[0] + vector[1]*vector[1] + vector[2]*vector[2]);
	vector[0] /= R;
  vector[1] /= R;
  vector[2] /= R;	
}

void IMU_Init(void){
	
  char SLAVE_ADDR_CHECK[1];
	char AUX_SLV_ADDR[1] = {0x14};
  char ACCR_PWR_ADDR[1] = {0x2D};
	char ACCR_PWR_CHECK[1];

	i2c.frequency(I2C_FREQUENCY);
		
	//****** IMU Fusion Board Initialization******//
	i2c_write2(IMU_ADDR_W, 0x14, 0x53);   // set 7-bit ACCR i2c slave address 0x53 //0x53 = 83
	i2c_write2(IMU_ADDR_W, 0x16, 0x0C);   // set full-scale range to 500 deg/s, DLPF to 20Hz
	i2c_write2(IMU_ADDR_W, 0x18, 0x32);   // set accr burst-read register
	i2c_write2(IMU_ADDR_W, 0x3D, 0x08);// set pass-through mode to primary I2C bus to turn it on
	i2c_write2(ACCR_ADDR_W, 0x2D, 0x08);  // set ACCR to measure mode//0x38 = 56
	i2c_write2(ACCR_ADDR_W, 0x31, 0x02);  // +/-8g range, 10-bit resolution bit
	i2c_write2(IMU_ADDR_W, 0x3D, 0x28);// cancel pass-through mode 
}

void PWM_Init(void){
//Initialize the PWM output
	LPC_SC->PCONP |= (1UL<<6);             // power up PWM1
	LPC_SC->PCLKSEL0 |= (3UL<<12);         // peripheral clock selection for PWM1 prescale 8
	LPC_PINCON->PINSEL4 &= ~(0x0000000F);  // Select PWM1.1 and 1.2 to function //PWM1.1 is DIP26.
	LPC_PINCON->PINSEL4 |= (0x00000001); 
	LPC_PINCON->PINMODE4 |= (0x00000002);  //Select PIN for PWM1.1 & 1.2 to no pull-up/down mode
	LPC_PWM1->MR0 = 15000;     // Set freq to 96MHz/8/15000 = 800, 96MHz/8/6000 = 2000
	LPC_PWM1->MCR |= (1UL<<1); // Reset Timer on Match 0
	LPC_PWM1->MR1 = 7500;      // Set duty cycle to 50% for PWM1.1
	LPC_PWM1->TCR |= (1UL<<0) | (1UL<<3); // Enable Timer counter and PWM
	
}

void Read_IMU_GYRO(void){
  
	static int i;
	short int GYRO_2byte[3];
	
	i2c.write(IMU_ADDR_W, GYRO_X, 1);
	i2c.read(IMU_ADDR_R, GYRO_data, 6);
	
 	GYRO_2byte[0] = ((GYRO_data[0] << 8) + GYRO_data[1]); // combine 16-bit xdata
	GYRO_2byte[1] = ((GYRO_data[2] << 8) + GYRO_data[3]); // combine 16-bit ydata
	GYRO_2byte[2] = ((GYRO_data[4] << 8) + GYRO_data[5]); // combine 16-bit zdata
	
	for(i=0;i<3;i++) 
	_gyro[i] = GYRO_2byte[i] * 500 / 32767; 
	
}
	
void Read_IMU_ACCR(void){
	
	static int i;
  short int ACCR_2byte[3];

	i2c.write(IMU_ADDR_W, ACCR_X, 1);
	i2c.read(IMU_ADDR_R, ACCR_data, 6); 
	ACCR_2byte[0] = ((ACCR_data[0] << 8) + ACCR_data[1]); // combine 16-bit xdata
	ACCR_2byte[1] = ((ACCR_data[2] << 8) + ACCR_data[3]); // combine 16-bit ydata
	ACCR_2byte[2] = ((ACCR_data[4] << 8) + ACCR_data[5]); // combine 16-bit zdata

	for(i=0;i<3;i++) 
	_accr[i] = 8 * ACCR_2byte[i]/512;
	
 }

int UpdateInclination(void){

	//static unsigned long oldTimeStamp;
	//static unsigned long newTimeStamp;
	static int i,j;
	float newACCR[3];
	//float newGYRO[3];
	//float detAngle;
	//float Ajz[2];
  //char signRzGYRO;
	//float Ajz_id[2];
 
	// get timestamp
	// newTimeStamp = TickCount;
  // interval = newTimeStamp - oldTimeStamp;
  // lastTimeStamp  =  newTimeStamp;
 
	Read_IMU_ACCR();  // write it in return form
  for (i=0;i<3;i++){
		newACCR[i] = _accr[i];
	}
  normalize3Dvector(newACCR);
	//printf("\r\n\r\nxACCR = %g  yACCR = %g  zACCR = %g\r\n",newACCR[0],newACCR[1],newACCR[2]);
	int  theta = atan2(newACCR[1],newACCR[2])*RAD_TO_DEG;
	if (theta<-135){
		theta+=360;
	}
		//printf("ANGLE = %d\r\n",theta);
	/*
	//%%%%
  if(FirstSample){
    for(j=0;j<3;j++)
		 newEst[j] = newACCR[j];
	}
  else{
		//printf("abs_newEst[2] = %g\r\n",abs(newEst[2]));
		if (abs(newEst[2]) < Z_THRESHOLD){ // if rotation is smaller than THRESHOLD, ignore the newly read GYRO data and take 
			                             // accr data as new estimation
		  for(j=0;j<3;j++)  
			newGYRO[j] = newEst[j];
		}
		else{
			Read_IMU_GYRO(); //update Gyroscope value //unit is deg/s
			//printf("xGYRO = %g  yGYRO = %g  zGYRO = %g\r\n",_gyro[0],_gyro[1],_gyro[2]);
		  newTimeStamp = TickCount;  //update timestamp
			//printf("newTimeStamp = %d\r\n", TickCount);
			interval = (newTimeStamp - oldTimeStamp)/1000; //unit:s
      oldTimeStamp = newTimeStamp;
			
		  for (j=0;j<2;j++) {
				newGYRO[j] = _gyro[j];
				detAngle = interval * _gyro[j]; //det Angle with respect to body fixed frame of last moment 
				Ajz[j] = atan2(newEst[j],newEst[2]) * RAD_TO_DEG; //Angle with respect to base frame, according to last estimation inclination
				Ajz[j] += detAngle; //new angle after the rotation
				
			//following for ideal
				Ajz_id[j] = atan2(_accr[j], _accr[2]) * RAD_TO_DEG;
				Ajz_id[j] += detAngle;
			}	
			  //printf("\r\nideal angle to base frame:%g\t%g\t%g\r\n",Ajz_id[0],Ajz_id[1]);
				signRzGYRO = (cos(Ajz[0] * DEG_TO_RAD) >= 0)?1:-1;
				
				newGYRO[0] = sin(Ajz[0] * DEG_TO_RAD);
				newGYRO[0] /= (1 + squared(cos(Ajz[0] * DEG_TO_RAD)) * squared(tan(Ajz[1] * DEG_TO_RAD)));
				newGYRO[1] = sin(Ajz[1] * DEG_TO_RAD);
				newGYRO[1] /= (1 + squared(cos(Ajz[1] * DEG_TO_RAD)) * squared(tan(Ajz[0] * DEG_TO_RAD)));
				
				newGYRO[2] = signRzGYRO * sqrt(1 - squared(newGYRO[0]) - squared(newGYRO[1]));
        				
				for(j=0;j<3;j++)
			     newEst[j] = (newACCR[j] + WEIGH_GYRO * newGYRO[j])/(1 + WEIGH_GYRO);
			  
			  normalize3Dvector(newEst);
			
			//float  theta = atan2(newEst[2],-newEst[1])*RAD_TO_DEG;
			//  printf("ANGLE = %g\r\n",theta);
			
			  //printf("\r\nxnewEst = %g  ynewEst = %g  znewEst = %g\r\n",newEst[0],newEst[1],newEst[2]);
			 
   }
			
 }
 FirstSample = 0;
 */
 return theta;
 
}

void PWM_Start(void){

	LPC_PWM1->PCR |= (3UL<<8); // Enable PWM output
  //wait(0.5);
}
void PWM_Stop(void){

	LPC_PWM1->PCR &= ~(3UL<<8); // disable PWM output
}

void Xbee_Init(void){
	rst1 = 0;
	wait_ms(1);
	rst1 = 1;
	wait_ms(1);
}

void main(void){
	char c;
	int angle;
	char CORRECT_FLAG = 50;
	char angle_char;
	
	Xbee_Init();
	IMU_Init();
	//PWM_Init();

while(1){
	
	angle = UpdateInclination();
	char angle_char[4];
  sprintf(angle_char,"%d",angle);
	//len = sizeof(angle_char);
	//pc.printf("current angle = %s",angle_char);
	//pc.printf("\r\n");

	//for(int i=0;i<)printf("%c", angle_char);
  xbee1.printf("%s.",angle_char);
	xbee1.putc(char_angle);
	//xbee1.putc('\r\n');
	//xbee1.putc(angle_char);
	//pc.printf("Current Angle = %d\r\n",angle);
	
	if(xbee1.readable()){
	   CORRECT_FLAG = xbee1.getc();
		 pc.printf("Flag = %d\r\n",CORRECT_FLAG);		
		 pc.printf("\r\n");
     		
		if(CORRECT_FLAG == 50 && motor == 0){ //error
			//PWM_Start();
			//motor = 1;
			wait(0.15);
			motor = 0;
			//PWM_Stop();
		}
		if(CORRECT_FLAG == 49){
		  //PWM_Stop();
			motor = 0;
    }
		//else //printf("Results cannot be read\r\n");
  }
	
	//c = pc.getc();
  //xbee1.putc(c);
	//pc.putc(c);
	
	//if(xbee1.readable()){
	   //a = xbee1.getc();
		// pc.putc(a);
 // }
  wait(0.1);
}
}

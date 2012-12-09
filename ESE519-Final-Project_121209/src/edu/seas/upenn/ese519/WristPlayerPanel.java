package edu.seas.upenn.ese519;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class WristPlayerPanel extends PlayerView {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double shoulderangle;
	private double elbowangle;
	private double wristangle;
	private int lrotateangle;
	private int rrotateangle;
	private Point lelbow;
	private Point relbow;
	private Point lwrist;
	private Point rwrist;
	private Point lfinger;
	private Point rfinger;

	// /** Milliseconds to block while waiting for port open */
	// private static final int TIME_OUT = 2000;
	//
	// /** Default bits per second for COM port /mbed */
	// private static final int DATA_RATE = 9600;

	private Point lshoulder;
	private Point rshoulder;
	private int uarmlen = 50;
	private int larmlen = 0;
	private int handlen = 30;

	private boolean startFlag = false;
	private int updateRate;

	private String wriststr = "Wrist Rotation Angle:  ";

	private DataGenerator dataGen;

	public WristPlayerPanel(int updateRate) {
		this.updateRate = updateRate;
		initModel();
	}

	public WristPlayerPanel(int updateRate, DataGenerator dataGen) {
		this.updateRate = updateRate;
		this.dataGen = dataGen;
		initModel();
	}

	public void initModel() {
		lelbow = new Point();
		relbow = new Point();
		lwrist = new Point();
		rwrist = new Point();
		lfinger = new Point();
		rfinger = new Point();
		lshoulder = new Point(295, 100);
		rshoulder = new Point(345, 100);

		shoulderangle = Math.toRadians(0.0);
		elbowangle = Math.toRadians(0.0);
		wristangle = Math.toRadians(0.0);
		lrotateangle = 0;
		rrotateangle = 0;
		lelbow.setLocation(
				lshoulder.getX() - Math.cos(shoulderangle) * uarmlen,
				lshoulder.getY() - Math.sin(shoulderangle) * uarmlen);
		relbow.setLocation(
				rshoulder.getX() + Math.cos(shoulderangle) * uarmlen,
				rshoulder.getY() - Math.sin(shoulderangle) * uarmlen);
		lwrist.setLocation(lelbow.getX() - Math.cos(elbowangle) * larmlen,
				lelbow.getY() - Math.sin(elbowangle) * larmlen);
		rwrist.setLocation(relbow.getX() + Math.cos(elbowangle) * larmlen,
				relbow.getY() - Math.sin(elbowangle) * larmlen);
		lfinger.setLocation(lwrist.getX() - Math.cos(wristangle) * handlen,
				lwrist.getY() - Math.sin(wristangle) * handlen);
		rfinger.setLocation(rwrist.getX() + Math.cos(wristangle) * handlen,
				rwrist.getY() - Math.sin(wristangle) * handlen);
	}

	public void update() {
		ArrayList<Double> anglelist = dataGen.getRealTimeAngles();
		double temp = Math.toDegrees(anglelist.get(4));
		lrotateangle = (int) temp;
		// System.out.println("current angle: " + lrotateangle);
		// lrotateangle = serialComm.getInputAngle();

		lfinger.setLocation(
				lwrist.getX() - Math.cos(Math.toRadians(90 - lrotateangle))
						* handlen,
				lwrist.getY() - Math.sin(Math.toRadians(90 - lrotateangle))
						* handlen);
		rfinger.setLocation(
				rwrist.getX() + Math.cos(Math.toRadians(90 + rrotateangle))
						* handlen,
				rwrist.getY() - Math.sin(Math.toRadians(90 + rrotateangle))
						* handlen);
	}

	public ArrayList<Double> getAngels() {
		ArrayList<Double> list = new ArrayList<Double>();
		list.add(Math.toRadians((double) lrotateangle));
		return list;
	}

	@Override
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, Constants.MotionViewer_LENGTH,
				Constants.MotionViewer_HEIGHT);
		int lshoulderX = (int) lshoulder.getX();
		int lshoulderY = (int) lshoulder.getY();
		int rshoulderX = (int) rshoulder.getX();
		int rshoulderY = (int) rshoulder.getY();
		int lelbowX = (int) lelbow.getX();
		int lelbowY = (int) lelbow.getY();
		int relbowX = (int) relbow.getX();
		int relbowY = (int) relbow.getY();
		int lwristX = (int) lwrist.getX();
		int lwristY = (int) lwrist.getY();
		int rwristX = (int) rwrist.getX();
		int rwristY = (int) rwrist.getY();
		int lfingerX = (int) lfinger.getX();
		int lfingerY = (int) lfinger.getY();
		int rfingerX = (int) rfinger.getX();
		int rfingerY = (int) rfinger.getY();

		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(3));
		g2d.setColor(Color.BLUE);
		g2d.drawOval(300, 30, 40, 40);
		g2d.setColor(Color.RED);
		g2d.drawLine(320, 70, 320, 200);
		g2d.drawLine(lshoulderX, lshoulderY, rshoulderX, rshoulderY);
		g2d.setColor(Color.BLUE);
		g2d.drawOval(310, 200, 20, 20);
		g2d.setColor(Color.RED);
		g2d.drawLine(320, 210, 250, 280);
		g2d.drawLine(320, 210, 390, 280);
		g2d.setColor(Color.BLUE);
		g2d.drawOval(lshoulderX - 5, lshoulderY - 5, 10, 10);
		g2d.drawOval(rshoulderX - 5, rshoulderY - 5, 10, 10);
		g2d.setColor(Color.RED);
		g2d.drawLine(lshoulderX, lshoulderY, lelbowX, lelbowY);
		g2d.drawLine(rshoulderX, rshoulderY, relbowX, relbowY);
		g2d.setColor(Color.BLUE);
		g2d.drawOval(lelbowX - 5, lelbowY - 5, 10, 10);
		g2d.drawOval(relbowX - 5, relbowY - 5, 10, 10);
		g2d.setColor(Color.RED);
		g2d.drawLine(lelbowX, lelbowY, lwristX, lwristY);
		g2d.drawLine(relbowX, relbowY, rwristX, rwristY);
		g2d.setColor(Color.BLUE);
		g2d.drawOval(lwristX - 5, lwristY - 5, 10, 10);
		g2d.drawOval(rwristX - 5, rwristY - 5, 10, 10);
		g2d.setColor(Color.RED);
		g2d.drawLine(lwristX, lwristY, lfingerX, lfingerY);
		g2d.drawLine(rwristX, rwristY, rfingerX, rfingerY);
		g2d.setColor(Color.BLACK);
		g2d.drawArc(lwristX - 15, lwristY - 30, 60, 60, 90, lrotateangle);
		g2d.drawArc(rwristX - 15, rwristY - 30, 60, 60, 90, rrotateangle);
		g2d.setColor(Color.BLACK);
		g2d.drawString(wriststr, 100, 350);
		g2d.drawString(Double.toString(lrotateangle), 250, 350);
	}

	// void connect() throws Exception
	// {
	// if(portIdentifier == null){
	// portIdentifier = CommPortIdentifier.getPortIdentifier("/dev/ttyACM0");
	// if ( portIdentifier.isCurrentlyOwned() )
	// {
	// System.out.println("Error: Port is currently in use");
	// }
	// else
	// {
	// CommPort commPort =
	// portIdentifier.open(this.getClass().getName(),TIME_OUT);
	//
	// if ( commPort instanceof SerialPort )
	// {
	// SerialPort serialPort = (SerialPort) commPort;
	// serialPort.setSerialPortParams(DATA_RATE,
	// SerialPort.DATABITS_8,
	// SerialPort.STOPBITS_1,
	// SerialPort.PARITY_NONE);
	//
	// in = serialPort.getInputStream();
	// }
	// else
	// {
	// System.out.println("Error: Only serial ports are handled by this example.");
	// }
	// }
	// }
	// }
	//
	// public void run ()
	// {
	// byte[] buffer = new byte[1024];
	// int len = -1;
	// startFlag = true;
	// try
	// {
	// while ( ( len = this.in.read(buffer)) > -1 && startFlag)
	// {
	// // if(buffer[len] == '\n' || buffer[len] == '\r')
	// // buffer[len] = '\0';
	// String temp = new String(buffer,0,len);
	// //System.out.print(new String(buffer,0,len));
	// Scanner fi = new Scanner(temp);
	// //anything other than alphanumberic characters,
	// //comma, dot or negative sign is skipped
	// fi.useDelimiter("[^\\p{Alnum},\\.-]");
	// int curAngle = lrotateangle;
	// if (fi.hasNextInt()){
	// curAngle = fi.nextInt();
	// // System.out.println("Int: " + curAngle);
	// }
	//
	// if(lrotateangle != curAngle){
	// lrotateangle = curAngle;
	// // System.out.println("Angle Changed to "+ curAngle);
	// update();
	// repaint();
	// }
	// }
	// }
	// catch ( IOException e )
	// {
	// e.printStackTrace();
	// }
	// }

	public void run() {
		startFlag = true;
		// serialComm.startReading();
		while (startFlag) {
			try {

				this.update();
				this.repaint();
				// Thread.sleep(updateRate);
			}
			// catch (InterruptedException e) {
			// System.out.println(e.getMessage());
			// }
			catch (RuntimeException e) {
				// System.out.print(e.getMessage());
			}
		}
	}

	public void setStop() {
		this.startFlag = false;
		// serialComm.stopReading();
	}

	public boolean isRunning() {
		return this.startFlag;
	}

}

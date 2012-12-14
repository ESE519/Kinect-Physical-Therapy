package edu.seas.upenn.ese519;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class PlayerPanel extends PlayerView{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9180141622349978180L;
	private double rshoulderangle;
	private double relbowangle;
	private double rwristangle;
	private double lshoulderangle;
	private double lelbowangle;
	private double lwristangle;
	private Point lelbow;
	private Point relbow;
	private Point lwrist;
	private Point rwrist;
	private Point lfinger;
	private Point rfinger;
//	private BufferedReader input;
	private DataGenerator dataGen;

	private Point lshoulder = new Point(295, 100);
	private Point rshoulder = new Point(345, 100);
	private int uarmlen = 50;
	private int larmlen = 50;
	private int handlen = 10;
//	private static String INPUT_PATH = "/home/qyr1987/workspace/ESE519-Final/codes/kinect/out.txt";
	
	private boolean startFlag = false;
	private int updateRate;
	
	private String lshoulderstr = "Left Shoulder Angle:  ";
	private String lelbowstr = "Left Elbow Angle:  ";
	

	public void initModel() {
		lelbow = new Point();
		relbow = new Point();
		lwrist = new Point();
		rwrist = new Point();
		lfinger = new Point();
		rfinger = new Point();		

		lshoulderangle = Math.toRadians(0.0);
		lelbowangle = Math.toRadians(90.0);
		lwristangle = Math.toRadians(0.0);
		rshoulderangle = Math.toRadians(0.0);
		relbowangle = Math.toRadians(90.0);
		rwristangle = Math.toRadians(0.0);
		lelbow.setLocation(
				lshoulder.getX() - Math.cos(lshoulderangle) * uarmlen,
				lshoulder.getY() - Math.sin(lshoulderangle) * uarmlen);
		relbow.setLocation(
				rshoulder.getX() + Math.cos(rshoulderangle) * uarmlen,
				rshoulder.getY() - Math.sin(rshoulderangle) * uarmlen);
		lwrist.setLocation(lelbow.getX() - Math.cos(lelbowangle) * larmlen,
				lelbow.getY() - Math.sin(lelbowangle) * larmlen);
		rwrist.setLocation(relbow.getX() + Math.cos(relbowangle) * larmlen,
				relbow.getY() - Math.sin(relbowangle) * larmlen);
		lfinger.setLocation(lwrist.getX() - Math.cos(lwristangle) * handlen,
				lwrist.getY() - Math.sin(lwristangle) * handlen);
		rfinger.setLocation(rwrist.getX() + Math.cos(rwristangle) * handlen,
				rwrist.getY() - Math.sin(rwristangle) * handlen);
	}
	
	public void update(){
		ArrayList<Double> anglelist = dataGen.getRealTimeAngles();
		
		lelbowangle = anglelist.get(0);
		lshoulderangle = anglelist.get(1);
		rshoulderangle = anglelist.get(2);
		relbowangle = anglelist.get(3);
//		input = new BufferedReader(new FileReader(INPUT_PATH));
//		if(input == null)
//			throw new IOException("File Not Valid");
//		
//		String line = null;
//		ArrayList<String> dataList = new ArrayList<String>();
//		while((line = input.readLine()) != null){
//			dataList.add(line);
//		}
//		
//		if(dataList.size() != 7)
//			throw new IOException("File Not Valid Format");
//		
//		String angelLine = dataList.get(6);
//		String[] angels = angelLine.split(" ");
//		if(angels.length != 4)
//			throw new IOException("File Not Valid Format, Angles Not Correct");
		
//		double tempRShoulderAngle = Double.parseDouble(angels[0]);
//		if(tempRShoulderAngle > 0)
//			rshoulderangle = (tempRShoulderAngle > Math.PI/2) ? (Math.PI - tempRShoulderAngle) : tempRShoulderAngle;
//		if(tempRShoulderAngle < 0)
//			rshoulderangle = (Math.abs(tempRShoulderAngle) > Math.PI/2) ? (Math.PI + tempRShoulderAngle) : tempRShoulderAngle;
//		else
//			rshoulderangle = tempRShoulderAngle;
		
//		rshoulderangle = Double.parseDouble(angels[0]) + Math.PI;
//		relbowangle = Double.parseDouble(angels[1]) + Math.PI;
//		lshoulderangle = Double.parseDouble(angels[2]);
//		lelbowangle = Double.parseDouble(angels[3]);
		//System.out.println(" " +  Math.toDegrees(rshoulderangle) + " " +  Math.toDegrees(relbowangle) + " " +  Math.toDegrees(lshoulderangle) + " " +  Math.toDegrees(lelbowangle));
		
		lelbow.setLocation(
				lshoulder.getX() - Math.cos(lshoulderangle) * uarmlen,
				lshoulder.getY() - Math.sin(lshoulderangle) * uarmlen);
		relbow.setLocation(
				rshoulder.getX() + Math.cos(rshoulderangle) * uarmlen,
				rshoulder.getY() - Math.sin(rshoulderangle) * uarmlen);
		lwrist.setLocation(lelbow.getX() - Math.cos(lelbowangle) * larmlen,
				lelbow.getY() - Math.sin(lelbowangle) * larmlen);
		rwrist.setLocation(relbow.getX() + Math.cos(relbowangle) * larmlen,
				relbow.getY() - Math.sin(relbowangle) * larmlen);
		lfinger.setLocation(lwrist.getX() - Math.cos(lwristangle) * handlen,
				lwrist.getY() - Math.sin(lwristangle) * handlen);
		rfinger.setLocation(rwrist.getX() + Math.cos(rwristangle) * handlen,
				rwrist.getY() - Math.sin(rwristangle) * handlen);
	}
	
	public PlayerPanel(){}

	public PlayerPanel(int updateRate) {
		initModel();
		this.updateRate = updateRate;
	}
	
	public PlayerPanel(int updateRate, DataGenerator dataGen){
		initModel();
		this.updateRate = updateRate;
		this.dataGen = dataGen;
	}
	
	public ArrayList<Double> getAngels(){
		ArrayList<Double> list = new ArrayList<Double>();
		list.add(lelbowangle);
		list.add(lshoulderangle);
		list.add(relbowangle); 
		list.add(rshoulderangle);
		return list;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
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
		g2d.drawString(lshoulderstr, 100, 350);
		g2d.drawString(lelbowstr, 100, 370);
		g2d.drawString(Double.toString(Math.toDegrees(lshoulderangle)), 250, 350);
		g2d.drawString(Double.toString(Math.toDegrees(lelbowangle)), 250, 370);
	}

	@Override
	public void run() {
		startFlag = true;
		while (startFlag) {
    		try {
    			this.update();
    			this.repaint();
    			Thread.sleep(updateRate);
    		}catch (InterruptedException e) {
    			System.out.println(e.getMessage());
    		}catch (RuntimeException e) {
				//System.out.print(e.getMessage());
			}
    	} 				
	}
	
	public void setStop(){
		this.startFlag = false;
	}
	
	public boolean isRunning(){
		return this.startFlag;
	}

}

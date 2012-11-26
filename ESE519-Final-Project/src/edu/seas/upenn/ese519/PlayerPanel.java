package edu.seas.upenn.ese519;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JPanel;

public class PlayerPanel extends JPanel implements Runnable{
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
	private BufferedReader input;

	private static Point lshoulder = new Point(295, 100);
	private static Point rshoulder = new Point(345, 100);
	private static int uarmlen = 50;
	private static int larmlen = 50;
	private static int handlen = 10;
	private static String INPUT_PATH = "/home/qyr1987/workspace/ESE519-Final/codes/kinect/out.txt";
	
	private boolean startFlag = false;
	private int updateRate;

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
	
	public void update() throws IOException {
		ArrayList<Double> anglelist = OverlayPanel.getRealTimeAngles();
		
		lshoulderangle = anglelist.get(0);
		lelbowangle = anglelist.get(1);
		rshoulderangle = anglelist.get(3);
		relbowangle = anglelist.get(4);
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

	public PlayerPanel(int updateRate) {
		initModel();
		this.updateRate = updateRate;
	}
	
	public ArrayList<Double> getAngels(){
		ArrayList<Double> list = new ArrayList<Double>();
		list.add(lshoulderangle);
		list.add(lelbowangle);
		list.add(rshoulderangle);
		list.add(relbowangle); 
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

		g.setColor(Color.BLUE);
		g.drawOval(300, 30, 40, 40);
		g.setColor(Color.RED);
		g.drawLine(320, 70, 320, 200);
		g.drawLine(lshoulderX, lshoulderY, rshoulderX, rshoulderY);
		g.setColor(Color.BLUE);
		g.drawOval(lshoulderX - 5, lshoulderY - 5, 10, 10);
		g.drawOval(rshoulderX - 5, rshoulderY - 5, 10, 10);
		g.setColor(Color.RED);
		g.drawLine(lshoulderX, lshoulderY, lelbowX, lelbowY);
		g.drawLine(rshoulderX, rshoulderY, relbowX, relbowY);
		g.setColor(Color.BLUE);
		g.drawOval(lelbowX - 5, lelbowY - 5, 10, 10);
		g.drawOval(relbowX - 5, relbowY - 5, 10, 10);
		g.setColor(Color.RED);
		g.drawLine(lelbowX, lelbowY, lwristX, lwristY);
		g.drawLine(relbowX, relbowY, rwristX, rwristY);
		g.setColor(Color.BLUE);
		g.drawOval(lwristX - 5, lwristY - 5, 10, 10);
		g.drawOval(rwristX - 5, rwristY - 5, 10, 10);
		g.setColor(Color.RED);
		g.drawLine(lwristX, lwristY, lfingerX, lfingerY);
		g.drawLine(rwristX, rwristY, rfingerX, rfingerY);
	}

	@Override
	public void run() {
		startFlag = true;
		while (startFlag) {
    		try {
    			this.update();
    			this.repaint();
    			Thread.sleep(updateRate);
    		}catch(IOException e){
    			System.out.println(e.getMessage());
    		}catch (InterruptedException e) {
    			System.out.println(e.getMessage());
    		}catch (RuntimeException e) {
				//System.out.print(e.getMessage());
			}
    	} 				
	}
	
	public void setStop(){
		this.startFlag = !this.startFlag;
	}
	
	public boolean isRunning(){
		return this.startFlag;
	}

}

package layout;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

public class SimDraw extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2813306048505989562L;
	private double shoulderangle;
	private double elbowangle;
	private double wristangle;
	private Point lelbow;
	private Point relbow;
	private Point lwrist;
	private Point rwrist;
	private Point lfinger;
	private Point rfinger;
	private boolean sflag;
	private boolean eflag;
	private boolean wflag;

	private static double shoulderanglemax = Math.toRadians(90.0);
	private static double elbowanglemax = Math.toRadians(180.0);
	private static double wristanglemax = Math.toRadians(45.0);
	private static Point lshoulder = new Point(295, 100);
	private static Point rshoulder = new Point(345, 100);
	private static int uarmlen = 50;
	private static int larmlen = 50;
	private static int handlen = 10;

	public void initModel() {
		sflag = true;
		eflag = true;
		wflag = true;
		lelbow = new Point();
		relbow = new Point();
		lwrist = new Point();
		rwrist = new Point();
		lfinger = new Point();
		rfinger = new Point();

		shoulderangle = Math.toRadians(0.0);
		elbowangle = Math.toRadians(90.0);
		wristangle = Math.toRadians(0.0);
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
	
	public void update(){
		if (sflag){
			shoulderangle += Math.toRadians(10.0);
			if (shoulderangle > shoulderanglemax){
				shoulderangle = shoulderanglemax;
				sflag = false;
			}
		}
		else{
			shoulderangle -= Math.toRadians(10.0);
			if (shoulderangle < 0){
				shoulderangle = 0;
				sflag = true;
			}		
		}
		
		if (eflag){
			elbowangle += Math.toRadians(5.0);
			if (elbowangle > elbowanglemax){
				elbowangle = elbowanglemax;
				eflag = false;
			}
		}
		else{
			elbowangle -= Math.toRadians(5.0);
			if (elbowangle < 0){
				elbowangle = 0;
				eflag = true;
			}		
		}
		
		if (wflag){
			//++
			if (wristangle > wristanglemax){
				wristangle = wristanglemax;
				wflag = false;
			}
		}
		else{
			//--
			if (wristangle < 0){
				wristangle = 0;
				wflag = true;
			}		
		}
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
		
		repaint();
	}

	public SimDraw() {
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
}

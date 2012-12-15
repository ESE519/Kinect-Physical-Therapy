package edu.seas.upenn.ese519;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class WristTestSampleView extends SampleView{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2813306048505989562L;
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
	private boolean cflag = false;
	private int count = 0;

	
	private boolean startFlag = false;
	private int updateRate;
	private int repetitions = Constants.DEFAULT_REPETITION;
	private int repetitionCounts;

	private char[] carray = { 'R', 'o', 't', 'a', 't', 'e', ' ', 'W', 'r', 'i',
			's', 't' };
	private String wriststr = "Wrist Rotation Angle:  ";

	public void initModel() {
		super.lrotateanglemax = 180;
		super.lrotateanglemin = -90;
		super.rrotateanglemax = 90;
		super.rrotateanglemin = -180;
		super.lshoulder = new Point(295, 100);
		super.rshoulder = new Point(345, 100);
		super.uarmlen = 50;
		super.larmlen = 0;
		super.handlen = 30;
		super.shoulderUsed = false;
		super.elbowUsed = false;
		super.wristUsed = true;
		super.angleUpdateRate = 1;
		
		lrflag = true;
		rrflag = true;
		lelbow = new Point();
		relbow = new Point();
		lwrist = new Point();
		rwrist = new Point();
		lfinger = new Point();
		rfinger = new Point();
		repetitionCounts = 0;

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
		if (lrflag) {
			lrotateangle += angleUpdateRate;
			if (lrotateangle > lrotateanglemax) {
				lrotateangle = lrotateanglemax;
				lrflag = false;
			}
		} else {
			lrotateangle -= angleUpdateRate;
			if (lrotateangle < lrotateanglemin) {
				lrotateangle = lrotateanglemin;
				lrflag = true;
				repetitionCounts++;
			}
		}

		if (rrflag) {
			rrotateangle -= angleUpdateRate;
			if (rrotateangle < rrotateanglemin) {
				rrotateangle = rrotateanglemin;
				rrflag = false;
			}
		} else {
			rrotateangle += angleUpdateRate;
			if (rrotateangle > rrotateanglemax) {
				rrotateangle = rrotateanglemax;
				rrflag = true;
			}
		}

		count++;
		if (count == 3) {
			cflag = !cflag;
			count = 0;
		}

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

	public WristTestSampleView(int updateRate) {
		initModel();
		this.updateRate = updateRate;
	}

	public ArrayList<Double> getAngels() {
		ArrayList<Double> list = new ArrayList<Double>();
		list.add(Math.toRadians((double)lrotateangle));
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
		g2d.drawArc(lwristX - 30, lwristY - 30, 60, 60, 90, lrotateangle);
		g2d.drawArc(rwristX - 30, rwristY - 30, 60, 60, 90, rrotateangle);
		if (cflag) {
			g2d.drawChars(carray, 0, carray.length, lwristX - 30, lwristY - 30);
			g2d.drawChars(carray, 0, carray.length, rwristX - 30, rwristY - 30);
		}
		g2d.setColor(Color.BLACK);
		g2d.drawString(wriststr, 100, 350);
		g2d.drawString(Double.toString(lrotateangle), 250, 350);
	}

	@Override
	public void run() {
		startFlag = true;
		while (startFlag && repetitionCounts <= repetitions) {
			try {
				this.update();
				this.repaint();
				Thread.sleep(updateRate);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			} catch (RuntimeException e) {
				// System.out.print(e.getMessage());
			}
		}
	}

	public void setStop() {
		this.startFlag = false;
	}

	public boolean isRunning() {
		return this.startFlag;
	}
	
	public int getRepititions(){
		return this.repetitions;
	}
	
	public void setRepititions(int repetitions){
		this.repetitions = repetitions;
	}
	
	public int getRepititionCounts(){
		return this.repetitionCounts;
	}
	
}

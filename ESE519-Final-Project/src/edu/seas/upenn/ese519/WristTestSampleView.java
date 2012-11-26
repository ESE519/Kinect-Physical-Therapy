package edu.seas.upenn.ese519;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

public class WristTestSampleView extends SampleView implements Runnable {
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
	private boolean lrflag;
	private boolean rrflag;
	private boolean cflag = false;
	private int count = 0;

	private static int lrotateanglemax = 90;
	private static int lrotateanglemin = -180;
	private static int rrotateanglemax = 180;
	private static int rrotateanglemin = -90;
	private static int anglechangerate = 5;
	private static Point lshoulder = new Point(295, 100);
	private static Point rshoulder = new Point(345, 100);
	private static int uarmlen = 50;
	private static int larmlen = 0;
	private static int handlen = 15;

	private boolean startFlag = false;
	private int updateRate;

	private char[] carray = { 'R', 'o', 't', 'a', 't', 'e', ' ', 'W', 'r', 'i',
			's', 't' };
	
	public int getAngChgRt(){
		return anglechangerate;
	}

	public void initModel() {
		lrflag = true;
		rrflag = true;
		lelbow = new Point();
		relbow = new Point();
		lwrist = new Point();
		rwrist = new Point();
		lfinger = new Point();
		rfinger = new Point();

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
			lrotateangle += anglechangerate;
			if (lrotateangle > lrotateanglemax) {
				lrotateangle = lrotateanglemax;
				lrflag = false;
			}
		} else {
			lrotateangle -= anglechangerate;
			if (lrotateangle < lrotateanglemin) {
				lrotateangle = lrotateanglemin;
				lrflag = true;
			}
		}

		if (rrflag) {
			rrotateangle -= anglechangerate;
			if (rrotateangle < rrotateanglemin) {
				rrotateangle = rrotateanglemin;
				rrflag = false;
			}
		} else {
			rrotateangle += anglechangerate;
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
		list.add(shoulderangle);
		list.add(elbowangle);
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
		g.setColor(Color.BLACK);
		g.drawArc(lwristX - 15, lwristY - 15, 30, 30, 90, lrotateangle);
		g.drawArc(rwristX - 15, rwristY - 15, 30, 30, 90, rrotateangle);
		if (cflag) {
			g.drawChars(carray, 0, carray.length, lwristX - 30, lwristY - 30);
			g.drawChars(carray, 0, carray.length, rwristX - 30, rwristY - 30);
		}
	}

	@Override
	public void run() {
		startFlag = true;
		while (startFlag) {
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

}

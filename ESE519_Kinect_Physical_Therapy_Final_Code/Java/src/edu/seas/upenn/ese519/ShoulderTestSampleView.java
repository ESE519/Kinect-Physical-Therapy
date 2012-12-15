package edu.seas.upenn.ese519;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class ShoulderTestSampleView extends SampleView {
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

	private boolean startFlag = false;
	private int updateRate;
	private int repetitions = Constants.DEFAULT_REPETITION;
	private int repetitionCounts;

	private String shoulderstr = "Shoulder Angle:  ";
	private String elbowstr = "Elbow Angle:  ";

	public void initModel() {
		super.shoulderanglemax = Math.toRadians(90.0);
		super.elbowanglemax = Math.toRadians(90.0);
		super.shoulderanglemin = Math.toRadians(-90.0);
		super.elbowanglemin = Math.toRadians(-90.0);
		super.shoulderUsed = true;
		super.elbowUsed = true;
		super.wristUsed = false;
		super.angleUpdateRate = Math.toRadians(5.0);
		super.lshoulder = new Point(295, 100);
		super.rshoulder = new Point(345, 100);
		super.uarmlen = 50;
		super.larmlen = 50;
		super.handlen = 10;
		sflag = true;
		eflag = true;
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
		if (sflag) {
			shoulderangle += angleUpdateRate;
			if (shoulderangle > shoulderanglemax) {
				shoulderangle = shoulderanglemax;
				sflag = false;
			}
		} else {
			shoulderangle -= angleUpdateRate;
			if (shoulderangle < shoulderanglemin) {
				shoulderangle = shoulderanglemin;
				sflag = true;
				repetitionCounts++;
			}
		}

		if (eflag) {
			elbowangle += angleUpdateRate;
			if (elbowangle > elbowanglemax) {
				elbowangle = elbowanglemax;
				eflag = false;
			}
		} else {
			elbowangle -= angleUpdateRate;
			if (elbowangle < elbowanglemin) {
				elbowangle = elbowanglemin;
				eflag = true;
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

		// repaint();
	}

	public ShoulderTestSampleView(int updateRate) {
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
		g2d.drawString(shoulderstr, 100, 350);
		g2d.drawString(elbowstr, 100, 370);
		g2d.drawString(Double.toString(Math.toDegrees(shoulderangle)), 250, 350);
		g2d.drawString(Double.toString(Math.toDegrees(elbowangle)), 250, 370);
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

	public int getRepititions() {
		return this.repetitions;
	}

	public void setRepititions(int repetitions) {
		this.repetitions = repetitions;
	}

	public int getRepititionCounts() {
		return this.repetitionCounts;
	}

}

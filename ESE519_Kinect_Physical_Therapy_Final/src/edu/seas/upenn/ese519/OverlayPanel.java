package edu.seas.upenn.ese519;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JPanel;

public class OverlayPanel extends JPanel implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DataGenerator dataGen;
	private SampleView sampleView;
	private boolean isRunning = false;
	public int exercise = 0;
	private int updateRate;

	// used for the average ms processing information
	private int imageCount = 0;
	private long totalTime = 0;
	private DecimalFormat df;
	private Font msgFont;

	private double shoulderangle;
	private double elbowangle;
	private int lrotateangle;
	private int rrotateangle;
	private Point lelbow;
	private Point relbow;
	private Point lwrist;
	private Point rwrist;
	private Point lfinger;
	private Point rfinger;
	public boolean sflag;
	public boolean eflag;
	public boolean lrflag;
	private boolean rrflag;

	private double shoulderanglemax;
	private double elbowanglemax;
	private double shoulderanglemin;
	private double elbowanglemin;

	private int lrotateanglemax;
	private int lrotateanglemin;
	private int rrotateanglemax;
	private int rrotateanglemin;

	public boolean shoulderUsed;
	public boolean elbowUsed;
	public boolean wristUsed;
	public double angleUpdateRate;
	private Point lshoulder;
	private Point rshoulder;
	private int uarmlen;
	private int larmlen;
	private int handlen;

	private double tolerance = Math.toDegrees(Constants.TOLERANCE);

	private Point marginlElbowUpper;
	private Point marginlHandUpper;
	private Point marginlElbowLower;
	private Point marginlHandLower;

	private Point marginrElbowUpper;
	private Point marginrHandUpper;
	private Point marginrElbowLower;
	private Point marginrHandLower;
	private double marginLenUpper = 50;
	private double marginLenLower = 50;

	private int repetitions = Constants.DEFAULT_REPETITION;
	private int repetitionCounts;

	public OverlayPanel(int updateRate) {
		this.updateRate = updateRate;
		setBackground(Color.WHITE);
		initModel();

		df = new DecimalFormat("0.#"); // 1 dp
		msgFont = new Font("SansSerif", Font.BOLD, 18);

	} // end of ViewerPanel()

	public OverlayPanel(int updateRate, SampleView sampleView,
			DataGenerator dataGen) {
		this.sampleView = sampleView;
		this.updateRate = updateRate;
		this.dataGen = dataGen;
		setBackground(Color.WHITE);
		initModel();

		df = new DecimalFormat("0.#"); // 1 dp
		msgFont = new Font("SansSerif", Font.BOLD, 18);

	} // end of ViewerPanel()

	public void initModel() {
		lelbow = new Point();
		relbow = new Point();
		lwrist = new Point();
		rwrist = new Point();
		lfinger = new Point();
		rfinger = new Point();
		repetitionCounts = 0;
		imageCount = 0;

		marginlElbowUpper = new Point();
		marginlHandUpper = new Point();
		marginlElbowLower = new Point();
		marginlHandLower = new Point();

		marginrElbowUpper = new Point();
		marginrHandUpper = new Point();
		marginrElbowLower = new Point();
		marginrHandLower = new Point();

		shoulderangle = Math.toRadians(0.0);
		if (exercise == Constants.FOREARMTEST) {
			elbowangle = Math.toRadians(90.0);
		} else {
			elbowangle = Math.toRadians(0.0);
		}
		lrotateangle = 0;
		rrotateangle = 0;

		// lshoulderangle = Math.toRadians(0.0);
		// lelbowangle = Math.toRadians(90.0);
		// lwristangle = Math.toRadians(0.0);
		// rshoulderangle = Math.toRadians(0.0);
		// relbowangle = Math.toRadians(90.0);
		// rwristangle = Math.toRadians(0.0);
		setToSampleParams();
		setJointsPosition();
	}

	public void setToSampleParams() {
		this.shoulderanglemax = sampleView.shoulderanglemax;
		this.elbowanglemax = sampleView.elbowanglemax;
		this.shoulderanglemin = sampleView.shoulderanglemin;
		this.elbowanglemin = sampleView.elbowanglemin;

		this.rrotateanglemax = sampleView.rrotateanglemax;
		this.rrotateanglemin = sampleView.rrotateanglemin;
		this.lrotateanglemax = sampleView.lrotateanglemax;
		this.lrotateanglemin = sampleView.lrotateanglemin;

		this.shoulderUsed = sampleView.shoulderUsed;
		this.elbowUsed = sampleView.elbowUsed;
		this.wristUsed = sampleView.wristUsed;
		this.angleUpdateRate = sampleView.angleUpdateRate;

		this.shoulderUsed = sampleView.shoulderUsed;
		this.elbowUsed = sampleView.elbowUsed;
		this.wristUsed = sampleView.wristUsed;

		this.lshoulder = sampleView.lshoulder;
		this.rshoulder = sampleView.rshoulder;

		this.uarmlen = sampleView.uarmlen;
		this.larmlen = sampleView.larmlen;
		this.handlen = sampleView.handlen;
	}

	@Override
	public void run()
	/*
	 * update and display the webcam image whenever the context is updated.
	 */
	{
		isRunning = true;
		while (isRunning && repetitionCounts <= repetitions) {
			try {
				updateSampleAngles();
				setJointsPosition();
				repaint();
				Thread.sleep(updateRate/2);
			} catch (InterruptedException e) {
				System.out.println(e);
			}
			long startTime = System.currentTimeMillis();
			imageCount++;
			totalTime += (System.currentTimeMillis() - startTime);
		}
	} // end of run()

	public void setStop() {
		isRunning = false;
	}

	public boolean isRunning() {
		return this.isRunning;
	}

	public void setJointsPosition() {
		Point[] shoulderPostions = dataGen.getCurrentShoulderPosition();
		lshoulder = shoulderPostions[0];
		rshoulder = shoulderPostions[1];

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

		if (exercise == Constants.FOREARMTEST) {
			double scaleFactorUpper = (elbowanglemax - elbowanglemin)
					/ (100 - 50) * 20;
			double scaleFactorLower = (elbowanglemax - elbowanglemin)
					/ (50 - 30) * 10;
			if (eflag) {
				marginLenUpper += scaleFactorUpper;
				if (marginLenUpper > 80)
					marginLenUpper = 80;
				marginLenLower -= scaleFactorLower;
				if (marginLenLower < 20)
					marginLenLower = 20;
			} else {
				marginLenUpper -= scaleFactorUpper;
				if (marginLenUpper < 50)
					marginLenUpper = 50;
				marginLenLower += scaleFactorLower;
				if (marginLenLower > 50)
					marginLenLower = 50;
			}
		}
		// marginLenUpper = 100;
		// marginLenLower = 30;

		marginlElbowUpper.setLocation(
				lshoulder.getX() - Math.cos(shoulderangle + tolerance)
						* marginLenUpper,
				lshoulder.getY() - Math.sin(shoulderangle + tolerance)
						* marginLenUpper);
		marginrElbowUpper.setLocation(
				rshoulder.getX() + Math.cos(shoulderangle + tolerance)
						* marginLenUpper,
				rshoulder.getY() - Math.sin(shoulderangle + tolerance)
						* marginLenUpper);
		marginlHandUpper.setLocation(
				marginlElbowUpper.getX() - Math.cos(elbowangle + tolerance)
						* marginLenUpper,
				marginlElbowUpper.getY() - Math.sin(elbowangle + tolerance)
						* marginLenUpper);
		marginrHandUpper.setLocation(
				marginrElbowUpper.getX() + Math.cos(elbowangle + tolerance)
						* marginLenUpper,
				marginrElbowUpper.getY() - Math.sin(elbowangle + tolerance)
						* marginLenUpper);
		// marginlHandUpper.setLocation(lelbow.getX() - Math.cos(elbowangle +
		// tolerance) * larmlen,
		// lelbow.getY() - Math.sin(elbowangle + tolerance) * larmlen);
		// marginrHandUpper.setLocation(relbow.getX() + Math.cos(elbowangle +
		// tolerance) * larmlen,
		// relbow.getY() - Math.sin(elbowangle + tolerance) * larmlen);

		marginlElbowLower.setLocation(
				lshoulder.getX() - Math.cos(shoulderangle - tolerance)
						* marginLenLower,
				lshoulder.getY() - Math.sin(shoulderangle - tolerance)
						* marginLenLower);
		marginrElbowLower.setLocation(
				rshoulder.getX() + Math.cos(shoulderangle - tolerance)
						* marginLenLower,
				rshoulder.getY() - Math.sin(shoulderangle - tolerance)
						* marginLenLower);
		marginlHandLower.setLocation(
				marginlElbowLower.getX() - Math.cos(elbowangle - tolerance)
						* marginLenLower,
				marginlElbowLower.getY() - Math.sin(elbowangle - tolerance)
						* marginLenLower);
		marginrHandLower.setLocation(
				marginrElbowLower.getX() + Math.cos(elbowangle - tolerance)
						* marginLenLower,
				marginrElbowLower.getY() - Math.sin(elbowangle - tolerance)
						* marginLenLower);
		// marginlHandLower.setLocation(lelbow.getX() - Math.cos(elbowangle -
		// tolerance) * larmlen,
		// lelbow.getY() - Math.sin(elbowangle - tolerance) * larmlen);
		// marginrHandLower.setLocation(relbow.getX() + Math.cos(elbowangle -
		// tolerance) * larmlen,
		// relbow.getY() - Math.sin(elbowangle - tolerance) * larmlen);
	}

	public void updateSampleAngles() {
		if (shoulderUsed) {
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
					if (exercise == Constants.SHOULDERTEST || exercise == Constants.ADAPTIVETEST)
						repetitionCounts++;
				}
			}
		}

		if (elbowUsed) {
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
					if (exercise == Constants.FOREARMTEST)
						repetitionCounts++;
				}
			}
		}

		if (wristUsed) {
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
					if (exercise == Constants.WRISTTEST)
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
		}
	}

	public ArrayList<Double> getSampleAngels() {
		ArrayList<Double> list = new ArrayList<Double>();
		if (exercise == Constants.FOREARMTEST) {
			list.add(elbowangle);
		} else if (exercise == Constants.SHOULDERTEST) {
			list.add(elbowangle);
			list.add(shoulderangle);
		} else if (exercise == Constants.WRISTTEST) {
			list.add(Math.toRadians((double) lrotateangle));
		} else if (exercise == Constants.ADAPTIVETEST) {
			if (elbowUsed) {
				list.add(elbowangle);
			}
			if (shoulderUsed) {
				list.add(shoulderangle);
			}
			if (wristUsed) {
				list.add(Math.toRadians((double) lrotateangle));
			}
		}
		return list;
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

	public void paintComponent(Graphics g)
	// Draw the depth image and statistics info
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		if (dataGen.isImageReady() && imageCount > 0)
			g2.drawImage(dataGen.getImage(), 0, 0, this);

		paintSample(g);
		writeStats(g2);
	} // end of paintComponent()

	public void paintSample(Graphics g) {
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

		int xpoints[] = { lshoulderX, (int) marginlElbowUpper.getX(),
				(int) marginlHandUpper.getX(), (int) marginlHandLower.getX(),
				(int) marginlElbowLower.getX() };
		int ypoints[] = { lshoulderY, (int) marginlElbowUpper.getY(),
				(int) marginlHandUpper.getY(), (int) marginlHandLower.getY(),
				(int) marginlElbowLower.getY() };
		int npoints = 5;

		g.setColor(Color.YELLOW);
		g.fillPolygon(xpoints, ypoints, npoints);

		int xrpoints[] = { rshoulderX, (int) marginrElbowUpper.getX(),
				(int) marginrHandUpper.getX(), (int) marginrHandLower.getX(),
				(int) marginrElbowLower.getX() };
		int yrpoints[] = { rshoulderY, (int) marginrElbowUpper.getY(),
				(int) marginrHandUpper.getY(), (int) marginrHandLower.getY(),
				(int) marginrElbowLower.getY() };
		int nrpoints = 5;

		g.setColor(Color.YELLOW);
		g.fillPolygon(xrpoints, yrpoints, nrpoints);

		int ShoulderCenterX = (int) (lshoulderX + rshoulderX) / 2;
		int ShoulderCenterY = (int) (lshoulderY + rshoulderY) / 2;
		g.setColor(Color.BLUE);
		// g.drawOval(300, 30, 40, 40);
		g.drawOval(ShoulderCenterX - 20, ShoulderCenterY - 70, 40, 40);
		g.setColor(Color.RED);
		// g.drawLine(320, 70, 320, 200);
		g.drawLine(ShoulderCenterX, ShoulderCenterY - 30, ShoulderCenterX,
				ShoulderCenterY + 100);
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

	private void writeStats(Graphics2D g2)
	/*
	 * write statistics in bottom-left corner, or "Loading" at start time
	 */
	{
		g2.setColor(Color.BLUE);
		g2.setFont(msgFont);
		int panelHeight = getHeight();
		if (imageCount > 0) {
			double avgGrabTime = (double) totalTime / imageCount;
			g2.drawString("Pic " + imageCount + "  " + df.format(avgGrabTime)
					+ " ms", 5, panelHeight - 10); // bottom left
		} else
			// no image yet
			g2.drawString("Loading...", 5, panelHeight - 10);
	} // end of writeStats()

}

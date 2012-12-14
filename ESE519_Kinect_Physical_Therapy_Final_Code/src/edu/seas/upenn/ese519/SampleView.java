package edu.seas.upenn.ese519;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JPanel;

public abstract class SampleView extends JPanel implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3781896455678535380L;
	public double shoulderanglemax;
	public double elbowanglemax;
	public double shoulderanglemin;
	public double elbowanglemin;
	public boolean shoulderUsed;
	public boolean elbowUsed;
	public boolean wristUsed;
	public double angleUpdateRate;

	public int lrotateanglemax;
	public int lrotateanglemin;
	public int rrotateanglemax;
	public int rrotateanglemin;

	public boolean sflag;
	public boolean eflag;
	public boolean lrflag;
	public boolean rrflag;

	public int uarmlen;
	public int larmlen;
	public int handlen;

	public Point lshoulder;
	public Point rshoulder;

	public abstract void initModel();

	public abstract void update();

	public abstract ArrayList<Double> getAngels();

	public abstract void setStop();

	public abstract boolean isRunning();

	public abstract int getRepititions();

	public abstract void setRepititions(int repetitions);

	public abstract int getRepititionCounts();
}

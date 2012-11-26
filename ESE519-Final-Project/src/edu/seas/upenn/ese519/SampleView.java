package edu.seas.upenn.ese519;

import java.util.ArrayList;

import javax.swing.JPanel;

public abstract class SampleView extends JPanel implements Runnable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3781896455678535380L;
	
	public abstract void initModel();
	
	public abstract void update();
	
	public abstract ArrayList<Double> getAngels();
	
	public abstract void setStop();
	
	public abstract boolean isRunning();
	
}

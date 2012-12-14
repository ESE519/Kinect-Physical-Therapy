package edu.seas.upenn.ese519;

import java.util.ArrayList;

import javax.swing.JPanel;

public abstract class PlayerView extends JPanel implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract void update();

	public abstract void initModel();

	public abstract ArrayList<Double> getAngels();

	public abstract void setStop();

	public abstract boolean isRunning();

}

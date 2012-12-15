package edu.seas.upenn.ese519;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class StasticView extends ChartPanel implements Runnable{

	public StasticView(JFreeChart chart) {
		super(chart);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private boolean startFlag = false;
	private int updateRate;
	
	private static final long serialVersionUID = 1261889547512863356L;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		startFlag = true;
		while (startFlag) {
    		try {
    			Thread.sleep(updateRate);
    		} catch (InterruptedException e) {
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
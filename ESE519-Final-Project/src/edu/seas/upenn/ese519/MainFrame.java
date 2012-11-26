package edu.seas.upenn.ese519;


import java.awt.BorderLayout;  
import java.awt.Color;
import java.awt.Dimension;  
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
  
import javax.swing.BorderFactory;  
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;  
import javax.swing.JFrame;  
import javax.swing.JLabel;  
import javax.swing.JPanel;  
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;  
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.OpenNI.StatusException;
  
  
public class MainFrame{	
	private JFrame theFrame;
	private PlayerPanel playerView;
	private JPanel judgementView;
	private SampleView sampleView;
	private JTabbedPane mainPanel;
	private JPanel showPanel;
	private OverlayPanel overlayPanel = new OverlayPanel();
	JRadioButton overlayMode;
	JRadioButton splitMode;
		
	private UpdateThread upThread;
	private Color bg;
	
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
		 MainFrame main=new MainFrame();  		 
		 main.buildGUI();
    }
    
    public void buildGUI() {
    	theFrame = new JFrame("Kinect For Physical Therapy");
    	theFrame.setLayout(new BorderLayout());
    	mainPanel = new JTabbedPane();
    	mainPanel.setPreferredSize(new Dimension(2 * Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT + 100));
    	
		JToolBar jtoolbar=new JToolBar();  
		JLabel jl=new JLabel("state");  
		jtoolbar.add(jl);  
		Box buttonBox = new Box(BoxLayout.Y_AXIS);
					
		overlayMode = new JRadioButton("Overlay Mode");
		splitMode = new JRadioButton("Split Mode");
		splitMode.setSelected(true);		
		
		buttonBox.add(overlayMode);
		buttonBox.add(splitMode);

		JButton start = new JButton("Start");
		start.setPreferredSize(new Dimension(Constants.BUTTON_LENGTH,Constants.BUTTON_WIDTH));
		start.addActionListener(new MyStartListener());
		buttonBox.add(start);
		
		JButton stop = new JButton("Stop");
		stop.setPreferredSize(new Dimension(Constants.BUTTON_LENGTH,Constants.BUTTON_WIDTH));
		stop.addActionListener(new MyStopListener());
		buttonBox.add(stop);
//		JButton lowerRate = new JButton("Lower Rate");
//		lowerRate.addActionListener(new LowerRateListener());
//		buttonBox.add(lowerRate);
		   
		JPanel forearmTestPanel = new JPanel(new BorderLayout());
		JPanel shoulderTestPanel = new JPanel(new BorderLayout());
		JPanel wristTestPanel = new JPanel(new BorderLayout());
		
		mainPanel.addTab("Left Forearm Stretch Test", forearmTestPanel);
		mainPanel.addTab("Shoulders Test", shoulderTestPanel);
		mainPanel.addTab("Wrist Test", wristTestPanel);
		mainPanel.setBorder(BorderFactory.createEtchedBorder()); 

		SimDraw forearmTestSampleView = new SimDraw(Constants.UPDATE_RATE);  
		forearmTestSampleView.setPreferredSize(new Dimension(Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT)); 
		forearmTestSampleView.setBorder(BorderFactory.createEtchedBorder());  
		JPanel forearmTestJudgementView = new JPanel();  
		forearmTestJudgementView.setPreferredSize(new Dimension(Constants.JUDGEMENT_LENGTH, Constants.JUDGEMENT_HEIGHT));
		PlayerPanel forearmTestPlayerView = new PlayerPanel(Constants.UPDATE_RATE);
		forearmTestPlayerView.setPreferredSize(new Dimension(Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT));		
		forearmTestJudgementView.setBorder(BorderFactory.createEtchedBorder());
		forearmTestPlayerView.setBorder(BorderFactory.createEtchedBorder());  
		//OverlayPanel forearmTestOverLayView = new OverlayPanel();
		
		forearmTestPanel.add(forearmTestSampleView,BorderLayout.WEST);  
		forearmTestPanel.add(forearmTestJudgementView,BorderLayout.NORTH); 
		forearmTestPanel.add(forearmTestPlayerView,BorderLayout.CENTER);
		//forearmTestPanel.add(forearmTestOverLayView,BorderLayout.EAST);

		ShoulderTestSampleView shoulderTestSampleView = new ShoulderTestSampleView(Constants.UPDATE_RATE);  
		shoulderTestSampleView.setPreferredSize(new Dimension(Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT)); 
		shoulderTestSampleView.setBorder(BorderFactory.createEtchedBorder());  
		JPanel shoulderTestJudgementView = new JPanel();  
		shoulderTestJudgementView.setPreferredSize(new Dimension(200, 100));
		PlayerPanel shoulderTestPlayerView = new PlayerPanel(Constants.UPDATE_RATE);
		shoulderTestPlayerView.setPreferredSize(new Dimension(Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT));		
		shoulderTestJudgementView.setBorder(BorderFactory.createEtchedBorder());
		shoulderTestPlayerView.setBorder(BorderFactory.createEtchedBorder());  
		//OverlayPanel shoulderTestOverLayView = new OverlayPanel();
		bg = shoulderTestJudgementView.getBackground();		
		
		shoulderTestPanel.add(shoulderTestSampleView,BorderLayout.WEST);  
		shoulderTestPanel.add(shoulderTestJudgementView,BorderLayout.NORTH); 
		shoulderTestPanel.add(shoulderTestPlayerView,BorderLayout.CENTER);

		
		WristTestSampleView wristTestSampleView = new WristTestSampleView(Constants.UPDATE_RATE);  
		wristTestSampleView.setPreferredSize(new Dimension(Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT)); 
		wristTestSampleView.setBorder(BorderFactory.createEtchedBorder());  
		JPanel wristTestJudgementView = new JPanel();  
		wristTestJudgementView.setPreferredSize(new Dimension(200, 100));
		PlayerPanel wristTestPlayerView = new PlayerPanel(Constants.UPDATE_RATE);
		wristTestPlayerView.setPreferredSize(new Dimension(Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT));		
		wristTestJudgementView.setBorder(BorderFactory.createEtchedBorder());
		wristTestPlayerView.setBorder(BorderFactory.createEtchedBorder());  
		
		
		wristTestPanel.add(wristTestSampleView,BorderLayout.WEST);  
		wristTestPanel.add(wristTestJudgementView,BorderLayout.NORTH); 
		wristTestPanel.add(wristTestPlayerView,BorderLayout.CENTER);
		
		
		theFrame.add(mainPanel, BorderLayout.WEST);
		theFrame.add(jtoolbar,BorderLayout.SOUTH);
		theFrame.add(buttonBox,BorderLayout.EAST); 
		theFrame.setSize(Constants.GUI_LENGTH, Constants.GUI_HEIGHT);  
		theFrame.setVisible(true);  
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		theFrame.setResizable(false);
		theFrame.pack();
		theFrame.addWindowListener(new MyWindowListener());
		
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
		    	int index = mainPanel.getSelectedIndex();
				overlayPanel.exercise = mainPanel.getTitleAt(index);
				if(overlayMode.isSelected()){
					overlayMode.setSelected(false);
					splitMode.setSelected(true);
				}
				stopAll();
				initSplit();
			}
		};
		mainPanel.addChangeListener(changeListener);
		
		
		ActionListener changeModeListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("Overlay Mode")){
					overlayMode.setSelected(true);
					splitMode.setSelected(false);
					
					stopAll();
					initOverlay();
				}else{
					overlayMode.setSelected(false);
					splitMode.setSelected(true);
					
					stopAll();
					initSplit();
				}
			}
		};
		overlayMode.addActionListener(changeModeListener);
		splitMode.addActionListener(changeModeListener);
    	
	}
    
    public void initOverlay(){
    	showPanel = (JPanel)mainPanel .getSelectedComponent();
		sampleView = (SampleView)showPanel.getComponent(0);
		judgementView = (JPanel)showPanel.getComponent(1);
		playerView = (PlayerPanel)showPanel.getComponent(2);										
		showPanel.add(overlayPanel,BorderLayout.EAST);		
		judgementView.setBackground(bg);
		overlayPanel.initModel();
		sampleView.setVisible(false);
		playerView.setVisible(false);
		overlayPanel.setVisible(true);
		overlayPanel.overlayModeFlag = true;
    }
    
    public void initSplit(){
    	showPanel = (JPanel)mainPanel .getSelectedComponent();
		sampleView = (SampleView)showPanel.getComponent(0);
		judgementView = (JPanel)showPanel.getComponent(1);
		playerView = (PlayerPanel)showPanel.getComponent(2);
		sampleView.initModel();
		judgementView.setBackground(bg);
		playerView.initModel();
		sampleView.setVisible(true);
		playerView.setVisible(true);
		overlayPanel.setVisible(false);
		overlayPanel.overlayModeFlag = false;
    }
    
    
    public void stopAll(){
    	if(playerView != null && playerView.isRunning())
	    	playerView.setStop();
	    if(sampleView != null && sampleView.isRunning())
	    	sampleView.setStop();
	    if(upThread != null && upThread.isAlive())
	    	upThread.setStop();
	    if(overlayPanel != null && overlayPanel.isRunning()){
	    	overlayPanel.setStop();
	    }
    }
    
    public synchronized void judge(List<Double> sampleAngels, List<Double> playerAngels){
    	judgementView.setBackground(Color.GREEN);
    	for(int i = 0; i < sampleAngels.size(); i++){
    		if(!isRoughlyEqual(sampleAngels.get(i), playerAngels.get(i))){
    			judgementView.setBackground(Color.RED);
    			break;
    		}
    	}
    }
    
    public boolean isRoughlyEqual(double a, double b){
    	if(a * b < 0)
    		return false;
    	boolean temp = Math.abs(Math.toDegrees(a) - Math.toDegrees(b)) <= Constants.TOLERANCE;
    	if(temp)
    		System.out.println(" " +  Math.toDegrees(a) + " " +  a + " " +  Math.toDegrees(b) + " " +  b);
    	return temp;
    }
    
    public class MyStartListener implements ActionListener{
		public void actionPerformed(ActionEvent a){
			showPanel = (JPanel)mainPanel .getSelectedComponent();
		    sampleView = (SampleView)showPanel.getComponent(0);
		    judgementView = (JPanel)showPanel.getComponent(1);
		    playerView = (PlayerPanel)showPanel.getComponent(2);
		    new Thread(sampleView).start();
		    new Thread(playerView).start();
		    new Thread(overlayPanel).start();
			upThread = new UpdateThread();
			upThread.start();
		}
	}
	
	public class MyStopListener implements ActionListener{
		public void actionPerformed(ActionEvent a){
			showPanel = (JPanel)mainPanel .getSelectedComponent();
		    sampleView = (SampleView)showPanel.getComponent(0);
		    judgementView = (JPanel)showPanel.getComponent(1);
		    playerView = (PlayerPanel)showPanel.getComponent(2);
		    stopAll();
		}
	}
	
	public class MyWindowListener extends WindowAdapter{
		public void windowClosing(WindowEvent e)
		{
			stopAll();
			try{
				overlayPanel.context.stopGeneratingAll();
			}catch (StatusException ex) {}
			overlayPanel.context.release();
			System.exit(0);
		}		
	}
	
//	public class LowerRateListener implements ActionListener{
//		public void actionPerformed(ActionEvent a){
//			int updateRate = upThread.getRate() + 10;
//			upThread.setStop();
//			upThread = new UpdateThread(updateRate);
//			upThread.start();
//		}
//	}
	
	class UpdateThread extends Thread {
		private boolean startFlag = false;
		
//		public UpdateThread(int _updateRate){
//			super();
//			updateRate = _updateRate;
//			this.startFlag = true;
//		}
		
		public UpdateThread(){
			super();
		}
		
		public void setStop(){
			this.startFlag = false;
		}
		
//		public int getRate(){
//			return this.updateRate;
//		}
//		
		public boolean isRunning(){
			return this.startFlag;
		}
		
        public void run() {
        	startFlag = true;
        	while (startFlag) {
        		try {
//        			sampleView.update();
//        			playerView.update();
//        			sampleView.repaint();
//        			playerView.repaint();
        			if(overlayMode.isSelected()){
        				judge(overlayPanel.getSampleAngels(), OverlayPanel.getRealTimeAngles());
        			}else{
        				judge(sampleView.getAngels(), playerView.getAngels());
        			}
        			Thread.sleep(Constants.UPDATE_RATE);
        		} catch (InterruptedException e) {
        			System.out.println(e.getMessage());
        		} catch (RuntimeException e) {
    				System.out.print(e.getMessage());
    			}
        	} 
        }
    }
} 
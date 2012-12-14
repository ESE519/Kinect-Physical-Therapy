package edu.seas.upenn.ese519;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

public class MainFrame {
	private JFrame theFrame;
	private PlayerView playerView;
	private JudgementPanel judgementView;
	private SampleView sampleView;
	private JTabbedPane mainPanel;
	private JPanel showPanel;
	private OverlayPanel overlayPanel;
	private SerialComm serialComm = new SerialComm();
	private DataGenerator dataGen = new DataGenerator(serialComm);
	JRadioButton overlayMode;
	JRadioButton splitMode;

	// private UpdateThread upThread;
	private Color bg;
	private int curExercise = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MainFrame main = new MainFrame();
		main.buildGUI();
	}

	public void buildGUI() {
		theFrame = new JFrame("Kinect For Physical Therapy");
		theFrame.setLayout(new BorderLayout());
		mainPanel = new JTabbedPane();
		mainPanel.setPreferredSize(new Dimension(
				2 * Constants.MotionViewer_LENGTH,
				Constants.MotionViewer_HEIGHT + 100));

		JToolBar jtoolbar = new JToolBar();
		JLabel jl = new JLabel("state");
		jtoolbar.add(jl);
		Box buttonBox = new Box(BoxLayout.Y_AXIS);

		overlayMode = new JRadioButton("Overlay Mode");
		splitMode = new JRadioButton("Split Mode");
		splitMode.setSelected(true);

		buttonBox.add(overlayMode);
		buttonBox.add(splitMode);

		JButton start = new JButton("Start");
		start.setPreferredSize(new Dimension(Constants.BUTTON_LENGTH,
				Constants.BUTTON_WIDTH));
		start.addActionListener(new MyStartListener());
		buttonBox.add(start);

		JButton stop = new JButton("Stop");
		stop.setPreferredSize(new Dimension(Constants.BUTTON_LENGTH,
				Constants.BUTTON_WIDTH));
		stop.addActionListener(new MyStopListener());
		buttonBox.add(stop);
		// JButton lowerRate = new JButton("Lower Rate");
		// lowerRate.addActionListener(new LowerRateListener());
		// buttonBox.add(lowerRate);

		JPanel forearmTestPanel = new JPanel(new BorderLayout());
		JPanel shoulderTestPanel = new JPanel(new BorderLayout());
		JPanel wristTestPanel = new JPanel(new BorderLayout());
		JPanel adaptiveTrackingPanel = new JPanel(new BorderLayout());

		mainPanel.addTab("Left Forearm Stretch Test", forearmTestPanel);
		mainPanel.addTab("Shoulders Test", shoulderTestPanel);
		mainPanel.addTab("Wrist Test", wristTestPanel);
		mainPanel.addTab("Adaptive Tracking Test", adaptiveTrackingPanel);
		mainPanel.setBorder(BorderFactory.createEtchedBorder());

		SimDraw forearmTestSampleView = new SimDraw(Constants.UPDATE_RATE);
		forearmTestSampleView.setPreferredSize(new Dimension(
				Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT));
		forearmTestSampleView.setBorder(BorderFactory.createEtchedBorder());
		PlayerPanel forearmTestPlayerView = new PlayerPanel(
				Constants.UPDATE_RATE, dataGen);
		forearmTestPlayerView.setPreferredSize(new Dimension(
				Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT));
		OverlayPanel forearmTestOverlayView = new OverlayPanel(
				Constants.UPDATE_RATE, forearmTestSampleView, dataGen);
		forearmTestOverlayView.setPreferredSize(new Dimension(
				Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT));
		forearmTestOverlayView.setVisible(false);
		JudgementPanel forearmTestJudgementView = new JudgementPanel(
				Constants.UPDATE_RATE, forearmTestSampleView,
				forearmTestOverlayView, forearmTestPlayerView, dataGen);
		forearmTestJudgementView.setPreferredSize(new Dimension(
				Constants.JUDGEMENT_LENGTH, Constants.JUDGEMENT_HEIGHT));
		forearmTestJudgementView.setBorder(BorderFactory.createEtchedBorder());
		forearmTestPlayerView.setBorder(BorderFactory.createEtchedBorder());
		forearmTestOverlayView.setBorder(BorderFactory.createEtchedBorder());

		forearmTestPanel.add(forearmTestSampleView, BorderLayout.WEST);
		forearmTestPanel.add(forearmTestJudgementView, BorderLayout.NORTH);
		forearmTestPanel.add(forearmTestPlayerView, BorderLayout.CENTER);
		forearmTestPanel.add(forearmTestOverlayView, BorderLayout.EAST);

		ShoulderTestSampleView shoulderTestSampleView = new ShoulderTestSampleView(
				Constants.UPDATE_RATE);
		shoulderTestSampleView.setPreferredSize(new Dimension(
				Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT));
		shoulderTestSampleView.setBorder(BorderFactory.createEtchedBorder());
		PlayerPanel shoulderTestPlayerView = new PlayerPanel(
				Constants.UPDATE_RATE, dataGen);
		shoulderTestPlayerView.setPreferredSize(new Dimension(
				Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT));
		OverlayPanel shoulderTestOverlayView = new OverlayPanel(
				Constants.UPDATE_RATE, shoulderTestSampleView, dataGen);
		shoulderTestOverlayView.setPreferredSize(new Dimension(
				Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT));
		shoulderTestOverlayView.setVisible(false);
		JudgementPanel shoulderTestJudgementView = new JudgementPanel(
				Constants.UPDATE_RATE, shoulderTestSampleView,
				shoulderTestOverlayView, shoulderTestPlayerView, dataGen);
		shoulderTestJudgementView.setPreferredSize(new Dimension(
				Constants.JUDGEMENT_LENGTH, Constants.JUDGEMENT_HEIGHT));
		shoulderTestJudgementView.setBorder(BorderFactory.createEtchedBorder());
		shoulderTestPlayerView.setBorder(BorderFactory.createEtchedBorder());
		shoulderTestOverlayView.setBorder(BorderFactory.createEtchedBorder());
		// OverlayPanel shoulderTestOverLayView = new OverlayPanel();
		bg = shoulderTestJudgementView.getBackground();

		shoulderTestPanel.add(shoulderTestSampleView, BorderLayout.WEST);
		shoulderTestPanel.add(shoulderTestJudgementView, BorderLayout.NORTH);
		shoulderTestPanel.add(shoulderTestPlayerView, BorderLayout.CENTER);
		shoulderTestPanel.add(shoulderTestOverlayView, BorderLayout.EAST);

		WristTestSampleView wristTestSampleView = new WristTestSampleView(
				Constants.UPDATE_RATE);
		wristTestSampleView.setPreferredSize(new Dimension(
				Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT));
		wristTestSampleView.setBorder(BorderFactory.createEtchedBorder());
		WristPlayerPanel wristTestPlayerView = new WristPlayerPanel(
				Constants.UPDATE_RATE, dataGen);
		wristTestPlayerView.setPreferredSize(new Dimension(
				Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT));
		OverlayPanel wristTestOverlayView = new OverlayPanel(
				Constants.UPDATE_RATE, wristTestSampleView, dataGen);
		wristTestOverlayView.setPreferredSize(new Dimension(
				Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT));
		wristTestOverlayView.setVisible(false);
		JudgementPanel wristTestJudgementView = new JudgementPanel(
				Constants.UPDATE_RATE, wristTestSampleView,
				wristTestOverlayView, wristTestPlayerView, dataGen, serialComm);
		wristTestJudgementView.setPreferredSize(new Dimension(
				Constants.JUDGEMENT_LENGTH, Constants.JUDGEMENT_HEIGHT));
		wristTestJudgementView.setBorder(BorderFactory.createEtchedBorder());
		wristTestPlayerView.setBorder(BorderFactory.createEtchedBorder());
		wristTestOverlayView.setBorder(BorderFactory.createEtchedBorder());

		wristTestPanel.add(wristTestSampleView, BorderLayout.WEST);
		wristTestPanel.add(wristTestJudgementView, BorderLayout.NORTH);
		wristTestPanel.add(wristTestPlayerView, BorderLayout.CENTER);
		wristTestPanel.add(wristTestOverlayView, BorderLayout.EAST);

		// ShoulderTestSampleView adaptiveTestSampleView = new
		// ShoulderTestSampleView(Constants.UPDATE_RATE);
		AdaptiveTestSampleView adaptiveTestSampleView = new AdaptiveTestSampleView();
		adaptiveTestSampleView.setPreferredSize(new Dimension(
				Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT));
		adaptiveTestSampleView.setBorder(BorderFactory.createEtchedBorder());
		PlayerPanel3D adaptiveTestPlayerView = new PlayerPanel3D(
				Constants.UPDATE_RATE, dataGen, serialComm);
		adaptiveTestPlayerView.setPreferredSize(new Dimension(
				Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT));
		OverlayPanel adaptiveTestOverlayView = new OverlayPanel(
				Constants.UPDATE_RATE, adaptiveTestSampleView, dataGen);
		adaptiveTestOverlayView.setPreferredSize(new Dimension(
				Constants.MotionViewer_LENGTH, Constants.MotionViewer_HEIGHT));
		adaptiveTestOverlayView.setVisible(false);
		JudgementPanel adaptiveTestJudgementView = new JudgementPanel(
				Constants.UPDATE_RATE, adaptiveTestSampleView,
				adaptiveTestOverlayView, adaptiveTestPlayerView, dataGen);
		adaptiveTestJudgementView.setPreferredSize(new Dimension(
				Constants.JUDGEMENT_LENGTH, Constants.JUDGEMENT_HEIGHT));
		adaptiveTestJudgementView.setBorder(BorderFactory.createEtchedBorder());
		adaptiveTestJudgementView.enableAdaptive();
		adaptiveTestPlayerView.setBorder(BorderFactory.createEtchedBorder());
		adaptiveTestOverlayView.setBorder(BorderFactory.createEtchedBorder());

		adaptiveTrackingPanel.add(adaptiveTestSampleView, BorderLayout.WEST);
		adaptiveTrackingPanel
				.add(adaptiveTestJudgementView, BorderLayout.NORTH);
		adaptiveTrackingPanel.add(adaptiveTestPlayerView, BorderLayout.CENTER);
		adaptiveTrackingPanel.add(adaptiveTestOverlayView, BorderLayout.EAST);

		theFrame.add(mainPanel, BorderLayout.WEST);
		theFrame.add(jtoolbar, BorderLayout.SOUTH);
		theFrame.add(buttonBox, BorderLayout.EAST);
		theFrame.setSize(Constants.GUI_LENGTH, Constants.GUI_HEIGHT);
		theFrame.setVisible(true);
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theFrame.setResizable(false);
		theFrame.pack();
		theFrame.addWindowListener(new MyWindowListener());

		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent changeEvent) {
				curExercise = mainPanel.getSelectedIndex();
				if (overlayMode.isSelected()) {
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
				if (e.getActionCommand().equals("Overlay Mode")) {
					overlayMode.setSelected(true);
					splitMode.setSelected(false);

					stopAll();
					initOverlay();
				} else {
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

	public void initOverlay() {
		setToCurrentPanel();
		judgementView.setBackground(bg);
		sampleView.initModel();
		judgementView.initModel();
		overlayPanel.exercise = curExercise;
		overlayPanel.initModel();
		sampleView.setVisible(false);
		playerView.setVisible(false);
		overlayPanel.setVisible(true);
		dataGen.overlayModeFlag = true;
		judgementView.overlayMode = true;
	}

	public void initSplit() {
		setToCurrentPanel();
		sampleView.initModel();
		judgementView.setBackground(bg);
		judgementView.initModel();
		playerView.initModel();
		sampleView.setVisible(true);
		playerView.setVisible(true);
		overlayPanel.setVisible(false);
		dataGen.overlayModeFlag = false;
		judgementView.overlayMode = false;
	}

	public void setToCurrentPanel() {
		showPanel = (JPanel) mainPanel.getSelectedComponent();
		sampleView = (SampleView) showPanel.getComponent(0);
		judgementView = (JudgementPanel) showPanel.getComponent(1);
		playerView = (PlayerView) showPanel.getComponent(2);
		overlayPanel = (OverlayPanel) showPanel.getComponent(3);

	}

	public void stopAll() {
		if (playerView != null && playerView.isRunning())
			playerView.setStop();
		if (sampleView != null && sampleView.isRunning())
			sampleView.setStop();
		if (judgementView != null && judgementView.isRunning())
			judgementView.setStop();
		if (overlayPanel != null && overlayPanel.isRunning())
			overlayPanel.setStop();
		if (dataGen != null && dataGen.isRunning())
			dataGen.setStop();
	}

	public void startAccordingThreads() {
		if (overlayMode.isSelected()) {
			new Thread(overlayPanel).start();
			new Thread(dataGen).start();
			new Thread(judgementView).start();
		} else {
			new Thread(sampleView).start();
			new Thread(playerView).start();
			new Thread(dataGen).start();
			new Thread(judgementView).start();
		}
	}

	public class MyStartListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			setToCurrentPanel();
			startAccordingThreads();
		}
	}

	public class MyStopListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			setToCurrentPanel();
			stopAll();
		}
	}

	public class MyWindowListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			stopAll();
			dataGen.releaseContext();
			System.exit(0);
		}
	}

}
package edu.seas.upenn.ese519;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JPanel;

public class JudgementPanel extends JPanel implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean startFlag = false;
	private int updateRate;
	public boolean overlayMode = false;

	private SampleView sampleView;
	private OverlayPanel overlayView;
	private PlayerView playerView;
	private DataGenerator dataGen;
	private SerialComm serialComm;
	private OutputStream out;
	private long judgementCounts = 0;
	private long missCounts = 0;
	private Font msgFont;
	private DecimalFormat df;
	private short[] judgementBuf;
	private static final int BUF_SIZE = 80;
	private int repetitionCounts = 0;
	private double updateanglemax = Math.toRadians(20.0);
	private double updateanglemin = Math.toRadians(5.0);
	private double angleUpdateRate = Math.toRadians(5.0);

	private double preAngle = 0.0;
	public short judgeFlag;

	private boolean adaptiveMode = false;

	public JudgementPanel(int _updateRate) {
		super();
		updateRate = _updateRate;
	}

	public JudgementPanel(int _updateRate, SampleView _sampleView,
			OverlayPanel _overlayPanel, PlayerView _playerView,
			DataGenerator _dataGen) {
		super();
		updateRate = _updateRate;
		sampleView = _sampleView;
		overlayView = _overlayPanel;
		playerView = _playerView;
		dataGen = _dataGen;
		initModel();
	}

	public JudgementPanel(int _updateRate, SampleView _sampleView,
			OverlayPanel _overlayPanel, PlayerView _playerView,
			DataGenerator _dataGen, SerialComm _serialComm) {
		super();
		updateRate = _updateRate;
		sampleView = _sampleView;
		overlayView = _overlayPanel;
		playerView = _playerView;
		dataGen = _dataGen;
		serialComm = _serialComm;
		initModel();
	}

	public void enableAdaptive() {
		this.adaptiveMode = true;
	}

	public void initModel() {
		msgFont = new Font("SansSerif", Font.BOLD, 18);
		judgementCounts = 0;
		missCounts = 0;
		judgementBuf = new short[BUF_SIZE];
	}

	public void setSampleView(SampleView sampleView) {
		this.sampleView = sampleView;
	}

	public void setOverlayView(OverlayPanel overlayView) {
		this.overlayView = overlayView;
	}

	public void setPlayerView(PlayerPanel playerView) {
		this.playerView = playerView;
	}

	public void setStop() {
		this.startFlag = false;
	}

	public int getRate() {
		return this.updateRate;
	}

	public boolean isRunning() {
		return this.startFlag;
	}

	public void run() {
		startFlag = true;
		if (serialComm != null) {
			try {
				out = serialComm.getOutputStream();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		while (startFlag
				&& sampleView.getRepititions() >= sampleView
						.getRepititionCounts()) {
			boolean correct;
			try {
				if (overlayMode) {
					correct = judge(overlayView.getSampleAngels(),
							dataGen.getRealTimeAngles());
				} else if (!overlayMode && out == null) {
					correct = judge(sampleView.getAngels(),
							playerView.getAngels());
				} else if (!overlayMode && out != null) {
					correct = judge(sampleView.getAngels(),
							playerView.getAngels());
					char decision = (correct) ? (char) 49 : (char) 50;
					out.write(decision);
				} else if (overlayMode && out != null) {
					correct = judge(overlayView.getSampleAngels(),
							dataGen.getRealTimeAngles());
					char decision = (correct) ? (char) 49 : (char) 50;
					out.write(decision);
				}
				Thread.sleep(Constants.UPDATE_RATE / 10);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			} catch (RuntimeException e) {
				System.out.print(e.getMessage());
			} catch (IOException ex) {
				System.out.print(ex.getMessage());
			}
		}
	}

	public synchronized boolean judge(List<Double> sampleAngels,
			List<Double> playerAngels) {
		judgementCounts++;
		judgeFlag = 0;
		for (int i = 0; i < sampleAngels.size(); i++) {
			// if(!isRoughlyEqual(sampleAngels.get(i), playerAngels.get(i))){
			// missCounts++;
			// break;
			// }
			if (isTooFast(sampleAngels.get(i), playerAngels.get(i))) {
				judgeFlag = 1;
				missCounts++;
				decreaseUpdateRate();
				break;
			}
			if (isTooSlow(sampleAngels.get(i), playerAngels.get(i))) {
				judgeFlag = -1;
				missCounts++;
				increaseUpdateRate();
				break;
			}
		}
		judgementBuf[(int) (judgementCounts % BUF_SIZE)] = judgeFlag;

		repaint();

		if (judgeFlag == 0) {
			this.setBackground(Color.GREEN);
			return true;
		} else {
			this.setBackground(Color.RED);
			return false;
		}
	}

	public void decreaseUpdateRate() {
		angleUpdateRate -= Math.toRadians(1.0);
		if (angleUpdateRate < updateanglemin) {
			angleUpdateRate = updateanglemin;
		}
		if (adaptiveMode && !overlayMode)
			sampleView.angleUpdateRate = angleUpdateRate;
		if (adaptiveMode && overlayMode)
			overlayView.angleUpdateRate = angleUpdateRate;
	}

	public void increaseUpdateRate() {
		angleUpdateRate += Math.toRadians(1.0);
		if (angleUpdateRate > updateanglemax) {
			angleUpdateRate = updateanglemax;
		}
		if (adaptiveMode && !overlayMode)
			sampleView.angleUpdateRate = angleUpdateRate;
		if (adaptiveMode && overlayMode)
			overlayView.angleUpdateRate = angleUpdateRate;
	}

	public boolean isRoughlyEqual(double sampleAngle, double playerAngle) {
		if (Math.abs(Math.toDegrees(sampleAngle)) > Constants.TOLERANCE
				&& sampleAngle * playerAngle < 0)
			return false;

		return Math.abs(Math.toDegrees(sampleAngle)
				- Math.toDegrees(playerAngle)) <= Constants.TOLERANCE;
	}

	public boolean isUpClimbing() {
		// double curAngle = playerAngle;
		// boolean result = false;
		// if (curAngle > preAngle) {
		// result = true;
		// } else {
		// result = false;
		// }
		// preAngle = curAngle;

		// if (overlayView.wristUsed)
		// overlayView.lrflag = result;
		// else if (overlayView.shoulderUsed)
		// overlayView.sflag = result;
		// else if (overlayView.elbowUsed)
		// overlayView.eflag = result;

		// return result;
		if (sampleView.wristUsed)
			return sampleView.lrflag;
		else if (sampleView.shoulderUsed)
			return sampleView.sflag;
		else
			return sampleView.eflag;
	}

	public boolean isTooFast(double sampleAngle, double playerAngle) {
		return (isUpClimbing() && (Math.toDegrees(playerAngle) - Math
				.toDegrees(sampleAngle)) > Constants.TOLERANCE)
				|| (!isUpClimbing() && (Math.toDegrees(sampleAngle) - Math
						.toDegrees(playerAngle)) > Constants.TOLERANCE);
	}

	public boolean isTooSlow(double sampleAngle, double playerAngle) {
		return (isUpClimbing() && (Math.toDegrees(sampleAngle) - Math
				.toDegrees(playerAngle)) > Constants.TOLERANCE)
				|| (!isUpClimbing() && (Math.toDegrees(playerAngle) - Math
						.toDegrees(sampleAngle)) > Constants.TOLERANCE);
	}

	public synchronized boolean isBufMostlyMisses() {
		int sum = 0;
		for (short i : judgementBuf) {
			sum += i;
		}
		return (sum < -(BUF_SIZE * 0.6) || sum > (BUF_SIZE * 0.6)) ? true
				: false;
	}

	public synchronized boolean isMostlySlower() {
		int sum = 0;
		for (short i : judgementBuf) {
			sum += i;
		}
		return (sum < -(BUF_SIZE * 0.6)) ? true : false;
	}

	public synchronized boolean isMostlyFaster() {
		int sum = 0;
		for (short i : judgementBuf) {
			sum += i;
		}
		return (sum > (BUF_SIZE * 0.6)) ? true : false;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		/* write statistics in bottom-left corner */
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.BLUE);
		g2.setFont(msgFont);
		int panelHeight = getHeight();
		if (judgementCounts > 0) {
			double missRate = (double) missCounts / (double) judgementCounts
					* 100;
			df = new DecimalFormat("#.##");
			repetitionCounts = (overlayMode) ? overlayView
					.getRepititionCounts() : sampleView.getRepititionCounts();
			g2.drawString("Exercise Repetitions " + repetitionCounts
					+ "  Miss Rate " + df.format(missRate) + "%", 5,
					panelHeight - 10); // bottom left
		} else
			// no image yet
			g2.drawString("Loading...", 5, panelHeight - 10);
	} // end of writeStats()
}

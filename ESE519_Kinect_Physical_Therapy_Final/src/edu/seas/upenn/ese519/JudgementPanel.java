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
	private static final int BUF_SIZE = 40;
	private int repetitionCounts = 0;
	private double updateanglemax = Math.toRadians(40.0);
	private double updateanglemin = Math.toRadians(0.5);
	private double angleUpdateRate = Math.toRadians(5.0);

	private double preAngle = 0.0;
	private double preError = 0.0;
	private double preDerror = 0.0;

	private static final double Kp = 0.65;
	private static final double Ki = 0.095;
	private static final double Kd = 0.09;
	// private static final double VV_DEADLINE = Math.toRadians(0.1);

	private static final double THRESHOLD = 0.02;

	public short judgeFlag;

	private boolean adaptiveMode = false;
	private boolean userClimb = false;

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
				Thread.sleep(Constants.UPDATE_RATE / 4);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}

			catch (RuntimeException e) {
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
				// decreaseUpdateRate();
				break;
			}
			if (isTooSlow(sampleAngels.get(i), playerAngels.get(i))) {
				judgeFlag = -1;
				missCounts++;
				// increaseUpdateRate();
				break;
			}
			// System.out.println(isUserUpClimbing(playerAngels.get(0)));
		}
		judgementBuf[(int) (judgementCounts % BUF_SIZE)] = judgeFlag;

		if (adaptiveMode)
			updateAngleRate(playerAngels.get(0), sampleAngels.get(0));

		repaint();

		// if (judgeFlag == 0) {
		if (!(this.isBufMostlyMisses())) {
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

	public synchronized void updateAngleRate(double playerAngle,
			double sampleAngle) {
		double curAngle = playerAngle;
		double curSangle = sampleAngle;
		double curPlayerRate = Math.abs(curAngle - preAngle);
		// if(curPlayerRate == 0.0){
		// preAngle = curAngle;
		// return;
		// }
		double resultUpdateRate = overlayView.angleUpdateRate;
		double error, d_error, dd_error;
		// if (pp->vi_FeedBack > 2)
		// VV_MIN = 0;
		// else
		// VV_MIN = 1500;

		// System.out.println("Cur Angle:" + playerAngle + " Prev Angle:" +
		// preAngle + " UpClimbing:" + userClimb);
		error = curPlayerRate - resultUpdateRate;
		// if (isUserUpClimbing(playerAngle) != isUpClimbing(playerAngle)){
		// error += Math.toRadians(90);
		// }

		d_error = error - preError;
		dd_error = d_error - preDerror;
		// if(Math.abs(dd_error) >= 20 * Math.abs(d_error))
		// dd_error = Math.abs(Kp/Kd) * d_error;

		preError = error;
		preDerror = d_error;

		// System.out.println("curPlayerRate: " + curPlayerRate
		// + " curSampleRate: " + overlayView.angleUpdateRate + " error: "
		// + error + " d_error: " + d_error + " dd_error: " + dd_error);

		if (Math.abs(curAngle - curSangle) <= Math
				.toRadians(Constants.TOLERANCE)
				&& (isUserUpClimbing(playerAngle) == isUpClimbing(playerAngle)))
			;
		// else if ((error < VV_DEADLINE) && (error > -VV_DEADLINE) &&
		// (isUserUpClimbing(playerAngle) == isUpClimbing(playerAngle)));

		else { // PID_test0= (pp -> v_Kp) * d_error;
			resultUpdateRate += Kp * d_error + Ki * error + Kd * dd_error;
		}

		if (resultUpdateRate >= updateanglemax) {
			resultUpdateRate = updateanglemax;
		} else if (resultUpdateRate <= updateanglemin) {
			resultUpdateRate = updateanglemin;
		}

		preAngle = curAngle;
		// System.out.println(resultUpdateRate);
		overlayView.angleUpdateRate = resultUpdateRate;
	}

	public boolean isRoughlyEqual(double sampleAngle, double playerAngle) {
		if (Math.abs(Math.toDegrees(sampleAngle)) > Constants.TOLERANCE
				&& sampleAngle * playerAngle < 0)
			return false;

		return Math.abs(Math.toDegrees(sampleAngle)
				- Math.toDegrees(playerAngle)) <= Constants.TOLERANCE;
	}

	public boolean isUserUpClimbing(double playerAngle) {
		// double curAngle = playerAngle;
		if (Math.abs(playerAngle - preAngle) <= THRESHOLD) {
		} else if (playerAngle - preAngle > 0) {
			userClimb = true;
		} else if (playerAngle - preAngle < 0) {
			userClimb = false;
		}
		// System.out.println("Cur Angle:" + playerAngle+" Prev Angle:" +
		// preAngle);
		// preAngle = playerAngle;
		return userClimb;
	}

	public boolean isUpClimbing(double playerAngle) {
		if (sampleView.wristUsed)
			return sampleView.lrflag;
		else if (sampleView.shoulderUsed)
			return sampleView.sflag;
		else
			return sampleView.eflag;
	}

	public boolean isTooFast(double sampleAngle, double playerAngle) {
		return (isUpClimbing(playerAngle) && (Math.toDegrees(playerAngle) - Math
				.toDegrees(sampleAngle)) > Constants.TOLERANCE)
				|| (!isUpClimbing(playerAngle) && (Math.toDegrees(sampleAngle) - Math
						.toDegrees(playerAngle)) > Constants.TOLERANCE);
	}

	public boolean isTooSlow(double sampleAngle, double playerAngle) {
		return (isUpClimbing(playerAngle) && (Math.toDegrees(sampleAngle) - Math
				.toDegrees(playerAngle)) > Constants.TOLERANCE)
				|| (!isUpClimbing(playerAngle) && (Math.toDegrees(playerAngle) - Math
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

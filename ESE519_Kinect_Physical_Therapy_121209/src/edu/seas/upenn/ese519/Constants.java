package edu.seas.upenn.ese519;

public class Constants {
	static final int MotionViewer_LENGTH = 640;
	static final int MotionViewer_HEIGHT = 480;

	static final int JUDGEMENT_LENGTH = 200;
	static final int JUDGEMENT_HEIGHT = 100;

	static final int BUTTON_LENGTH = 140;
	static final int BUTTON_WIDTH = 10;

	static final int GUI_LENGTH = 2 * MotionViewer_LENGTH + BUTTON_LENGTH + 20;
	static final int GUI_HEIGHT = MotionViewer_HEIGHT + 120;

	static final int UPDATE_RATE = 150;
	static final double TOLERANCE = 10.0;

	static final int DEFAULT_REPETITION = 30;

	static final int FOREARMTEST = 0;
	static final int SHOULDERTEST = 1;
	static final int WRISTTEST = 2;
	static final int ADAPTIVETEST = 3;

}

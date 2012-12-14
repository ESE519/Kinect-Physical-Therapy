package edu.seas.upenn.rendering;

import javax.vecmath.*;


/**
 * The class HandSimulation provides a control layer for the <a href="HandShape.html">HandShape</a> class.
 * You can set a hand's start and target position, and move it from the one to the other, by means of forward or inverse kinematics.
 * One can also choose the number of frames, in which to perform the animation.
 * @since JDK1.2
 * @author Stefan Hendrickx
 * <br><br>
 * You are free to use the software for personal, non-commercial use. The software is protected by copyright and all rights are reserved by the author. It comes without warranty of any kind, no liability is assumed for its use.
*/
public class HandSimulation extends HandShape {

	float[] m_startOrientationHand, m_targetOrientationHand, m_tempOrientationHand;
	float[][] m_startOrientationFingers, m_targetOrientationFingers, m_fingerOrientationTemp;
	int m_numberOfSteps, m_currentStep;
	boolean m_forward;

	/** 
	 *	Constructs a HandSimulation node with default properties.
	 * @param default properties are inherited from <a href="HandShape.html#HandShape()">HandShape()</a>.
	 * Also by default, the number of steps is set to zero and the kinematics mode is set to forward.
	*/
	public HandSimulation() {
		super();
		m_startOrientationHand = getGlobalOrientation(); m_startOrientationFingers = getFingerOrientation();
		m_targetOrientationHand = getGlobalOrientation(); m_targetOrientationFingers = getFingerOrientation();
		m_tempOrientationHand = new float[6]; m_fingerOrientationTemp = new float[5][3];
		m_numberOfSteps = 0; m_currentStep = 0;
		m_forward = true;
	}

	/** 
	 *	Constructs and initializes geometry and appearance of a specified HandSimulation node.
	 * @param parameters length, height, fingerProperties, t, rx, ry and rz are inherited from <a href="HandShape.html#HandShape(float, float, float[][], javax.vecmath.Vector3f, float, float, float)">HandShape(float length, float height, float[][] fingerProperties, Vector3f t, float rx, float ry, float rz)</a>
	 * 
	*/
	public HandSimulation(float length, float height, float[][] fingerProperties, Vector3f t, float rx, float ry, float rz, boolean forward) {
		super(length, height, fingerProperties, t, rx, ry, rz);
		m_startOrientationHand = getGlobalOrientation();  m_startOrientationFingers = getFingerOrientation();
		m_targetOrientationHand = getGlobalOrientation();  m_targetOrientationFingers = getFingerOrientation();
		m_tempOrientationHand = new float[6]; m_fingerOrientationTemp = new float[5][3];
		m_numberOfSteps = 0; m_currentStep = 0;
		m_forward = true;
	}

	/**
	 *	This method moves the hand one step forward in the animation.
	 */
	public void move() {
		if (m_forward)
		// forward kinematics
		{
			if ((m_numberOfSteps > 0)
			 && (m_currentStep < m_numberOfSteps)) {
				m_currentStep++;
				float alphaInterpolator = (float) m_currentStep / m_numberOfSteps;
				interpolateHandOrientation(alphaInterpolator);
				interpolateFingerOrientation(alphaInterpolator);
				setGlobalOrientation(m_tempOrientationHand);
				setFingerOrientation(m_fingerOrientationTemp);
				super.update(); // updates visualisation
			}
		}
		else
		// inverse kinematics
		{
		}
	} // end of method move in class HandSimulation

	/**
	 *	With this method, you can move the hand to an arbitrary animation frame.
	 * @param step is the step to which the hand will be set.
	 */
	public void move(int step) {
		if (m_forward)
		// forward kinematics
		{
			if (m_numberOfSteps > 0) {
				m_currentStep = step;
				float alphaInterpolator = (float) m_currentStep / m_numberOfSteps;
				interpolateHandOrientation(alphaInterpolator);
				interpolateFingerOrientation(alphaInterpolator);
				setGlobalOrientation(m_tempOrientationHand);
				setFingerOrientation(m_fingerOrientationTemp);
				super.update(); // updates visualisation
			}
		}
		else
		// inverse kinematics
		{
		}
	} // end of method move in class HandSimulation

	/**
	 *	This method lets you define the hand's start orientation.
	 * @param handOrientation is a float[6], containing the translations tx, ty and tz, and the rotations rx, ry and rz of the hand.
	 */
	public void setStartOrientationHand(float[] handOrientation) {
		m_startOrientationHand = handOrientation;
	} // end of method setStartOrientationHand in class HandSimulation

	/**
	 *	This method lets you define the start orientation of the hand's fingers.
	 * @param fingerOrienation is a float[5][3], each row containing rotations rx and ry, and bending angle rr of one finger.
	 */
	public void setStartOrientationFingers(float[][] fingerOrientation) {
		m_startOrientationFingers = fingerOrientation;
	} // end of method setStartOrientationFingers in class HandSimulation

	/**
	 *	This method lets you define the hand's target orientation.
	 * @param handOrientation is a float[6], containing the translations tx, ty and tz, and the rotations rx, ry and rz of the hand.
	 */
	public void setTargetOrientationHand(float[] handOrientation) {
		m_targetOrientationHand = handOrientation;
	} // end of method setTargetOrientationHand in class HandSimulation

	/**
	 *	This method lets you define the target orientation of the hand's fingers.
	 * @param fingerOrienation is a float[5][3], each row containing rotations rx and ry, and bending angle rr of one finger.
	 */
	public void setTargetOrientationFingers(float[][] fingerOrientation) {
		m_targetOrientationFingers = fingerOrientation;
	} // end of method setStartOrientationFingers in class HandSimulation

	/**
	 *	This method lets you retrieve the hand's start orientation.
	 * @return a float[6], containing the translations tx, ty and tz, and the rotations rx, ry and rz of the hand.
	 */
	public float[] getStartOrientationHand() {
		return m_startOrientationHand;
	} // end of method getStartOrientationHand in class HandSimulation

	/**
	 *	This method lets you retrieve the start orientation of the hand's fingers.
	 * @return a float[5][3], each row containing rotations rx and ry, and bending angle rr of one finger.
	 */
	public float[][] getStartOrientationFingers() {
		return m_startOrientationFingers;
	} // end of method getStartOrientationFingers in class HandSimulation

	/**
	 *	This method lets you retrieve the hand's target orientation.
	 * @return a float[6], containing the translations tx, ty and tz, and the rotations rx, ry and rz of the hand.
	 */
	public float[] getTargetOrientationHand() {
		return m_targetOrientationHand;
	} // end of method getTargetOrientationHand in class HandSimulation

	/**
	 *	This method lets you retrieve the target orientation of the hand's fingers.
	 * @return a float[5][3], each row containing rotations rx and ry, and bending angle rr of one finger.
	 */
	public float[][] getTargetOrientationFingers() {
		return m_startOrientationFingers;
	} // end of method getTargetOrientationFingers in class HandSimulation

	/**
	 *	This method lets you adapt the number of steps (animation frames) from start to target.
	 * @param numberOfSteps the number of steps
	 */
	public void setNumberOfSteps(int numberOfSteps) {
		m_numberOfSteps = numberOfSteps;
	} // end of method setNumberOfSteps in class HandSimulation

	/**
	 *	This method lets you retrieve the number of steps (animation frames) from start to target.
	 * @return the number of animation frames.
	 */
	public int getNumberOfSteps() {
		return m_numberOfSteps;
	} // end of method getNumberOfSteps in class HandSimulation

	/**
	 *	Sets the animation mode to forward kinematics.
	 */
	public void setModeFK() {
		m_forward = true;
	} // end of method setModeFK in class HandSimulation

	/**
	 *	Sets the animation mode to inverse kinematics.
	 */
	public void setModeIK() {
		m_forward = false;
	} // end of method setModeIK in class HandSimulation

	/**
	 *	Question whether the kinematics mode is forward.
	 * @return true if animation mode is set to forward kinematics, false if set to inverse kinematics.
	 */
	public boolean isModeFK() {
		if (m_forward) return true;
		else return false;
	} // end of method isModeFK in class HandSimulation

	/**
	 *	Question whether the kinematics mode is inverse.
	 * @return true if animation mode is set to inverse kinematics, false if set to forward kinematics.
	 */
	public boolean isModeIK() {
		if (m_forward) return false;
		else return true;
	} // end of method isModeIK in class HandSimulation

	/**
	 *	This method lets you set the actual animation frame number.
	 * @param currentStep the current step in the animation.
	 */
	public void setCurrentStep(int currentStep) {
		m_currentStep = currentStep;
	}

	/**
	 *	This method lets you retrieve the actual animation frame number.
	 * @return the current step in the animation.
	 */
	public int getCurrentStep() {
		return m_currentStep;
	} // end of method getCurrentStep in class HandSimulation

	/**
	 *	This method updates the hand's and finger's start and target orientations at once. So, unlike the HandShape class, this method does not update the visualisation. It has to be used after setting the new positions with setStartOrientationHand, setStartOrientationFingers, setTargetOrientationHand and setTargetOrientationFingers. You never have to update the hand's visualisation explicitely. The <a href="HandShape.html#update()">HandShape.update()</a> method, which does update the visualisation, is called automatically by the instances of move, whenever the hand is moved to another frame.
	 */
	public void update() {
		super.update();
		m_startOrientationHand = getGlobalOrientation(); m_startOrientationFingers = getFingerOrientation();
		m_targetOrientationHand = getGlobalOrientation(); m_targetOrientationFingers = getFingerOrientation();
	}

	private void interpolateHandOrientation(float interpolator) {
		for (int i = 0; i < 6; i++){
			m_tempOrientationHand[i] = interpolator * m_startOrientationHand[i] + (1.0f - interpolator) * m_targetOrientationHand[i];
		}
	} // end of method interpolateHandOrientation in class HandSimulation

	private void interpolateFingerOrientation(float interpolator) {
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 3; j++)
				m_fingerOrientationTemp[i][j] = interpolator * m_startOrientationFingers[i][j] + (1.0f - interpolator) * m_targetOrientationFingers[i][j];
	} // end of method interpolateFingerOrientation in class HandSimulation
	
} // end of class HandSimulation

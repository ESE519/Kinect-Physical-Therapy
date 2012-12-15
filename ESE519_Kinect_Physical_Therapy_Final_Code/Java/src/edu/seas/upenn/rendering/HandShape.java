package edu.seas.upenn.rendering;

import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * The class HandShape provides the geometry to study the kinematics of a
 * humanoid hand.
 * <ul>
 * <li>Any geometrical parameter can be animated.
 * <ul>
 * <li>Finger orientation: rx, ry.
 * <li>Finger geometry: rr (bending).
 * <li>Translation and rotation parameters of wrist (six degrees of freedom).
 * </ul>
 * <li>Any visualisation parameter can be animated.
 * <ul>
 * <li>Surface properties (diffuse color, specular color, shine).
 * <li>Number of faces used to model fingers.
 * <li>Wireframe or phong shading.
 * <li>Welding fingers or not.
 * </ul>
 * </ul>
 * <p>
 * Since you may want to animate several parameters in one animation frame, the
 * object has to be updated explicitely with the <code>Update()</code> method.
 * This is required for as well the efficiency as the smoothness of your
 * animation.
 * </p>
 * <p>
 * Finger rotation angles are in the local coordinate system of the hand.
 * </p>
 * <img SRC="hand.gif">
 * 
 * @since JDK1.2
 * @author Stefan Hendrickx <br>
 * <br>
 *         You are free to use the software for personal, non-commercial use.
 *         The software is protected by copyright and all rights are reserved by
 *         the author. It comes without warranty of any kind, no liability is
 *         assumed for its use.
 */

public class HandShape extends Shape3D {

	// //////////////////////////////////////////
	//
	// create Shape3D with geometry and appearance
	// the geometry is created in method HandShape
	// the appearance is created in method handAppearance
	//

	// independent member variables

	private Vector3f m_t; // the translation of the hand
	private float m_height; // the heigth of the palm of the hand
	float m_rx;
	float m_ry;
	float m_rz; // the rotation of the hand

	private float[] m_finger_l0; // an array containing the length of 'onderste
									// vingerkootje'
	private float[] m_finger_l1;
	private float[] m_finger_l2;

	private float[] m_finger_r0;
	private float[] m_finger_r1;
	private float[] m_finger_r2;
	private float[] m_finger_r3;

	private float[] m_finger_rx;
	private float[] m_finger_ry;
	private float[] m_finger_rr;

	private float m_length_0, m_length_1, m_length_2, m_length_3, m_length_4;

	// dependent member variables

	private Vector3f[] m_fingerTranslationVector; // an array containing the
													// translations of each
													// finger
	private Matrix4f m_handTransformMatrix;
	private Matrix4f[] m_fingerTransformMatrix; // an array containing the
												// transformation matrices of
												// each finger
	private Matrix4f m_palmTransformMatrix; // transformation matrix of the hand
											// palm

	private TriangleStripArray[] m_finger; // an array of TriangleStripArrays
											// which are fingers
	private TriangleStripArray m_handBottom, m_handTop; // a
														// TriangleStripArray...
	private TriangleStripArray m_handPalm;

	// visualisation member variables

	private boolean m_weldFingers, m_wireMode;
	private int m_N, m_palmN;
	private Color3f m_diffuseColor;
	private Color3f m_specularColor;
	float m_shine;

	// additional member variables

	private Point3f m_point;
	private Vector3f m_normal;

	private int i, j;

	private static final float[][] defaultFingerProperties = {
			{ 0.20f, 0.20f, 0.15f, 0.050f, 0.040f, 0.035f, 0.027f, 0.70f,
					-0.10f, -0.2f },
			{ 0.19f, 0.17f, 0.10f, 0.045f, 0.035f, 0.030f, 0.022f, 0.00f,
					0.05f, 1.2f },
			{ 0.22f, 0.20f, 0.12f, 0.045f, 0.035f, 0.033f, 0.025f, 0.05f,
					-0.05f, 0.9f },
			{ 0.18f, 0.16f, 0.10f, 0.045f, 0.035f, 0.030f, 0.022f, 0.10f,
					-0.10f, 1.0f },
			{ 0.13f, 0.11f, 0.07f, 0.040f, 0.030f, 0.025f, 0.020f, 0.20f,
					-0.20f, 1.1f } };

	/**
	 * Constructs a HandShape Shape3D node with default properties.
	 * 
	 * @param defaultFingerProperties
	 *            <ul>
	 *            <li>default <code>length</code> of palm is 0.45.
	 *            <li>default <code>heigth</code> of palm is 0.09.
	 *            <li>default <code>fingerProperties</code> are:
	 *            <ul>
	 *            <code>
	 * <li>{{0.20f, 0.20f, 0.15f, 0.050f, 0.040f, 0.035f, 0.027f, 0.70f, -0.10f, -0.2f},
	 * <li> {0.19f, 0.17f, 0.10f, 0.045f, 0.035f, 0.030f, 0.022f, 0.00f,  0.05f,  1.2f},
	 * <li> {0.22f, 0.20f, 0.12f, 0.045f, 0.035f, 0.033f, 0.025f, 0.05f, -0.05f,  0.9f},
	 * <li> {0.18f, 0.16f, 0.10f, 0.045f, 0.035f, 0.030f, 0.022f, 0.10f, -0.10f,  1.0f},
	 * <li> {0.13f, 0.11f, 0.07f, 0.040f, 0.030f, 0.025f, 0.020f, 0.20f, -0.20f,  1.1f}}
	 * </code>
	 *            </ul>
	 *            <li>default <code>translation</code> is (0, 0, 0).
	 *            <li>default <code>rotX</code> is 0.
	 *            <li>default <code>rotY</code> is 0.
	 *            <li>default <code>rotZ</code> is 0.
	 *            </ul>
	 */
	public HandShape() {

		this(0.45f, 0.09f, defaultFingerProperties, new Vector3f(0.0f, 0.0f,
				0.0f), 0.0f, 0.0f, 0.0f);

	} // end of default HandShape constructor

	/**
	 * Constructs and initializes geometry and appearance of a specified
	 * HandShape Shape3D node.
	 * 
	 * @param length
	 *            is the length of the palm.
	 * @param heigth
	 *            is the height of the palm.
	 * @param fingerProperties
	 *            is a <code>float[5][10]</code> matrix with geometrical
	 *            properties of the fingers.
	 *            <ul>
	 *            <li>the 0th column containing lengths l0 of fingers 0 -> 4.
	 *            <li>the 1st column containing lengths l1 of fingers 0 -> 4.
	 *            <li>the 2nd column containing lengths l2 of fingers 0 -> 4.
	 *            <li>the 3rd column containing radii r0 of fingers 0 ->4.</li>
	 *            <li>the 4th column containing radii r1 of fingers 0 ->4.</li>
	 *            <li>the 5th column containing radii r2 of fingers 0 ->4.</li>
	 *            <li>the 6th column containing radii r3 of fingers 0 ->4.</li>
	 *            <li>the 7th column containing rotation angles about the X of
	 *            fingers 0 -> 4.</li>
	 *            <li>the 8th column containing rotation angles about the Y of
	 *            fingers 0 -> 4.</li>
	 *            <li>the 9th column containing rotation angles about the Z of
	 *            fingers 0 -> 4.</li>
	 *            </ul>
	 * @param translation
	 *            is the translation of the center of the wrist.
	 * @param rotX
	 *            is the rotation angle about X of wrist.
	 * @param rotY
	 *            is the rotation angle about Y of wrist.
	 * @param rotZ
	 *            is the rotation angle about Z of wrist.
	 */
	public HandShape(float length, float height, float[][] fingerProperties,
			Vector3f t, float rx, float ry, float rz) {

		// visualisation member variables

		m_weldFingers = true;
		m_wireMode = false;
		m_N = 32;
		m_palmN = 16;

		m_diffuseColor = new Color3f(1.0f, 0.7f, 0.7f);
		m_specularColor = new Color3f(1.0f, 1.0f, 1.0f);
		m_shine = 20.0f;

		// helper member variables

		m_point = new Point3f();
		m_normal = new Vector3f();

		// independent member variables

		m_t = new Vector3f();

		m_t = t;
		m_height = height;
		m_rx = rx;
		m_ry = ry;
		m_rz = rz;

		m_finger_l0 = new float[5];
		m_finger_l1 = new float[5];
		m_finger_l2 = new float[5];

		m_finger_r0 = new float[5];
		m_finger_r1 = new float[5];
		m_finger_r2 = new float[5];
		m_finger_r3 = new float[5];

		m_finger_rx = new float[5];
		m_finger_ry = new float[5];
		m_finger_rr = new float[5];

		for (i = 0; i < 5; i++) {
			m_finger_l0[i] = fingerProperties[i][0];
			m_finger_l1[i] = fingerProperties[i][1];
			m_finger_l2[i] = fingerProperties[i][2];

			m_finger_r0[i] = fingerProperties[i][3];
			m_finger_r1[i] = fingerProperties[i][4];
			m_finger_r2[i] = fingerProperties[i][5];
			m_finger_r3[i] = fingerProperties[i][6];

			m_finger_rx[i] = fingerProperties[i][7];
			m_finger_ry[i] = fingerProperties[i][8];
			m_finger_rr[i] = fingerProperties[i][9];
		}

		m_length_0 = length;
		m_length_1 = length * (1.0f - 1.0f / 64f);
		m_length_2 = length * (1.0f - 1.0f / 32f);
		m_length_3 = length * (1.0f - 1.0f / 16f);
		m_length_4 = length * (1.0f - 1.0f / 8f);

		// dependent member variables

		m_finger = new TriangleStripArray[5];

		int totalN = 2 * m_N + 2;
		int stripCounts[] = { totalN, totalN, totalN };

		for (i = 0; i < 5; i++) {
			m_finger[i] = new TriangleStripArray(
					totalN * 3,
					TriangleStripArray.COORDINATES | TriangleStripArray.NORMALS,
					stripCounts);
		}

		for (i = 0; i < 5; i++) {
			m_finger[i] = handFinger(m_finger_l0[i], m_finger_l1[i],
					m_finger_l2[i], m_finger_r0[i], m_finger_r1[i],
					m_finger_r2[i], m_finger_r3[i], m_finger_rx[i],
					m_finger_ry[i], m_finger_rr[i]);
		}

		int handPalmN = 16, handPalmStripCounts[] = { handPalmN, handPalmN };
		m_handPalm = new TriangleStripArray(handPalmN * 2,
				TriangleStripArray.COORDINATES | TriangleStripArray.NORMALS,
				handPalmStripCounts);
		m_handPalm = handPalm();

		m_fingerTranslationVector = new Vector3f[5];
		m_handTransformMatrix = new Matrix4f();
		m_fingerTransformMatrix = new Matrix4f[5];
		m_palmTransformMatrix = new Matrix4f();

		setFingerTransformations();
		setPalmTransformations();

		// Shape3D construction

		this.setGeometry(HandShape());
		this.setAppearance(handAppearance());

	} // end of HandShape constructor

	/**
	 * Customizes the hand's visualisation, but it does not update it.
	 * 
	 * @param weldFingers
	 *            is a boolean that lets you decide to weld the different parts
	 *            of a finger or not. Not welding speeds ups the animation, but
	 *            not a lot. Setting this parameter to false though, might be an
	 *            advantage on a slow system.
	 * @param wireMode
	 *            lets you decide whether you want a wireframe model or a Phong
	 *            shaded one.
	 * @param diffuseColor
	 *            the basic color of the hand.
	 * @param specularColor
	 *            the color of the hand at highlighted points.
	 * @param shine
	 *            lets you adjust the shininess of the hand's surface.
	 * @param N
	 *            the number of faces by which a finger part is defined.
	 */
	public void setVisualisationMode(boolean weldFingers, boolean wireMode,
			Color3f diffuseColor, Color3f specularColor, float shine, int N) {

		m_weldFingers = weldFingers;
		m_wireMode = wireMode;
		m_diffuseColor = diffuseColor;
		m_specularColor = specularColor;
		m_shine = shine;
		m_N = N;

		for (i = 0; i < 5; i++) { // m_weldFingers may change fingers' geometry
			m_finger[i] = handFinger(m_finger_l0[i], m_finger_l1[i],
					m_finger_l2[i], m_finger_r0[i], m_finger_r1[i],
					m_finger_r2[i], m_finger_r3[i], m_finger_rx[i],
					m_finger_ry[i], m_finger_rr[i]);
		}
	}

	/**
	 * This method updates the geometry and appearance of the HandShape Shape3D
	 * Node. Since you may want to animate several parameters in one animation
	 * frame, the object has to be updated explicitely with the
	 * <code>Update()</code> method. This is required for as well the efficiency
	 * as the smoothness of your animation.
	 */
	public void update() {
		this.setGeometry(HandShape());
		this.setAppearance(handAppearance());
	}

	/**
	 * With this method you can change the six degrees of freedom of the wrist,
	 * but it does not update it's visualisation.
	 * 
	 * @param handProperties
	 *            is a float[6] containing the translations x, y en z of the
	 *            wrist and the rotation angles of the wrist about X, Y & Z
	 *            axis.
	 */
	public void setGlobalOrientation(float[] handProperties) {

		m_t.x = handProperties[0];
		m_t.y = handProperties[1];
		m_t.z = handProperties[2];
		m_rx = handProperties[3];
		m_ry = handProperties[4];
		m_rz = handProperties[5];

		setFingerTransformations();
		setPalmTransformations();

	} // end of method setGlobalOrientation in class HandShape

	/**
	 * This method sets the geometric properties of all fingers, without
	 * updating the object's visualisation.
	 * 
	 * @param fingerProperties
	 *            is a <code>float[5][10]</code> matrix with geometrical
	 *            properties of the fingers.
	 */
	public void setFingerGeometry(float[][] fingerProperties) {

		float[] singleFingerProperties = new float[10];

		for (i = 0; i < 5; i++) {
			for (j = 0; j < 10; j++)
				singleFingerProperties[j] = fingerProperties[i][j];
			setFingerGeometry(i, singleFingerProperties);
		}
	} // end of method setFingerGeometry in class HandShape

	/**
	 * This method changes the geometric properties of finger i, but does not
	 * update it's visualisation.
	 * 
	 * @param f
	 *            is the number of the finger you want to set.
	 * @param fingerProperties
	 *            is a float[10] of parameters l0, l1, l2, r0, r1, r2, r3, rx,
	 *            ry, rr.
	 */
	public void setFingerGeometry(int f, float[] fingerProperties) {

		m_finger_l0[f] = fingerProperties[0];
		m_finger_l1[f] = fingerProperties[1];
		m_finger_l2[f] = fingerProperties[2];

		m_finger_r0[f] = fingerProperties[3];
		m_finger_r1[f] = fingerProperties[4];
		m_finger_r2[f] = fingerProperties[5];
		m_finger_r3[f] = fingerProperties[6];

		m_finger_rx[f] = fingerProperties[7];
		m_finger_ry[f] = fingerProperties[8];
		m_finger_rr[f] = fingerProperties[9];

		m_finger[f] = handFinger(m_finger_l0[f], m_finger_l1[f],
				m_finger_l2[f], m_finger_r0[f], m_finger_r1[f], m_finger_r2[f],
				m_finger_r3[f], m_finger_rx[f], m_finger_ry[f], m_finger_rr[f]);

	} // end of method setFingerGeometry in class HandShape

	/**
	 * This method sets the rotation angles about X and Y axis in the hand's
	 * coordinate system and the bending angle about the X axis of all fingers,
	 * but does not update it's visualisation.
	 * 
	 * @param fingerProperties
	 *            is a float[5][3] with the above described rotation angles and
	 *            the bending angle about the X axis of finger all fingers.
	 */
	public void setFingerOrientation(float[][] fingerProperties) {

		float[] singleFingerProperties = new float[3];

		for (i = 0; i < 5; i++) {
			for (j = 0; j < 3; j++)
				singleFingerProperties[j] = fingerProperties[i][j];
			setFingerOrientation(i, singleFingerProperties);
		}
	} // end of method setFingerOrientation in class HandShape

	/**
	 * This method sets the rotation angles about X and Y axis in the hand's
	 * coordinate system and the bending angle about the X axis of a single
	 * finger, but does not update it's visualisation.
	 * 
	 * @param f
	 *            is the number of the finger you want to set.
	 * @param fingerProperties
	 *            is a float[3] with the above described rotation angles and the
	 *            bending angle about the X axis of finger f.
	 */
	public void setFingerOrientation(int f, float[] fingerProperties) {

		m_finger_rx[f] = fingerProperties[0];
		m_finger_ry[f] = fingerProperties[1];
		m_finger_rr[f] = fingerProperties[2];

		m_finger[f] = handFinger(m_finger_l0[f], m_finger_l1[f],
				m_finger_l2[f], m_finger_r0[f], m_finger_r1[f], m_finger_r2[f],
				m_finger_r3[f], m_finger_rx[f], m_finger_ry[f], m_finger_rr[f]);

	} // end of method setFingerOrientation class HandShape

	private void setFingerTransformations() {

		m_handTransformMatrix.setIdentity();
		Matrix4f handTranslationMatrix = new Matrix4f();
		handTranslationMatrix.setIdentity();
		handTranslationMatrix.setTranslation(m_t);
		Matrix4f handRotXMatrix = new Matrix4f();
		handRotXMatrix.setIdentity();
		handRotXMatrix.rotX(m_rx);
		Matrix4f handRotYMatrix = new Matrix4f();
		handRotYMatrix.setIdentity();
		handRotYMatrix.rotY(m_ry);
		Matrix4f handRotZMatrix = new Matrix4f();
		handRotZMatrix.setIdentity();
		handRotZMatrix.rotZ(m_rz);
		m_handTransformMatrix.mul(handRotXMatrix, m_handTransformMatrix);
		m_handTransformMatrix.mul(handRotYMatrix, m_handTransformMatrix);
		m_handTransformMatrix.mul(handRotZMatrix, m_handTransformMatrix);
		m_handTransformMatrix.mul(handTranslationMatrix, m_handTransformMatrix);

		m_fingerTranslationVector[0] = new Vector3f(m_finger_r0[1]
				+ (m_finger_r0[2] + m_finger_r0[3]) * 2, 0.0f, 0.0f);
		m_fingerTranslationVector[1] = new Vector3f(m_finger_r0[1]
				+ (m_finger_r0[2] + m_finger_r0[3]) * 2, 0.0f, m_length_2);
		m_fingerTranslationVector[2] = new Vector3f(m_finger_r0[2]
				+ m_finger_r0[3] * 2, 0.0f, m_length_0);
		m_fingerTranslationVector[3] = new Vector3f(m_finger_r0[3], 0.0f,
				m_length_2);
		m_fingerTranslationVector[4] = new Vector3f(-m_finger_r0[4], 0.0f,
				m_length_4);

		Matrix4f fingerTranslation = new Matrix4f();

		for (i = 0; i < 5; i++) {

			fingerTranslation.setIdentity();
			fingerTranslation.setTranslation(m_fingerTranslationVector[i]);

			m_fingerTransformMatrix[i] = new Matrix4f();
			m_fingerTransformMatrix[i].setIdentity();
			m_fingerTransformMatrix[i].mul(fingerTranslation,
					m_fingerTransformMatrix[i]);
			m_fingerTransformMatrix[i].mul(m_handTransformMatrix,
					m_fingerTransformMatrix[i]);
		}
	} // end of method setFingerTransformations in class HandShape

	private void setPalmTransformations() {

		m_palmTransformMatrix = m_handTransformMatrix;

	} // end of method setPalmTransformations in class HandShape

	private float wristTransform(float[] a) {
		float hr = (float) -Math.sin(a[0]) * m_height;
		a[0] += a[1];
		return hr;
	}

	private int fingerPartN() {
		return 2 * m_N + 2;
	};

	// //////////////////////////////////////////
	//
	// create hand geometry
	//
	private Geometry HandShape() {

		int stripCounts[] = { fingerPartN(), fingerPartN(), fingerPartN(),
				fingerPartN(), fingerPartN(), fingerPartN(), fingerPartN(),
				fingerPartN(), fingerPartN(), fingerPartN(), fingerPartN(),
				fingerPartN(), fingerPartN(), fingerPartN(), fingerPartN(),
				m_palmN, m_palmN };

		TriangleStripArray hand = new TriangleStripArray(fingerPartN() * 15
				+ m_palmN * 2, TriangleStripArray.COORDINATES
				| TriangleStripArray.NORMALS, stripCounts);

		for (i = 0; i < 5; i++)
			for (j = 0; j < fingerPartN() * 3; j++) {
				m_finger[i].getCoordinate(j, m_point);
				m_fingerTransformMatrix[i].transform(m_point);
				hand.setCoordinate(j + fingerPartN() * i * 3, m_point);
				m_finger[i].getNormal(j, m_normal);
				m_fingerTransformMatrix[i].transform(m_normal);
				m_normal.normalize();
				hand.setNormal(j + fingerPartN() * i * 3, m_normal);
			}

		for (i = 0; i < m_palmN * 2; i++) {
			m_handPalm.getCoordinate(i, m_point);
			m_palmTransformMatrix.transform(m_point);
			hand.setCoordinate(fingerPartN() * 15 + i, m_point);
			m_handPalm.getNormal(i, m_normal);
			m_palmTransformMatrix.transform(m_normal);
			m_normal.normalize();
			hand.setNormal(fingerPartN() * 15 + i, m_normal);
		}

		return hand;

	} // end of method HandShape in class HandShape

	// //////////////////////////////////////////
	//
	// create hand appearance
	//
	private Appearance handAppearance() {

		Appearance appearance = new Appearance();
		PolygonAttributes polyAttrib = new PolygonAttributes();
		Material material = new Material();

		material.setDiffuseColor(m_diffuseColor);
		material.setSpecularColor(m_specularColor);
		material.setShininess(m_shine);
		appearance.setMaterial(material);

		polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
		polyAttrib.setBackFaceNormalFlip(true);
		if (m_wireMode)
			polyAttrib.setPolygonMode(PolygonAttributes.POLYGON_LINE);
		else
			polyAttrib.setPolygonMode(PolygonAttributes.POLYGON_FILL);

		appearance.setPolygonAttributes(polyAttrib);

		return appearance;

	} // end of method handAppearance of class HandShape

	// //////////////////////////////////////////
	//
	// create a finger
	//
	private TriangleStripArray handPalm() {

		int handPalmN = 16;

		int handPalmStripCounts[] = { handPalmN, handPalmN };

		TriangleStripArray handPalm = new TriangleStripArray(handPalmN * 2,
				TriangleStripArray.COORDINATES | TriangleStripArray.NORMALS,
				handPalmStripCounts);

		Point3f upperPoint = new Point3f();
		Point3f lowerPoint = new Point3f();
		Point3f p = new Point3f();

		float w = (m_finger_r0[1] + m_finger_r0[2] + m_finger_r0[3]) / 2;
		float w_inc = (m_finger_r0[1] + m_finger_r0[2] + m_finger_r0[3]) / 6;
		float h = m_height / 2;
		float a[] = { (float) Math.PI / 12, (float) Math.PI * 5 / 36 };

		upperPoint.x = -2 * m_finger_r0[4];
		upperPoint.y = -m_height / 2.0f;
		upperPoint.z = m_length_4;
		handPalm.setCoordinate(0, upperPoint);

		upperPoint.x += m_finger_r0[4];
		upperPoint.z = m_length_3;
		handPalm.setCoordinate(1, upperPoint);

		lowerPoint.x = 0.0f;
		lowerPoint.y = wristTransform(a);
		lowerPoint.z = 0.0f;
		handPalm.setCoordinate(2, lowerPoint);

		upperPoint.x = 0.0f;
		upperPoint.z = m_length_2;
		handPalm.setCoordinate(3, upperPoint);

		lowerPoint.x += m_finger_r0[3];
		lowerPoint.y = wristTransform(a);
		handPalm.setCoordinate(4, lowerPoint);

		upperPoint.x += m_finger_r0[3];
		upperPoint.z = m_length_1;
		handPalm.setCoordinate(5, upperPoint);

		lowerPoint.x += m_finger_r0[3];
		lowerPoint.y = wristTransform(a);
		handPalm.setCoordinate(6, lowerPoint);

		upperPoint.x += m_finger_r0[3];
		upperPoint.z = m_length_0;
		handPalm.setCoordinate(7, upperPoint);

		lowerPoint.x += m_finger_r0[2];
		lowerPoint.y = wristTransform(a);
		handPalm.setCoordinate(8, lowerPoint);

		upperPoint.x += m_finger_r0[2];
		handPalm.setCoordinate(9, upperPoint);

		lowerPoint.x += m_finger_r0[2];
		lowerPoint.y = wristTransform(a);
		handPalm.setCoordinate(10, lowerPoint);

		upperPoint.x += m_finger_r0[2];
		handPalm.setCoordinate(11, upperPoint);

		lowerPoint.x += m_finger_r0[1];
		lowerPoint.y = wristTransform(a);
		handPalm.setCoordinate(12, lowerPoint);

		upperPoint.x += m_finger_r0[1];
		upperPoint.z = m_length_1;
		handPalm.setCoordinate(13, upperPoint);

		lowerPoint.x += m_finger_r0[1];
		lowerPoint.y = wristTransform(a);
		handPalm.setCoordinate(14, lowerPoint);

		upperPoint.x += m_finger_r0[1];
		upperPoint.z = m_length_2;
		handPalm.setCoordinate(15, upperPoint);

		for (i = 0; i < m_palmN; i++) {
			handPalm.getCoordinate(i, m_point);
			m_point.y = -m_point.y;
			handPalm.setCoordinate(m_palmN + i, m_point);
		}

		for (i = 0; i < m_palmN * 2; i++) {
			handPalm.getCoordinate(i, p);
			if (i % 2 == 0)
				m_normal.sub(p, new Point3f(
						m_finger_r0[3] * 2 + m_finger_r0[2], 0.0f, 0.0f));
			else
				m_normal.sub(p, new Point3f(
						m_finger_r0[3] * 2 + m_finger_r0[2], m_length_0, 0.0f));
			handPalm.setNormal(i, m_normal);
		}

		return handPalm;

	} // end of method handPalm of class HandShape

	// //////////////////////////////////////////
	//
	// create a finger
	//
	private TriangleStripArray handFinger(float l1, float l2, float l3,
			float r1, float r2, float r3, float r4, float rx, float ry, float rr) {

		int totalN = 2 * m_N + 2;
		int stripCounts[] = { totalN, totalN, totalN };
		float xt0;
		float yt0;
		float zt0;
		float xt1;
		float yt1;
		float zt1;

		TriangleStripArray finger;

		Matrix4f position = new Matrix4f();
		Point3f p1 = new Point3f();
		Point3f p2 = new Point3f();
		Vector3f n1 = new Vector3f();
		Vector3f n2 = new Vector3f();

		finger = new TriangleStripArray(totalN * 3,
				TriangleStripArray.COORDINATES | TriangleStripArray.NORMALS,
				stripCounts);

		m_point = new Point3f();

		TriangleStripArray tsa1 = Conus(m_point, r1, r2, l1, rx, 0.0f, m_N);

		position.setIdentity();
		position.rotX(rx);
		m_point = new Point3f(0, 0, l1);
		position.transform(m_point);

		TriangleStripArray tsa2 = Conus(m_point, r2, r3, l2, rx + rr, 0.0f, m_N);

		position.setIdentity();
		position.rotX(rx + rr);
		position.setTranslation(new Vector3f(m_point.x, m_point.y, m_point.z));
		m_point = new Point3f(0, 0, l2);
		position.transform(m_point);

		TriangleStripArray tsa3 = Conus(m_point, r3, r4, l3, rx + 2 * rr, 0.0f,
				m_N);

		for (int i = 0; i < totalN; i++) {
			tsa1.getCoordinate(i, m_point);
			tsa1.getNormal(i, m_normal);
			finger.setCoordinate(i, m_point);
			finger.setNormal(i, m_normal);
		}
		for (int i = totalN; i < totalN * 2; i += 2) {
			tsa2.getCoordinate(i - totalN, p1);
			tsa2.getNormal(i - totalN, n1);

			if (m_weldFingers) {
				tsa1.getCoordinate(i - totalN + 1, p2);
				tsa1.getNormal(i - totalN + 1, n2);
				m_point.interpolate(p1, p2, 0.5f);
				m_normal.interpolate(n1, n2, 0.5f);
				finger.setCoordinate(i, m_point);
				finger.setNormal(i, m_normal);
				finger.setCoordinate(i - totalN + 1, m_point);
				finger.setNormal(i - totalN + 1, m_normal);
			} else {
				finger.setCoordinate(i, p1);
				finger.setNormal(i, n1);
			}

			tsa2.getCoordinate(i - totalN + 1, m_point);
			tsa2.getNormal(i - totalN + 1, m_normal);
			finger.setCoordinate(i + 1, m_point);
			finger.setNormal(i + 1, m_normal);
		}
		for (int i = totalN * 2; i < totalN * 3; i += 2) {
			tsa3.getCoordinate(i - totalN * 2, p1);
			tsa3.getNormal(i - totalN * 2, n1);

			if (m_weldFingers) {
				tsa2.getCoordinate(i - totalN * 2 + 1, p2);
				tsa2.getNormal(i - totalN * 2 + 1, n2);
				m_point.interpolate(p1, p2, 0.5f);
				m_normal.interpolate(n1, n2, 0.5f);
				finger.setCoordinate(i, m_point);
				finger.setNormal(i, m_normal);
				finger.setCoordinate(i - totalN + 1, m_point);
				finger.setNormal(i - totalN + 1, m_normal);

			} else {
				finger.setCoordinate(i, p1);
				finger.setNormal(i, n1);
			}

			tsa3.getCoordinate(i - totalN * 2 + 1, m_point);
			tsa3.getNormal(i - totalN * 2 + 1, m_normal);
			finger.setCoordinate(i + 1, m_point);
			finger.setNormal(i + 1, m_normal);
		}

		position.setIdentity();
		position.rotY(ry);

		for (int i = 0; i < totalN * 3; i++) {
			finger.getCoordinate(i, m_point);
			position.transform(m_point);
			finger.setCoordinate(i, m_point);
		}

		return finger;

	} // end of method handFinger of class HandShape

	// create Conus
	//
	TriangleStripArray Conus(Point3f t, float r1, float r2, float w, float rx,
			float rz, int N) {

		Matrix4f conePosition = new Matrix4f();
		int totalN = 2 * N + 2;
		Point3f coords[] = new Point3f[totalN];
		int stripCounts[] = { totalN };
		int n;
		double a;
		Point3f p = new Point3f();
		Point3f p1 = new Point3f();
		Point3f p2 = new Point3f();
		Point3f p_trans = new Point3f();
		Point3f p_low = new Point3f(0.0f, 0.0f, 0.0f);
		Point3f p_up = new Point3f(0.0f, 0.0f, w);

		conePosition.setIdentity();
		conePosition.rotX(rx);
		conePosition.setTranslation(new Vector3f(t.x, t.y, t.z));

		p1.z = 0.0f;
		p2.z = w;

		double delta = 2.0 * Math.PI / (totalN - 2);

		for (a = 0, n = 0; n < totalN - 2; n += 2) {
			a = delta * n;
			p1.x = (float) (r1 * Math.cos(a));
			p1.y = (float) (r1 * Math.sin(a));
			p2.x = (float) (r2 * Math.cos(a + delta * 0.5d));
			p2.y = (float) (r2 * Math.sin(a + delta * 0.5d));

			conePosition.transform(p1, p_trans);
			coords[n + 0] = new Point3f(p_trans);

			conePosition.transform(p2, p_trans);
			coords[n + 1] = new Point3f(p_trans);
		}

		coords[totalN - 2] = coords[0];
		coords[totalN - 1] = coords[1];

		TriangleStripArray cone = new TriangleStripArray(totalN,
				TriangleStripArray.COORDINATES | TriangleStripArray.NORMALS,
				stripCounts);

		cone.setCoordinates(0, coords);

		conePosition.transform(p_low);
		conePosition.transform(p_up);

		for (int z = 0; z < totalN; z++) {
			cone.getCoordinate(z, p);
			if (z % 2 == 0)
				m_normal.sub(p, p_low);
			else
				m_normal.sub(p, p_up);
			cone.setNormal(z, m_normal);
		}

		return cone;

	} // end of method Conus of class HandShape

	/**
	 * This method lets you retrieve the six degrees of freedom of the wrist.
	 * 
	 * @return a float[6] containing the translations x, y en z of the wrist and
	 *         the rotation angles of the wrist about X, Y & Z axis.
	 */
	public float[] getGlobalOrientation() {

		float[] handOrientation = new float[6];

		handOrientation[0] = m_t.x;
		handOrientation[1] = m_t.y;
		handOrientation[2] = m_t.z;
		handOrientation[3] = m_rx;
		handOrientation[4] = m_ry;
		handOrientation[5] = m_rz;

		return handOrientation;

	} // end of method getGlobalOrientation in class HandShape

	/**
	 * This method lets you retrieve the rotations of the fingers.
	 * 
	 * @return a float[5][3] of which each row represents rotations rx, ry and
	 *         rr of a finger.
	 */
	public float[][] getFingerOrientation() {

		float[][] fingerProperties = new float[5][3];
		float[] rx = getProperty_rx();
		float[] ry = getProperty_ry();
		float[] rr = getProperty_rr();

		for (int i = 0; i < 5; i++) {
			fingerProperties[i][0] = rx[i];
			fingerProperties[i][1] = ry[i];
			fingerProperties[i][2] = rr[i];
		}

		return fingerProperties;
	} // end of method getFingerOrientation in class HandShape

	/**
	 * Retrieves the l0 property of the hand's fingers.
	 * 
	 * @return a float[] array containing lengths l0 of fingers 0 -> 4.
	 */
	public float[] getProperty_l0() {
		return m_finger_l0;
	}

	/**
	 * Retrieves the l0 property of one finger.
	 * 
	 * @return a float which is length l0 of finger i.
	 */
	public float getProperty_l0(int i) {
		return m_finger_l0[i];
	}

	/**
	 * Retrieves the l1 property of the hand's fingers.
	 * 
	 * @return a float[] array containing lengths l1 of fingers 0 -> 4.
	 */
	public float[] getProperty_l1() {
		return m_finger_l1;
	}

	/**
	 * Retrieves the l1 property of one finger.
	 * 
	 * @return a float which is length l1 of finger i.
	 */
	public float getProperty_l1(int i) {
		return m_finger_l1[i];
	}

	/**
	 * Retrieves the l2 property of the hand's fingers.
	 * 
	 * @return a float[] array containing lengths l2 of fingers 0 -> 4.
	 */
	public float[] getProperty_l2() {
		return m_finger_l2;
	}

	/**
	 * Retrieves the l2 property of one finger.
	 * 
	 * @return a float which is length l2 of finger i.
	 */
	public float getProperty_l2(int i) {
		return m_finger_l2[i];
	}

	/**
	 * Retrieves the r0 property of the hand's fingers.
	 * 
	 * @return a float[] array containing radii r0 of fingers 0 -> 4.
	 */
	public float[] getProperty_r0() {
		return m_finger_r0;
	}

	/**
	 * Retrieves the r0 property of one finger.
	 * 
	 * @return a float which is radius r0 of finger i.
	 */
	public float getProperty_r0(int i) {
		return m_finger_r0[i];
	}

	/**
	 * Retrieves the r1 property of the hand's fingers.
	 * 
	 * @return a float[] array containing radii r1 of fingers 0 -> 4.
	 */
	public float[] getProperty_r1() {
		return m_finger_r1;
	}

	/**
	 * Retrieves the r1 property of one finger.
	 * 
	 * @return a float which is radius r1 of finger i.
	 */
	public float getProperty_r1(int i) {
		return m_finger_r1[i];
	}

	/**
	 * Retrieves the r2 property of the hand's fingers.
	 * 
	 * @return a float[] array containing radii r2 of fingers 0 -> 4.
	 */
	public float[] getProperty_r2() {
		return m_finger_r2;
	}

	/**
	 * Retrieves the r2 property of one finger.
	 * 
	 * @return a float which is radius r2 of finger i.
	 */
	public float getProperty_r2(int i) {
		return m_finger_r2[i];
	}

	/**
	 * Retrieves the r3 property of the hand's fingers.
	 * 
	 * @return a float[] array containing radii r3 of fingers 0 -> 4.
	 */
	public float[] getProperty_r3() {
		return m_finger_r3;
	}

	/**
	 * Retrieves the r3 property of one finger.
	 * 
	 * @return a float which is radius r3 of finger i.
	 */
	public float getProperty_r3(int i) {
		return m_finger_r3[i];
	}

	/**
	 * Retrieves the rx property of one finger.
	 * 
	 * @return a float[] array containing rotation angles about X of fingers 0
	 *         -> 4.
	 */
	public float[] getProperty_rx() {
		return m_finger_rx;
	}

	/**
	 * Retrieves the rx property of the hand's fingers.
	 * 
	 * @return a float which is the rotation angle about X of finger i.
	 */
	public float getProperty_rx(int i) {
		return m_finger_rx[i];
	}

	/**
	 * Retrieves the ry property of one finger.
	 * 
	 * @return a float[] array containing rotation angles about Y of fingers 0
	 *         -> 4.
	 */
	public float[] getProperty_ry() {
		return m_finger_ry;
	}

	/**
	 * Retrieves the ry property of the hand's fingers.
	 * 
	 * @return a float which is the rotation angle about Y of finger i.
	 */
	public float getProperty_ry(int i) {
		return m_finger_ry[i];
	}

	/**
	 * Retrieves the rr property of one finger.
	 * 
	 * @return a float[] array containing bending angles about X of fingers 0 ->
	 *         4.
	 */
	public float[] getProperty_rr() {
		return m_finger_rr;
	}

	/**
	 * Retrieves the rr property of the hand's fingers.
	 * 
	 * @return a float which is the bending angle about X of finger i.
	 */
	public float getProperty_rr(int i) {
		return m_finger_rr[i];
	}

	/**
	 * Retrieves the height property of the hand.
	 */
	public float getProperty_height() {
		return m_height;
	}

	/**
	 * Retrieves the length property of the hand.
	 */
	public float getProperty_lenght() {
		return m_length_0;
	}

} // end of class HandShape
package edu.seas.upenn.rendering;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;

import com.sun.j3d.utils.applet.MainFrame; 
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import java.awt.event.*;
import java.util.Enumeration;

/**
 * The class HandSimulationApp illustrates how to make use of the <a href="HandSimulation.html">HandSimulation</a> class. Two default HandSimulation objects are constructed, appearance, geometry, start position, target position and number of frames are modified, and finally updated. The scene also contains an AmbientLight, a DirectionalLight, and a PointLight, in order to shade the hands' surfaces.
 * <br><br>
 * A basic user interface is provided, which gives the user the opportunity to rotate, translate, zoom, and animate the scene:
 * <ul>
 * <li>left mouse button: rotate scene.
 * <li>right mouse button: translate scene.
 * <li>Alt + left mouse button: zoom scene.
 * <li>animate the shaded hand, by hitting a key.
 * </ul>
 * <blockquote><blockquote>
 * <table border="0">
 * <tr>
 * 	<td align="center"><A HREF="startPosition1.gif">
 * 		<IMG SRC="tn_startPosition1.gif" border="0" alt="startPosition1.gif" width="150" height="123"></A>
 * 	</td>
 * 	<td align="center"><A HREF="startPosition2.gif">
 * 		<IMG SRC="tn_startPosition2.gif" border="0" alt="startPosition2.gif" width="150" height="123"></A>
 * 	</td>
 * </tr>
 * <tr>
 * 	<td align="center"><A HREF="tempPosition1.gif">
 * 		<IMG SRC="tn_tempPosition1.gif" border="0" alt="tempPosition1.gif" width="150" height="123"></A>
 * 	</td>
 * 	<td align="center"><A HREF="tempPosition2.gif">
 * 		<IMG SRC="tn_tempPosition2.gif" border="0" alt="tempPosition2.gif" width="150" height="123"></A>
 * 	</td>
 * </tr>
 * <tr>
 * 	<td align="center"><A HREF="targetPosition1.gif">
 * 		<IMG SRC="tn_targetPosition1.gif" border="0" alt="targetPosition1.gif" width="150" height="123"></A>
 * 	</td>
 * 	<td align="center"><A HREF="targetPosition2.gif">
 * 		<IMG SRC="tn_targetPosition2.gif" border="0" alt="targetPosition2.gif" width="150" height="123"></A>
 * 	</td>
 * </tr>
 * </table>
 * </blockquote></blockquote>
 * @since JDK1.2
 * @author Stefan Hendrickx
 * <br><br>
 * You are free to use the software for personal, non-commercial use. The software is protected by copyright and all rights are reserved by the author. It comes without warranty of any kind, no liability is assumed for its use.
 */
public class HandSimulationApp extends Applet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6108084046040800259L;

	class OpenBehavior extends Behavior {

		private HandSimulation m_targetHand;
		private WakeupCriterion m_pairPostCondition;
		private WakeupCriterion m_wakeupNextFrame;
		private WakeupCriterion m_AWTEventCondition;
		private int m_currentStep;

		OpenBehavior(HandSimulation targetHand) {
			m_targetHand = targetHand;
			m_AWTEventCondition = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
			m_wakeupNextFrame = new WakeupOnElapsedFrames(0);
		}

		public void setBehaviorObjectPartner(Behavior behaviorObject) {
			m_pairPostCondition = new WakeupOnBehaviorPost(behaviorObject, 1);
		}

		public void initialize() {
			this.wakeupOn(m_AWTEventCondition);
			m_currentStep = 0;
		}

		public void processStimulus(Enumeration criteria) {
			if (criteria.nextElement().equals(m_pairPostCondition)) {
				System.out.println("ready to open hand");
				this.wakeupOn(m_AWTEventCondition);
				m_currentStep = 0;
			}
			else { // could be KeyPress or nextFrame, in either case: open
				if (m_currentStep < m_targetHand.getNumberOfSteps()) {
					m_currentStep++;
					m_targetHand.move(m_currentStep);
					this.wakeupOn(m_wakeupNextFrame);
				}
				else { // finished opening hand, signal other behavior
					System.out.println("hand is open");
					this.wakeupOn(m_pairPostCondition);
					postId(1);
				}
			}
		}

	} // end of class OpenBehavior

	class CloseBehavior extends Behavior{

		private HandSimulation m_targetHand;
		private WakeupCriterion m_pairPostCondition;
		private WakeupCriterion m_wakeupNextFrame;
		private WakeupCriterion m_AWTEventCondition;
		private int m_currentStep;

		CloseBehavior(HandSimulation targetHand) {
			m_targetHand = targetHand;
			m_AWTEventCondition = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
			m_wakeupNextFrame = new WakeupOnElapsedFrames(0);
		        }

		public void setBehaviorObjectPartner(Behavior behaviorObject) {
			m_pairPostCondition = new WakeupOnBehaviorPost(behaviorObject, 1);
		}

		public void initialize() {
			this.wakeupOn(m_pairPostCondition);
			m_currentStep = m_targetHand.getNumberOfSteps();
		}
    
		public void processStimulus(Enumeration criteria) {
			if (criteria.nextElement().equals(m_pairPostCondition)) {
				System.out.println("ready to close hand");
				this.wakeupOn(m_AWTEventCondition);
				m_currentStep = m_targetHand.getNumberOfSteps();
			}
			else { // could be KeyPress or nextFrame, in either case: close
				if (m_currentStep > 0) {
					m_currentStep--;
					m_targetHand.move(m_currentStep);
					this.wakeupOn(m_wakeupNextFrame);
				} else { // finished opening hand, signal other behavior
				System.out.println("hand is closed");
				this.wakeupOn(m_pairPostCondition);
				postId(1);
	        	        }
			}
		}

    } // end of class CloseBehavior

	/////////////////////////////////////////////////
	//
	// create scene graph branch group
	//
	
	BranchGroup createSceneGraph() {

		BranchGroup objRoot = new BranchGroup();

		  // Create the transform group node and initialize it to the
		  // identity.	Enable the TRANSFORM_WRITE capability so that
		  // our behavior code can modify it at runtime.  Add it to the
		  // root of the subgraph.

        TransformGroup objTransform = new TransformGroup();
        objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

		HandSimulation hand1 = new HandSimulation(); 
		HandSimulation hand2 = new HandSimulation();
	
		hand1.setCapability(Shape3D.ALLOW_APPEARANCE_READ); hand1.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		hand1.setCapability(Shape3D.ALLOW_GEOMETRY_READ); hand1.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
	
		hand2.setCapability(Shape3D.ALLOW_APPEARANCE_READ); hand2.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		hand2.setCapability(Shape3D.ALLOW_GEOMETRY_READ); hand2.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
	
		float[][] fingerOrientation = {{0.70f, -0.10f, -0.2f},
						{0.0f,  0.10f, 0.2f},
						{0.0f, -0.10f, 0.2f},
						{0.0f, -0.20f, 0.2f},
						{0.0f, -0.30f, 0.2f}};
	
		float[] handOrientation1 = {-0.6f, 0.0f, -0.3f, -0.7f, 0.0f, -1.0f};
		float[] handOrientation2 = {0.4f, 0.0f, 0.0f, -0.2f, -0.1f, 0.0f};
	
		hand1.setGlobalOrientation(handOrientation2);
		hand1.update();
	
		hand1.setNumberOfSteps(20);
		hand1.setStartOrientationHand(handOrientation1);
		hand1.setTargetOrientationHand(handOrientation2);
		hand1.setStartOrientationFingers(fingerOrientation);
	
        objTransform.addChild(hand1);
		hand2.setGlobalOrientation(handOrientation2);
		hand2.setVisualisationMode(true, true, new Color3f(0.0f, 0.0f, 0.0f), new Color3f(0.0f, 0.0f, 0.0f), 0.0f, 3);
		hand2.update();
        objTransform.addChild(hand2);
	
		AmbientLight lightA = new AmbientLight();
		lightA.setInfluencingBounds(new BoundingSphere());
		objTransform.addChild(lightA);
	
		DirectionalLight lightD1 = new DirectionalLight();
		lightD1.setInfluencingBounds(new BoundingSphere(new Point3d(0.0d, 0.0d, 0.0d), 5.0d));
		Vector3f direction = new Vector3f(0.0f, 1.0f, 0.0f);
		direction.normalize();
		lightD1.setDirection(direction);
		lightD1.setColor(new Color3f(0.5f, 0.5f, 0.5f));
		objTransform.addChild(lightD1);
	
		PointLight lampLight = new PointLight();
		lampLight.setPosition(0.8f, 1.0f, 0.5f);
		lampLight.setInfluencingBounds(new BoundingSphere());
		objTransform.addChild(lampLight);
	
		Background background = new Background();
		background.setApplicationBounds(new BoundingSphere());
		background.setColor(0.5f, 0.5f, 0.5f);
		objRoot.addChild(background);

        MouseRotate myMouseRotate = new MouseRotate();
        myMouseRotate.setTransformGroup(objTransform);
        myMouseRotate.setSchedulingBounds(new BoundingSphere());
        objTransform.addChild(myMouseRotate);

        MouseTranslate myMouseTranslate = new MouseTranslate();
        myMouseTranslate.setTransformGroup(objTransform);
        myMouseTranslate.setSchedulingBounds(new BoundingSphere());
        objTransform.addChild(myMouseTranslate);

        MouseZoom myMouseZoom = new MouseZoom();
        myMouseZoom.setTransformGroup(objTransform);
        myMouseZoom.setSchedulingBounds(new BoundingSphere());
        objTransform.addChild(myMouseZoom);

        OpenBehavior openHand  = new OpenBehavior(hand1);
        CloseBehavior closeHand = new CloseBehavior(hand1);

        //prepare the behavior objects
        openHand.setBehaviorObjectPartner(closeHand);
        closeHand.setBehaviorObjectPartner(openHand);

        // set scheduling bounds for behavior objects
        BoundingSphere bounds = new BoundingSphere();
        openHand.setSchedulingBounds(bounds);
        closeHand.setSchedulingBounds(bounds);

        objRoot.addChild(openHand);
        objRoot.addChild(closeHand);
        objRoot.addChild(objTransform);

	// Let Java 3D perform optimizations on this scene graph.
		objRoot.compile();

		return objRoot;
	} // end of CreateSceneGraph method of HandSimulationApp

	// Create a simple scene and attach it to the virtual universe

	public HandSimulationApp() {

		setLayout(new BorderLayout());
		GraphicsConfiguration config =
				SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas3D = new Canvas3D(config);
		add("Center", canvas3D);
	
		BranchGroup scene = createSceneGraph();
	
		// SimpleUniverse is a Convenience Utility class
		SimpleUniverse simpleU = new SimpleUniverse(canvas3D);
	
		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		simpleU.getViewingPlatform().setNominalViewingTransform();

		simpleU.addBranchGraph(scene);
	} // end of HandSimulationApp constructor

	//	The following allows this to be run as an application
	//	as well as an applet

	static void main(String[] args) {
		Frame frame = new MainFrame(new HandSimulationApp(), 640, 640);
	} // end of main method of HandSimulationApp

} // end of class HandSimulationApp
package edu.seas.upenn.rendering;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Frame;
import com.sun.j3d.utils.applet.MainFrame; 
import com.sun.j3d.utils.behaviors.mouse.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import java.awt.GraphicsConfiguration;
import com.sun.j3d.utils.universe.SimpleUniverse;


/**
 * The class HandShapeApp illustrates how to make use of the <a href="HandShape.html">HandShape</a> class. Two default HandShape objects are constructed, appearance and geometry are manipulated, and finally updated. The scene also contains an AmbientLight, a DirectionalLight, and a PointLight, in order to shade the hands' surfaces.
 * <br><br>
 * A basic user interface is provided, which gives the user the opportunity to rotate, translate, and zoom the scene:
 * <ul>
 * <li>left mouse button: rotate scene.
 * <li>right mouse button: translate scene.
 * <li>Alt + left mouse button: zoom scene.
 * </ul>
 * @since JDK1.2
 * @author Stefan Hendrickx
 * <br><br>
 * You are free to use the software for personal, non-commercial use. The software is protected by copyright and all rights are reserved by the author. It comes without warranty of any kind, no liability is assumed for its use.
 */
public class HandShapeApp extends Applet {

	/////////////////////////////////////////////////
	//
	// create scene graph branch group
	//
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6739303850824284934L;

	BranchGroup createSceneGraph() {

	BranchGroup objRoot = new BranchGroup();

	  // Create the transform group node and initialize it to the
	  // identity.	Enable the TRANSFORM_WRITE capability so that
	  // our behavior code can modify it at runtime.  Add it to the
	  // root of the subgraph.

        TransformGroup objTransform = new TransformGroup();
        objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        objRoot.addChild(objTransform);

	HandShape hand1 = new HandShape();
	HandShape hand2 = new HandShape();

	float[] fingerOrientation = {0.0f, 0.0f, 0.0f};
	float[] handOrientation1 = {-0.5f, 0.0f, 0.0f, -0.2f, -0.1f, 0.0f};
	hand1.setFingerOrientation(1, fingerOrientation);
	hand1.setGlobalOrientation(handOrientation1);
	hand1.update();
        objTransform.addChild(hand1);

	float[] handOrientation2 = {0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
	hand2.setGlobalOrientation(handOrientation2);
	hand2.setVisualisationMode(true, true, new Color3f(0.0f, 0.0f, 0.0f), new Color3f(0.0f, 0.0f, 0.0f), 0.0f, 8);
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

	// Let Java 3D perform optimizations on this scene graph.
		objRoot.compile();

	return objRoot;
	} // end of CreateSceneGraph method of HandShapeApp

	// Create a simple scene and attach it to the virtual universe

	public HandShapeApp() {

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
	} // end of HandShapeApp constructor

	//	The following allows this to be run as an application
	//	as well as an applet

	static void main(String[] args) {
		Frame frame = new MainFrame(new HandShapeApp(), 640, 640);
	} // end of main method of HandShapeApp

} // end of class HandShapeApp
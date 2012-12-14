// TrackerPanel3D.java
// Andrew Davison, October 2011, ad@fivedots.psu.ac.th

/* This class has two main tasks:
 * create the 3D scene
 * create the OpenNI context, user generator, and skeletons
 (which is done in configOpenNI()).

 This class builds a Java 3D scene consisting of a dark green 
 and blue tiled surface with labels along the X and Z axes, 
 a blue background, lit from two different directions. 

 The user (viewer) can move the camera through the scene by moving the mouse.

 Each detected user is represented by a 3D skeleton made up of limbs
 (cylinders) and joints (spheres). The skeletons are managed by the 
 SkelsManager class.

 All of the scene graph, apart from the skeletons,
 comes from the Checkers3D example in Chapter 15,
 "Killer Game Programming in Java"
 (http://fivedots.coe.psu.ac.th/~ad/jg/ch8/), and is explained
 in detail there.
 */
package edu.seas.upenn.rendering;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JPanel;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.OpenNI.Context;
import org.OpenNI.License;
import org.OpenNI.StatusException;
import org.OpenNI.UserGenerator;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class TrackerPanel3D extends JPanel implements Runnable
// Holds the 3D canvas where the skeletons are displayed
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3996897900189486313L;
	private static final int PWIDTH = 640; // size of panel
	private static final int PHEIGHT = 480;

	private static final int BOUNDSIZE = 100; // larger than world

	private static final Point3d USERPOSN = new Point3d(0, 6.5, 10);
	// initial user position

	// 3D vars
	private SimpleUniverse su;
	private BranchGroup sceneBG;
	private BoundingSphere bounds; // for environment nodes

	private volatile boolean isRunning;

	// OpenNI
	private Context context;
	private UserGenerator userGen;

	private SkelsManager skels; // the skeletons manager

	public TrackerPanel3D() {
		setLayout(new BorderLayout());
		setBackground(Color.white);
		setOpaque(false);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));

		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();
		Canvas3D canvas3D = new Canvas3D(config);
		add("Center", canvas3D);
		canvas3D.setFocusable(true); // give focus to the canvas
		canvas3D.requestFocus();

		su = new SimpleUniverse(canvas3D);

		createSceneGraph();
		initUserPosition(); // set user's viewpoint
		orbitControls(canvas3D); // controls for moving the viewpoint

		su.addBranchGraph(sceneBG);

		configOpenNI();

		new Thread(this).start(); // start updating the panel's image
	} // end of TrackerPanel3D()

	private void createSceneGraph()
	// initilise the scene
	{
		sceneBG = new BranchGroup();
		sceneBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND); // to attach &
																	// detach
																	// skeletons
		sceneBG.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
		sceneBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

		bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE);

		lightScene(); // add the lights
		// addBackground(); // add the sky
		// sceneBG.addChild( new CheckerFloor().getBG() ); // add the floor

		sceneBG.compile(); // fix the scene
	} // end of createSceneGraph()

	private void lightScene()
	/* One ambient light, 2 directional lights */
	{
		Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

		// Set up the ambient light
		AmbientLight ambientLightNode = new AmbientLight(white);
		ambientLightNode.setInfluencingBounds(bounds);
		sceneBG.addChild(ambientLightNode);

		// Set up the directional lights
		Vector3f light1Direction = new Vector3f(-1.0f, -1.0f, -1.0f);
		// left, down, backwards
		Vector3f light2Direction = new Vector3f(1.0f, -1.0f, 1.0f);
		// right, down, forwards

		DirectionalLight light1 = new DirectionalLight(white, light1Direction);
		light1.setInfluencingBounds(bounds);
		sceneBG.addChild(light1);

		DirectionalLight light2 = new DirectionalLight(white, light2Direction);
		light2.setInfluencingBounds(bounds);
		sceneBG.addChild(light2);
	} // end of lightScene()

	private void addBackground()
	// A blue sky
	{
		Background back = new Background();
		back.setApplicationBounds(bounds);
		back.setColor(0.17f, 0.65f, 0.92f); // sky colour
		sceneBG.addChild(back);
	} // end of addBackground()

	private void orbitControls(Canvas3D c)
	/*
	 * OrbitBehaviour allows the user to rotate around the scene, and to zoom in
	 * and out.
	 */
	{
		OrbitBehavior orbit = new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
		orbit.setSchedulingBounds(bounds);

		ViewingPlatform vp = su.getViewingPlatform();
		vp.setViewPlatformBehavior(orbit);
	} // end of orbitControls()

	private void initUserPosition()
	// Set the user's initial viewpoint using lookAt()
	{
		ViewingPlatform vp = su.getViewingPlatform();
		TransformGroup steerTG = vp.getViewPlatformTransform();

		Transform3D t3d = new Transform3D();
		steerTG.getTransform(t3d);

		// args are: viewer posn, where looking, up direction
		t3d.lookAt(USERPOSN, new Point3d(0, 0, 0), new Vector3d(0, 1, 0));
		t3d.invert();

		steerTG.setTransform(t3d);
	} // end of initUserPosition()

	// ------------------------OpenNI ---------------

	private void configOpenNI()
	// create context, user generator, and skeletons
	{
		try {
			context = new Context();

			// add the NITE Licence
			License licence = new License("PrimeSense",
					"0KOIk2JeIBYClPWVnMoRKn5cdY4=");
			context.addLicense(licence);
			context.setGlobalMirror(true); // set mirror mode

			userGen = UserGenerator.create(context);
			skels = new SkelsManager(userGen, sceneBG);

			context.startGeneratingAll();
			System.out.println("Started context generating...");
		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	} // end of configOpenNI()

	public void run()
	/*
	 * update and display the userGenerator and skeletons whenever the context
	 * is updated.
	 */
	{
		isRunning = true;
		while (isRunning) {
			try {
				context.waitAnyUpdateAll();
			} catch (StatusException e) {
				System.out.println(e);
				System.exit(1);
			}
			skels.update(); // get the skeletons manager to carry out the
							// updates
		}
		// close down
		try {
			context.stopGeneratingAll();
		} catch (StatusException e) {
		}
		context.release();
		System.exit(0);
	} // end of run()

	public void closeDown() {
		isRunning = false;
	}

} // end of TrackerPanel3D class


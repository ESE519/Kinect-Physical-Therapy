package edu.seas.upenn.ese519;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.util.ArrayList;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.universe.ViewingPlatform;

public class PlayerPanel3D extends PlayerView {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int BOUNDSIZE = 100; // larger than world

	private static final Point3d USERPOSN = new Point3d(0, 6.5, 10);
	// initial user position

	// 3D vars
	private SimpleUniverse su;
	private BranchGroup sceneBG;
	private BoundingSphere bounds; // for environment nodes
	// private Canvas3D canvas3D;

	private volatile boolean isRunning;

	private DataGenerator dataGen; // the skeletons manager
	private SerialComm serialComm; // Serial Communication manager

	private int updateRate;

	private double rshoulderangle;
	private double relbowangle;
	private double lshoulderangle;
	private double lelbowangle;
	private int lrotateangle;

	Canvas3D canvas3D;

	public PlayerPanel3D(int updateRate, DataGenerator dataGen,
			SerialComm serialComm) {
		this.updateRate = updateRate;
		this.dataGen = dataGen;
		this.serialComm = serialComm;

		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();
		canvas3D = new Canvas3D(config);
		add("Center", canvas3D);
		su = new SimpleUniverse(canvas3D);

		createSceneGraph();
		orbitControls(canvas3D); // controls for moving the viewpoint

		su.addBranchGraph(sceneBG);
		this.dataGen.set3DScene(sceneBG);
		canvas3D.setFocusable(true); // give focus to the canvas
		canvas3D.requestFocus();

		initUserPosition(); // set user's viewpoint
		setBackground(Color.white);
		setOpaque(false);
	} // end of TrackerPanel3D()

	public void initModel() {
		// GraphicsConfiguration config =
		// SimpleUniverse.getPreferredConfiguration();
		// canvas3D = new Canvas3D(config);
		//
		// add("Center", canvas3D);
		// su = new SimpleUniverse(canvas3D);
		//
		// createSceneGraph();
		// orbitControls(canvas3D); // controls for moving the viewpoint
		//
		// su.addBranchGraph( sceneBG );
		// this.dataGen.set3DScene(sceneBG);
		// canvas3D.setFocusable(true); // give focus to the canvas
		// canvas3D.requestFocus();
		//
		// initUserPosition(); // set user's viewpoint
		// setBackground(Color.white);
		// setOpaque( false );
	}

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
		addBackground(); // add the sky

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

	public void run()
	/*
	 * update and display the userGenerator and skeletons whenever the context
	 * is updated.
	 */
	{
		isRunning = true;
		while (isRunning) {
			update();
			dataGen.update3DSkeleton();
		}
	} // end of run()

	public void setStop() {
		this.isRunning = false;
	}

	public boolean isRunning() {
		return this.isRunning;
	}

	@Override
	public void update() {
		ArrayList<Double> anglelist = dataGen.getRealTimeAngles();

		lelbowangle = anglelist.get(0);
		lshoulderangle = anglelist.get(1);
		rshoulderangle = anglelist.get(2);
		relbowangle = anglelist.get(3);

		double temp = Math.toDegrees(anglelist.get(4));
		lrotateangle = (int) temp;

	}

	@Override
	public ArrayList<Double> getAngels() {
		ArrayList<Double> list = new ArrayList<Double>();
		list.add(lelbowangle);
		list.add(lshoulderangle);
		list.add(relbowangle);
		list.add(rshoulderangle);
		list.add(Math.toRadians((double) lrotateangle));
		return list;
	}

}

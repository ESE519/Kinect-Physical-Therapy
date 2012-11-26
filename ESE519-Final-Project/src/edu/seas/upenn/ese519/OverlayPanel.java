package edu.seas.upenn.ese519;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import org.OpenNI.CalibrationProgressEventArgs;
import org.OpenNI.CalibrationProgressStatus;
import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.GeneralException;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.ImageGenerator;
import org.OpenNI.ImageMetaData;
import org.OpenNI.License;
import org.OpenNI.MapOutputMode;
import org.OpenNI.NativeAccess;
import org.OpenNI.PixelFormat;
import org.OpenNI.Point3D;
import org.OpenNI.PoseDetectionCapability;
import org.OpenNI.PoseDetectionEventArgs;
import org.OpenNI.SkeletonCapability;
import org.OpenNI.SkeletonJoint;
import org.OpenNI.SkeletonJointPosition;
import org.OpenNI.StatusException;
import org.OpenNI.UserEventArgs;
import org.OpenNI.UserGenerator;



public class OverlayPanel extends JPanel implements Runnable{
	String calibPose = "Psi";
	class NewUserObserver implements IObserver<UserEventArgs>
	{
		@Override
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args)
		{
			System.out.println("New user " + args.getId());
			try
			{
				poseDetectionCap.startPoseDetection(calibPose, args.getId());
			} catch (StatusException e){
				e.printStackTrace();
			}
		}
	}
	class LostUserObserver implements IObserver<UserEventArgs>
	{
		@Override
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args)
		{
			System.out.println("Lost user " + args.getId());
			joints.remove(args.getId());
		}
	}
	
	class CalibrationCompleteObserver implements IObserver<CalibrationProgressEventArgs>
	{
		@Override
		public void update(IObservable<CalibrationProgressEventArgs> observable,
				CalibrationProgressEventArgs args)
		{
			System.out.println("Calibraion complete: " + args.getStatus());
			try
			{
			if (args.getStatus() == CalibrationProgressStatus.OK)
			{
				System.out.println("starting tracking "  +args.getUser());
					skeletonCap.startTracking(args.getUser());
	                joints.put(new Integer(args.getUser()), new HashMap<SkeletonJoint, SkeletonJointPosition>());
			}
			else if (args.getStatus() != CalibrationProgressStatus.MANUAL_ABORT)
			{
				poseDetectionCap.startPoseDetection(calibPose, args.getUser());
			}
			} catch (StatusException e)
			{
				e.printStackTrace();
			}
		}
	}
	class PoseDetectedObserver implements IObserver<PoseDetectionEventArgs>
	{
		@Override
		public void update(IObservable<PoseDetectionEventArgs> observable,
				PoseDetectionEventArgs args)
		{
			System.out.println("Pose " + args.getPose() + " detected for " + args.getUser());
			try
			{
				poseDetectionCap.stopPoseDetection(args.getUser());
				skeletonCap.requestSkeletonCalibration(args.getUser(), true);
			} catch (StatusException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Context context;
	private UserGenerator userGen;
	private ImageGenerator imageGen;
	private DepthGenerator depthGen;
	private SkeletonCapability skeletonCap;
	private PoseDetectionCapability poseDetectionCap;
	private int[] pixelInts;
	private ByteBuffer imageBB; 
	HashMap<Integer, HashMap<SkeletonJoint, SkeletonJointPosition>> joints;
	
	int width, height;  
	private BufferedImage image = null;
	
	private volatile boolean isRunning;
	public boolean overlayModeFlag = false;
	public String exercise = "Left Forearm Stretch Test";
	private int updateRate; 
	
	// used for the average ms processing information
	private int imageCount = 0;
	private long totalTime = 0;
	private DecimalFormat df;
	private Font msgFont;
	 
	
	private static double rshoulderangle;
	private static double relbowangle;
	private static double rwristangle;
	private static double lshoulderangle;
	private static double lelbowangle;
	private	static double lwristangle;
	private static ArrayList<Double> angleList;
	private double shoulderangle;
	private double elbowangle;
	private double wristangle;
	private Point lelbow;
	private Point relbow;
	private Point lwrist;
	private Point rwrist;
	private Point lfinger;
	private Point rfinger;
	private boolean sflag;
	private boolean eflag;
	private boolean wflag;

	private static double shoulderanglemax = Math.toRadians(90.0);
	private static double elbowanglemax = Math.toRadians(180.0);
	private static double wristanglemax = Math.toRadians(45.0);
	private static Point lshoulder = new Point(295, 100);
	private static Point rshoulder = new Point(345, 100);
	private static int uarmlen = 50;
	private static int larmlen = 50;
	private static int handlen = 10;
	
	public OverlayPanel()
	{
		setBackground(Color.WHITE);
		initModel();
		
		df = new DecimalFormat("0.#");  // 1 dp
		msgFont = new Font("SansSerif", Font.BOLD, 18);
		
		configOpenNI();
		pixelInts = new int[width * height];
		imageBB = ByteBuffer.allocateDirect(width * height * 3);
		image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB);
		
		System.out.println("Image dimensions (" + width + ", " +
		                                          height + ")");
	
	} // end of ViewerPanel()
	
	public void initModel() {
		lelbow = new Point();
		relbow = new Point();
		lwrist = new Point();
		rwrist = new Point();
		lfinger = new Point();
		rfinger = new Point();		

		shoulderangle = Math.toRadians(0.0);
		elbowangle = Math.toRadians(90.0);
		wristangle = Math.toRadians(0.0);
		
		lshoulderangle = Math.toRadians(0.0);
		lelbowangle = Math.toRadians(90.0);
		lwristangle = Math.toRadians(0.0);
		rshoulderangle = Math.toRadians(0.0);
		relbowangle = Math.toRadians(90.0);
		rwristangle = Math.toRadians(0.0);
		
		lelbow.setLocation(
				lshoulder.getX() - Math.cos(shoulderangle) * uarmlen,
				lshoulder.getY() - Math.sin(shoulderangle) * uarmlen);
		relbow.setLocation(
				rshoulder.getX() + Math.cos(shoulderangle) * uarmlen,
				rshoulder.getY() - Math.sin(shoulderangle) * uarmlen);
		lwrist.setLocation(lelbow.getX() - Math.cos(elbowangle) * larmlen,
				lelbow.getY() - Math.sin(elbowangle) * larmlen);
		rwrist.setLocation(relbow.getX() + Math.cos(elbowangle) * larmlen,
				relbow.getY() - Math.sin(elbowangle) * larmlen);
		lfinger.setLocation(lwrist.getX() - Math.cos(wristangle) * handlen,
				lwrist.getY() - Math.sin(wristangle) * handlen);
		rfinger.setLocation(rwrist.getX() + Math.cos(wristangle) * handlen,
				rwrist.getY() - Math.sin(wristangle) * handlen);
	}

	private void configOpenNI()
	// create context and image generator
	{
		try {
			context = new Context();
  
			// add the NITE Licence 
			License licence = new License("PrimeSense", "0KOIk2JeIBYClPWVnMoRKn5cdY4=");   // vendor, key
			context.addLicense(licence); 
  
			userGen = UserGenerator.create(context);
			skeletonCap = userGen.getSkeletonCapability();
			poseDetectionCap = userGen.getPoseDetectionCapability();
			imageGen = ImageGenerator.create(context);
			
			MapOutputMode mapMode = new MapOutputMode(640, 480, 30);   // xRes, yRes, FPS
			imageGen.setMapOutputMode(mapMode); 
			imageGen.setPixelFormat(PixelFormat.RGB24);
  
            
			depthGen = DepthGenerator.create(context);
            userGen.getNewUserEvent().addObserver(new NewUserObserver());
            userGen.getLostUserEvent().addObserver(new LostUserObserver());
            skeletonCap.getCalibrationCompleteEvent().addObserver(new CalibrationCompleteObserver());
            poseDetectionCap.getPoseDetectedEvent().addObserver(new PoseDetectedObserver());
            
            joints = new HashMap<Integer, HashMap<SkeletonJoint,SkeletonJointPosition>>();
            skeletonCap.setJointActive(SkeletonJoint.LEFT_SHOULDER, true);
            skeletonCap.setJointActive(SkeletonJoint.LEFT_ELBOW, true);
            skeletonCap.setJointActive(SkeletonJoint.LEFT_HAND, true);
            skeletonCap.setJointActive(SkeletonJoint.RIGHT_SHOULDER, true);
            skeletonCap.setJointActive(SkeletonJoint.RIGHT_ELBOW, true);
            skeletonCap.setJointActive(SkeletonJoint.RIGHT_HAND, true);
            
			// set Mirror mode for all 
			context.setGlobalMirror(true);

			context.startGeneratingAll(); 
			System.out.println("Started context generating..."); 

			ImageMetaData imageMD = imageGen.getMetaData();
			width = imageMD.getFullXRes();
			height = imageMD.getFullYRes();
		} 
		catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	}  // end of configOpenNI()

	public void getJoint(int user, SkeletonJoint joint) throws StatusException
    {
        SkeletonJointPosition pos = skeletonCap.getSkeletonJointPosition(user, joint);
		if (pos.getPosition().getZ() != 0)
		{
			joints.get(user).put(joint, new SkeletonJointPosition(depthGen.convertRealWorldToProjective(pos.getPosition()), pos.getConfidence()));
		}
		else
		{
			joints.get(user).put(joint, new SkeletonJointPosition(new Point3D(), 0));
		}
    }
    
    public void getJoints(int user) throws StatusException
    {    	
    	getJoint(user, SkeletonJoint.LEFT_SHOULDER);
    	getJoint(user, SkeletonJoint.LEFT_ELBOW);
    	getJoint(user, SkeletonJoint.LEFT_HAND);

    	getJoint(user, SkeletonJoint.RIGHT_SHOULDER);
    	getJoint(user, SkeletonJoint.RIGHT_ELBOW);
    	getJoint(user, SkeletonJoint.RIGHT_HAND);
    }
	
    public static ArrayList<Double> getRealTimeAngles(){    	
    	angleList = new ArrayList<Double>();
    	angleList.add(lshoulderangle);
    	angleList.add(lelbowangle);
    	angleList.add(lwristangle);
    	angleList.add(rshoulderangle);
    	angleList.add(relbowangle);
    	angleList.add(rwristangle);
    	return angleList;
    }

	public Dimension getPreferredSize()
	{ 
		return new Dimension(width, height);
	}

	@Override
	public void run()
	/* update and display the webcam image whenever the context
     is updated.
	 */
	{	
		isRunning = true;
		while (isRunning) {
			try {
				context.waitAnyUpdateAll();
				Thread.sleep(Constants.UPDATE_RATE);
				int[] users = userGen.getUsers();
				for (int i = 0; i < users.length; ++i)
				{			    	
					if (skeletonCap.isSkeletonTracking(users[i]))
					{
						updateAngles(users[i]);
					}
				}
			}
			catch(StatusException e){  
				System.out.println(e.getMessage()); 
				System.exit(1);
			}
			catch (InterruptedException e) {
				System.out.println(e); 
			}
			long startTime = System.currentTimeMillis();
			imageCount++;
			totalTime += (System.currentTimeMillis() - startTime);
			if(overlayModeFlag){
				updateImage();
				repaint();
			}
		}
	} // end of run()


	public void setStop()
	{	isRunning = false; }
	  
	public boolean isRunning()
	{	return this.isRunning;  } 

	private void updateImage()
	// get image data as bytes; convert to an image
	{
		try {
			long ptr = imageGen.getImageMap().getNativePtr();
			NativeAccess.copyToBuffer(imageBB, ptr, width * height * 3);
			//ByteBuffer imageBB = imageGen.getImageMap().createByteBuffer();
			
			int rowStart = 0;
	        // rowStart will index the first byte (red) in each row;
	        // starts with first row, and moves down

			int bbIdx;               // index into ByteBuffer
			int i = 0;               // index into pixels int[]
			int rowLen = width * 3;    // number of bytes in each row
			for (int row = 0; row < height; row++) {
				bbIdx = rowStart;
				// System.out.println("bbIdx: " + bbIdx);
				for (int col = 0; col < width; col++) {
					int pixR = imageBB.get( bbIdx++ );
					int pixG = imageBB.get( bbIdx++ );
					int pixB = imageBB.get( bbIdx++ );
					pixelInts[i++] = 
							0xFF000000 | ((pixR & 0xFF) << 16) | 
							((pixG & 0xFF) << 8) | (pixB & 0xFF);
				}
				rowStart += rowLen;   // move to next row
			}

			// create a BufferedImage from the pixel data			
			image.setRGB( 0, 0, width, height, pixelInts, 0, width );
		}
		catch (GeneralException e) {
			System.out.println(e);
		}
		
		updateSampleAngles();
		
		lelbow.setLocation(
				lshoulder.getX() - Math.cos(shoulderangle) * uarmlen,
				lshoulder.getY() - Math.sin(shoulderangle) * uarmlen);
		relbow.setLocation(
				rshoulder.getX() + Math.cos(shoulderangle) * uarmlen,
				rshoulder.getY() - Math.sin(shoulderangle) * uarmlen);
		lwrist.setLocation(lelbow.getX() - Math.cos(elbowangle) * larmlen,
				lelbow.getY() - Math.sin(elbowangle) * larmlen);
		rwrist.setLocation(relbow.getX() + Math.cos(elbowangle) * larmlen,
				relbow.getY() - Math.sin(elbowangle) * larmlen);
		lfinger.setLocation(lwrist.getX() - Math.cos(wristangle) * handlen,
				lwrist.getY() - Math.sin(wristangle) * handlen);
		rfinger.setLocation(rwrist.getX() + Math.cos(wristangle) * handlen,
				rwrist.getY() - Math.sin(wristangle) * handlen);
	}  // end of updateImage()

	public void updateAngles(int user) throws StatusException{
		getJoints(user);
    	HashMap<SkeletonJoint, SkeletonJointPosition> dict = joints.get(new Integer(user));
    	Point3D LEFT_SHOULDER = dict.get(SkeletonJoint.LEFT_SHOULDER).getPosition();
		Point3D LEFT_ELBOW = dict.get(SkeletonJoint.LEFT_ELBOW).getPosition();
		Point3D LEFT_HAND = dict.get(SkeletonJoint.LEFT_HAND).getPosition();
		
		Point3D RIGHT_SHOULDER = dict.get(SkeletonJoint.RIGHT_SHOULDER).getPosition();
		Point3D RIGHT_ELBOW = dict.get(SkeletonJoint.RIGHT_ELBOW).getPosition();
		Point3D RIGHT_HAND = dict.get(SkeletonJoint.RIGHT_HAND).getPosition();
		
		rshoulderangle = Math.atan2(-RIGHT_ELBOW.getY()+RIGHT_SHOULDER.getY(), RIGHT_ELBOW.getX()-RIGHT_SHOULDER.getX());;//right shoulder XY angle
		relbowangle = Math.atan2(-RIGHT_HAND.getY()+RIGHT_ELBOW.getY(), RIGHT_HAND.getX()-RIGHT_ELBOW.getX());;//right elbow XY angle 
		lshoulderangle = Math.atan2(LEFT_ELBOW.getY()-LEFT_SHOULDER.getY(), LEFT_ELBOW.getX()-LEFT_SHOULDER.getX())  + Math.PI; //left shoulder XY angle
		lelbowangle = Math.atan2(LEFT_HAND.getY()-LEFT_ELBOW.getY(), LEFT_HAND.getX()-LEFT_ELBOW.getX()) + Math.PI; //left elbowr XY angle
 		
		//System.out.println(" " +  Math.toDegrees(rshoulderangle) + " " +  Math.toDegrees(relbowangle) + " " +  Math.toDegrees(lshoulderangle) + " " +  Math.toDegrees(lelbowangle));
	}
	
	public void updateSampleAngles(){
		if(this.exercise.equals("Left Forearm Stretch Test")){		
			if (eflag){
				elbowangle += Math.toRadians(5.0);
				if (elbowangle > elbowanglemax){
					elbowangle = elbowanglemax;
					eflag = false;
				}
			}
			else{
				elbowangle -= Math.toRadians(5.0);
				if (elbowangle < 0){
					elbowangle = 0;
					eflag = true;
				}		
			}
			
		}else{
			if (sflag){
				shoulderangle += Math.toRadians(10.0);
				if (shoulderangle > shoulderanglemax){
					shoulderangle = shoulderanglemax;
					sflag = false;
				}
			}
			else{
				shoulderangle -= Math.toRadians(10.0);
				if (shoulderangle < 0){
					shoulderangle = 0;
					sflag = true;
				}		
			}
		}
		
		if (wflag){
			//++
			if (wristangle > wristanglemax){
				wristangle = wristanglemax;
				wflag = false;
			}
		}
		else{
			//--
			if (wristangle < 0){
				wristangle = 0;
				wflag = true;
			}		
		}
	}
	
	public ArrayList<Double> getSampleAngels(){
		ArrayList<Double> list = new ArrayList<Double>();
		list.add(shoulderangle);
		list.add(elbowangle); 
		return list;
	}
	
	public void paintComponent(Graphics g)
	// Draw the depth image and statistics info
	{ 
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		if (image != null)
			g2.drawImage(image, 0, 0, this);
		
		paintSample(g);
		writeStats(g2);
	} // end of paintComponent()
	
	
	public void paintSample(Graphics g){
		int lshoulderX = (int) lshoulder.getX();
		int lshoulderY = (int) lshoulder.getY();
		int rshoulderX = (int) rshoulder.getX();
		int rshoulderY = (int) rshoulder.getY();
		int lelbowX = (int) lelbow.getX();
		int lelbowY = (int) lelbow.getY();
		int relbowX = (int) relbow.getX();
		int relbowY = (int) relbow.getY();
		int lwristX = (int) lwrist.getX();
		int lwristY = (int) lwrist.getY();
		int rwristX = (int) rwrist.getX();
		int rwristY = (int) rwrist.getY();
		int lfingerX = (int) lfinger.getX();
		int lfingerY = (int) lfinger.getY();
		int rfingerX = (int) rfinger.getX();
		int rfingerY = (int) rfinger.getY();

		g.setColor(Color.BLUE);
		g.drawOval(300, 30, 40, 40);
		g.setColor(Color.RED);
		g.drawLine(320, 70, 320, 200);
		g.drawLine(lshoulderX, lshoulderY, rshoulderX, rshoulderY);
		g.setColor(Color.BLUE);
		g.drawOval(lshoulderX - 5, lshoulderY - 5, 10, 10);
		g.drawOval(rshoulderX - 5, rshoulderY - 5, 10, 10);
		g.setColor(Color.RED);
		g.drawLine(lshoulderX, lshoulderY, lelbowX, lelbowY);
		g.drawLine(rshoulderX, rshoulderY, relbowX, relbowY);
		g.setColor(Color.BLUE);
		g.drawOval(lelbowX - 5, lelbowY - 5, 10, 10);
		g.drawOval(relbowX - 5, relbowY - 5, 10, 10);
		g.setColor(Color.RED);
		g.drawLine(lelbowX, lelbowY, lwristX, lwristY);
		g.drawLine(relbowX, relbowY, rwristX, rwristY);
		g.setColor(Color.BLUE);
		g.drawOval(lwristX - 5, lwristY - 5, 10, 10);
		g.drawOval(rwristX - 5, rwristY - 5, 10, 10);
		g.setColor(Color.RED);
		g.drawLine(lwristX, lwristY, lfingerX, lfingerY);
		g.drawLine(rwristX, rwristY, rfingerX, rfingerY);
	}


	private void writeStats(Graphics2D g2)
	/* write statistics in bottom-left corner, or
     	"Loading" at start time */
	{
		g2.setColor(Color.BLUE);
		g2.setFont(msgFont);
		int panelHeight = getHeight();
		if (imageCount > 0) {
			double avgGrabTime = (double) totalTime / imageCount;
			g2.drawString("Pic " + imageCount + "  " +
					df.format(avgGrabTime) + " ms", 
					5, panelHeight-10);  // bottom left
		}
		else  // no image yet
			g2.drawString("Loading...", 5, panelHeight-10);
	}  // end of writeStats()

}

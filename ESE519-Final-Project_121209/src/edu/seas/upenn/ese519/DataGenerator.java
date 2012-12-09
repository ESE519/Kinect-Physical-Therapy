package edu.seas.upenn.ese519;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.j3d.BranchGroup;

import org.OpenNI.CalibrationProgressEventArgs;
import org.OpenNI.CalibrationProgressStatus;
import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.GeneralException;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.ImageGenerator;
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
import org.OpenNI.SkeletonProfile;
import org.OpenNI.StatusException;
import org.OpenNI.UserEventArgs;
import org.OpenNI.UserGenerator;

import edu.seas.upenn.rendering.Skeleton3D;

public class DataGenerator implements Runnable {

	String calibPose = "Psi";

	class NewUserObserver implements IObserver<UserEventArgs> {
		@Override
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args) {
			System.out.println("New user " + args.getId());
			try {
				poseDetectionCap.startPoseDetection(calibPose, args.getId());
			} catch (StatusException e) {
				e.printStackTrace();
			}
		}
	}

	class LostUserObserver implements IObserver<UserEventArgs> {
		@Override
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args) {
			int userID = args.getId();
			System.out.println("Lost user " + userID);
			joints.remove(args.getId());

			// delete skeleton from userSkels3D and the scene graph
			Skeleton3D skel = userSkels3D.remove(userID);
			if (skel == null)
				return;
			skel.delete();
		}
	}

	class CalibrationCompleteObserver implements
			IObserver<CalibrationProgressEventArgs> {
		@Override
		public void update(
				IObservable<CalibrationProgressEventArgs> observable,
				CalibrationProgressEventArgs args) {
			System.out.println("Calibraion complete: " + args.getStatus());
			try {
				if (args.getStatus() == CalibrationProgressStatus.OK) {
					int userID = args.getUser();
					System.out.println("starting tracking " + userID);
					skeletonCap.startTracking(userID);
					joints.put(new Integer(args.getUser()),
							new HashMap<SkeletonJoint, SkeletonJointPosition>());

					Skeleton3D skel = new Skeleton3D(userID, skeletonCap);
					userSkels3D.put(userID, skel);
					sceneBG.addChild(skel.getBG());
				} else if (args.getStatus() != CalibrationProgressStatus.MANUAL_ABORT) {
					poseDetectionCap.startPoseDetection(calibPose,
							args.getUser());
				}
			} catch (StatusException e) {
				e.printStackTrace();
			}
		}
	}

	class PoseDetectedObserver implements IObserver<PoseDetectionEventArgs> {
		@Override
		public void update(IObservable<PoseDetectionEventArgs> observable,
				PoseDetectionEventArgs args) {
			System.out.println("Pose " + args.getPose() + " detected for "
					+ args.getUser());
			try {
				poseDetectionCap.stopPoseDetection(args.getUser());
				skeletonCap.requestSkeletonCalibration(args.getUser(), true);
			} catch (StatusException e) {
				e.printStackTrace();
			}
		}
	}

	private Context context;
	private UserGenerator userGen;
	private ImageGenerator imageGen;
	private DepthGenerator depthGen;
	private SkeletonCapability skeletonCap;
	private PoseDetectionCapability poseDetectionCap;
	private SerialComm serialComm;

	private int[] pixelInts;
	private ByteBuffer imageBB;
	private int width = Constants.MotionViewer_LENGTH,
			height = Constants.MotionViewer_HEIGHT;
	private BufferedImage image = null;

	private HashMap<Integer, HashMap<SkeletonJoint, SkeletonJointPosition>> joints;
	private HashMap<Integer, Skeleton3D> userSkels3D;
	private BranchGroup sceneBG; // the scene graph
	// maps user IDs --> a 3D skeleton

	private volatile boolean isRunning;
	private int updateRate;
	public boolean overlayModeFlag = false;

	private double rshoulderangle;
	private double relbowangle;
	private double lshoulderangle;
	private double lelbowangle;
	private int lrotateangle;
	private ArrayList<Double> angleList;

	private Point lshoulder = new Point(295, 100);
	private Point rshoulder = new Point(345, 100);

	public DataGenerator() {

		configOpenNI();
		// updateRate = Constants.UPDATE_RATE;
		pixelInts = new int[width * height];
		imageBB = ByteBuffer.allocateDirect(width * height * 3);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		System.out.println("Finished OpenNI configuration");
	}

	public DataGenerator(int updateRate) {
		configOpenNI();
		// this.updateRate = updateRate;
		pixelInts = new int[width * height];
		imageBB = ByteBuffer.allocateDirect(width * height * 3);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		System.out.println("Finished OpenNI configuration");
	}

	public DataGenerator(SerialComm serialComm) {
		this.serialComm = serialComm;
		configOpenNI();
		// updateRate = Constants.UPDATE_RATE;
		pixelInts = new int[width * height];
		imageBB = ByteBuffer.allocateDirect(width * height * 3);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		System.out.println("Finished OpenNI configuration");
	}

	private void configOpenNI()
	// create context and image generator
	{
		try {
			context = new Context();

			// add the NITE Licence
			License licence = new License("PrimeSense",
					"0KOIk2JeIBYClPWVnMoRKn5cdY4="); // vendor, key
			context.addLicense(licence);

			userGen = UserGenerator.create(context);
			skeletonCap = userGen.getSkeletonCapability();
			poseDetectionCap = userGen.getPoseDetectionCapability();
			imageGen = ImageGenerator.create(context);

			MapOutputMode mapMode = new MapOutputMode(width, height, 30); // xRes,
																			// yRes,
																			// FPS
			imageGen.setMapOutputMode(mapMode);
			imageGen.setPixelFormat(PixelFormat.RGB24);

			depthGen = DepthGenerator.create(context);
			userGen.getNewUserEvent().addObserver(new NewUserObserver());
			userGen.getLostUserEvent().addObserver(new LostUserObserver());
			skeletonCap.getCalibrationCompleteEvent().addObserver(
					new CalibrationCompleteObserver());
			poseDetectionCap.getPoseDetectedEvent().addObserver(
					new PoseDetectedObserver());

			joints = new HashMap<Integer, HashMap<SkeletonJoint, SkeletonJointPosition>>();
			userSkels3D = new HashMap<Integer, Skeleton3D>();
			skeletonCap.setSkeletonProfile(SkeletonProfile.ALL);
			// skeletonCap.setJointActive(SkeletonJoint.LEFT_SHOULDER, true);
			// skeletonCap.setJointActive(SkeletonJoint.LEFT_ELBOW, true);
			// skeletonCap.setJointActive(SkeletonJoint.LEFT_HAND, true);
			// skeletonCap.setJointActive(SkeletonJoint.RIGHT_SHOULDER, true);
			// skeletonCap.setJointActive(SkeletonJoint.RIGHT_ELBOW, true);
			// skeletonCap.setJointActive(SkeletonJoint.RIGHT_HAND, true);

			// set Mirror mode for all
			context.setGlobalMirror(true);

			context.startGeneratingAll();
			System.out.println("Started context generating...");

		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	} // end of configOpenNI()

	public void set3DScene(BranchGroup sceneBG) {
		this.sceneBG = sceneBG;
	}

	public void update3DSkeleton()
	// update skeleton of each user being tracked
	{
		try {
			int[] userIDs = userGen.getUsers(); // there may be many users in
												// the scene
			for (int i = 0; i < userIDs.length; i++) {
				int userID = userIDs[i];
				if (skeletonCap.isSkeletonCalibrating(userID))
					continue; // test to avoid occassional crashes with
								// isSkeletonTracking()
				if (skeletonCap.isSkeletonTracking(userID))
					userSkels3D.get(userID).update();
			}
		} catch (StatusException e) {
			System.out.println(e);
		}
	} // end of update()

	private void updateImage()
	// get image data as bytes; convert to an image
	{
		try {
			long ptr = imageGen.getImageMap().getNativePtr();
			NativeAccess.copyToBuffer(imageBB, ptr, width * height * 3);
			// ByteBuffer imageBB = imageGen.getImageMap().createByteBuffer();

			int rowStart = 0;
			// rowStart will index the first byte (red) in each row;
			// starts with first row, and moves down

			int bbIdx; // index into ByteBuffer
			int i = 0; // index into pixels int[]
			int rowLen = width * 3; // number of bytes in each row
			for (int row = 0; row < height; row++) {
				bbIdx = rowStart;
				// System.out.println("bbIdx: " + bbIdx);
				for (int col = 0; col < width; col++) {
					int pixR = imageBB.get(bbIdx++);
					int pixG = imageBB.get(bbIdx++);
					int pixB = imageBB.get(bbIdx++);
					pixelInts[i++] = 0xFF000000 | ((pixR & 0xFF) << 16)
							| ((pixG & 0xFF) << 8) | (pixB & 0xFF);
				}
				rowStart += rowLen; // move to next row
			}

			// create a BufferedImage from the pixel data
			image.setRGB(0, 0, width, height, pixelInts, 0, width);
		} catch (GeneralException e) {
			System.out.println(e);
		}
	} // end of updateImage()

	public BufferedImage getImage() {
		return this.image;
	}

	public boolean isImageReady() {
		return this.image != null;
	}

	public void updateAngles(int user) throws StatusException {
		getJoints(user);
		HashMap<SkeletonJoint, SkeletonJointPosition> dict = joints
				.get(new Integer(user));
		Point3D LEFT_SHOULDER = dict.get(SkeletonJoint.LEFT_SHOULDER)
				.getPosition();
		lshoulder.setLocation(LEFT_SHOULDER.getX(), LEFT_SHOULDER.getY());
		Point3D LEFT_ELBOW = dict.get(SkeletonJoint.LEFT_ELBOW).getPosition();
		Point3D LEFT_HAND = dict.get(SkeletonJoint.LEFT_HAND).getPosition();

		Point3D RIGHT_SHOULDER = dict.get(SkeletonJoint.RIGHT_SHOULDER)
				.getPosition();
		rshoulder.setLocation(RIGHT_SHOULDER.getX(), RIGHT_SHOULDER.getY());
		Point3D RIGHT_ELBOW = dict.get(SkeletonJoint.RIGHT_ELBOW).getPosition();
		Point3D RIGHT_HAND = dict.get(SkeletonJoint.RIGHT_HAND).getPosition();

		lshoulderangle = Math.atan2(LEFT_SHOULDER.getY() - LEFT_ELBOW.getY(),
				LEFT_SHOULDER.getX() - LEFT_ELBOW.getX()); // left shoulder XY
															// angle
		lelbowangle = Math.atan2(LEFT_ELBOW.getY() - LEFT_HAND.getY(),
				LEFT_ELBOW.getX() - LEFT_HAND.getX()); // left elbowr XY angle
		rshoulderangle = Math.atan2(RIGHT_SHOULDER.getY() - RIGHT_ELBOW.getY(),
				RIGHT_ELBOW.getX() - RIGHT_SHOULDER.getX());// right shoulder XY
															// angle
		relbowangle = Math.atan2(RIGHT_ELBOW.getY() - RIGHT_HAND.getY(),
				RIGHT_HAND.getX() - RIGHT_ELBOW.getX());// right elbow XY angle

		System.out.println(" " + Math.toDegrees(lshoulderangle) + " "
				+ Math.toDegrees(lelbowangle) + " "
				+ Math.toDegrees(rshoulderangle) + " "
				+ Math.toDegrees(relbowangle));
	}

	public Point[] getCurrentShoulderPosition() {
		Point[] shoulderPostions = new Point[2];
		shoulderPostions[0] = lshoulder;
		shoulderPostions[1] = rshoulder;
		return shoulderPostions;
	}

	public void getJoint(int user, SkeletonJoint joint) throws StatusException {
		SkeletonJointPosition pos = skeletonCap.getSkeletonJointPosition(user,
				joint);
		if (pos.getPosition().getZ() != 0) {
			joints.get(user).put(
					joint,
					new SkeletonJointPosition(depthGen
							.convertRealWorldToProjective(pos.getPosition()),
							pos.getConfidence()));
		} else {
			joints.get(user).put(joint,
					new SkeletonJointPosition(new Point3D(), 0));
		}
	}

	public void getJoints(int user) throws StatusException {
		getJoint(user, SkeletonJoint.LEFT_SHOULDER);
		getJoint(user, SkeletonJoint.LEFT_ELBOW);
		getJoint(user, SkeletonJoint.LEFT_HAND);

		getJoint(user, SkeletonJoint.RIGHT_SHOULDER);
		getJoint(user, SkeletonJoint.RIGHT_ELBOW);
		getJoint(user, SkeletonJoint.RIGHT_HAND);
	}

	public ArrayList<Double> getRealTimeAngles() {
		angleList = new ArrayList<Double>();
		angleList.add(lelbowangle);
		angleList.add(lshoulderangle);
		angleList.add(rshoulderangle);
		angleList.add(relbowangle);
		angleList.add(Math.toRadians(lrotateangle));
		return angleList;
	}

	@Override
	public void run() {
		isRunning = true;
		serialComm.startReading();

		while (isRunning) {
			try {
				context.waitAnyUpdateAll();
				// Thread.sleep(updateRate);
				int[] users = userGen.getUsers();
				for (int i = 0; i < users.length; ++i) {
					int userID = users[i];
					if (skeletonCap.isSkeletonCalibrating(userID))
						continue; // test to avoid occassional crashes with
									// isSkeletonTracking()
					if (skeletonCap.isSkeletonTracking(userID))
						updateAngles(users[i]);
				}
			} catch (StatusException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
			// catch (InterruptedException e) {
			// System.out.println(e);
			// }

			lrotateangle = serialComm.getInputAngle();
			if (overlayModeFlag)
				updateImage();
		}
	} // end of run()

	public void setStop() {
		serialComm.stopReading();
		isRunning = false;
	}

	public boolean isRunning() {
		return this.isRunning;
	}

	public void releaseContext() {
		try {
			context.stopGeneratingAll();
		} catch (StatusException ex) {
		}
		context.release();
	}

}

package edu.seas.upenn.ese519;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

//import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class SerialComm {
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;

	/** Default bits per second for COM port /mbed */
	private static final int DATA_RATE = 9600;

	private CommPortIdentifier portIdentifier;
	private SerialPort serialPort;

	private SerialReader reader;
	private SerialWriter writer;

	private JudgementPanel judge;

	public SerialComm() {
		super();
		try {
			connect();
		} catch (Exception ex) {
			System.out.println("Serial Communication is not available");
		}
	}

	public void setToJudegementPanel(JudgementPanel judge) {
		this.judge = judge;
	}

	public void connect() throws Exception {
		portIdentifier = CommPortIdentifier.getPortIdentifier("/dev/ttyUSB0");
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(),
					TIME_OUT);

			if (commPort instanceof SerialPort) {
				serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(DATA_RATE,
						SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				reader = new SerialReader();
				/**
				 * Set the Update Rate of Decision Making five times slower
				 */
				writer = new SerialWriter(5 * Constants.UPDATE_RATE);
			} else {
				System.out
						.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

	public void startReading() {
		try {
			InputStream in = serialPort.getInputStream();
			reader.setInputStream(in);
			new Thread(reader).start();
		} catch (IOException ex) {
			System.out.println("Input Stream not available");
		}
	}

	public void stopReading() {
		if (reader.isRunning())
			reader.setStop();
	}

	public boolean isReading() {
		return reader.isRunning();
	}

	public void startWriting() {
		try {
			OutputStream out = serialPort.getOutputStream();
			writer.setOutputStream(out);
			new Thread(writer).start();
		} catch (IOException ex) {
			System.out.println("Output Stream not available");
		}
	}

	public void stopWriting() {
		if (writer.isRunning())
			writer.setStop();
	}

	public boolean isWriting() {
		return writer.isRunning();
	}

	public int getInputAngle() {
		return reader.getCurrentAngle();
	}

	/** */
	public static class SerialReader implements Runnable {
		InputStream in;
		private int curAngle;
		private boolean startFlag = false;
		StringBuffer sb = null;

		public void setInputStream(InputStream in) {
			this.in = in;
		}

		public void run() {
			byte[] buffer = new byte[1024];
			int len = -1;
			startFlag = true;
			try {
				while ((len = this.in.read(buffer)) > -1 && startFlag) {
					if (sb == null) {
						sb = new StringBuffer();
					}

					String temp = new String(buffer, 0, len);

					Scanner fi = new Scanner(temp);
					// anything other than alphanumberic characters,
					// comma, dot or negative sign is skipped
					// fi.useDelimiter("[^\\p{Alnum},\\.-]");
					if (fi.hasNext("-")) {
						sb.append(fi.next());
					} else if (fi.hasNextInt()) {
						sb.append(fi.nextInt());
					} else if (fi.hasNext(".")) {
						try {
							int tempint = Integer.parseInt(sb.toString());
							curAngle = tempint;
							//System.out.println("Int: " + curAngle);
							sb = null;
						} catch (NumberFormatException ex) {
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException ex) {
				System.out.println(ex.getMessage());
			}
		}

		public void setStop() {
			this.startFlag = false;
		}

		public boolean isRunning() {
			return this.startFlag;
		}

		public int getCurrentAngle() {
			return this.curAngle;
		}
	}

	public OutputStream getOutputStream() throws IOException {
		return serialPort.getOutputStream();
	}

	/** */
	public class SerialWriter implements Runnable {
		OutputStream out;
		private boolean startFlag = false;
		private int updateRate;

		public SerialWriter(int updateRate) {
			super();
			this.updateRate = updateRate;
		}

		public void setOutputStream(OutputStream out) {
			this.out = out;
		}

		public void run() {
			startFlag = true;
			try {
				while (startFlag) {
					int decision;
					if (judge.judgeFlag==0){
						decision = 0;
					}
					else {
						decision = 1;
					}
					//int decision = (judge.isBufMostlyMisses()) ? 0 : 1;
					this.out.write(decision);
					Thread.sleep(updateRate);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void setStop() {
			this.startFlag = false;
		}

		public boolean isRunning() {
			return this.startFlag;
		}
	}

}

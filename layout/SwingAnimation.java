package layout;

import java.awt.*;
import javax.swing.*;

public class SwingAnimation {
	Thread th;
	ImageIcon images;
	JFrame frame;
	JLabel lbl;
	SimDraw sd;
	int i = 0;
	int j;

	public static void main(String[] args) {
		SwingAnimation sa = new SwingAnimation();
	}

	public SwingAnimation() {
		frame = new JFrame("Animation Frame");
		th = new Thread();
		sd = new SimDraw();
		sd.initModel();
		frame.add(sd, BorderLayout.CENTER);
		frame.setSize(640, 480);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		while (true) {
			SwingAnimator();
		}
	}

	public void SwingAnimator() {
		try {
			sd.update();
			th.sleep(10);
		} catch (InterruptedException e) {
		}
	}
}
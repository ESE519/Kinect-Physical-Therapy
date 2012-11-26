package edu.seas.upenn.ese519;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

public class JudgementPanel extends JPanel{
	public void paintComponent(Graphics g){
		Rectangle bounds = this.getBounds();
		
		Graphics2D g2d = (Graphics2D) g;
		int red = (int) (Math.random()*255);
		int green = (int) (Math.random()*255);
		int blue = (int) (Math.random()*255);
		Color startColor = new Color(red,green,blue);
		
		red = (int) (Math.random()*255);
		green = (int) (Math.random()*255);
		blue = (int)(Math.random()*255);
		Color endColor = new Color(red,green,blue);
		
		g2d.setColor(Color.RED);
		g2d.fillOval(bounds.x+20, bounds.y+20, 20, 20);
		
		//System.out.println(" " +  bounds.x + " " +  bounds.y + " " +  (bounds.x + bounds.width) + " " + (bounds.y + bounds.height));	
	}

}


package layout;

import java.awt.Graphics;
import javax.swing.JPanel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Travis Shao
 */
public class SimDraw extends JPanel {

    public SimDraw() {
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawLine(100, 100, 150, 150);
    }
}

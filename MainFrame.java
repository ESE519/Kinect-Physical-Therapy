 import java.awt.BorderLayout;  
import java.awt.Dimension;  
  
import javax.swing.BorderFactory;  
import javax.swing.JButton;  
import javax.swing.JFrame;  
import javax.swing.JLabel;  
import javax.swing.JPanel;  
import javax.swing.JToolBar;  
  
  
public class MainFrame extends JFrame{  
  
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
        // TODO Auto-generated method stub  
                 MainFrame mai=new MainFrame();  
                 mai.setLayout(new BorderLayout());  
                 JToolBar jtoolbar=new JToolBar();  
                 JLabel jl=new JLabel("state");  
                 jtoolbar.add(jl);  
                 JPanel jpanel1=new JPanel();  
                 JButton jb1=new JButton("North");  
                   
                 jpanel1.setPreferredSize(new Dimension(640, 480));//关键代码,设置JPanel的大小  
                 jpanel1.add(jb1);  
                  jpanel1.setBorder(BorderFactory.createEtchedBorder());  
                 JButton jb2=new JButton("Center");  
                 JPanel jpanel3=new JPanel();  
                 jpanel3.setPreferredSize(new Dimension(200, 100));
                 JPanel jpanel4=new JPanel();  
                 jpanel4.add(jb2);
                 jpanel3.setBorder(BorderFactory.createEtchedBorder());
                 jpanel4.setBorder(BorderFactory.createEtchedBorder());  
                   
                 mai.add(jpanel1,BorderLayout.WEST);  
                 mai.add(jpanel3,BorderLayout.NORTH); 
                 mai.add(jpanel4,BorderLayout.CENTER);  
                 mai.add(jtoolbar,BorderLayout.SOUTH);  
                 mai.setSize(1300, 640);  
                 mai.setVisible(true);  
                 mai.setDefaultCloseOperation(EXIT_ON_CLOSE);  
                   
    }  
  
} 
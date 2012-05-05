/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package givertaker;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author MultiTool
 * To git:
 * git clone https://MultiTool@github.com/MultiTool/GiverTaker.git
 */
public class GiverTaker extends JFrame {

  private JPanel MainPanel = new JPanel(); // North quadrant
  public static final Things.GridWorld world = new Things.GridWorld();

  public GiverTaker() {
    this.setSize(500, 500);
    setTitle("");
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    world.Init_Topology(20, 20);
    world.Init_Seed();
    if (false) {
      GiverTaker window = new GiverTaker();
      window.setVisible(true);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } else {
      JFrame frame = new JFrame("GiverTaker");
      frame.addKeyListener(null);

      frame.addKeyListener(new KeyListener() {

        @Override
        public void keyPressed(KeyEvent e) {
          if (e.getKeyChar() == e.VK_ESCAPE) {
            System.exit(0);
          }
        }
        // unused abstract methods 

        @Override
        public void keyTyped(KeyEvent e) {
        }

        public void keyReleased(KeyEvent e) {
        }
      });

      JPanel MainPanel = new JPanel() {

        @Override
        public void paintComponent(Graphics g) {
          super.paintComponent(g);
          Graphics2D g2 = (Graphics2D) g;
          world.Draw_Me(g2, 10, 10);
          try {
            Thread.sleep(10);
          } catch (Exception ex) {
          }
          world.Run_Cycle();
          this.repaint();
        }
      };
      //MainPanel.setSize(500, 500);
      MainPanel.setBackground(Color.white);
      frame.getContentPane().add(MainPanel);
      frame.setSize(500, 500);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //frame.pack();
      frame.setVisible(true);
    }
  }
}

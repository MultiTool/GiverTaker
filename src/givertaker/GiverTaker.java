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
 */
public class GiverTaker extends JFrame {
  private JPanel MainPanel = new JPanel(); // North quadrant
  public GiverTaker() {
    this.setSize(500, 500);
    setTitle("");
  }
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    if (false) {
      GiverTaker window = new GiverTaker();
      window.setVisible(true);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } else {
      JFrame frame = new JFrame("GiverTaker");
      JPanel MainPanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
          super.paintComponent(g);
          Graphics2D g2 = (Graphics2D) g;
          boolean nop = true;
        }
      };
      //MainPanel.setSize(500, 500);
      MainPanel.setBackground(Color.red);
      frame.getContentPane().add(MainPanel);
      frame.setSize(500, 500);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //frame.pack();
      frame.setVisible(true);
    }
  }
}

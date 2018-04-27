package net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ContaminantesTableProgressBar extends JProgressBar {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ContaminantesTableProgressBar() {
    JFrame myFrame = new JFrame();
    myFrame.setSize(300, 100);
    myFrame.setTitle("Building list contaminates");
    JPanel myPanel = new JPanel();
    myPanel.add(this);
    myFrame.add(myPanel);
    myFrame.setVisible(true);
  }

}

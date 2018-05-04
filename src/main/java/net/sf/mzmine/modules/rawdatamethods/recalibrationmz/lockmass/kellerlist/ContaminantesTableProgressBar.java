package net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass.kellerlist;

import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ContaminantesTableProgressBar extends JFrame {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private JFrame frame;
  private JPanel panel;
  private JLabel plotXIC;
  private String contaminante;

  public ContaminantesTableProgressBar(JLabel plotXIC) {
    this.plotXIC = plotXIC;
    this.frame = new JFrame();
    this.panel = new JPanel();
    this.frame.setAlwaysOnTop(true);
    frame.setSize(305, 110);
    frame.setTitle("Building XIC for contaminate: " + contaminante);
    frame.setLayout(new FlowLayout());
    panel.setLayout(new FlowLayout());
    panel.add(plotXIC);
    frame.add(panel);
    frame.setVisible(true);
  }

  public JLabel getPlotXIC() {
    return plotXIC;
  }

  public void setPlotXIC(JLabel plotXIC) {
    this.plotXIC = plotXIC;
    panel.add(plotXIC);
  }

  public String getContaminante() {
    return contaminante;
  }

  public void setContaminante(String contaminante) {
    this.contaminante = contaminante;
    frame.setTitle("Building XIC for contaminate: " + contaminante);
  }
}

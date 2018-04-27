package net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass;

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class LockMassCellRenderer extends DefaultTableCellRenderer {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  ArrayList<JLabel> lbl = new ArrayList<JLabel>();

  // ImageIcon icon = new ImageIcon(getClass().getResource("sample.png"));

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {

    // lbl.setIcon(icon);
    return lbl.get(row);
  }
}

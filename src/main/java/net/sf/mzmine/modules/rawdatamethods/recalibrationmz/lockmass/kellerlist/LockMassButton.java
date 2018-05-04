package net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass.kellerlist;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass.LockMass;

public class LockMassButton extends JButton implements TableCellRenderer {


  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public LockMassButton() {
    setOpaque(true);
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {

    setText("Use for calibration");
    setBackground(Color.black);

    return this;
  }
}


class ButtonEditor extends DefaultCellEditor {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  protected JButton button;
  private String label;
  private boolean isPushed;
  private LockMass lockMass;

  public ButtonEditor(JCheckBox checkBox, JTable table, int row, int column) {
    super(checkBox);
    button = new JButton();
    button.setOpaque(true);
    button.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        fireEditingStopped();
      }
    });
  }

  @Override
  public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
      int row, int column) {
    if (isSelected) {
      button.setForeground(table.getSelectionForeground());
      button.setBackground(table.getSelectionBackground());
    }
    button.setBackground(Color.BLACK);
    System.out.println(table.getValueAt(row, column - 2));
    button.setText("Use?");
    lockMass = new LockMass(Double.parseDouble(table.getValueAt(row, column - 2).toString()));
    isPushed = true;
    return button;
  }

  @Override
  public Object getCellEditorValue() {
    if (isPushed) {
      JOptionPane.showMessageDialog(button, "Exact mass has been added for calibration");
      button.setText("In use");
      button.setForeground(Color.GREEN);
    }
    isPushed = false;
    return label;
  }

  @Override
  public boolean stopCellEditing() {
    isPushed = false;
    return super.stopCellEditing();
  }

  public LockMass getLockMass() {
    return lockMass;
  }
}

package net.sf.mzmine.modules.tools.kellerlist;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class KellerListTableFrame extends JFrame {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private JScrollPane scrollPane;

  public KellerListTableFrame(String fileName, DefaultTableModel model,
      KellerListCellRenderer rendererEic) {

    super.setTitle("List of potential interference- or contaminant ions in " + fileName);

    // new table
    JTable table = new JTable(model);
    // format table
    for (int i = 0; i < table.getRowCount(); i++) {
      table.setRowHeight(i, 100);
    }
    table.getColumnModel().getColumn(1).setCellRenderer(rendererEic);
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    table.setDefaultRenderer(String.class, centerRenderer);
    table.setDefaultRenderer(Double.class, centerRenderer);
    table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
    table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
    table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
    table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
    table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

    table.getColumnModel().getColumn(1).setMinWidth(300);
    table.getColumnModel().getColumn(1).setMaxWidth(300);
    table.getColumnModel().getColumn(1).setPreferredWidth(300);
    table.getColumnModel().getColumn(1).setResizable(false);
    scrollPane = new JScrollPane();
    scrollPane.add(table);
    scrollPane.setViewportView(table);
    this.add(scrollPane);
    this.setSize(900, 900);
  }
}

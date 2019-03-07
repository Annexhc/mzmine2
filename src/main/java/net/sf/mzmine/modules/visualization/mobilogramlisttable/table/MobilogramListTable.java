/*
 * Copyright 2006-2018 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 2; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package net.sf.mzmine.modules.visualization.mobilogramlisttable.table;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.event.RowSorterEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableRowSorter;
import net.sf.mzmine.datamodel.MobilogramIdentity;
import net.sf.mzmine.datamodel.MobilogramList;
import net.sf.mzmine.datamodel.MobilogramListRow;
import net.sf.mzmine.modules.visualization.mobilogramlisttable.MobilogramListTableParameters;
import net.sf.mzmine.modules.visualization.mobilogramlisttable.MobilogramListTablePopupMenu;
import net.sf.mzmine.modules.visualization.mobilogramlisttable.MobilogramListTableWindow;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.util.components.ComponentToolTipManager;
import net.sf.mzmine.util.components.ComponentToolTipProvider;
import net.sf.mzmine.util.components.GroupableTableHeader;
import net.sf.mzmine.util.components.PopupListener;

public class MobilogramListTable extends JTable implements ComponentToolTipProvider {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  static final String EDIT_IDENTITY = "Edit";
  static final String REMOVE_IDENTITY = "Remove";
  static final String NEW_IDENTITY = "Add new...";

  private static final Font comboFont = new Font("SansSerif", Font.PLAIN, 10);

  private MobilogramListTableWindow window;
  private MobilogramListTableModel pkTableModel;
  private MobilogramList mobilogramList;
  private MobilogramListRow mobilogramListRow;
  private TableRowSorter<MobilogramListTableModel> sorter;
  private MobilogramListTableColumnModel cm;
  private ComponentToolTipManager ttm;
  private DefaultCellEditor currentEditor = null;

  public MobilogramListTable(MobilogramListTableWindow window, ParameterSet parameters,
      MobilogramList mobilogramList) {

    this.window = window;
    this.mobilogramList = mobilogramList;

    this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    this.setAutoCreateColumnsFromModel(false);

    this.pkTableModel = new MobilogramListTableModel(mobilogramList);
    setModel(pkTableModel);

    GroupableTableHeader header = new GroupableTableHeader();
    setTableHeader(header);

    cm = new MobilogramListTableColumnModel(header, pkTableModel, parameters, mobilogramList);
    cm.setColumnMargin(0);
    setColumnModel(cm);

    // create default columns
    cm.createColumns();

    // Initialize sorter
    sorter = new TableRowSorter<MobilogramListTableModel>(pkTableModel);
    setRowSorter(sorter);

    MobilogramListTablePopupMenu popupMenu =
        new MobilogramListTablePopupMenu(window, this, cm, mobilogramList);
    addMouseListener(new PopupListener(popupMenu));
    header.addMouseListener(new PopupListener(popupMenu));

    int rowHeight = parameters.getParameter(MobilogramListTableParameters.rowHeight).getValue();
    setRowHeight(rowHeight);

    ttm = new ComponentToolTipManager();
    ttm.registerComponent(this);

  }

  @Override
  public JComponent getCustomToolTipComponent(MouseEvent event) {

    JComponent component = null;
    String text = this.getToolTipText(event);
    if (text == null) {
      return null;
    }

    // if (text.contains(ComponentToolTipManager.CUSTOM)) {
    // String values[] = text.split("-");
    // int myID = Integer.parseInt(values[1].trim());
    // for (MobilogramListRow row : mobilogramList.getRows()) {
    // if (row.getID() == myID) {
    // component = new MobilogramSummaryComponent(row, mobilogramList.getRawDataFiles(), true,
    // false, false, true, false, ComponentToolTipManager.bg);
    // break;
    // }
    // }
    //
    // } else {
    // text = "<html>" + text.replace("\n", "<br>") + "</html>";
    // JLabel label = new JLabel(text);
    // label.setFont(UIManager.getFont("ToolTip.font"));
    // JPanel panel = new JPanel();
    // panel.setBackground(ComponentToolTipManager.bg);
    // panel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    // panel.add(label);
    // component = panel;
    // }

    return component;

  }

  public MobilogramList getMobilogramList() {
    return mobilogramList;
  }

  public TableRowSorter<MobilogramListTableModel> getTableRowSorter() {
    return sorter;
  }

  @Override
  public TableCellEditor getCellEditor(int row, int column) {

    CommonColumnType commonColumn = pkTableModel.getCommonColumn(column);
    if (commonColumn == CommonColumnType.IDENTITY) {

      row = this.convertRowIndexToModel(row);
      mobilogramListRow = mobilogramList.getRow(row);

      MobilogramIdentity identities[] = mobilogramListRow.getMobilogramIdentities();
      MobilogramIdentity preferredIdentity = mobilogramListRow.getPreferredMobilogramIdentity();
      JComboBox<Object> combo;

      if ((identities != null) && (identities.length > 0)) {
        combo = new JComboBox<Object>(identities);
        combo.addItem("-------------------------");
        combo.addItem(REMOVE_IDENTITY);
        combo.addItem(EDIT_IDENTITY);
      } else {
        combo = new JComboBox<Object>();
      }

      combo.setFont(comboFont);
      combo.addItem(NEW_IDENTITY);
      if (preferredIdentity != null) {
        combo.setSelectedItem(preferredIdentity);
      }

      combo.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          JComboBox<?> combo = (JComboBox<?>) e.getSource();
          Object item = combo.getSelectedItem();
          // if (item != null) {
          // if (item.toString() == NEW_IDENTITY) {
          // MobilogramIdentitySetupDialog dialog =
          // new MobilogramIdentitySetupDialog(window, mobilogramListRow);
          // dialog.setVisible(true);
          // return;
          // }
          // if (item.toString() == EDIT_IDENTITY) {
          // MobilogramIdentitySetupDialog dialog = new MobilogramIdentitySetupDialog(window,
          // mobilogramListRow, mobilogramListRow.getPreferredMobilogramIdentity());
          // dialog.setVisible(true);
          // return;
          // }
          // if (item.toString() == REMOVE_IDENTITY) {
          // MobilogramIdentity identity = mobilogramListRow.getPreferredMobilogramIdentity();
          // if (identity != null) {
          // mobilogramListRow.removeMobilogramIdentity(identity);
          // DefaultComboBoxModel<?> comboModel = (DefaultComboBoxModel<?>) combo.getModel();
          // comboModel.removeElement(identity);
          // }
          // return;
          // }
          // if (item instanceof MobilogramIdentity) {
          // mobilogramListRow.setPreferredMobilogramIdentity((MobilogramIdentity) item);
          // return;
          // }
          // }

        }
      });

      // Keep the reference to the editor
      currentEditor = new DefaultCellEditor(combo);

      return currentEditor;
    }

    return super.getCellEditor(row, column);

  }

  /**
   * When user sorts the table, we have to cancel current combobox for identity selection.
   * Unfortunately, this doesn't happen automatically.
   */
  @Override
  public void sorterChanged(RowSorterEvent e) {
    if (currentEditor != null) {
      currentEditor.stopCellEditing();
    }
    super.sorterChanged(e);
  }

}

/*
 * Copyright 2006-2019 The MZmine 2 Development Team
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

package net.sf.mzmine.modules.visualization.kendrickmassplot;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import net.sf.mzmine.parameters.parametertypes.StringComponent;
import net.sf.mzmine.util.GUIUtils;

/**
 * Kendrick mass plot toolbar class
 * 
 * @author Ansgar Korf (ansgar.korf@uni-muenster.de)
 */
public class KendrickMassPlotToolBar extends JToolBar {

  private static final long serialVersionUID = 1L;
  static final Icon blockSizeIcon = new ImageIcon("icons/blocksizeicon.png");
  static final Icon backColorIcon = new ImageIcon("icons/bgicon.png");
  static final Icon gridIcon = new ImageIcon("icons/gridicon.png");
  static final Icon annotationsIcon = new ImageIcon("icons/annotationsicon.png");
  static final Icon arrowUpIcon = new ImageIcon("icons/arrowupicon.png");
  static final Icon arrowDownIcon = new ImageIcon("icons/arrowdownicon.png");
  static final Icon kmdIcon = new ImageIcon("icons/KMDIcon.png");
  static final Icon rkmIcon = new ImageIcon("icons/RKMIcon.png");

  private JLabel yAxisDivisorLabel;
  private JLabel xAxisDivisorLabel;
  private JLabel zAxisDivisorLabel;

  private JCheckBox yUseAutoChargeCheckBox;
  private JCheckBox xUseAutoChargeCheckBox;
  private JCheckBox zUseAutoChargeCheckBox;

  private StringComponent yChargeCarrier;
  private StringComponent xChargeCarrier;
  private StringComponent zChargeCarrier;

  private JCheckBox yUseChargeCarrierCheckBox;
  private JCheckBox xUseChargeCarrierCheckBox;
  private JCheckBox zUseChargeCarrierCheckBox;

  DecimalFormat shiftFormat = new DecimalFormat("0.##");

  public KendrickMassPlotToolBar(ActionListener masterFrame, // listener
      double xAxisShift, double yAxisShift, double zAxisShift, // shifts
      int xAxisCharge, int yAxisCharge, int zAxisCharge, // charge
      int xAxisDivisor, int yAxisDivisor, int zAxisDivisor, // divisor
      boolean useCustomXAxis, boolean useCustomZAxis, // custom axis
      boolean useXAxisRKM, boolean useYAxisRKM, boolean useZAxisRKM, // RKM or KMD icon
      boolean yUseAutoCharge, boolean xUseAutoCharge, boolean zUseAutoCharge, // auto charge
                                                                              // correction
      boolean yUseChargeCarrier, boolean xUseChargeCarrier, boolean zUseChargeCarrier // Charge
                                                                                      // Carrier
  ) {

    super(JToolBar.VERTICAL);

    shiftFormat.format(xAxisShift);
    shiftFormat.format(yAxisShift);
    shiftFormat.format(zAxisShift);

    setFloatable(false);
    setFocusable(false);
    setMargin(new Insets(5, 5, 5, 5));
    setBackground(Color.white);

    // list for all components
    ArrayList<JComponent> componentsList = new ArrayList<JComponent>();

    // toggle buttons
    componentsList.add(GUIUtils.addButton(this, null, blockSizeIcon, masterFrame,
        "TOGGLE_BLOCK_SIZE", "Toggle block size"));

    componentsList.add(GUIUtils.addLabel(this, ""));

    componentsList.add(GUIUtils.addButton(this, null, backColorIcon, masterFrame,
        "TOGGLE_BACK_COLOR", "Toggle background color white/black"));

    componentsList
        .add(GUIUtils.addButton(this, null, gridIcon, masterFrame, "TOGGLE_GRID", "Toggle grid"));

    componentsList.add(GUIUtils.addLabel(this, ""));

    componentsList.add(GUIUtils.addButton(this, null, annotationsIcon, masterFrame,
        "TOGGLE_ANNOTATIONS", "Toggle annotations"));

    // add empty row
    addEmptyRow(componentsList);

    // yAxis components

    // header
    componentsList.add(GUIUtils.addLabel(this, "Y-Axis:"));
    componentsList.add(GUIUtils.addLabel(this, null));
    componentsList.add(GUIUtils.addLabel(this, null));

    // labels
    componentsList.add(GUIUtils.addLabel(this, "Shift", null, JLabel.CENTER, null));
    componentsList.add(GUIUtils.addLabel(this, "Charge", null, JLabel.CENTER, null));
    componentsList.add(GUIUtils.addLabel(this, "Divisor", null, JLabel.CENTER, null));

    // arrow up
    componentsList.add(
        GUIUtils.addButton(this, null, arrowUpIcon, masterFrame, "SHIFT_KMD_UP_Y", "Shift KMD up"));
    componentsList.add(GUIUtils.addButton(this, null, arrowUpIcon, masterFrame,
        "CHANGE_CHARGE_UP_Y", "Increase charge"));
    componentsList.add(GUIUtils.addButton(this, null, arrowUpIcon, masterFrame,
        "CHANGE_DIVISOR_UP_Y", "Increase divisor"));

    // arrow down
    componentsList.add(GUIUtils.addButton(this, null, arrowDownIcon, masterFrame,
        "SHIFT_KMD_DOWN_Y", "Shift KMD down"));
    componentsList.add(GUIUtils.addButton(this, null, arrowDownIcon, masterFrame,
        "CHANGE_CHARGE_DOWN_Y", "Decrease charge"));
    componentsList.add(GUIUtils.addButton(this, null, arrowDownIcon, masterFrame,
        "CHANGE_DIVISOR_DOWN_Y", "Decrease divisor"));

    // current
    componentsList.add(GUIUtils.addLabel(this, //
        String.valueOf(shiftFormat.format(yAxisShift)), null, JLabel.CENTER, null));
    componentsList.add(GUIUtils.addLabel(this, //
        String.valueOf(shiftFormat.format(yAxisCharge)), null, JLabel.CENTER, null));
    yAxisDivisorLabel = GUIUtils.addLabel(this, //
        String.valueOf(shiftFormat.format(yAxisDivisor)), null, JLabel.CENTER, null);
    componentsList.add(yAxisDivisorLabel);

    componentsList.add(GUIUtils.addLabel(this, "KMD/RKM", null, JLabel.CENTER, null));
    componentsList.add(GUIUtils.addLabel(this, "Auto Charge", null, JLabel.CENTER, null));
    yUseChargeCarrierCheckBox =
        GUIUtils.addCheckbox(this, "Charge Carrier", null, masterFrame, "USE_CHARGE_CARRIER_Y", "");
    yUseChargeCarrierCheckBox.setSelected(yUseChargeCarrier);
    componentsList.add(yUseChargeCarrierCheckBox);

    // use remainders instead of defects check box
    if (useYAxisRKM == false) {
      componentsList.add(GUIUtils.addButton(this, null, kmdIcon, masterFrame, "TOGGLE_RKM_KMD_Y",
          "Toggle RKM (remainders of Kendrick masses) and KMD (Kendrick mass defect)"));
    } else {
      componentsList.add(GUIUtils.addButton(this, null, rkmIcon, masterFrame, "TOGGLE_RKM_KMD_Y",
          "Toggle RKM (remainders of Kendrick masses) and KMD (Kendrick mass defect)"));
    }

    // use auto charge detection
    yUseAutoChargeCheckBox = GUIUtils.addCheckbox(this, null, null, masterFrame,
        "USE_AUTO_CHARGE_Y", "Automatic charge detection to cluster feature of the same compound");
    yUseAutoChargeCheckBox.setSelected(yUseAutoCharge);
    componentsList.add(yUseAutoChargeCheckBox);
    yChargeCarrier = new StringComponent(6);
    componentsList.add(yChargeCarrier);

    // xAxis
    if (useCustomXAxis) {

      // add empty row
      addEmptyRow(componentsList);

      // header
      componentsList.add(GUIUtils.addLabel(this, "X-Axis:"));
      componentsList.add(GUIUtils.addLabel(this, null));
      componentsList.add(GUIUtils.addLabel(this, null));

      // labels
      componentsList.add(GUIUtils.addLabel(this, "Shift", null, JLabel.CENTER, null));
      componentsList.add(GUIUtils.addLabel(this, "Charge", null, JLabel.CENTER, null));
      componentsList.add(GUIUtils.addLabel(this, "Divisor", null, JLabel.CENTER, null));

      // arrow up
      componentsList.add(GUIUtils.addButton(this, null, arrowUpIcon, masterFrame, "SHIFT_KMD_UP_X",
          "Shift KMD up"));
      componentsList.add(GUIUtils.addButton(this, null, arrowUpIcon, masterFrame,
          "CHANGE_CHARGE_UP_X", "Increase charge"));
      componentsList.add(GUIUtils.addButton(this, null, arrowUpIcon, masterFrame,
          "CHANGE_DIVISOR_UP_X", "Increase divisor"));

      // arrow down
      componentsList.add(GUIUtils.addButton(this, null, arrowDownIcon, masterFrame,
          "SHIFT_KMD_DOWN_X", "Shift KMD down"));
      componentsList.add(GUIUtils.addButton(this, null, arrowDownIcon, masterFrame,
          "CHANGE_CHARGE_DOWN_X", "Decrease charge"));
      componentsList.add(GUIUtils.addButton(this, null, arrowDownIcon, masterFrame,
          "CHANGE_DIVISOR_DOWN_X", "Decrease divisor"));

      // current
      componentsList.add(GUIUtils.addLabel(this, //
          String.valueOf(shiftFormat.format(xAxisShift)), null, JLabel.CENTER, null));
      componentsList.add(GUIUtils.addLabel(this, //
          String.valueOf(shiftFormat.format(xAxisCharge)), null, JLabel.CENTER, null));
      xAxisDivisorLabel = GUIUtils.addLabel(this, //
          String.valueOf(shiftFormat.format(xAxisDivisor)), null, JLabel.CENTER, null);
      componentsList.add(xAxisDivisorLabel);

      // use remainders instead of defects check box
      if (useXAxisRKM == false) {
        componentsList.add(GUIUtils.addButton(this, null, kmdIcon, masterFrame, "TOGGLE_RKM_KMD_X",
            "Toggle RKM (remainders of Kendrick masses) and KMD (Kendrick mass defect)"));
      } else {
        componentsList.add(GUIUtils.addButton(this, null, rkmIcon, masterFrame, "TOGGLE_RKM_KMD_X",
            "Toggle RKM (remainders of Kendrick masses) and KMD (Kendrick mass defect)"));
      }
      // use auto charge detection
      xUseAutoChargeCheckBox = GUIUtils.addCheckbox(this, "Auto charge detection", null,
          masterFrame, "USE_AUTO_CHARGE_X",
          "Automatic charge detection to cluster feature of the same compound");
      xUseAutoChargeCheckBox.setSelected(xUseAutoCharge);
      componentsList.add(xUseAutoChargeCheckBox);
      componentsList.add(GUIUtils.addLabel(this, null));
    }

    // zAxis
    if (useCustomZAxis) {

      // add empty row
      addEmptyRow(componentsList);

      // header
      componentsList.add(GUIUtils.addLabel(this, "Z-Axis:"));
      componentsList.add(GUIUtils.addLabel(this, null));
      componentsList.add(GUIUtils.addLabel(this, null));

      // labels
      componentsList.add(GUIUtils.addLabel(this, "Shift", null, JLabel.CENTER, null));
      componentsList.add(GUIUtils.addLabel(this, "Charge", null, JLabel.CENTER, null));
      componentsList.add(GUIUtils.addLabel(this, "Divisor", null, JLabel.CENTER, null));

      // arrow up
      componentsList.add(GUIUtils.addButton(this, null, arrowUpIcon, masterFrame, "SHIFT_KMD_UP_Z",
          "Shift KMD up"));
      componentsList.add(GUIUtils.addButton(this, null, arrowUpIcon, masterFrame,
          "CHANGE_CHARGE_UP_Z", "Increase charge"));
      componentsList.add(GUIUtils.addButton(this, null, arrowUpIcon, masterFrame,
          "CHANGE_DIVISOR_UP_Z", "Increase divisor"));

      // arrow down
      componentsList.add(GUIUtils.addButton(this, null, arrowDownIcon, masterFrame,
          "SHIFT_KMD_DOWN_Z", "Shift KMD down"));
      componentsList.add(GUIUtils.addButton(this, null, arrowDownIcon, masterFrame,
          "CHANGE_CHARGE_DOWN_Z", "Decrease charge"));
      componentsList.add(GUIUtils.addButton(this, null, arrowDownIcon, masterFrame,
          "CHANGE_DIVISOR_DOWN_Z", "Decrease divisor"));

      // current
      componentsList.add(GUIUtils.addLabel(this, //
          String.valueOf(shiftFormat.format(zAxisShift)), null, JLabel.CENTER, null));
      componentsList.add(GUIUtils.addLabel(this, //
          String.valueOf(shiftFormat.format(zAxisCharge)), null, JLabel.CENTER, null));
      zAxisDivisorLabel = GUIUtils.addLabel(this, //
          String.valueOf(shiftFormat.format(zAxisDivisor)), null, JLabel.CENTER, null);
      componentsList.add(zAxisDivisorLabel);

      // use remainders instead of defects check box
      if (useZAxisRKM == false) {
        componentsList.add(GUIUtils.addButton(this, null, kmdIcon, masterFrame, "TOGGLE_RKM_KMD_Z",
            "Toggle RKM (remainders of Kendrick masses) and KMD (Kendrick mass defect)"));
      } else {
        componentsList.add(GUIUtils.addButton(this, null, rkmIcon, masterFrame, "TOGGLE_RKM_KMD_Z",
            "Toggle RKM (remainders of Kendrick masses) and KMD (Kendrick mass defect)"));
      }
      // use auto charge detection
      zUseAutoChargeCheckBox = GUIUtils.addCheckbox(this, "Auto charge detection", null,
          masterFrame, "USE_AUTO_CHARGE_Z",
          "Automatic charge detection to cluster feature of the same compound");
      zUseAutoChargeCheckBox.setSelected(zUseAutoCharge);
      componentsList.add(zUseAutoChargeCheckBox);
      componentsList.add(GUIUtils.addLabel(this, null));
    }
    JComponent[] components = new JComponent[componentsList.size()];

    JPanel componentsPanel =
        GUIUtils.makeTablePanel(components.length / 3, 3, componentsList.toArray(components));
    componentsPanel.setBackground(Color.WHITE);
    this.add(componentsPanel);
  }

  private void addEmptyRow(ArrayList<JComponent> componentsList) {
    componentsList.add(GUIUtils.addLabel(this, ""));
    componentsList.add(GUIUtils.addLabel(this, ""));
    componentsList.add(GUIUtils.addLabel(this, ""));
  }

  public JLabel getyAxisDivisorLabel() {
    return yAxisDivisorLabel;
  }

  public void setyAxisDivisorLabel(JLabel yAxisDivisorLabel) {
    this.yAxisDivisorLabel = yAxisDivisorLabel;
  }

  public JLabel getxAxisDivisorLabel() {
    return xAxisDivisorLabel;
  }

  public void setxAxisDivisorLabel(JLabel xAxisDivisorLabel) {
    this.xAxisDivisorLabel = xAxisDivisorLabel;
  }

  public JLabel getzAxisDivisorLabel() {
    return zAxisDivisorLabel;
  }

  public void setzAxisDivisorLabel(JLabel zAxisDivisorLabel) {
    this.zAxisDivisorLabel = zAxisDivisorLabel;
  }

  public JCheckBox getyUseAutoCharge() {
    return yUseAutoChargeCheckBox;
  }

  public void setyUseAutoCharge(JCheckBox yUseAutoCharge) {
    this.yUseAutoChargeCheckBox = yUseAutoCharge;
  }

  public JCheckBox getxUseAutoCharge() {
    return xUseAutoChargeCheckBox;
  }

  public void setxUseAutoCharge(JCheckBox xUseAutoCharge) {
    this.xUseAutoChargeCheckBox = xUseAutoCharge;
  }

  public JCheckBox getzUseAutoCharge() {
    return zUseAutoChargeCheckBox;
  }

  public void setzUseAutoCharge(JCheckBox zUseAutoCharge) {
    this.zUseAutoChargeCheckBox = zUseAutoCharge;
  }

  public StringComponent getyChargeCarrier() {
    return yChargeCarrier;
  }

  public void setyChargeCarrier(StringComponent yChargeCarrier) {
    this.yChargeCarrier = yChargeCarrier;
  }

  public StringComponent getxChargeCarrier() {
    return xChargeCarrier;
  }

  public void setxChargeCarrier(StringComponent xChargeCarrier) {
    this.xChargeCarrier = xChargeCarrier;
  }

  public StringComponent getzChargeCarrier() {
    return zChargeCarrier;
  }

  public void setzChargeCarrier(StringComponent zChargeCarrier) {
    this.zChargeCarrier = zChargeCarrier;
  }

  public JCheckBox getyUseChargeCarrierCheckBox() {
    return yUseChargeCarrierCheckBox;
  }

  public void setyUseChargeCarrierCheckBox(JCheckBox yUseChargeCarrierCheckBox) {
    this.yUseChargeCarrierCheckBox = yUseChargeCarrierCheckBox;
  }

  public JCheckBox getxUseChargeCarrierCheckBox() {
    return xUseChargeCarrierCheckBox;
  }

  public void setxUseChargeCarrierCheckBox(JCheckBox xUseChargeCarrierCheckBox) {
    this.xUseChargeCarrierCheckBox = xUseChargeCarrierCheckBox;
  }

  public JCheckBox getzUseChargeCarrierCheckBox() {
    return zUseChargeCarrierCheckBox;
  }

  public void setzUseChargeCarrierCheckBox(JCheckBox zUseChargeCarrierCheckBox) {
    this.zUseChargeCarrierCheckBox = zUseChargeCarrierCheckBox;
  }

}

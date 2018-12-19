package net.sf.mzmine.modules.visualization.kendrickmassplot;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
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
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.Feature;
import net.sf.mzmine.datamodel.PeakIdentity;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.visualization.peaklisttable.table.CommonColumnType;
import net.sf.mzmine.modules.visualization.peaklisttable.table.DataFileColumnType;
import net.sf.mzmine.modules.visualization.peaklisttable.table.PeakListTable;
import net.sf.mzmine.modules.visualization.peaklisttable.table.PeakListTableColumnModel;
import net.sf.mzmine.modules.visualization.spectra.SpectraVisualizerModule;
import net.sf.mzmine.modules.visualization.threed.ThreeDVisualizerModule;
import net.sf.mzmine.modules.visualization.tic.TICPlotType;
import net.sf.mzmine.modules.visualization.tic.TICVisualizerModule;
import net.sf.mzmine.modules.visualization.twod.TwoDVisualizerModule;
import net.sf.mzmine.parameters.parametertypes.selectors.ScanSelection;
import net.sf.mzmine.util.GUIUtils;

/**
 * Peak-list table pop-up menu.
 */
public class KendrickMassPlotPopUpMenu extends JPopupMenu implements ActionListener {

  private static final long serialVersionUID = 1L;

  private final PeakListTable table;
  private final PeakList peakList;
  private final PeakListTableColumnModel columnModel;

  private final JMenu showMenu;
  private final JMenuItem showSpectrumItem;
  private final JMenuItem showXICItem;
  private final JMenuItem showXICSetupItem;
  private final JMenuItem showMSMSItem;
  private final JMenuItem showIsotopePatternItem;
  private final JMenuItem show2DItem;
  private final JMenuItem show3DItem;

  private final KendrickMassPlotWindow window;
  private RawDataFile clickedDataFile;
  private PeakListRow clickedPeakListRow;
  private PeakListRow[] allClickedPeakListRows;

  // For copying and pasting IDs - shared by all peak-list table instances.
  // Currently only accessed from this
  // class.
  private static PeakIdentity copiedId = null;

  public KendrickMassPlotPopUpMenu(final KendrickMassPlotWindow window, PeakListTable listTable,
      final PeakListTableColumnModel model, final PeakList list) {

    this.window = window;
    table = listTable;
    peakList = list;
    columnModel = model;

    clickedDataFile = null;
    clickedPeakListRow = null;
    allClickedPeakListRows = null;

    showMenu = new JMenu("Show");
    add(showMenu);

    showXICItem = GUIUtils.addMenuItem(showMenu, "XIC (base peak) (quick)", this);
    showXICSetupItem = GUIUtils.addMenuItem(showMenu, "XIC (dialog)", this);
    showSpectrumItem = GUIUtils.addMenuItem(showMenu, "Mass spectrum", this);
    show2DItem = GUIUtils.addMenuItem(showMenu, "Peak in 2D", this);
    show3DItem = GUIUtils.addMenuItem(showMenu, "Peak in 3D", this);
    showMSMSItem = GUIUtils.addMenuItem(showMenu, "MS/MS", this);
    showIsotopePatternItem = GUIUtils.addMenuItem(showMenu, "Isotope pattern", this);
  }

  @Override
  public void show(final Component invoker, final int x, final int y) {

    // Select the row where clicked?
    final Point clickedPoint = new Point(x, y);
    final int clickedRow = table.rowAtPoint(clickedPoint);
    if (table.getSelectedRowCount() < 2) {
      ListSelectionModel selectionModel = table.getSelectionModel();
      selectionModel.setSelectionInterval(clickedRow, clickedRow);
    }

    // First, disable all the Show... items
    show2DItem.setEnabled(false);
    show3DItem.setEnabled(false);
    showMSMSItem.setEnabled(false);
    showIsotopePatternItem.setEnabled(false);

    // Enable row items if applicable
    final int[] selectedRows = table.getSelectedRows();
    final boolean rowsSelected = selectedRows.length > 0;
    showMenu.setEnabled(rowsSelected);

    final boolean oneRowSelected = selectedRows.length == 1;

    // Find the row and column where the user clicked
    clickedDataFile = null;
    final int clickedColumn =
        columnModel.getColumn(table.columnAtPoint(clickedPoint)).getModelIndex();
    if (clickedRow >= 0 && clickedColumn >= 0) {

      final int rowIndex = table.convertRowIndexToModel(clickedRow);
      clickedPeakListRow = peakList.getRow(rowIndex);
      allClickedPeakListRows = new PeakListRow[selectedRows.length];
      for (int i = 0; i < selectedRows.length; i++) {

        allClickedPeakListRows[i] = peakList.getRow(table.convertRowIndexToModel(selectedRows[i]));
      }

      // Enable items.
      show2DItem.setEnabled(oneRowSelected);
      show3DItem.setEnabled(oneRowSelected);

      // If we clicked on data file columns, check the peak
      if (clickedColumn >= CommonColumnType.values().length) {

        // Find the actual peak, if we have it.
        clickedDataFile = peakList.getRawDataFile((clickedColumn - CommonColumnType.values().length)
            / DataFileColumnType.values().length);

        final Feature clickedPeak =
            peakList.getRow(table.convertRowIndexToModel(clickedRow)).getPeak(clickedDataFile);

        // If we have the peak, enable Show... items
        if (clickedPeak != null && oneRowSelected) {
          showIsotopePatternItem.setEnabled(clickedPeak.getIsotopePattern() != null);
          showMSMSItem.setEnabled(clickedPeak.getMostIntenseFragmentScanNumber() > 0);
        }

      } else {

        showIsotopePatternItem
            .setEnabled(clickedPeakListRow.getBestIsotopePattern() != null && oneRowSelected);
        if (clickedPeakListRow.getBestPeak() != null) {
          showMSMSItem
              .setEnabled(clickedPeakListRow.getBestPeak().getMostIntenseFragmentScanNumber() > 0
                  && oneRowSelected);
        }
      }
    }

    super.show(invoker, x, y);
  }

  @Override
  public void actionPerformed(final ActionEvent e) {

    final Object src = e.getSource();

    if (showXICItem.equals(src) && allClickedPeakListRows.length != 0) {

      // Map peaks to their identity labels.
      final Map<Feature, String> labelsMap =
          new HashMap<Feature, String>(allClickedPeakListRows.length);

      final RawDataFile selectedDataFile =
          clickedDataFile == null ? allClickedPeakListRows[0].getBestPeak().getDataFile()
              : clickedDataFile;

      Range<Double> mzRange = null;
      final List<Feature> selectedPeaks = new ArrayList<Feature>(allClickedPeakListRows.length);
      for (final PeakListRow row : allClickedPeakListRows) {

        for (final Feature peak : row.getPeaks()) {
          if (mzRange == null) {
            mzRange = peak.getRawDataPointsMZRange();
            double upper = mzRange.upperEndpoint();
            double lower = mzRange.lowerEndpoint();
            if ((upper - lower) < 0.000001) {
              // Workaround to make ultra narrow mzRanges (e.g. from imported mzTab peaklist),
              // a more reasonable default for a HRAM instrument (~5ppm)
              double fiveppm = (upper * 5E-6);
              mzRange = Range.closed(lower - fiveppm, upper + fiveppm);
            }
          } else {
            mzRange = mzRange.span(peak.getRawDataPointsMZRange());
          }
        }

        final Feature filePeak = row.getPeak(selectedDataFile);
        if (filePeak != null) {

          selectedPeaks.add(filePeak);

          // Label the peak with the row's preferred identity.
          final PeakIdentity identity = row.getPreferredPeakIdentity();
          if (identity != null) {
            labelsMap.put(filePeak, identity.getName());
          }
        }
      }

      ScanSelection scanSelection = new ScanSelection(selectedDataFile.getDataRTRange(1), 1);

      TICVisualizerModule.showNewTICVisualizerWindow(new RawDataFile[] {selectedDataFile},
          selectedPeaks.toArray(new Feature[selectedPeaks.size()]), labelsMap, scanSelection,
          TICPlotType.BASEPEAK, mzRange);
    }

    if (showXICSetupItem.equals(src) && allClickedPeakListRows.length != 0) {

      // Map peaks to their identity labels.
      final Map<Feature, String> labelsMap =
          new HashMap<Feature, String>(allClickedPeakListRows.length);

      final RawDataFile[] selectedDataFiles = clickedDataFile == null ? peakList.getRawDataFiles()
          : new RawDataFile[] {clickedDataFile};

      Range<Double> mzRange = null;
      final ArrayList<Feature> allClickedPeaks =
          new ArrayList<Feature>(allClickedPeakListRows.length);
      final ArrayList<Feature> selectedClickedPeaks =
          new ArrayList<Feature>(allClickedPeakListRows.length);
      for (final PeakListRow row : allClickedPeakListRows) {

        // Label the peak with the row's preferred identity.
        final PeakIdentity identity = row.getPreferredPeakIdentity();

        for (final Feature peak : row.getPeaks()) {

          allClickedPeaks.add(peak);
          if (peak.getDataFile() == clickedDataFile) {
            selectedClickedPeaks.add(peak);
          }

          if (mzRange == null) {
            mzRange = peak.getRawDataPointsMZRange();
          } else {
            mzRange = mzRange.span(peak.getRawDataPointsMZRange());
          }

          if (identity != null) {
            labelsMap.put(peak, identity.getName());
          }
        }
      }

      ScanSelection scanSelection = new ScanSelection(selectedDataFiles[0].getDataRTRange(1), 1);

      TICVisualizerModule.setupNewTICVisualizer(
          MZmineCore.getProjectManager().getCurrentProject().getDataFiles(), selectedDataFiles,
          allClickedPeaks.toArray(new Feature[allClickedPeaks.size()]),
          selectedClickedPeaks.toArray(new Feature[selectedClickedPeaks.size()]), labelsMap,
          scanSelection, mzRange);
    }

    if (show2DItem.equals(src)) {

      final Feature showPeak = getSelectedPeak();
      if (showPeak != null) {

        TwoDVisualizerModule.show2DVisualizerSetupDialog(showPeak.getDataFile(),
            getPeakMZRange(showPeak), getPeakRTRange(showPeak));
      }
    }

    if (show3DItem.equals(src)) {

      final Feature showPeak = getSelectedPeak();
      if (showPeak != null) {

        ThreeDVisualizerModule.setupNew3DVisualizer(showPeak.getDataFile(),
            getPeakMZRange(showPeak), getPeakRTRange(showPeak));
      }
    }

    if (showSpectrumItem.equals(src)) {

      final Feature showPeak = getSelectedPeak();
      if (showPeak != null) {

        SpectraVisualizerModule.showNewSpectrumWindow(showPeak.getDataFile(),
            showPeak.getRepresentativeScanNumber(), showPeak);
      }
    }

    if (showMSMSItem.equals(src)) {

      final Feature showPeak = getSelectedPeak();
      if (showPeak != null) {

        final int scanNumber = showPeak.getMostIntenseFragmentScanNumber();
        if (scanNumber > 0) {
          SpectraVisualizerModule.showNewSpectrumWindow(showPeak.getDataFile(), scanNumber);
        } else {
          MZmineCore.getDesktop().displayMessage(window,
              "There is no fragment for "
                  + MZmineCore.getConfiguration().getMZFormat().format(showPeak.getMZ())
                  + " m/z in the current raw data.");
        }
      }
    }

    if (showIsotopePatternItem.equals(src)) {

      final Feature showPeak = getSelectedPeak();
      if (showPeak != null && showPeak.getIsotopePattern() != null) {

        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            SpectraVisualizerModule.showNewSpectrumWindow(showPeak.getDataFile(),
                showPeak.getRepresentativeScanNumber(), showPeak.getIsotopePattern());
          }
        });
      }
    }
  }


  /**
   * Get a peak's m/z range.
   * 
   * @param peak the peak.
   * @return The peak's m/z range.
   */
  private static Range<Double> getPeakMZRange(final Feature peak) {

    final Range<Double> peakMZRange = peak.getRawDataPointsMZRange();

    // By default, open the visualizer with the m/z range of
    // "peak_width x 2", but no smaller than 0.1 m/z, because with smaller
    // ranges VisAD tends to show nasty anti-aliasing artifacts.
    // For example of such artifacts, set mzMin = 440.27, mzMax = 440.28 and
    // mzResolution = 500
    final double minRangeCenter = (peakMZRange.upperEndpoint() + peakMZRange.lowerEndpoint()) / 2.0;
    final double minRangeWidth =
        Math.max(0.1, (peakMZRange.upperEndpoint() - peakMZRange.lowerEndpoint()) * 2);
    double mzMin = minRangeCenter - (minRangeWidth / 2);
    if (mzMin < 0)
      mzMin = 0;
    double mzMax = minRangeCenter + (minRangeWidth / 2);
    return Range.closed(mzMin, mzMax);
  }

  /**
   * Get a peak's RT range.
   * 
   * @param peak the peak.
   * @return The peak's RT range.
   */
  private static Range<Double> getPeakRTRange(final Feature peak) {

    final Range<Double> range = peak.getRawDataPointsRTRange();
    final double rtLen = range.upperEndpoint() - range.lowerEndpoint();
    return Range.closed(Math.max(0.0, range.lowerEndpoint() - rtLen),
        range.upperEndpoint() + rtLen);
  }

  /**
   * Get the selected peak.
   * 
   * @return the peak.
   */
  private Feature getSelectedPeak() {

    return clickedDataFile != null ? clickedPeakListRow.getPeak(clickedDataFile)
        : clickedPeakListRow.getBestPeak();
  }
}

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

package net.sf.mzmine.modules.visualization.mobilogramlisttable;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.IMSFeature;
import net.sf.mzmine.datamodel.MobilogramIdentity;
import net.sf.mzmine.datamodel.MobilogramList;
import net.sf.mzmine.datamodel.MobilogramListRow;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.visualization.mobilogramlisttable.table.CommonColumnType;
import net.sf.mzmine.modules.visualization.mobilogramlisttable.table.DataFileColumnType;
import net.sf.mzmine.modules.visualization.mobilogramlisttable.table.MobilogramListTable;
import net.sf.mzmine.modules.visualization.mobilogramlisttable.table.MobilogramListTableColumnModel;
import net.sf.mzmine.modules.visualization.spectra.simplespectra.SpectraVisualizerModule;
import net.sf.mzmine.modules.visualization.threed.ThreeDVisualizerModule;
import net.sf.mzmine.modules.visualization.twod.TwoDVisualizerModule;
import net.sf.mzmine.util.GUIUtils;

/**
 * Mobilogram-list table pop-up menu.
 */
public class MobilogramListTablePopupMenu extends JPopupMenu implements ActionListener {

  private static final long serialVersionUID = 1L;

  private final MobilogramListTable table;
  private final MobilogramList mobilogramList;
  private final MobilogramListTableColumnModel columnModel;

  private final JMenu showMenu;
  private final JMenu searchMenu;
  private final JMenu idsMenu;
  private final JMenu exportMenu;
  private final JMenuItem deleteRowsItem;
  private final JMenuItem addNewRowItem;
  private final JMenuItem plotRowsItem;
  private final JMenuItem showSpectrumItem;
  private final JMenuItem showXICItem;
  private final JMenuItem showXICSetupItem;
  private final JMenuItem showMSMSItem;
  private final JMenuItem showIsotopePatternItem;
  private final JMenuItem show2DItem;
  private final JMenuItem show3DItem;
  private final JMenuItem exportIsotopesItem;
  private final JMenuItem exportMSMSItem;

  ///// kaidu edit
  private final JMenuItem exportToSirius;
  ////
  private final JMenuItem manuallyDefineItem;
  private final JMenuItem showMobilogramRowSummaryItem;
  private final JMenuItem clearIdsItem;
  private final JMenuItem dbSearchItem;
  private final JMenuItem formulaItem;
  private final JMenuItem siriusItem;
  private final JMenuItem nistSearchItem;
  private final JMenuItem copyIdsItem;
  private final JMenuItem pasteIdsItem;

  private final MobilogramListTableWindow window;
  private RawDataFile clickedDataFile;
  private MobilogramListRow clickedMobilogramListRow;
  private MobilogramListRow[] allClickedMobilogramListRows;

  // For copying and pasting IDs - shared by all mobilogram-list table instances.
  // Currently only accessed from this
  // class.
  private static MobilogramIdentity copiedId = null;

  public MobilogramListTablePopupMenu(final MobilogramListTableWindow window,
      MobilogramListTable listTable, final MobilogramListTableColumnModel model,
      final MobilogramList list) {

    this.window = window;
    table = listTable;
    mobilogramList = list;
    columnModel = model;

    clickedDataFile = null;
    clickedMobilogramListRow = null;
    allClickedMobilogramListRows = null;

    showMenu = new JMenu("Show");
    add(showMenu);

    showXICItem = GUIUtils.addMenuItem(showMenu, "XIC (base mobilogram) (quick)", this);
    showXICSetupItem = GUIUtils.addMenuItem(showMenu, "XIC (dialog)", this);
    showSpectrumItem = GUIUtils.addMenuItem(showMenu, "Mass spectrum", this);
    show2DItem = GUIUtils.addMenuItem(showMenu, "Mobilogram in 2D", this);
    show3DItem = GUIUtils.addMenuItem(showMenu, "Mobilogram in 3D", this);
    showMSMSItem = GUIUtils.addMenuItem(showMenu, "MS/MS", this);
    showIsotopePatternItem = GUIUtils.addMenuItem(showMenu, "Isotope pattern", this);
    showMobilogramRowSummaryItem = GUIUtils.addMenuItem(showMenu, "Mobilogram row summary", this);

    searchMenu = new JMenu("Search");
    add(searchMenu);
    dbSearchItem = GUIUtils.addMenuItem(searchMenu, "Search online database", this);
    nistSearchItem = GUIUtils.addMenuItem(searchMenu, "NIST MS Search", this);
    formulaItem = GUIUtils.addMenuItem(searchMenu, "Predict molecular formula", this);
    siriusItem = GUIUtils.addMenuItem(searchMenu, "SIRIUS structure prediction", this);

    exportMenu = new JMenu("Export");
    add(exportMenu);
    exportIsotopesItem = GUIUtils.addMenuItem(exportMenu, "Isotope pattern", this);
    // kaidu edit
    exportToSirius = GUIUtils.addMenuItem(exportMenu, "Export to SIRIUS", this);
    //
    exportMSMSItem = GUIUtils.addMenuItem(exportMenu, "MS/MS pattern", this);

    // Identities menu.
    idsMenu = new JMenu("Identities");
    add(idsMenu);
    clearIdsItem = GUIUtils.addMenuItem(idsMenu, "Clear", this);
    copyIdsItem = GUIUtils.addMenuItem(idsMenu, "Copy", this);
    pasteIdsItem = GUIUtils.addMenuItem(idsMenu, "Paste", this);

    plotRowsItem = GUIUtils.addMenuItem(this, "Plot using Intensity Plot module", this);
    manuallyDefineItem = GUIUtils.addMenuItem(this, "Manually define mobilogram", this);
    deleteRowsItem = GUIUtils.addMenuItem(this, "Delete selected row(s)", this);
    addNewRowItem = GUIUtils.addMenuItem(this, "Add new row", this);
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
    manuallyDefineItem.setEnabled(false);
    showMSMSItem.setEnabled(false);
    showIsotopePatternItem.setEnabled(false);
    showMobilogramRowSummaryItem.setEnabled(false);
    exportIsotopesItem.setEnabled(false);
    exportMSMSItem.setEnabled(false);
    exportMenu.setEnabled(false);

    // Enable row items if applicable
    final int[] selectedRows = table.getSelectedRows();
    final boolean rowsSelected = selectedRows.length > 0;
    deleteRowsItem.setEnabled(rowsSelected);
    clearIdsItem.setEnabled(rowsSelected);
    pasteIdsItem.setEnabled(rowsSelected && copiedId != null);
    plotRowsItem.setEnabled(rowsSelected);
    showMenu.setEnabled(rowsSelected);
    idsMenu.setEnabled(rowsSelected);
    exportIsotopesItem.setEnabled(rowsSelected);
    exportToSirius.setEnabled(rowsSelected);
    exportMenu.setEnabled(rowsSelected);

    final boolean oneRowSelected = selectedRows.length == 1;
    searchMenu.setEnabled(oneRowSelected);

    // Find the row and column where the user clicked
    clickedDataFile = null;
    final int clickedColumn =
        columnModel.getColumn(table.columnAtPoint(clickedPoint)).getModelIndex();
    if (clickedRow >= 0 && clickedColumn >= 0) {

      final int rowIndex = table.convertRowIndexToModel(clickedRow);
      clickedMobilogramListRow = mobilogramList.getRow(rowIndex);
      allClickedMobilogramListRows = new MobilogramListRow[selectedRows.length];
      for (int i = 0; i < selectedRows.length; i++) {

        allClickedMobilogramListRows[i] =
            mobilogramList.getRow(table.convertRowIndexToModel(selectedRows[i]));
      }

      // Enable items.
      show2DItem.setEnabled(oneRowSelected);
      show3DItem.setEnabled(oneRowSelected);
      showMobilogramRowSummaryItem.setEnabled(oneRowSelected);

      if (clickedMobilogramListRow.getBestMobilogram() != null) {
        exportMSMSItem.setEnabled(oneRowSelected
            && clickedMobilogramListRow.getBestMobilogram().getMostIntenseFragmentScanNumber() > 0);
      }

      // If we clicked on data file columns, check the mobilogram
      if (clickedColumn >= CommonColumnType.values().length) {

        // Enable manual mobilogram picking
        manuallyDefineItem.setEnabled(oneRowSelected);

        // Find the actual mobilogram, if we have it.
        clickedDataFile =
            mobilogramList.getRawDataFile((clickedColumn - CommonColumnType.values().length)
                / DataFileColumnType.values().length);

        final IMSFeature clickedMobilogram = mobilogramList
            .getRow(table.convertRowIndexToModel(clickedRow)).getMobilogram(clickedDataFile);

        // If we have the mobilogram, enable Show... items
        if (clickedMobilogram != null && oneRowSelected) {
          showIsotopePatternItem.setEnabled(clickedMobilogram.getIsotopePattern() != null);
          showMSMSItem.setEnabled(clickedMobilogram.getMostIntenseFragmentScanNumber() > 0);
        }

      } else {

        showIsotopePatternItem
            .setEnabled(clickedMobilogramListRow.getBestIsotopePattern() != null && oneRowSelected);
        if (clickedMobilogramListRow.getBestMobilogram() != null) {
          showMSMSItem.setEnabled(
              clickedMobilogramListRow.getBestMobilogram().getMostIntenseFragmentScanNumber() > 0
                  && oneRowSelected);
        }
      }
    }

    copyIdsItem.setEnabled(
        oneRowSelected && allClickedMobilogramListRows[0].getPreferredMobilogramIdentity() != null);

    super.show(invoker, x, y);
  }

  @Override
  public void actionPerformed(final ActionEvent e) {

    final Object src = e.getSource();

    if (deleteRowsItem.equals(src)) {

      final int[] rowsToDelete = table.getSelectedRows();

      final int[] unsortedIndexes = new int[rowsToDelete.length];
      for (int i = rowsToDelete.length - 1; i >= 0; i--) {

        unsortedIndexes[i] = table.convertRowIndexToModel(rowsToDelete[i]);
      }

      // sort row indexes and start removing from the last
      Arrays.sort(unsortedIndexes);

      // delete the rows starting from last
      for (int i = unsortedIndexes.length - 1; i >= 0; i--) {
        mobilogramList.removeRow(unsortedIndexes[i]);
      }

      // Notify the GUI that mobilogramlist contents have changed
      updateTableGUI();
    }

    // if (plotRowsItem.equals(src)) {
    //
    // final int[] selectedTableRows = table.getSelectedRows();
    //
    // final MobilogramListRow[] selectedRows = new MobilogramListRow[selectedTableRows.length];
    // for (int i = 0; i < selectedTableRows.length; i++) {
    //
    // selectedRows[i] = mobilogramList.getRow(table.convertRowIndexToModel(selectedTableRows[i]));
    // }
    //
    // SwingUtilities.invokeLater(new Runnable() {
    // @Override
    // public void run() {
    // IntensityPlotModule.showIntensityPlot(MZmineCore.getProjectManager().getCurrentProject(),
    // mobilogramList, selectedRows);
    // }
    // });
    // }

    // if (showXICItem.equals(src) && allClickedMobilogramListRows.length != 0) {
    //
    // // Map mobilograms to their identity labels.
    // final Map<IMSFeature, String> labelsMap =
    // new HashMap<IMSFeature, String>(allClickedMobilogramListRows.length);
    //
    // final RawDataFile selectedDataFile = clickedDataFile == null
    // ? allClickedMobilogramListRows[0].getBestMobilogram().getDataFile()
    // : clickedDataFile;
    //
    // Range<Double> mzRange = null;
    // final List<IMSFeature> selectedMobilograms =
    // new ArrayList<IMSFeature>(allClickedMobilogramListRows.length);
    // for (final MobilogramListRow row : allClickedMobilogramListRows) {
    //
    // for (final IMSFeature mobilogram : row.getMobilograms()) {
    // if (mzRange == null) {
    // mzRange = mobilogram.getRawDataPointsMZRange();
    // double upper = mzRange.upperEndpoint();
    // double lower = mzRange.lowerEndpoint();
    // if ((upper - lower) < 0.000001) {
    // // Workaround to make ultra narrow mzRanges (e.g. from imported mzTab mobilogramlist),
    // // a more reasonable default for a HRAM instrument (~5ppm)
    // double fiveppm = (upper * 5E-6);
    // mzRange = Range.closed(lower - fiveppm, upper + fiveppm);
    // }
    // } else {
    // mzRange = mzRange.span(mobilogram.getRawDataPointsMZRange());
    // }
    // }
    //
    // final IMSFeature fileMobilogram = row.getMobilogram(selectedDataFile);
    // if (fileMobilogram != null) {
    //
    // selectedMobilograms.add(fileMobilogram);
    //
    // // Label the mobilogram with the row's preferred identity.
    // final MobilogramIdentity identity = row.getPreferredMobilogramIdentity();
    // if (identity != null) {
    // labelsMap.put(fileMobilogram, identity.getName());
    // }
    // }
    // }
    //
    // ScanSelection scanSelection = new ScanSelection(selectedDataFile.getDataRTRange(1), 1);
    //
    // TICVisualizerModule.showNewTICVisualizerWindow(new RawDataFile[] {selectedDataFile},
    // selectedMobilograms.toArray(new IMSFeature[selectedMobilograms.size()]), labelsMap,
    // scanSelection, TICPlotType.BASEPEAK, mzRange);
    // }

    // if (showXICSetupItem.equals(src) && allClickedMobilogramListRows.length != 0) {
    //
    // // Map mobilograms to their identity labels.
    // final Map<IMSFeature, String> labelsMap =
    // new HashMap<IMSFeature, String>(allClickedMobilogramListRows.length);
    //
    // final RawDataFile[] selectedDataFiles =
    // clickedDataFile == null ? mobilogramList.getRawDataFiles()
    // : new RawDataFile[] {clickedDataFile};
    //
    // Range<Double> mzRange = null;
    // final ArrayList<IMSFeature> allClickedMobilograms =
    // new ArrayList<IMSFeature>(allClickedMobilogramListRows.length);
    // final ArrayList<IMSFeature> selectedClickedMobilograms =
    // new ArrayList<IMSFeature>(allClickedMobilogramListRows.length);
    // for (final MobilogramListRow row : allClickedMobilogramListRows) {
    //
    // // Label the mobilogram with the row's preferred identity.
    // final MobilogramIdentity identity = row.getPreferredMobilogramIdentity();
    //
    // for (final IMSFeature mobilogram : row.getMobilograms()) {
    //
    // allClickedMobilograms.add(mobilogram);
    // if (mobilogram.getDataFile() == clickedDataFile) {
    // selectedClickedMobilograms.add(mobilogram);
    // }
    //
    // if (mzRange == null) {
    // mzRange = mobilogram.getRawDataPointsMZRange();
    // } else {
    // mzRange = mzRange.span(mobilogram.getRawDataPointsMZRange());
    // }
    //
    // if (identity != null) {
    // labelsMap.put(mobilogram, identity.getName());
    // }
    // }
    // }
    //
    // ScanSelection scanSelection = new ScanSelection(selectedDataFiles[0].getDataRTRange(1), 1);
    //
    // IVisualizerModule.setupNewTICVisualizer(
    // MZmineCore.getProjectManager().getCurrentProject().getDataFiles(), selectedDataFiles,
    // allClickedMobilograms.toArray(new IMSFeature[allClickedMobilograms.size()]),
    // selectedClickedMobilograms.toArray(new IMSFeature[selectedClickedMobilograms.size()]),
    // labelsMap, scanSelection, mzRange);
    // }

    if (show2DItem.equals(src)) {

      final IMSFeature showMobilogram = getSelectedMobilogram();
      if (showMobilogram != null) {

        TwoDVisualizerModule.show2DVisualizerSetupDialog(showMobilogram.getDataFile(),
            getMobilogramMZRange(showMobilogram), getMobilogramRTRange(showMobilogram));
      }
    }

    if (show3DItem.equals(src)) {

      final IMSFeature showMobilogram = getSelectedMobilogram();
      if (showMobilogram != null) {

        ThreeDVisualizerModule.setupNew3DVisualizer(showMobilogram.getDataFile(),
            getMobilogramMZRange(showMobilogram), getMobilogramRTRange(showMobilogram));
      }
    }

    // if (manuallyDefineItem.equals(src)) {
    //
    // ManualMobilogramPickerModule.runManualDetection(clickedDataFile, clickedMobilogramListRow,
    // mobilogramList, table);
    // }
    //
    // if (showSpectrumItem.equals(src)) {
    //
    // final IMSFeature showMobilogram = getSelectedMobilogram();
    // if (showMobilogram != null) {
    //
    // SpectraVisualizerModule.showNewSpectrumWindow(showMobilogram.getDataFile(),
    // showMobilogram.getRepresentativeScanNumber(), showMobilogram);
    // }
    // }

    if (showMSMSItem.equals(src)) {

      final IMSFeature showMobilogram = getSelectedMobilogram();
      if (showMobilogram != null) {

        final int scanNumber = showMobilogram.getMostIntenseFragmentScanNumber();
        if (scanNumber > 0) {
          SpectraVisualizerModule.showNewSpectrumWindow(showMobilogram.getDataFile(), scanNumber);
        } else {
          MZmineCore.getDesktop().displayMessage(window,
              "There is no fragment for "
                  + MZmineCore.getConfiguration().getMZFormat().format(showMobilogram.getMZ())
                  + " m/z in the current raw data.");
        }
      }
    }

    if (showIsotopePatternItem.equals(src)) {

      final IMSFeature showMobilogram = getSelectedMobilogram();
      if (showMobilogram != null && showMobilogram.getIsotopePattern() != null) {

        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            SpectraVisualizerModule.showNewSpectrumWindow(showMobilogram.getDataFile(),
                showMobilogram.getRepresentativeScanNumber(), showMobilogram.getIsotopePattern());
          }
        });
      }
    }

    // if (formulaItem != null && formulaItem.equals(src)) {
    //
    // SwingUtilities.invokeLater(new Runnable() {
    // @Override
    // public void run() {
    // FormulaPredictionModule.showSingleRowIdentificationDialog(clickedMobilogramListRow);
    // }
    // });
    //
    // }
    //
    // // //TODO: what is going on here?
    // // TODO: probably remove singlerowidentificationDialog as Sirius works with spectrum, not 1
    // // mobilogram.
    // if (siriusItem != null && siriusItem.equals(src)) {
    //
    // SwingUtilities.invokeLater(new Runnable() {
    // @Override
    // public void run() {
    // SiriusProcessingModule.showSingleRowIdentificationDialog(clickedMobilogramListRow);
    // }
    // });
    //
    // }
    //
    // if (dbSearchItem != null && dbSearchItem.equals(src)) {
    //
    // SwingUtilities.invokeLater(new Runnable() {
    // @Override
    // public void run() {
    // OnlineDBSearchModule.showSingleRowIdentificationDialog(clickedMobilogramListRow);
    // }
    // });
    //
    // }

    // if (nistSearchItem != null && nistSearchItem.equals(src)) {
    //
    // NistMsSearchModule.singleRowSearch(mobilogramList, clickedMobilogramListRow);
    // }

    // if (addNewRowItem.equals(src)) {
    //
    // // find maximum ID and add 1
    // int newID = 1;
    // for (final MobilogramListRow row : mobilogramList.getRows()) {
    // if (row.getID() >= newID) {
    // newID = row.getID() + 1;
    // }
    // }
    //
    // // create a new row
    // final MobilogramListRow newRow = new SimpleMobilogramListRow(newID);
    // ManualMobilogramPickerModule.runManualDetection(mobilogramList.getRawDataFiles(), newRow,
    // mobilogramList, table);
    //
    // }
    //
    // if (showMobilogramRowSummaryItem.equals(src)) {
    //
    // MobilogramSummaryVisualizerModule.showNewMobilogramSummaryWindow(clickedMobilogramListRow);
    // }
    //
    // if (exportIsotopesItem.equals(src)) {
    // IsotopePatternExportModule.exportIsotopePattern(clickedMobilogramListRow);
    // }
    // if (exportToSirius.equals(src)) {
    // SiriusExportModule.exportSingleMobilogramList(clickedMobilogramListRow);
    // }
    //
    // if (exportMSMSItem.equals(src)) {
    // MSMSExportModule.exportMSMS(clickedMobilogramListRow);
    // }

    if (clearIdsItem.equals(src)) {

      // Delete identities of selected rows.
      for (final MobilogramListRow row : allClickedMobilogramListRows) {

        // Selected row index.
        for (final MobilogramIdentity id : row.getMobilogramIdentities()) {

          // Remove id.
          row.removeMobilogramIdentity(id);
        }
      }

      // Update table GUI.
      updateTableGUI();
    }

    if (copyIdsItem.equals(src) && allClickedMobilogramListRows.length > 0) {

      final MobilogramIdentity id =
          allClickedMobilogramListRows[0].getPreferredMobilogramIdentity();
      if (id != null) {

        copiedId = (MobilogramIdentity) id.clone();
      }
    }

    if (pasteIdsItem.equals(src) && copiedId != null) {

      // Paste identity into selected rows.
      for (final MobilogramListRow row : allClickedMobilogramListRows) {

        row.setPreferredMobilogramIdentity((MobilogramIdentity) copiedId.clone());
      }

      // Update table GUI.
      updateTableGUI();
    }
  }

  /**
   * Update the table.
   */
  private void updateTableGUI() {
    ((AbstractTableModel) table.getModel()).fireTableDataChanged();
    MZmineCore.getProjectManager().getCurrentProject().notifyObjectChanged(mobilogramList, true);
  }

  /**
   * Get a mobilogram's m/z range.
   * 
   * @param mobilogram the mobilogram.
   * @return The mobilogram's m/z range.
   */
  private static Range<Double> getMobilogramMZRange(final IMSFeature mobilogram) {

    final Range<Double> mobilogramMZRange = mobilogram.getRawIMSDataPointsMZRange();

    // By default, open the visualizer with the m/z range of
    // "mobilogram_width x 2", but no smaller than 0.1 m/z, because with smaller
    // ranges VisAD tends to show nasty anti-aliasing artifacts.
    // For example of such artifacts, set mzMin = 440.27, mzMax = 440.28 and
    // mzResolution = 500
    final double minRangeCenter =
        (mobilogramMZRange.upperEndpoint() + mobilogramMZRange.lowerEndpoint()) / 2.0;
    final double minRangeWidth =
        Math.max(0.1, (mobilogramMZRange.upperEndpoint() - mobilogramMZRange.lowerEndpoint()) * 2);
    double mzMin = minRangeCenter - (minRangeWidth / 2);
    if (mzMin < 0)
      mzMin = 0;
    double mzMax = minRangeCenter + (minRangeWidth / 2);
    return Range.closed(mzMin, mzMax);
  }

  /**
   * Get a mobilogram's RT range.
   * 
   * @param mobilogram the mobilogram.
   * @return The mobilogram's RT range.
   */
  private static Range<Double> getMobilogramRTRange(final IMSFeature mobilogram) {

    final Range<Double> range = mobilogram.getRawIMSDataPointsRTRange();
    final double rtLen = range.upperEndpoint() - range.lowerEndpoint();
    return Range.closed(Math.max(0.0, range.lowerEndpoint() - rtLen),
        range.upperEndpoint() + rtLen);
  }

  /**
   * Get the selected mobilogram.
   * 
   * @return the mobilogram.
   */
  private IMSFeature getSelectedMobilogram() {

    return clickedDataFile != null ? clickedMobilogramListRow.getMobilogram(clickedDataFile)
        : clickedMobilogramListRow.getBestMobilogram();
  }
}

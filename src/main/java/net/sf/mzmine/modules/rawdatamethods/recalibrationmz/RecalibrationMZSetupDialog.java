/*
 * Copyright 2006-2015 The MZmine 2 Development Team
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

package net.sf.mzmine.modules.rawdatamethods.recalibrationmz;

import java.awt.Color;
import java.awt.Window;
import java.util.ArrayList;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.datamodel.impl.SimpleDataPoint;
import net.sf.mzmine.datamodel.impl.SimpleScan;
import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.naive.NaiveRecalibrationMZParameters;
import net.sf.mzmine.modules.visualization.spectra.SpectraPlot;
import net.sf.mzmine.modules.visualization.spectra.datasets.ScanDataSet;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.dialogs.ParameterSetupDialogWithScanPreview;

/**
 * This class extends ParameterSetupDialog class, including a spectraPlot. This is used to preview
 * how the selected mass detector and his parameters works over the raw data file.
 */
public class RecalibrationMZSetupDialog extends ParameterSetupDialogWithScanPreview {

  private static final long serialVersionUID = 1L;
  private RecalibrationMZMethod recalibrationMZMethod;
  private ParameterSet parameters;
  private double mzDiff;
  private String mzDiffType;

  /**
   * @param parameters
   * @param massDetectorTypeNumber
   */
  public RecalibrationMZSetupDialog(Window parent, boolean valueCheckRequired,
      Class<?> RecalibrationMZMethodClass, ParameterSet parameters) {

    super(parent, valueCheckRequired, parameters);

    this.parameters = parameters;
    this.mzDiff = parameters.getParameter(NaiveRecalibrationMZParameters.mzDiff).getValue();
    this.mzDiffType = parameters.getParameter(NaiveRecalibrationMZParameters.mzDiffType).getValue();
    for (RecalibrationMZMethod method : RecalibrationMZParameters.recalibrationMZMethods) {
      if (method.getClass().equals(RecalibrationMZMethodClass)) {
        this.recalibrationMZMethod = method;
      }
    }
  }

  protected void loadPreview(SpectraPlot spectrumPlot, Scan previewScan) {

    ScanDataSet spectraOriginalDataSet = new ScanDataSet("Original scan", previewScan);

    Scan scan = previewScan;
    final SimpleScan newScan = new SimpleScan(scan);
    DataPoint[] oldDPs = scan.getDataPoints();
    DataPoint[] newDPs = new DataPoint[scan.getNumberOfDataPoints()];

    System.out.println(mzDiffType);
    // Loop through every data point
    for (int j = 0; j < scan.getNumberOfDataPoints(); j++) {
      if (mzDiffType.equals("absolute")) {
        newDPs[j] = new SimpleDataPoint(oldDPs[j].getMZ() + mzDiff, oldDPs[j].getIntensity());
      }
      if (mzDiffType.equals("relative ppm")) {
        double dpSpecificMZDiff = oldDPs[j].getMZ() / 1000000 * mzDiff;
        newDPs[j] =
            new SimpleDataPoint(oldDPs[j].getMZ() + dpSpecificMZDiff, oldDPs[j].getIntensity());
      }
    }

    newScan.setDataPoints(newDPs);
    ScanDataSet spectraRecalibratedDataSet = new ScanDataSet("Recalibrated scan", newScan);
    // Set plot mode only if it hasn't been set before
    // if the scan is centroided, switch to centroid mode
    spectrumPlot.setPlotMode(previewScan.getSpectrumType());

    spectrumPlot.removeAllDataSets();
    spectrumPlot.addDataSet(spectraOriginalDataSet, Color.red, false);
    spectrumPlot.addDataSet(spectraRecalibratedDataSet, Color.green, false);

    // If there is some illegal value, do not load the preview but just exit
    ArrayList<String> errorMessages = new ArrayList<String>();
    boolean paramsOK = parameterSet.checkParameterValues(errorMessages);
    if (!paramsOK)
      return;

    // if the scan is centroided, switch to centroid mode
    spectrumPlot.setPlotMode(previewScan.getSpectrumType());

  }

}

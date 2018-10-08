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

package net.sf.mzmine.modules.masslistmethods.imschromatogrambuilder;

import java.util.Arrays;
import java.util.logging.Logger;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.Feature;
import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.MassList;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.datamodel.impl.SimplePeakList;
import net.sf.mzmine.datamodel.impl.SimplePeakListRow;
import net.sf.mzmine.modules.peaklistmethods.qualityparameters.QualityParameters;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.parametertypes.selectors.ScanSelection;
import net.sf.mzmine.parameters.parametertypes.tolerances.MZTolerance;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;
import net.sf.mzmine.util.PeakSorter;
import net.sf.mzmine.util.SortingDirection;
import net.sf.mzmine.util.SortingProperty;

public class ChromatogramBuilderTaskIMS extends AbstractTask {

  private Logger logger = Logger.getLogger(this.getClass().getName());

  private MZmineProject project;
  private RawDataFile dataFile;

  // scan counter
  private int processedScans = 0, totalScans;
  private ScanSelection scanSelection;
  private int newPeakID = 1;
  private Scan[] scans;

  // User parameters
  private String suffix, massListName;
  private MZTolerance mzTolerance;
  private double minimumTimeSpan, minimumHeight;

  private SimplePeakList newPeakList;

  /**
   * @param dataFile
   * @param parameters
   */
  public ChromatogramBuilderTaskIMS(MZmineProject project, RawDataFile dataFile,
      ParameterSet parameters) {

    this.project = project;
    this.dataFile = dataFile;
    this.scanSelection =
        parameters.getParameter(ChromatogramBuilderParametersIMS.scanSelection).getValue();
    this.massListName =
        parameters.getParameter(ChromatogramBuilderParametersIMS.massList).getValue();
    this.mzTolerance =
        parameters.getParameter(ChromatogramBuilderParametersIMS.mzTolerance).getValue();
    this.minimumTimeSpan =
        parameters.getParameter(ChromatogramBuilderParametersIMS.minimumTimeSpan).getValue();
    this.minimumHeight =
        parameters.getParameter(ChromatogramBuilderParametersIMS.minimumHeight).getValue();

    this.suffix = parameters.getParameter(ChromatogramBuilderParametersIMS.suffix).getValue();

  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
   */
  @Override
  public String getTaskDescription() {
    return "Detecting chromatograms in " + dataFile;
  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getFinishedPercentage()
   */
  @Override
  public double getFinishedPercentage() {
    if (totalScans == 0)
      return 0;
    else
      return (double) processedScans / totalScans;
  }

  public RawDataFile getDataFile() {
    return dataFile;
  }

  /**
   * @see Runnable#run()
   */
  @Override
  public void run() {
    setStatus(TaskStatus.PROCESSING);

    logger.info("Started chromatogram builder IMS on " + dataFile);
    scans = scanSelection.getMatchingScans(dataFile);
    // Scan scansToFilter[] = scanSelection.getMatchingScans(dataFile);
    //
    // ArrayList<Scan> scansList = new ArrayList<Scan>();
    // for (int i = 0; i < scansToFilter.length; i++) {
    // if (i == 0) {
    // scansList.add(scansToFilter[i]);
    // } else if (scansToFilter[i].getRetentionTime() != scansToFilter[i - 1].getRetentionTime()) {
    // scansList.add(scansToFilter[i]);
    // }
    // }
    //
    // // find scan with highest intensity of one retention time
    //
    // for (int i = 0; i < scansToFilter.length; i++) {
    // if (i == 0) {
    // scansList.add(scansToFilter[i]);
    // } else if (scansToFilter[i].getRetentionTime() != scansToFilter[i - 1].getRetentionTime()) {
    // scansList.add(scansToFilter[i]);
    // }
    // }
    //
    // scans = new Scan[scansList.size()];
    // for (int i = 0; i < scansList.size(); i++) {
    // scans[i] = scansList.get(i);
    // System.out.println(scans[i].getScanNumber()+"\n"+scans[i].get);
    // }
    // System.out.println(scans.length);
    // Combine scans with same retention time
    // scansCombined = combineScansWithSameRetentionTime(scans);

    // create new combined mass list
    // createCombinedMassListForScansWithSameRetentionTimes(scans);
    // toDo combined Scans!!!

    int allScanNumbers[] = scanSelection.getMatchingScanNumbers(dataFile);
    totalScans = scans.length;

    // Check if the scans are properly ordered by RT
    double prevRT = Double.NEGATIVE_INFINITY;
    for (Scan s : scans) {
      if (s.getRetentionTime() < prevRT) {
        setStatus(TaskStatus.ERROR);
        final String msg = "Retention time of scan #" + s.getScanNumber()
            + " is smaller then the retention time of the previous scan."
            + " Please make sure you only use scans with increasing retention times."
            + " You can restrict the scan numbers in the parameters, or you can use the Crop filter module";
        setErrorMessage(msg);
        return;
      }
      prevRT = s.getRetentionTime();
    }

    // Create new peak list
    newPeakList = new SimplePeakList(dataFile + " " + suffix, dataFile);

    ChromatogramIMS[] chromatograms;
    HighestDataPointConnectorIMS massConnector = new HighestDataPointConnectorIMS(dataFile,
        allScanNumbers, minimumTimeSpan, minimumHeight, mzTolerance);

    int numberofBins = getNumberOfBins(scans);

    for (int i = 0; i < scans.length - numberofBins; i++) {

      if (isCanceled())
        return;

      MassList massList = scans[i + numberofBins].getMassList(massListName);
      if (massList == null) {
        setStatus(TaskStatus.ERROR);
        setErrorMessage("Scan " + dataFile + " #" + scans[i + numberofBins].getScanNumber()
            + " does not have a mass list " + massListName);
        return;
      }

      DataPoint mzValues[] = massList.getDataPoints();

      if (mzValues == null) {
        setStatus(TaskStatus.ERROR);
        setErrorMessage("Mass list " + massListName + " does not contain m/z values for scan #"
            + scans[i].getScanNumber() + " of file " + dataFile);
        return;
      }

      massConnector.addScan(scans[i + numberofBins].getScanNumber(), mzValues);
      processedScans++;
    }

    chromatograms = massConnector.finishChromatograms();

    // Sort the final chromatograms by m/z
    Arrays.sort(chromatograms, new PeakSorter(SortingProperty.MZ, SortingDirection.Ascending));

    // Add the chromatograms to the new peak list
    for (Feature finishedPeak : chromatograms) {
      SimplePeakListRow newRow = new SimplePeakListRow(newPeakID);
      newPeakID++;
      newRow.addPeak(dataFile, finishedPeak);
      newPeakList.addRow(newRow);
    }

    // Add new peaklist to the project
    project.addPeakList(newPeakList);

    // Add quality parameters to peaks
    QualityParameters.calculateQualityParameters(newPeakList);

    setStatus(TaskStatus.FINISHED);

    logger.info("Finished chromatogram builder on " + dataFile);

  }

  private int getNumberOfBins(Scan scans[]) {
    int numberOfBins = 0;
    double scanRetentionTime = scans[0].getRetentionTime();
    for (int i = 0; i < scans.length; i++) {
      if (scanRetentionTime == scans[i].getRetentionTime()) {
        numberOfBins++;
      } else {
        break;
      }
    }
    return numberOfBins;
  }
}

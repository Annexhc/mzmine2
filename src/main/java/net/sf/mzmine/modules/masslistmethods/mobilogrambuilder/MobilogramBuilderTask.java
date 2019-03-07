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

package net.sf.mzmine.modules.masslistmethods.mobilogrambuilder;

import java.util.Arrays;
import java.util.logging.Logger;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.IMSFeature;
import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.MassList;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.datamodel.impl.SimpleMobilogramList;
import net.sf.mzmine.datamodel.impl.SimpleMobilogramListRow;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.parametertypes.selectors.ScanSelection;
import net.sf.mzmine.parameters.parametertypes.tolerances.MZTolerance;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;
import net.sf.mzmine.util.MobilogramSorter;
import net.sf.mzmine.util.SortingDirection;
import net.sf.mzmine.util.SortingProperty;

public class MobilogramBuilderTask extends AbstractTask {

  private Logger logger = Logger.getLogger(this.getClass().getName());

  private MZmineProject project;
  private RawDataFile dataFile;

  // scan counter
  private int processedScans = 0, totalScans;
  private ScanSelection scanSelection;
  private int newMobilogramID = 1;
  private Scan[] scans;

  // User parameters
  private String suffix, massListName;
  private MZTolerance mzTolerance;
  private double minimumTimeSpan, minimumHeight;
  private int minimumMobilitySpan;

  private SimpleMobilogramList newMobilogramList;

  /**
   * @param dataFile
   * @param parameters
   */
  public MobilogramBuilderTask(MZmineProject project, RawDataFile dataFile,
      ParameterSet parameters) {

    this.project = project;
    this.dataFile = dataFile;
    this.scanSelection =
        parameters.getParameter(MobilogramBuilderParameters.scanSelection).getValue();
    this.massListName = parameters.getParameter(MobilogramBuilderParameters.massList).getValue();
    this.mzTolerance = parameters.getParameter(MobilogramBuilderParameters.mzTolerance).getValue();
    this.minimumTimeSpan =
        parameters.getParameter(MobilogramBuilderParameters.minimumTimeSpan).getValue();
    this.minimumMobilitySpan =
        parameters.getParameter(MobilogramBuilderParameters.minimumMobilitySpan).getValue();
    this.minimumHeight =
        parameters.getParameter(MobilogramBuilderParameters.minimumHeight).getValue();

    this.suffix = parameters.getParameter(MobilogramBuilderParameters.suffix).getValue();

  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
   */
  @Override
  public String getTaskDescription() {
    return "Detecting mobilograms in " + dataFile;
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

    logger.info("Started mobilogram builder IMS on " + dataFile);
    scans = scanSelection.getMatchingScans(dataFile);

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
    newMobilogramList = new SimpleMobilogramList(dataFile + " " + suffix, dataFile);

    int numberofBins = getNumberOfBins(scans);

    Mobilogram[] mobilograms;
    HighestDataPointConnectorIMS massConnector = new HighestDataPointConnectorIMS(dataFile,
        allScanNumbers, minimumTimeSpan, minimumHeight, mzTolerance, numberofBins);

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

    mobilograms = massConnector.finishMobilograms();

    // Sort the final mobilograms by m/z
    Arrays.sort(mobilograms, new MobilogramSorter(SortingProperty.MZ, SortingDirection.Ascending));

    // Add the mobilograms to the new mobilogram list
    for (IMSFeature finishedMobilogram : mobilograms) {
      SimpleMobilogramListRow newRow = new SimpleMobilogramListRow(newMobilogramID);
      newMobilogramID++;
      newRow.addMobilogram(dataFile, finishedMobilogram);
      newMobilogramList.addRow(newRow);
    }

    // Add new peaklist to the project
    project.addMobilogramList(newMobilogramList);

    setStatus(TaskStatus.FINISHED);

    logger.info("Finished mobilogram builder on " + dataFile);

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

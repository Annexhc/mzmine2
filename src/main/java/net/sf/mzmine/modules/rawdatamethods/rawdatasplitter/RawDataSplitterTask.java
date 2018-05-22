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

package net.sf.mzmine.modules.rawdatamethods.rawdatasplitter;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.RawDataFileWriter;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;

public class RawDataSplitterTask extends AbstractTask {


  private final MZmineProject project;
  private Logger logger = Logger.getLogger(this.getClass().getName());
  private final RawDataFile dataFile;

  // scan counter
  private int processedScans = 0, totalScans = 0;

  // User parameters
  private String suffix;
  private int numberOfSplits;
  private int[] scanNumbers;
  private RawDataFileSplitPosition[] rawDataFileSplitPositions;

  /**
   * @param dataFile
   * @param parameters
   */
  public RawDataSplitterTask(MZmineProject project, RawDataFile dataFile, ParameterSet parameters) {

    this.dataFile = dataFile;

    this.project = project;

    this.suffix = parameters.getParameter(RawDataSplitterParameters.suffix).getValue();

    this.numberOfSplits = parameters
        .getParameter(RawDataSplitterParameters.rawDataFileSplitPositions).getValue().length;

    this.rawDataFileSplitPositions =
        parameters.getParameter(RawDataSplitterParameters.rawDataFileSplitPositions).getChoices();

  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
   */
  public String getTaskDescription() {
    return "Splitting raw file " + dataFile;
  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getFinishedPercentage()
   */
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
  public void run() {

    setStatus(TaskStatus.PROCESSING);

    logger.info("Started splitting raw file " + dataFile);

    scanNumbers = dataFile.getScanNumbers(1);
    totalScans = scanNumbers.length;

    // sort split positions
    double[] sortedSplitPositions = sortSplitPositions(rawDataFileSplitPositions);

    // determine start split position
    double startSplitPosition = 0.0;
    for (int i = 0; i < numberOfSplits; i++) {
      if (i - 1 >= 0) {
        startSplitPosition = sortedSplitPositions[i - 1];
      }
      double endSplitPosition = sortedSplitPositions[i];
      newSplittedRawDataFile(dataFile, startSplitPosition, endSplitPosition);
    }

    processedScans++;

    setStatus(TaskStatus.FINISHED);

    logger.info("Finished splitting raw file " + dataFile);
  }

  private RawDataFile newSplittedRawDataFile(RawDataFile data, double startSplitPosition,
      double endSplitPosition) {

    RawDataFile newRawDataFile = null;

    RawDataFileWriter newRawDataFileWriter = null;

    try {
      newRawDataFileWriter =
          MZmineCore.createNewFile(dataFile.getName() + "splitted@RT" + endSplitPosition + suffix);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // Get start split position depending on last split
    Range<Double> rtRange = Range.closed(startSplitPosition, endSplitPosition);
    dataFile.getScanNumbers(1, rtRange);
    for (int i = 0; i < dataFile.getScanNumbers(1, rtRange).length; i++) {
      System.out.println(dataFile.getScanNumbers(1, rtRange)[i]);
    }
    // Loop through all scans
    for (int i = 0; i < dataFile.getScanNumbers(1, rtRange).length; i++) {
      Scan scan = dataFile.getScan(dataFile.getScanNumbers(1, rtRange)[0] + i);
      try {
        newRawDataFileWriter.addScan(scan);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    // Finalize writing
    try {
      newRawDataFile = newRawDataFileWriter.finishWriting();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // Add the newly created file to the project
    project.addFile(newRawDataFile);
    return newRawDataFile;
  }

  private double[] sortSplitPositions(RawDataFileSplitPosition[] rawDataFileSplitPosition) {
    double[] sortedSplitPositionRTs = new double[rawDataFileSplitPosition.length];

    for (int i = 0; i < rawDataFileSplitPosition.length; i++) {
      sortedSplitPositionRTs[i] = rawDataFileSplitPosition[i].getSplitPosition();
    }
    Arrays.sort(sortedSplitPositionRTs, 0, sortedSplitPositionRTs.length);
    return sortedSplitPositionRTs;
  }

}

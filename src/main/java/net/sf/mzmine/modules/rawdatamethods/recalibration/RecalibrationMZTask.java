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

package net.sf.mzmine.modules.rawdatamethods.recalibration;

import java.io.IOException;
import java.util.logging.Logger;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.RawDataFileWriter;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.datamodel.impl.SimpleDataPoint;
import net.sf.mzmine.datamodel.impl.SimpleScan;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;

public class RecalibrationMZTask extends AbstractTask {

  private Logger logger = Logger.getLogger(this.getClass().getName());

  private final MZmineProject project;
  private final RawDataFile dataFile;

  // scan counter
  private int processedScans = 0, totalScans;
  private int[] scanNumbers;

  // User parameters
  private String suffix;
  private double mzDiff;
  private String mzDiffType;
  private boolean removeOriginal;
  private RawDataFile newRDF = null;

  /**
   * @param dataFile
   * @param parameters
   */
  public RecalibrationMZTask(MZmineProject project, RawDataFile dataFile, ParameterSet parameters) {

    this.project = project;
    this.dataFile = dataFile;

    this.suffix = parameters.getParameter(RecalibrationMZParameters.suffix).getValue();
    this.mzDiff = parameters.getParameter(RecalibrationMZParameters.mzDiff).getValue();
    this.mzDiffType = parameters.getParameter(RecalibrationMZParameters.mzDiffType).getValue();
    this.removeOriginal = parameters.getParameter(RecalibrationMZParameters.removeOld).getValue();

  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
   */
  public String getTaskDescription() {
    return "Normalize m/z values in " + dataFile;
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

    logger.info("Started m/z recalibration on " + dataFile);

    scanNumbers = dataFile.getScanNumbers(1);
    totalScans = scanNumbers.length;

    RawDataFileWriter newRDFW = null;
    try {
      newRDFW = MZmineCore.createNewFile(dataFile.getName() + ' ' + suffix);

      // Loop through all scans
      for (int i = 0; i < totalScans; i++) {

        if (isCanceled())
          return;

        Scan scan = dataFile.getScan(scanNumbers[i]);
        final SimpleScan newScan = new SimpleScan(scan);
        DataPoint[] oldDPs = scan.getDataPoints();
        DataPoint[] newDPs = new DataPoint[scan.getNumberOfDataPoints()];

        // Loop through every data point
        for (int j = 0; j < scan.getNumberOfDataPoints(); j++) {
          if (mzDiffType.equals("aboslute")) {
            newDPs[j] = new SimpleDataPoint(oldDPs[j].getMZ() + mzDiff, oldDPs[j].getIntensity());
          }
          if (mzDiffType.equals("relative ppm")) {
            double dpSpecificMZDiff = oldDPs[j].getMZ() / 1000000 * mzDiff;
            newDPs[j] =
                new SimpleDataPoint(oldDPs[j].getMZ() + dpSpecificMZDiff, oldDPs[j].getIntensity());
          }
        }

        newScan.setDataPoints(newDPs);

        // Copy meta data to new scan
        newScan.setMSLevel(scan.getMSLevel());

        newRDFW.addScan(newScan);
        processedScans++;
      }

      if (!isCanceled()) {

        // Finalize writing
        newRDF = newRDFW.finishWriting();

        // Add the newly created file to the project
        project.addFile(newRDF);

        // Remove the original data file if requested
        if (removeOriginal) {
          project.removeFile(dataFile);
        }

        setStatus(TaskStatus.FINISHED);


        logger.info("Finished m/z recalibration on " + dataFile + ".");

      }

    } catch (

    IOException e) {
      e.printStackTrace();
    }

  }

}

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

import java.io.IOException;
import java.util.logging.Logger;
import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.RawDataFileWriter;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.MZmineProcessingStep;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.parametertypes.selectors.ScanSelection;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;

public class RecalibrationMZTask extends AbstractTask {

  private Logger logger = Logger.getLogger(this.getClass().getName());

  private final RawDataFile dataFile;
  private final MZmineProject project;
  private RawDataFile newRawDataFile;

  // scan counter
  private int processedScans = 0, totalScans = 0;
  private ScanSelection scanSelection;
  private int[] scanNumbers;

  // Recalibration method
  private MZmineProcessingStep<RecalibrationMZMethod> recalibrationMZMethod;

  private String suffix;

  /**
   * @param dataFile
   * @param parameters
   */
  public RecalibrationMZTask(MZmineProject project, RawDataFile dataFile, ParameterSet parameters) {

    this.dataFile = dataFile;
    this.project = project;

    this.recalibrationMZMethod =
        parameters.getParameter(RecalibrationMZParameters.recalibrationMZMethod).getValue();

    this.scanSelection =
        parameters.getParameter(RecalibrationMZParameters.scanSelection).getValue();

    this.suffix = parameters.getParameter(RecalibrationMZParameters.suffix).getValue();
  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
   */
  public String getTaskDescription() {
    return "Recalibrating m/z values in " + dataFile;
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

    final Scan scans[] = scanSelection.getMatchingScans(dataFile);
    totalScans = scans.length;

    RecalibrationMZMethod method = recalibrationMZMethod.getModule();

    scanNumbers = dataFile.getScanNumbers(1);
    totalScans = scanNumbers.length;
    RawDataFileWriter newRawDataFileWriter = null;
    try {
      newRawDataFileWriter = MZmineCore.createNewFile(dataFile.getName() + suffix);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // Loop through all scans
    for (int i = 0; i < totalScans; i++) {
      Scan scan = method.getScan(scans[i], recalibrationMZMethod.getParameterSet());
      try {
        newRawDataFileWriter.addScan(scan);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    processedScans++;

    if (!isCanceled()) {

      // Finalize writing
      try {
        newRawDataFile = newRawDataFileWriter.finishWriting();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      // Add the newly created file to the project
      project.addFile(newRawDataFile);
      System.out.println(recalibrationMZMethod.getParameterSet()
          .getParameter(RecalibrationMZParameters.removeOld).getValue());
      // Remove the original data file if requested
      if (recalibrationMZMethod.getParameterSet().getParameter(RecalibrationMZParameters.removeOld)
          .getValue()) {
        project.removeFile(dataFile);
      }

      setStatus(TaskStatus.FINISHED);

      logger.info("Finished m/z recalibration on " + dataFile);
    }
  }
}

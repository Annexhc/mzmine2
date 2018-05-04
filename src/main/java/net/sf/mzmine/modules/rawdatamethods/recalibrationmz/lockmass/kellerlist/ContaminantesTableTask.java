package net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass.kellerlist;

import java.util.logging.Logger;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;

public class ContaminantesTableTask extends AbstractTask {

  private Logger logger = Logger.getLogger(this.getClass().getName());

  private ParameterSet parameters;

  // scan counter
  private int processedScans = 0, totalScans = 761;

  /**
   * @param dataFile
   * @param parameters
   */
  public ContaminantesTableTask(ParameterSet parameters) {

    this.parameters = parameters;

  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
   */
  public String getTaskDescription() {
    return "Building XICs for contaminantes tables";
  }

  /**
   * @see net.sf.mzmine.taskcontrol.Task#getFinishedPercentage()
   */
  public double getFinishedPercentage() {
    // if (totalScans == 0)
    // return 0;
    // else
    return (double) processedScans / totalScans;
  }

  /**
   * @see Runnable#run()
   */
  public void run() {
    setStatus(TaskStatus.PROCESSING);
    // Task canceled?
    if (isCanceled())
      return;

    ContaminantesTableFrame table = new ContaminantesTableFrame(parameters);
    table.setVisible(true);
    table.validate();

    logger.info("Finished");
    setStatus(TaskStatus.FINISHED);
  }
}


package net.sf.mzmine.modules.tools.kellerlist;

import java.awt.Window;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.selectors.RawDataFilesParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.ScanSelection;
import net.sf.mzmine.parameters.parametertypes.selectors.ScanSelectionParameter;
import net.sf.mzmine.parameters.parametertypes.tolerances.MZToleranceParameter;
import net.sf.mzmine.util.ExitCode;

public class KellerListParameters extends SimpleParameterSet {

  public static final RawDataFilesParameter dataFiles = new RawDataFilesParameter();

  public static final ScanSelectionParameter scanSelection =
      new ScanSelectionParameter(new ScanSelection(1));

  public static final MZToleranceParameter mzTolerance = new MZToleranceParameter(
      "m/z tolerance to search for contaminantes", "m/z tolerance to search for contaminantes");

  public KellerListParameters() {
    super(new Parameter[] {dataFiles, scanSelection, mzTolerance});
  }

  @Override
  public ExitCode showSetupDialog(Window parent, boolean valueCheckRequired) {

    ExitCode exitCode = super.showSetupDialog(parent, valueCheckRequired);

    if (exitCode != ExitCode.OK)
      return exitCode;

    return exitCode;

  }

}

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

import java.awt.Window;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.SplitPositionChoiceParameter;
import net.sf.mzmine.parameters.parametertypes.StringParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.RawDataFilesParameter;
import net.sf.mzmine.util.ExitCode;

public class RawDataSplitterParameters extends SimpleParameterSet {


  public static final RawDataFilesParameter dataFiles = new RawDataFilesParameter();

  public static final StringParameter suffix =
      new StringParameter("Suffix", "Add a suffix to file name");

  public static final SplitPositionChoiceParameter rawDataFileSplitPositions =
      new SplitPositionChoiceParameter("Split positions",
          "Add positions to performe a raw data split", new RawDataFileSplitPosition[0]);

  public RawDataSplitterParameters() {
    super(new Parameter[] {dataFiles, rawDataFileSplitPositions, suffix});
  }

  @Override
  public ExitCode showSetupDialog(Window parent, boolean valueCheckRequired) {

    RawDataSplitterSetupDialog dialog =
        new RawDataSplitterSetupDialog(parent, valueCheckRequired, this);
    dialog.setVisible(true);
    return dialog.getExitCode();

  }

}

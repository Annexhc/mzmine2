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

import java.awt.Window;
import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.internalstandard.InternalStandardRecalibrationMZ;
import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass.LockMassRecalibrationMZ;
import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.naive.NaiveRecalibrationMZ;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.BooleanParameter;
import net.sf.mzmine.parameters.parametertypes.ModuleComboParameter;
import net.sf.mzmine.parameters.parametertypes.StringParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.RawDataFilesParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.ScanSelection;
import net.sf.mzmine.parameters.parametertypes.selectors.ScanSelectionParameter;
import net.sf.mzmine.util.ExitCode;

public class RecalibrationMZParameters extends SimpleParameterSet {

  public static final RecalibrationMZMethod recalibrationMZMethods[] = {new NaiveRecalibrationMZ(),
      new LockMassRecalibrationMZ(), new InternalStandardRecalibrationMZ()};

  public static final RawDataFilesParameter dataFiles = new RawDataFilesParameter();

  public static final ScanSelectionParameter scanSelection =
      new ScanSelectionParameter(new ScanSelection(1));

  public static final ModuleComboParameter<RecalibrationMZMethod> recalibrationMZMethod =
      new ModuleComboParameter<RecalibrationMZMethod>("m/z Recalibration",
          "Method to use for m/z recalibration and its parameters", recalibrationMZMethods);

  public static final StringParameter suffix =
      new StringParameter("Suffix", "This string is added to filename as suffix", "recalibrated");

  public static final BooleanParameter removeOld =
      new BooleanParameter("Remove prev files", "Remove processed files to save memory.", false);

  public RecalibrationMZParameters() {
    super(new Parameter[] {dataFiles, suffix, scanSelection, recalibrationMZMethod, removeOld});
  }

  @Override
  public ExitCode showSetupDialog(Window parent, boolean valueCheckRequired) {

    ExitCode exitCode = super.showSetupDialog(parent, valueCheckRequired);

    // If the parameters are not complete, let's just stop here
    if (exitCode != ExitCode.OK)
      return exitCode;

    return exitCode;

  }

}

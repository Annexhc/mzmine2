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

package net.sf.mzmine.modules.rawdatamethods.recalibrationmz.lockmass;

import java.awt.Window;
import net.sf.mzmine.modules.rawdatamethods.recalibrationmz.RecalibrationMZSetupDialog;
import net.sf.mzmine.parameters.UserParameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.DoubleParameter;
import net.sf.mzmine.parameters.parametertypes.LockMassParameter;
import net.sf.mzmine.parameters.parametertypes.tolerances.MZToleranceParameter;
import net.sf.mzmine.util.ExitCode;

public class LockMassRecalibrationMZParameters extends SimpleParameterSet {

  public static final LockMassParameter lockMass = new LockMassParameter("Exact mass of lock mass",
      "Enter the exact mass of the lock mass", new LockMass[0]);

  public static final MZToleranceParameter mzTolerance = new MZToleranceParameter(
      "m/z tolerance to search for lock mass", "m/z tolerance to search for lock mass");

  public static final DoubleParameter noiseLevel =
      new DoubleParameter("Noise level", "Set noise level");

  public LockMassRecalibrationMZParameters() {
    super(new UserParameter[] {lockMass, noiseLevel, mzTolerance});
  }

  public ExitCode showSetupDialog(Window parent, boolean valueCheckRequired) {
    RecalibrationMZSetupDialog dialog = new RecalibrationMZSetupDialog(parent, valueCheckRequired,
        LockMassRecalibrationMZ.class, this);
    dialog.setVisible(true);
    return dialog.getExitCode();
  }

}

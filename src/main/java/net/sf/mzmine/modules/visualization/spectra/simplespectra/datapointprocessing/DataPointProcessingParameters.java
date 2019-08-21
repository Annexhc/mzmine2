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

package net.sf.mzmine.modules.visualization.spectra.simplespectra.datapointprocessing;

import net.sf.mzmine.modules.visualization.spectra.simplespectra.datapointprocessing.datamodel.customguicomponents.ProcessingParameter;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.BooleanParameter;
import net.sf.mzmine.parameters.parametertypes.HiddenParameter;

public class DataPointProcessingParameters extends SimpleParameterSet {
  /**
   * Processing
   */
  public static final HiddenParameter<BooleanParameter, Boolean> enableProcessing =
      new HiddenParameter<>(new BooleanParameter("Enable Processing", "", false));
  
  public static final ProcessingParameter processingParameters = new ProcessingParameter("Processing queues",
      "Set the modules to be executed in the processing queue.");

  public DataPointProcessingParameters() {
    super(new Parameter[] {processingParameters, enableProcessing});
  }
}

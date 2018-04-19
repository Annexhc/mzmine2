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
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.modules.visualization.tic.TICPlot;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.dialogs.ParameterSetupDialogWithChromatogramPreview;

/**
 * This class extends ParameterSetupDialog class, including a spectraPlot. This is used to preview
 * how the selected mass detector and his parameters works over the raw data file.
 */
public class RawDataSplitterSetupDialog extends ParameterSetupDialogWithChromatogramPreview {

  private static final long serialVersionUID = 1L;
  private ParameterSet parameters;

  /**
   * @param parameters
   * @param massDetectorTypeNumber
   */
  public RawDataSplitterSetupDialog(Window parent, boolean valueCheckRequired,
      Class<?> massDetectorClass, ParameterSet parameters) {

    super(parent, valueCheckRequired, parameters);

    this.parameters = parameters;
  }

  protected void loadPreview(TICPlot ticPlot, RawDataFile dataFile, Range<Double> rtRange,
      Range<Double> mzRange) {

    ticPlot.setVisible(true);
  }

}

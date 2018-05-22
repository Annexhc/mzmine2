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

package net.sf.mzmine.modules.peaklistmethods.featurecorrelation;

import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.DoubleParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.PeakListsParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.PeakSelectionParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.RawDataFilesParameter;

public class FeatureCorrelationParameters extends SimpleParameterSet {

  public static final RawDataFilesParameter dataFiles = new RawDataFilesParameter();

  public static final PeakListsParameter peakLists = new PeakListsParameter();

  public static final PeakSelectionParameter peakSelection = new PeakSelectionParameter();

  // public static final RTToleranceParameter rtTolerance = new RTToleranceParameter();

  // public static final MZToleranceParameter mzTolerance = new MZToleranceParameter("m/z
  // tolerance",
  // "Tolerance value of the m/z difference between peaks in MS/MS scans");

  public static final DoubleParameter minCoefficientOfDetermination = new DoubleParameter(
      "Min R-squared",
      "Minimum R-squared value to group features as correlated. Enter value between 0.01 and 1.00",
      MZmineCore.getConfiguration().getRTFormat());

  public FeatureCorrelationParameters() {
    super(new Parameter[] {dataFiles, peakLists, peakSelection, minCoefficientOfDetermination});
  }


}

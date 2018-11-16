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

package net.sf.mzmine.modules.visualization.massremainderanalysis;

import java.awt.Window;
import java.text.DecimalFormat;
import java.util.Arrays;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.ComboParameter;
import net.sf.mzmine.parameters.parametertypes.OptionalParameter;
import net.sf.mzmine.parameters.parametertypes.StringParameter;
import net.sf.mzmine.parameters.parametertypes.WindowSettingsParameter;
import net.sf.mzmine.parameters.parametertypes.ranges.DoubleRangeParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.PeakListsParameter;
import net.sf.mzmine.parameters.parametertypes.selectors.PeakSelectionParameter;
import net.sf.mzmine.util.ExitCode;
import net.sf.mzmine.util.PeakListRowSorter;
import net.sf.mzmine.util.SortingDirection;
import net.sf.mzmine.util.SortingProperty;

/**
 * parameters for mass remainder analysis
 * 
 * @author Ansgar Korf (ansgar.korf@uni-muenster.de)
 */
public class MassRemainderAnalysisParameters extends SimpleParameterSet {
  public static final PeakListsParameter peakList = new PeakListsParameter(1, 1);

  public static final PeakSelectionParameter selectedRows = new PeakSelectionParameter();

  public static final StringParameter yAxisMolecularUnit =
      new StringParameter("Molecular unit for y-Axis",
          "Enter a sum formula to use for mass remainder calculation, e.g. \"CH2\" ");

  public static final ComboParameter<Object> yAxisCharge =
      new ComboParameter<Object>("Y-Axis charge",
          "Select charge value for MolecularUnit. Select 1 for mass remainder analysis",
          new Object[] {1, 2, 3, 4, 5, 6, 7, 8, "auto"});

  public static final OptionalParameter<StringParameter> massOfChargeCarrier =
      new OptionalParameter<>(new StringParameter("Sum formula of charge carrier",
          "Enter a sum formula of the charge carrier, including the charge, e.g. NH4+"));

  public static final ComboParameter<String> xAxisValues =
      new ComboParameter<>("X-Axis", "Select what to display on x-Axis",
          new String[] {"m/z", "m/z*z", "(m/z-Mass of charge carrier)*z"});

  public static final OptionalParameter<StringParameter> xAxisCustomMolecularUnit =
      new OptionalParameter<>(new StringParameter("Molecular unit for x-Axis",
          "Enter a sum formula to use for mass remainder calculation to display a 2D Kendrick mass defect plot"));

  public static final ComboParameter<String> zAxisValues = new ComboParameter<>("Z-Axis",
      "Select a parameter for a third dimension, displayed as a heatmap or select none for a 2D plot",
      new String[] {"none", "Retention time", "Intensity", "Area", "Tailing factor",
          "Asymmetry factor", "FWHM", "m/z"});

  public static final OptionalParameter<StringParameter> zAxisCustomMolecularUnit =
      new OptionalParameter<>(new StringParameter("Molecular unit for z-Axis",
          "Enter a sum formula to use for mass remainder calculation in form of a heatmap"));

  public static final ComboParameter<String> zScaleType = new ComboParameter<>("Z-Axis scale",
      "Select Z-Axis scale", new String[] {"percentile", "custom"});

  public static final DoubleRangeParameter zScaleRange = new DoubleRangeParameter(
      "Range for z-Axis scale",
      "Set the range for z-Axis scale."
          + " If percentile is used for z-Axis scale type, you can remove extreme values of the scale."
          + " E. g. type 0.5 and 99.5 to ignore the 0.5 smallest and 0.5 highest values. "
          + "If you choose custom, set ranges manually "
          + "Features out of scale range are displayed in magenta",
      new DecimalFormat("##0.00"));

  public static final ComboParameter<String> paintScale = new ComboParameter<>("Heatmap style",
      "Select the style for the third dimension", new String[] {"Rainbow", "Monochrome red",
          "Monochrome green", "Monochrome yellow", "Monochrome cyan"});

  public static final WindowSettingsParameter windowSettings = new WindowSettingsParameter();

  public MassRemainderAnalysisParameters() {
    super(new Parameter[] {peakList, selectedRows, yAxisMolecularUnit, yAxisCharge,
        massOfChargeCarrier, xAxisValues, xAxisCustomMolecularUnit, zAxisValues,
        zAxisCustomMolecularUnit, zScaleType, zScaleRange, paintScale, windowSettings});
  }

  @Override
  public ExitCode showSetupDialog(Window parent, boolean valueCheckRequired) {

    PeakList selectedPeakLists[] = getParameter(peakList).getValue().getMatchingPeakLists();
    if (selectedPeakLists.length > 0) {
      PeakListRow plRows[] = selectedPeakLists[0].getRows();
      Arrays.sort(plRows, new PeakListRowSorter(SortingProperty.MZ, SortingDirection.Ascending));
    }

    return super.showSetupDialog(parent, valueCheckRequired);
  }

}

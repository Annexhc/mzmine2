package net.sf.mzmine.util;

import java.util.Comparator;
import net.sf.mzmine.datamodel.IMSFeature;
import net.sf.mzmine.datamodel.MobilogramListRow;

public class MobilogramListRowSorter implements Comparator<MobilogramListRow> {

  private SortingProperty property;
  private SortingDirection direction;

  public MobilogramListRowSorter(SortingProperty property, SortingDirection direction) {
    this.property = property;
    this.direction = direction;
  }

  @Override
  public int compare(MobilogramListRow row1, MobilogramListRow row2) {

    Double row1Value = getValue(row1);
    Double row2Value = getValue(row2);

    if (direction == SortingDirection.Ascending)
      return row1Value.compareTo(row2Value);
    else
      return row2Value.compareTo(row1Value);

  }

  private double getValue(MobilogramListRow row) {
    switch (property) {
      case Area:
        IMSFeature[] areaMobilograms = row.getMobilograms();
        double[] MobilogramAreas = new double[areaMobilograms.length];
        for (int i = 0; i < MobilogramAreas.length; i++)
          MobilogramAreas[i] = areaMobilograms[i].getArea();
        double medianArea = MathUtils.calcQuantile(MobilogramAreas, 0.5);
        return medianArea;
      case Intensity:
        IMSFeature[] intensityMobilograms = row.getMobilograms();
        double[] MobilogramIntensities = new double[intensityMobilograms.length];
        for (int i = 0; i < intensityMobilograms.length; i++)
          MobilogramIntensities[i] = intensityMobilograms[i].getArea();
        double medianIntensity = MathUtils.calcQuantile(MobilogramIntensities, 0.5);
        return medianIntensity;
      case Height:
        IMSFeature[] heightMobilograms = row.getMobilograms();
        double[] MobilogramHeights = new double[heightMobilograms.length];
        for (int i = 0; i < MobilogramHeights.length; i++)
          MobilogramHeights[i] = heightMobilograms[i].getHeight();
        double medianHeight = MathUtils.calcQuantile(MobilogramHeights, 0.5);
        return medianHeight;
      case MZ:
        return row.getAverageMZ();
      case RT:
        return row.getAverageRT();
      case ID:
        return row.getID();
      case Ccs:
        return row.getAverageCcs();
      case Mobility:
        return row.getAverageMobility();
    }

    // We should never get here, so throw exception
    throw (new IllegalStateException());
  }

}

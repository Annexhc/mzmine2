package net.sf.mzmine.util;

import java.util.Comparator;
import net.sf.mzmine.datamodel.IMSFeature;

public class MobilogramSorter implements Comparator<IMSFeature> {

  private SortingProperty property;
  private SortingDirection direction;

  public MobilogramSorter(SortingProperty property, SortingDirection direction) {
    this.property = property;
    this.direction = direction;
  }

  @Override
  public int compare(IMSFeature peak1, IMSFeature peak2) {

    Double peak1Value = getValue(peak1);
    Double peak2Value = getValue(peak2);

    if (direction == SortingDirection.Ascending)
      return peak1Value.compareTo(peak2Value);
    else
      return peak2Value.compareTo(peak1Value);

  }

  private double getValue(IMSFeature peak) {
    switch (property) {
      case Area:
        return peak.getArea();
      case Height:
        return peak.getHeight();
      case MZ:
        return peak.getMZ() + peak.getRT() / 1000000.0;
      case RT:
        return peak.getRT() + peak.getMZ() / 1000000.0;
      default:
        // We should never get here, so throw exception
        throw (new IllegalStateException());
    }

  }

}

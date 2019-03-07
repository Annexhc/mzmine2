package net.sf.mzmine.util;

import java.util.Comparator;
import net.sf.mzmine.datamodel.IMSDataPoint;

public class IMSDataPointSorter implements Comparator<IMSDataPoint> {

  private SortingProperty property;
  private SortingDirection direction;

  public IMSDataPointSorter(SortingProperty property, SortingDirection direction) {
    this.property = property;
    this.direction = direction;
  }

  public int compare(IMSDataPoint dp1, IMSDataPoint dp2) {

    int result;

    switch (property) {
      case MZ:

        result = Double.compare(dp1.getMZ(), dp2.getMZ());

        // If the data points have same m/z, we do a second comparison of
        // intensity, to ensure that this comparator is consistent with
        // equality: (compare(x, y)==0) == (x.equals(y)),
        if (result == 0)
          result = Double.compare(dp1.getIntensity(), dp2.getIntensity());

        if (direction == SortingDirection.Ascending)
          return result;
        else
          return -result;

      case Mobility:

        result = Double.compare(dp1.getMobility(), dp2.getMobility());

        // If the data points have same m/z, we do a second comparison of
        // intensity, to ensure that this comparator is consistent with
        // equality: (compare(x, y)==0) == (x.equals(y)),
        if (result == 0)
          result = Double.compare(dp1.getIntensity(), dp2.getIntensity());

        if (direction == SortingDirection.Ascending)
          return result;
        else
          return -result;

      case Intensity:
        result = Double.compare(dp1.getIntensity(), dp2.getIntensity());

        // If the data points have same intensity, we do a second comparison
        // of m/z, to ensure that this comparator is consistent with
        // equality: (compare(x, y)==0) == (x.equals(y)),
        if (result == 0)
          result = Double.compare(dp1.getMZ(), dp2.getMZ());

        if (direction == SortingDirection.Ascending)
          return result;
        else
          return -result;
      default:
        // We should never get here, so throw an exception
        throw (new IllegalStateException());
    }

  }
}

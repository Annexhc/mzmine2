package net.sf.mzmine.parameters.parametertypes.selectors;

import java.text.NumberFormat;
import com.google.common.base.Strings;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.MobilogramListRow;
import net.sf.mzmine.main.MZmineCore;

public class MobilogramSelection {

  private final Range<Integer> idRange;
  private final Range<Double> mzRange, rtRange, mobilityRange;
  private final String name;

  public MobilogramSelection(Range<Integer> idRange, Range<Double> mzRange, Range<Double> rtRange,
      Range<Double> mobilityRange, String name) {
    this.idRange = idRange;
    this.mzRange = mzRange;
    this.rtRange = rtRange;
    this.mobilityRange = mobilityRange;
    this.name = name;
  }

  public Range<Integer> getIDRange() {
    return idRange;
  }

  public Range<Double> getMZRange() {
    return mzRange;
  }

  public Range<Double> getRTRange() {
    return rtRange;
  }

  public Range<Double> getMobilityRange() {
    return mobilityRange;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    if (idRange == null && mzRange == null && rtRange == null && mobilityRange == null
        && Strings.isNullOrEmpty(name))
      return "All";
    StringBuilder sb = new StringBuilder();
    if (idRange != null) {
      if (sb.length() > 0)
        sb.append(", ");
      sb.append("ID: ");
      if (idRange.lowerEndpoint().equals(idRange.upperEndpoint()))
        sb.append(idRange.lowerEndpoint().toString());
      else
        sb.append(idRange.toString());
    }
    if (mzRange != null) {
      NumberFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();
      if (sb.length() > 0)
        sb.append(", ");
      sb.append("m/z: ");
      if (mzRange.lowerEndpoint().equals(mzRange.upperEndpoint()))
        sb.append(mzFormat.format(mzRange.lowerEndpoint()));
      else {
        sb.append(mzFormat.format(mzRange.lowerEndpoint()));
        sb.append("-");
        sb.append(mzFormat.format(mzRange.upperEndpoint()));
      }
    }
    if (rtRange != null) {
      NumberFormat rtFormat = MZmineCore.getConfiguration().getRTFormat();
      if (sb.length() > 0)
        sb.append(", ");
      sb.append("RT: ");
      if (rtRange.lowerEndpoint().equals(rtRange.upperEndpoint()))
        sb.append(rtFormat.format(rtRange.lowerEndpoint()));
      else {
        sb.append(rtFormat.format(rtRange.lowerEndpoint()));
        sb.append("-");
        sb.append(rtFormat.format(rtRange.upperEndpoint()));
      }
      sb.append(" min");
    }
    if (mobilityRange != null) {
      NumberFormat rtFormat = MZmineCore.getConfiguration().getRTFormat();
      if (sb.length() > 0)
        sb.append(", ");
      sb.append("Mobility: ");
      if (mobilityRange.lowerEndpoint().equals(mobilityRange.upperEndpoint()))
        sb.append(rtFormat.format(mobilityRange.lowerEndpoint()));
      else {
        sb.append(rtFormat.format(mobilityRange.lowerEndpoint()));
        sb.append("-");
        sb.append(rtFormat.format(mobilityRange.upperEndpoint()));
      }
      sb.append(" min");
    }
    if (!Strings.isNullOrEmpty(name)) {
      if (sb.length() > 0)
        sb.append(", ");
      sb.append("name: ");
      sb.append(name);
    }
    return sb.toString();
  }

  public boolean checkMobilogramListRow(MobilogramListRow row) {
    if ((idRange != null) && (!idRange.contains(row.getID())))
      return false;

    if ((mzRange != null) && (!mzRange.contains(row.getAverageMZ())))
      return false;

    if ((rtRange != null) && (!rtRange.contains(row.getAverageRT())))
      return false;

    if ((mobilityRange != null) && (!mobilityRange.contains(row.getAverageMobility())))
      return false;

    if (!Strings.isNullOrEmpty(name)) {
      if ((row.getPreferredMobilogramIdentity() == null)
          || (row.getPreferredMobilogramIdentity().getName() == null))
        return false;
      if (!row.getPreferredMobilogramIdentity().getName().toLowerCase()
          .contains(name.toLowerCase()))
        return false;
    }

    return true;

  }
}

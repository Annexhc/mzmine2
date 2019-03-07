package net.sf.mzmine.util;

import java.text.Format;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.IMSFeature;
import net.sf.mzmine.datamodel.IsotopePattern;
import net.sf.mzmine.datamodel.PeakIdentity;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.main.MZmineCore;

public class MobilogramUtils {

  /**
   * Common utility method to be used as Peak.toString() method in various Peak implementations
   * 
   * @param mobilogram Peak to be converted to String
   * @return String representation of the mobilogram
   */
  public static String mobilogramToString(IMSFeature mobilogram) {
    StringBuffer buf = new StringBuffer();
    Format mzFormat = MZmineCore.getConfiguration().getMZFormat();
    Format timeFormat = MZmineCore.getConfiguration().getRTFormat();
    buf.append(mzFormat.format(mobilogram.getMZ()));
    buf.append(" m/z @");
    buf.append(timeFormat.format(mobilogram.getRT()));
    buf.append(" [" + mobilogram.getDataFile().getName() + "] ,");
    buf.append(timeFormat.format(mobilogram.getMobility()));
    buf.append(" [" + mobilogram.getDataFile().getName() + "]");
    return buf.toString();
  }

  /**
   * Compares identities of two mobilogram list rows. 1) if preferred identities are available, they
   * must be same 2) if no identities are available on both rows, return true 3) otherwise all
   * identities on both rows must be same
   * 
   * @return True if identities match between rows
   * 
   */
  public static boolean compareIdentities(PeakListRow row1, PeakListRow row2) {

    if ((row1 == null) || (row2 == null))
      return false;

    // If both have preferred identity available, then compare only those
    PeakIdentity row1PreferredIdentity = row1.getPreferredPeakIdentity();
    PeakIdentity row2PreferredIdentity = row2.getPreferredPeakIdentity();
    if ((row1PreferredIdentity != null) && (row2PreferredIdentity != null)) {
      if (row1PreferredIdentity.getName().equals(row2PreferredIdentity.getName()))
        return true;
      else
        return false;
    }

    // If no identities at all for both rows, then return true
    PeakIdentity[] row1Identities = row1.getPeakIdentities();
    PeakIdentity[] row2Identities = row2.getPeakIdentities();
    if ((row1Identities.length == 0) && (row2Identities.length == 0))
      return true;

    // Otherwise compare all against all and require that each identity has
    // a matching identity on the other row
    if (row1Identities.length != row2Identities.length)
      return false;
    boolean sameID = false;
    for (PeakIdentity row1Identity : row1Identities) {
      sameID = false;
      for (PeakIdentity row2Identity : row2Identities) {
        if (row1Identity.getName().equals(row2Identity.getName())) {
          sameID = true;
          break;
        }
      }
      if (!sameID)
        break;
    }

    return sameID;
  }

  /**
   * Compare charge state of the best MS/MS precursor masses
   * 
   * @param row1 PeaklistRow 1
   * @param row2 PeakListRow 2
   * 
   * @return true, same charge state
   */
  public static boolean compareChargeState(PeakListRow row1, PeakListRow row2) {

    assert ((row1 != null) && (row2 != null));

    int firstCharge = row1.getBestPeak().getCharge();
    int secondCharge = row2.getBestPeak().getCharge();

    return (firstCharge == 0) || (secondCharge == 0) || (firstCharge == secondCharge);

  }

  /**
   * Returns true if mobilogram list row contains a compound identity matching to id
   * 
   */
  public static boolean containsIdentity(PeakListRow row, PeakIdentity id) {

    for (PeakIdentity identity : row.getPeakIdentities()) {
      if (identity.getName().equals(id.getName()))
        return true;
    }

    return false;
  }

  /**
   * Copies properties such as identification results and comments from the source row to the target
   * row.
   */
  public static void copyPeakListRowProperties(PeakListRow source, PeakListRow target) {

    // Combine the comments
    String targetComment = target.getComment();
    if ((targetComment == null) || (targetComment.trim().length() == 0)) {
      targetComment = source.getComment();
    } else {
      if ((source.getComment() != null) && (source.getComment().trim().length() > 0))
        targetComment += "; " + source.getComment();
    }
    target.setComment(targetComment);

    // Copy all mobilogram identities, if these are not already present
    for (PeakIdentity identity : source.getPeakIdentities()) {
      if (!containsIdentity(target, identity))
        target.addPeakIdentity(identity, false);
    }

    // Set the preferred identity
    target.setPreferredPeakIdentity(source.getPreferredPeakIdentity());

  }

  /**
   * Copies properties such as isotope pattern and charge from the source mobilogram to the target
   * mobilogram
   */
  public static void copyPeakProperties(IMSFeature source, IMSFeature target) {

    // Copy isotope pattern
    IsotopePattern originalPattern = source.getIsotopePattern();
    if (originalPattern != null)
      target.setIsotopePattern(originalPattern);

    // Copy charge
    int charge = source.getCharge();
    target.setCharge(charge);

  }

  /**
   * Finds a combined m/z range that covers all given mobilograms
   */
  public static Range<Double> findMZRange(IMSFeature mobilograms[]) {

    Range<Double> mzRange = null;

    for (IMSFeature p : mobilograms) {
      if (mzRange == null) {
        mzRange = p.getRawDataPointsMZRange();
      } else {
        mzRange = mzRange.span(p.getRawDataPointsMZRange());
      }
    }

    return mzRange;

  }

}

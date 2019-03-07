package net.sf.mzmine.datamodel.impl;

import java.text.Format;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.mzmine.datamodel.IMSFeature;
import net.sf.mzmine.datamodel.IsotopePattern;
import net.sf.mzmine.datamodel.MobilogramIdentity;
import net.sf.mzmine.datamodel.MobilogramInformation;
import net.sf.mzmine.datamodel.MobilogramListRow;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.util.MobilogramSorter;
import net.sf.mzmine.util.SortingDirection;
import net.sf.mzmine.util.SortingProperty;

public class SimpleMobilogramListRow implements MobilogramListRow {

  // faster than Hashtable
  private ConcurrentHashMap<RawDataFile, IMSFeature> mobilograms;
  private IMSFeature preferredMobilogram;
  private List<MobilogramIdentity> identities;
  private MobilogramIdentity preferredIdentity;
  private String comment;
  private MobilogramInformation information;
  private int myID;
  private double maxDataPointIntensity = 0;

  /**
   * These variables are used for caching the average values, so we don't need to calculate them
   * again and again
   */
  private double averageRT, averageMZ, averageMobility, averageCcs, averageHeight, averageArea;
  private int rowCharge;

  public SimpleMobilogramListRow(int myID) {
    this.myID = myID;
    mobilograms = new ConcurrentHashMap<RawDataFile, IMSFeature>();
    identities = new Vector<MobilogramIdentity>();
    information = null;
    preferredMobilogram = null;
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramListRow#getID()
   */
  @Override
  public int getID() {
    return myID;
  }

  /**
   * Return Mobilograms assigned to this row
   */
  @Override
  public IMSFeature[] getMobilograms() {
    return mobilograms.values().toArray(new IMSFeature[0]);
  }

  @Override
  public void removeMobilogram(RawDataFile file) {
    this.mobilograms.remove(file);
    calculateAverageValues();
  }

  /**
   * Returns opened raw data files with a Mobilogram on this row
   */
  @Override
  public RawDataFile[] getRawDataFiles() {
    return mobilograms.keySet().toArray(new RawDataFile[0]);
  }

  /**
   * Returns Mobilogram for given raw data file
   */
  @Override
  public IMSFeature getMobilogram(RawDataFile rawData) {
    return mobilograms.get(rawData);
  }

  @Override
  public synchronized void addMobilogram(RawDataFile rawData, IMSFeature Mobilogram) {
    if (Mobilogram == null)
      throw new IllegalArgumentException("Cannot add null Mobilogram to a Mobilogram list row");

    // ConcurrentHashMap is already synchronized
    mobilograms.put(rawData, Mobilogram);

    if (Mobilogram.getRawIMSDataPointsIntensityRange().upperEndpoint() > maxDataPointIntensity)
      maxDataPointIntensity = Mobilogram.getRawIMSDataPointsIntensityRange().upperEndpoint();
    calculateAverageValues();
  }

  @Override
  public double getAverageMZ() {
    return averageMZ;
  }

  @Override
  public double getAverageRT() {
    return averageRT;
  }

  @Override
  public double getAverageHeight() {
    return averageHeight;
  }

  @Override
  public double getAverageArea() {
    return averageArea;
  }

  @Override
  public int getRowCharge() {
    return rowCharge;
  }

  private synchronized void calculateAverageValues() {
    double rtSum = 0, mzSum = 0, mobilitySum = 0, ccsSum = 0, heightSum = 0, areaSum = 0;
    int charge = 0;
    HashSet<Integer> chargeArr = new HashSet<Integer>();
    Enumeration<IMSFeature> MobilogramEnum = mobilograms.elements();
    while (MobilogramEnum.hasMoreElements()) {
      IMSFeature p = MobilogramEnum.nextElement();
      rtSum += p.getRT();
      mzSum += p.getMZ();
      mobilitySum += p.getMobility();
      ccsSum += p.getCcs();
      heightSum += p.getHeight();
      areaSum += p.getArea();
      if (p.getCharge() > 0) {
        chargeArr.add(p.getCharge());
        charge = p.getCharge();
      }
    }
    averageRT = rtSum / mobilograms.size();
    averageMZ = mzSum / mobilograms.size();
    averageMobility = mobilitySum / mobilograms.size();
    averageCcs = ccsSum / mobilograms.size();
    averageHeight = heightSum / mobilograms.size();
    averageArea = areaSum / mobilograms.size();
    if (chargeArr.size() < 2) {
      rowCharge = charge;
    } else {
      rowCharge = 0;
    }
  }

  /**
   * Returns number of Mobilograms assigned to this row
   */
  @Override
  public int getNumberOfMobilograms() {
    return mobilograms.size();
  }

  @Override
  public String toString() {
    StringBuffer buf = new StringBuffer();
    Format mzFormat = MZmineCore.getConfiguration().getMZFormat();
    Format timeFormat = MZmineCore.getConfiguration().getRTFormat();
    buf.append("#" + myID + " ");
    buf.append(mzFormat.format(getAverageMZ()));
    buf.append(" m/z @ rt ");
    buf.append(timeFormat.format(getAverageRT()));
    buf.append(" @ mobility ");
    buf.append(timeFormat.format(getAverageMobility()));
    if (preferredIdentity != null)
      buf.append(" " + preferredIdentity.getName());
    if ((comment != null) && (comment.length() > 0))
      buf.append(" (" + comment + ")");
    return buf.toString();
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramListRow#getComment()
   */
  @Override
  public String getComment() {
    return comment;
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramListRow#setComment(java.lang.String)
   */
  @Override
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramListRow#setAverageMZ(java.lang.String)
   */
  @Override
  public void setAverageMZ(double mz) {
    this.averageMZ = mz;
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramListRow#setAverageRT(java.lang.String)
   */
  @Override
  public void setAverageRT(double rt) {
    this.averageRT = rt;
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramListRow#addCompoundIdentity(net.sf.mzmine.datamodel.MobilogramIdentity)
   */
  @Override
  public synchronized void addMobilogramIdentity(MobilogramIdentity identity, boolean preferred) {

    // Verify if exists already an identity with the same name
    for (MobilogramIdentity testId : identities) {
      if (testId.getName().equals(identity.getName())) {
        return;
      }
    }

    identities.add(identity);
    if ((preferredIdentity == null) || (preferred)) {
      setPreferredMobilogramIdentity(identity);
    }
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramListRow#addCompoundIdentity(net.sf.mzmine.datamodel.MobilogramIdentity)
   */
  @Override
  public synchronized void removeMobilogramIdentity(MobilogramIdentity identity) {
    identities.remove(identity);
    if (preferredIdentity == identity) {
      if (identities.size() > 0) {
        MobilogramIdentity[] identitiesArray = identities.toArray(new MobilogramIdentity[0]);
        setPreferredMobilogramIdentity(identitiesArray[0]);
      } else
        preferredIdentity = null;
    }
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramListRow#getMobilogramIdentities()
   */
  @Override
  public MobilogramIdentity[] getMobilogramIdentities() {
    return identities.toArray(new MobilogramIdentity[0]);
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramListRow#getPreferredMobilogramIdentity()
   */
  @Override
  public MobilogramIdentity getPreferredMobilogramIdentity() {
    return preferredIdentity;
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramListRow#setPreferredMobilogramIdentity(net.sf.mzmine.datamodel.MobilogramIdentity)
   */
  @Override
  public void setPreferredMobilogramIdentity(MobilogramIdentity identity) {

    if (identity == null)
      return;

    preferredIdentity = identity;

    if (!identities.contains(identity)) {
      identities.add(identity);
    }

  }

  @Override
  public void setMobilogramInformation(MobilogramInformation information) {
    this.information = information;
  }

  @Override
  public MobilogramInformation getMobilogramInformation() {
    return information;
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramListRow#getDataPointMaxIntensity()
   */
  @Override
  public double getDataPointMaxIntensity() {
    return maxDataPointIntensity;
  }

  @Override
  public boolean hasMobilogram(IMSFeature Mobilogram) {
    return mobilograms.containsValue(Mobilogram);
  }

  @Override
  public boolean hasMobilogram(RawDataFile file) {
    return mobilograms.containsKey(file);
  }

  /**
   * Returns the highest isotope pattern of a Mobilogram in this row
   */
  @Override
  public IsotopePattern getBestIsotopePattern() {
    IMSFeature mobilograms[] = getMobilograms();
    Arrays.sort(mobilograms,
        new MobilogramSorter(SortingProperty.Height, SortingDirection.Descending));

    for (IMSFeature mobilogram : mobilograms) {
      IsotopePattern ip = mobilogram.getIsotopePattern();
      if (ip != null)
        return ip;
    }

    return null;
  }

  /**
   * Returns the highest Mobilogram in this row
   */
  @Override
  public IMSFeature getBestMobilogram() {

    IMSFeature mobilograms[] = getMobilograms();
    Arrays.sort(mobilograms,
        new MobilogramSorter(SortingProperty.Height, SortingDirection.Descending));
    if (mobilograms.length == 0)
      return null;
    return mobilograms[0];
  }

  @Override
  public Scan getBestFragmentation() {

    Double bestTIC = 0.0;
    Scan bestScan = null;
    for (IMSFeature Mobilogram : this.getMobilograms()) {
      Double theTIC = 0.0;
      RawDataFile rawData = Mobilogram.getDataFile();
      int bestScanNumber = Mobilogram.getMostIntenseFragmentScanNumber();
      Scan theScan = rawData.getScan(bestScanNumber);
      if (theScan != null) {
        theTIC = theScan.getTIC();
      }

      if (theTIC > bestTIC) {
        bestTIC = theTIC;
        bestScan = theScan;
      }
    }
    return bestScan;
  }

  // DorresteinLab edit
  /**
   * set the ID number
   */

  @Override
  public void setID(int id) {
    myID = id;
    return;
  }
  // End DorresteinLab edit

  // Gauthier edit
  /**
   * Update average values
   */
  public void update() {
    this.calculateAverageValues();
  }
  // End Gauthier edit

  @Override
  public double getAverageMobility() {
    return averageMobility;
  }

  @Override
  public double getAverageCcs() {
    return averageCcs;
  }

  @Override
  public void setAverageMobility(double mobility) {
    this.averageMobility = mobility;
  }


}
// End DorresteinLab edit

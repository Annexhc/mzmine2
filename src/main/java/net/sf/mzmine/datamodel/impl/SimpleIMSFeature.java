package net.sf.mzmine.datamodel.impl;

import java.util.Arrays;
import javax.annotation.Nonnull;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.IMSDataPoint;
import net.sf.mzmine.datamodel.IMSFeature;
import net.sf.mzmine.datamodel.IsotopePattern;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.util.MobilogramUtils;

public class SimpleIMSFeature implements IMSFeature {

  private SimpleMobilogramInformation mobilogramInfo;
  private IMSFeatureStatus mobilogramStatus;
  private RawDataFile dataFile;

  // Scan numbers
  private int scanNumbers[];

  private IMSDataPoint dataPointsPerScan[];

  // M/Z, RT, Height and Area, FWHM, Tailing factor, Asymmetry factor
  private double mz, rt, mobility, ccs, height, area;
  private Double fwhm, tf, af;

  // Boundaries of the mobilogram raw data points
  private Range<Double> rtRange, mzRange, intensityRange;

  // Number of representative scan
  private int representativeScan;

  // Number of most intense fragment scan
  private int fragmentScanNumber;

  // Numbers of all MS2 fragment scans
  private int[] allMS2FragmentScanNumbers;

  // Isotope pattern. Null by default but can be set later by deisotoping
  // method.
  private IsotopePattern isotopePattern;
  private int charge = 0;

  /**
   * Initializes a new mobilogram using given values
   * 
   */
  public SimpleIMSFeature(RawDataFile dataFile, double MZ, double RT, double mobility, double ccs,
      double height, double area, int[] scanNumbers, IMSDataPoint[] dataPointsPerScan,
      IMSFeatureStatus mobilogramStatus, int representativeScan, int fragmentScanNumber,
      int[] allMS2FragmentScanNumbers, Range<Double> rtRange, Range<Double> mzRange,
      Range<Double> intensityRange) {

    if (dataPointsPerScan.length == 0) {
      throw new IllegalArgumentException(
          "Cannot create a SimpleMobilogram instance with no data points");
    }

    this.dataFile = dataFile;
    this.mz = MZ;
    this.rt = RT;
    this.mobility = mobility;
    this.ccs = ccs;
    this.height = height;
    this.area = area;
    this.scanNumbers = scanNumbers;
    this.mobilogramStatus = mobilogramStatus;
    this.representativeScan = representativeScan;
    this.fragmentScanNumber = fragmentScanNumber;
    this.allMS2FragmentScanNumbers = allMS2FragmentScanNumbers;
    this.rtRange = rtRange;
    this.mzRange = mzRange;
    this.intensityRange = intensityRange;
    this.dataPointsPerScan = dataPointsPerScan;
    this.fwhm = null;
    this.tf = null;
    this.af = null;
  }

  /**
   * Copy constructor
   */
  public SimpleIMSFeature(IMSFeature p) {

    this.dataFile = p.getDataFile();

    this.mz = p.getMZ();
    this.rt = p.getRT();
    this.mobility = p.getMobility();
    this.ccs = p.getCcs();
    this.height = p.getHeight();
    this.area = p.getArea();
    this.fwhm = p.getFWHM();
    this.tf = p.getTailingFactor();
    this.af = p.getAsymmetryFactor();


    this.rtRange = p.getRawIMSDataPointsRTRange();
    this.mzRange = p.getRawIMSDataPointsMZRange();
    this.intensityRange = p.getRawIMSDataPointsIntensityRange();

    this.scanNumbers = p.getScanNumbers();

    this.dataPointsPerScan = new IMSDataPoint[scanNumbers.length];

    for (int i = 0; i < scanNumbers.length; i++) {
      dataPointsPerScan[i] = p.getIMSDataPoint(scanNumbers[i]);

    }

    this.mobilogramStatus = p.getIMSFeatureStatus();

    this.representativeScan = p.getRepresentativeScanNumber();
    this.fragmentScanNumber = p.getMostIntenseFragmentScanNumber();
    this.allMS2FragmentScanNumbers = p.getAllMS2FragmentScanNumbers();

  }

  /**
   * Copy constructor
   */
  // public SimpleIMSFeature(RawDataFile dataFile, IMSFeatureStatus status,
  // io.github.msdk.datamodel.IMSFeature msdkIMSFeature) {
  //
  // this.dataFile = dataFile;
  //
  // this.mz = msdkIMSFeature.getMz();
  // this.rt = msdkIMSFeature.getRetentionTime() / 60.0;
  // this.height = msdkIMSFeature.getHeight();
  // this.area = msdkIMSFeature.getArea();
  //
  // Chromatogram msdkIMSFeatureChromatogram = msdkIMSFeature.getChromatogram();
  // final double mzValues[] = msdkIMSFeatureChromatogram.getMzValues();
  // final float rtValues[] = msdkIMSFeatureChromatogram.getRetentionTimes();
  // final float intensityValues[] = msdkIMSFeatureChromatogram.getIntensityValues();
  //
  // this.rtRange =
  // Range.closed(msdkIMSFeatureChromatogram.getRtRange().lowerEndpoint().doubleValue() / 60.0,
  // msdkIMSFeatureChromatogram.getRtRange().upperEndpoint().doubleValue() / 60.0);
  // this.mzRange = Range.encloseAll(Doubles.asList(mzValues));
  // this.intensityRange = Range.closed(0.0, msdkIMSFeature.getHeight().doubleValue());
  //
  // this.scanNumbers = new int[rtValues.length];
  // this.dataPointsPerScan = new DataPoint[scanNumbers.length];
  // for (int i = 0; i < scanNumbers.length; i++) {
  // scanNumbers[i] = RawDataFileUtils.getClosestScanNumber(dataFile, rtValues[i] / 60.0);
  // dataPointsPerScan[i] = new SimpleDataPoint(mzValues[i], intensityValues[i]);
  // }
  //
  // this.mobilogramStatus = status;
  //
  // this.representativeScan = RawDataFileUtils.getClosestScanNumber(dataFile, this.rt);
  // this.fragmentScanNumber = ScanUtils.findBestFragmentScan(dataFile, this.rtRange, this.mzRange);
  //
  // for (int i = 0; i < scanNumbers.length; i++) {
  // if (height < dataPointsPerScan[i].getIntensity()) {
  // representativeScan = scanNumbers[i];
  // }
  // }
  //
  // }

  /**
   * This method returns the status of the mobilogram
   */
  @Override
  public @Nonnull IMSFeatureStatus getIMSFeatureStatus() {
    return mobilogramStatus;
  }

  /**
   * This method returns M/Z value of the mobilogram
   */
  @Override
  public double getMZ() {
    return mz;
  }

  public void setMZ(double mz) {
    this.mz = mz;
  }

  public void setRT(double rt) {
    this.rt = rt;
  }

  /**
   * This method returns retention time of the mobilogram
   */
  @Override
  public double getRT() {
    return rt;
  }

  /**
   * This method returns the raw height of the mobilogram
   */
  @Override
  public double getHeight() {
    return height;
  }

  /**
   * @param height The height to set.
   */
  public void setHeight(double height) {
    this.height = height;

    intensityRange = Range.closed(0.0, height);
  }

  /**
   * This method returns the raw area of the mobilogram
   */
  @Override
  public double getArea() {
    return area;
  }

  /**
   * @param area The area to set.
   */
  public void setArea(double area) {
    this.area = area;
  }

  /**
   * This method returns numbers of scans that contain this mobilogram
   */
  @Override
  public @Nonnull int[] getScanNumbers() {
    return scanNumbers;
  }

  /**
   * This method returns a representative datapoint of this mobilogram in a given scan
   */
  @Override
  public IMSDataPoint getIMSDataPoint(int scanNumber) {
    int index = Arrays.binarySearch(scanNumbers, scanNumber);
    if (index < 0)
      return null;
    return dataPointsPerScan[index];
  }

  /**
   * @see net.sf.mzmine.datamodel.IMSFeature#getDataFile()
   */
  @Override
  public @Nonnull RawDataFile getDataFile() {
    return dataFile;
  }

  /**
   * @see net.sf.mzmine.datamodel.IMSFeature#setDataFile()
   */
  public void setDataFile(RawDataFile dataFile) {
    this.dataFile = dataFile;
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return MobilogramUtils.mobilogramToString(this);
  }

  /**
   * @see net.sf.mzmine.datamodel.IMSFeature#getRawDataPointsIntensityRange()
   */
  @Override
  public @Nonnull Range<Double> getRawIMSDataPointsIntensityRange() {
    return intensityRange;
  }

  /**
   * @see net.sf.mzmine.datamodel.IMSFeature#getRawDataPointsMZRange()
   */
  @Override
  public @Nonnull Range<Double> getRawIMSDataPointsMZRange() {
    return mzRange;
  }

  /**
   * @see net.sf.mzmine.datamodel.IMSFeature#getRawDataPointsRTRange()
   */
  @Override
  public @Nonnull Range<Double> getRawIMSDataPointsRTRange() {
    return rtRange;
  }

  /**
   * @see net.sf.mzmine.datamodel.IMSFeature#getRepresentativeScanNumber()
   */
  @Override
  public int getRepresentativeScanNumber() {
    return representativeScan;
  }

  @Override
  public int getMostIntenseFragmentScanNumber() {
    return fragmentScanNumber;
  }

  @Override
  public int[] getAllMS2FragmentScanNumbers() {
    return allMS2FragmentScanNumbers;
  }

  @Override
  public IsotopePattern getIsotopePattern() {
    return isotopePattern;
  }

  @Override
  public void setIsotopePattern(@Nonnull IsotopePattern isotopePattern) {
    this.isotopePattern = isotopePattern;
  }

  @Override
  public int getCharge() {
    return charge;
  }

  @Override
  public void setCharge(int charge) {
    this.charge = charge;
  }

  /**
   * This method returns the full width at half maximum (FWHM) of the mobilogram
   */
  @Override
  public Double getFWHM() {
    return fwhm;
  }

  /**
   * @param fwhm The full width at half maximum (FWHM) to set.
   */
  @Override
  public void setFWHM(Double fwhm) {
    this.fwhm = fwhm;
  }

  /**
   * This method returns the tailing factor of the mobilogram
   */
  @Override
  public Double getTailingFactor() {
    return tf;
  }

  /**
   * @param tf The tailing factor to set.
   */
  @Override
  public void setTailingFactor(Double tf) {
    this.tf = tf;
  }

  /**
   * This method returns the asymmetry factor of the mobilogram
   */
  @Override
  public Double getAsymmetryFactor() {
    return af;
  }

  /**
   * @param af The asymmetry factor to set.
   */
  @Override
  public void setAsymmetryFactor(Double af) {
    this.af = af;
  }

  // dulab Edit
  @Override
  public void outputChromToFile() {

  }

  // End dulab Edit

  @Override
  public double getMobility() {
    return mobility;
  }

  public void setMobility(double mobility) {
    this.mobility = mobility;
  }

  @Override
  public void setMobilogramInformation(SimpleMobilogramInformation MobilogramInfoIn) {
    // TODO Auto-generated method stub

  }

  @Override
  public SimpleMobilogramInformation getMobilogramInformation() {
    return mobilogramInfo;
  }

  @Override
  public double getCcs() {
    return ccs;
  }

  public void setCcs(double ccs) {
    this.ccs = ccs;
  }


}

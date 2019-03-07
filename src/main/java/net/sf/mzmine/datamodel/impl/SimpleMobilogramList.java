package net.sf.mzmine.datamodel.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.IMSFeature;
import net.sf.mzmine.datamodel.MobilogramList;
import net.sf.mzmine.datamodel.MobilogramListRow;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.desktop.impl.projecttree.MobilogramListTreeModel;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.project.impl.MZmineProjectImpl;

public class SimpleMobilogramList implements MobilogramList {
  private String name;
  private RawDataFile[] dataFiles;
  private ArrayList<MobilogramListRow> MobilogramListRows;
  private double maxDataPointIntensity = 0;
  private Vector<MobilogramListAppliedMethod> descriptionOfAppliedTasks;
  private String dateCreated;
  private Range<Double> mzRange, rtRange;

  public static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

  public SimpleMobilogramList(String name, RawDataFile dataFile) {
    this(name, new RawDataFile[] {dataFile});
  }

  public SimpleMobilogramList(String name, RawDataFile[] dataFiles) {
    if ((dataFiles == null) || (dataFiles.length == 0)) {
      throw (new IllegalArgumentException("Cannot create a Mobilogram list with no data files"));
    }
    this.name = name;
    this.dataFiles = new RawDataFile[dataFiles.length];

    RawDataFile dataFile;
    for (int i = 0; i < dataFiles.length; i++) {
      dataFile = dataFiles[i];
      this.dataFiles[i] = dataFile;
    }
    MobilogramListRows = new ArrayList<MobilogramListRow>();
    descriptionOfAppliedTasks = new Vector<MobilogramListAppliedMethod>();

    dateCreated = dateFormat.format(new Date());

  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }



  /**
   * Returns number of raw data files participating in the alignment
   */
  @Override
  public int getNumberOfRawDataFiles() {
    return dataFiles.length;
  }

  /**
   * Returns all raw data files participating in the alignment
   */
  @Override
  public RawDataFile[] getRawDataFiles() {
    return dataFiles;
  }

  @Override
  public RawDataFile getRawDataFile(int position) {
    return dataFiles[position];
  }

  /**
   * Returns number of rows in the alignment result
   */
  @Override
  public int getNumberOfRows() {
    return MobilogramListRows.size();
  }

  /**
   * Returns the Mobilogram of a given raw data file on a give row of the alignment result
   * 
   * @param row Row of the alignment result
   * @param rawDataFile Raw data file where the Mobilogram is detected/estimated
   */
  @Override
  public IMSFeature getMobilogram(int row, RawDataFile rawDataFile) {
    return MobilogramListRows.get(row).getMobilogram(rawDataFile);
  }

  /**
   * Returns all Mobilograms for a raw data file
   */
  @Override
  public IMSFeature[] getMobilograms(RawDataFile rawDataFile) {
    Vector<IMSFeature> MobilogramSet = new Vector<IMSFeature>();
    for (int row = 0; row < getNumberOfRows(); row++) {
      IMSFeature p = MobilogramListRows.get(row).getMobilogram(rawDataFile);
      if (p != null)
        MobilogramSet.add(p);
    }
    return MobilogramSet.toArray(new IMSFeature[0]);
  }

  /**
   * Returns all Mobilograms on one row
   */
  @Override
  public MobilogramListRow getRow(int row) {
    return MobilogramListRows.get(row);
  }

  @Override
  public MobilogramListRow[] getRows() {
    return MobilogramListRows.toArray(new MobilogramListRow[0]);
  }

  @Override
  public MobilogramListRow[] getRowsInsideMZRange(Range<Double> mzRange) {
    Range<Double> all = Range.all();
    return getRowsInsideScanAndMZRange(all, mzRange);
  }

  @Override
  public MobilogramListRow[] getRowsInsideScanRange(Range<Double> rtRange) {
    Range<Double> all = Range.all();
    return getRowsInsideScanAndMZRange(rtRange, all);
  }

  @Override
  public MobilogramListRow[] getRowsInsideScanAndMZRange(Range<Double> rtRange,
      Range<Double> mzRange) {
    Vector<MobilogramListRow> rowsInside = new Vector<MobilogramListRow>();

    for (MobilogramListRow row : MobilogramListRows) {
      if (rtRange.contains(row.getAverageRT()) && mzRange.contains(row.getAverageMZ()))
        rowsInside.add(row);
    }

    return rowsInside.toArray(new MobilogramListRow[0]);
  }

  @Override
  public void addRow(MobilogramListRow row) {
    List<RawDataFile> myFiles = Arrays.asList(this.getRawDataFiles());
    for (RawDataFile testFile : row.getRawDataFiles()) {
      if (!myFiles.contains(testFile))
        throw (new IllegalArgumentException(
            "Data file " + testFile + " is not in this Mobilogram list"));
    }

    MobilogramListRows.add(row);
    if (row.getDataPointMaxIntensity() > maxDataPointIntensity) {
      maxDataPointIntensity = row.getDataPointMaxIntensity();
    }

    if (mzRange == null) {
      mzRange = Range.singleton(row.getAverageMZ());
      rtRange = Range.singleton(row.getAverageRT());
    } else {
      mzRange = mzRange.span(Range.singleton(row.getAverageMZ()));
      rtRange = rtRange.span(Range.singleton(row.getAverageRT()));
    }
  }

  /**
   * Returns all Mobilograms overlapping with a retention time range
   * 
   * @param startRT Start of the retention time range
   * @param endRT End of the retention time range
   * @return
   */
  @Override
  public IMSFeature[] getMobilogramsInsideScanRange(RawDataFile file, Range<Double> rtRange) {
    Range<Double> all = Range.all();
    return getMobilogramsInsideScanAndMZRange(file, rtRange, all);
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramList#getMobilogramsInsideMZRange(double, double)
   */
  @Override
  public IMSFeature[] getMobilogramsInsideMZRange(RawDataFile file, Range<Double> mzRange) {
    Range<Double> all = Range.all();
    return getMobilogramsInsideScanAndMZRange(file, all, mzRange);
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramList#getMobilogramsInsideScanAndMZRange(double, double,
   *      double, double)
   */
  @Override
  public IMSFeature[] getMobilogramsInsideScanAndMZRange(RawDataFile file, Range<Double> rtRange,
      Range<Double> mzRange) {
    Vector<IMSFeature> MobilogramsInside = new Vector<IMSFeature>();

    IMSFeature[] Mobilograms = getMobilograms(file);
    for (IMSFeature p : Mobilograms) {
      if (rtRange.contains(p.getRT()) && mzRange.contains(p.getMZ()))
        MobilogramsInside.add(p);
    }

    return MobilogramsInside.toArray(new IMSFeature[0]);
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramList#removeRow(net.sf.mzmine.datamodel.MobilogramListRow)
   */
  @Override
  public void removeRow(MobilogramListRow row) {
    MobilogramListRows.remove(row);

    // We have to update the project tree model
    MZmineProjectImpl project =
        (MZmineProjectImpl) MZmineCore.getProjectManager().getCurrentProject();
    MobilogramListTreeModel treeModel = project.getMobilogramListTreeModel();
    treeModel.removeObject(row);

    updateMaxIntensity();
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramList#removeRow(net.sf.mzmine.datamodel.MobilogramListRow)
   */
  @Override
  public void removeRow(int rowNum) {
    removeRow(MobilogramListRows.get(rowNum));
  }

  private void updateMaxIntensity() {
    maxDataPointIntensity = 0;
    mzRange = null;
    rtRange = null;
    for (MobilogramListRow MobilogramListRow : MobilogramListRows) {
      if (MobilogramListRow.getDataPointMaxIntensity() > maxDataPointIntensity)
        maxDataPointIntensity = MobilogramListRow.getDataPointMaxIntensity();

      if (mzRange == null) {
        mzRange = Range.singleton(MobilogramListRow.getAverageMZ());
        rtRange = Range.singleton(MobilogramListRow.getAverageRT());
      } else {
        mzRange = mzRange.span(Range.singleton(MobilogramListRow.getAverageMZ()));
        rtRange = rtRange.span(Range.singleton(MobilogramListRow.getAverageRT()));
      }
    }
  }

  @Override
  public Stream<MobilogramListRow> stream() {
    return MobilogramListRows.stream();
  }

  @Override
  public Stream<MobilogramListRow> parallelStream() {
    return MobilogramListRows.parallelStream();
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramList#getMobilogramRowNum(net.sf.mzmine.datamodel.IMSFeature)
   */
  @Override
  public int getMobilogramRowNum(IMSFeature Mobilogram) {

    MobilogramListRow rows[] = getRows();

    for (int i = 0; i < rows.length; i++) {
      if (rows[i].hasMobilogram(Mobilogram))
        return i;
    }

    return -1;
  }

  /**
   * @see net.sf.mzmine.datamodel.MobilogramList#getDataPointMaxIntensity()
   */
  @Override
  public double getDataPointMaxIntensity() {
    return maxDataPointIntensity;
  }

  @Override
  public boolean hasRawDataFile(RawDataFile hasFile) {
    return Arrays.asList(dataFiles).contains(hasFile);
  }

  @Override
  public MobilogramListRow getMobilogramRow(IMSFeature Mobilogram) {
    MobilogramListRow rows[] = getRows();

    for (int i = 0; i < rows.length; i++) {
      if (rows[i].hasMobilogram(Mobilogram))
        return rows[i];
    }

    return null;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void addDescriptionOfAppliedTask(MobilogramListAppliedMethod appliedMethod) {
    descriptionOfAppliedTasks.add(appliedMethod);
  }

  @Override
  public MobilogramListAppliedMethod[] getAppliedMethods() {
    return descriptionOfAppliedTasks.toArray(new MobilogramListAppliedMethod[0]);
  }

  public String getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(String date) {
    this.dateCreated = date;
  }

  @Override
  public Range<Double> getRowsMZRange() {
    updateMaxIntensity(); // Update range before returning value
    return mzRange;
  }

  @Override
  public Range<Double> getRowsRTRange() {
    updateMaxIntensity(); // Update range before returning value
    return rtRange;
  }

  @Override
  public Range<Double> getRowsMobilityRange() {
    // TODO Auto-generated method stub
    return null;
  }

}

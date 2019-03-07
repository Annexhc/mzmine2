package net.sf.mzmine.modules.visualization.mobilogramlisttable.table;

import java.util.ArrayList;
import org.jfree.data.xy.AbstractXYDataset;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;

/**
 * XYZataset for IMS plots
 * 
 * @author Ansgar Korf (ansgar.korf@uni-muenster.de)
 */
class ImsVisualizerXYDataset extends AbstractXYDataset {

  private static final long serialVersionUID = 1L;

  private RawDataFile dataFiles[];
  private Scan scans[];
  private Range<Double> mzRange;
  ArrayList<Double> retentionTimes;
  private double[] xValues;
  private double[] yValues;

  public ImsVisualizerXYDataset(RawDataFile dataFiles[], Scan scans[], Range<Double> mzRange) {

    // Calc xValues retention time
    retentionTimes = new ArrayList<Double>();
    for (int i = 1601; i < scans.length; i++) {
      if (i == 1601) {
        retentionTimes.add(scans[i].getRetentionTime());
      } else if (scans[i].getRetentionTime() != scans[i - 1].getRetentionTime()) {
        retentionTimes.add(scans[i].getRetentionTime());
      }
    }

    xValues = new double[retentionTimes.size()];
    for (int i = 0; i < retentionTimes.size(); i++) {
      xValues[i] = retentionTimes.get(i);
    }

    // Calc yValues Intensity
    yValues = new double[retentionTimes.size()];
    for (int k = 0; k < retentionTimes.size(); k++) {
      for (int i = 1601; i < scans.length; i++) {
        if (scans[i].getRetentionTime() == retentionTimes.get(k)) {
          DataPoint dataPoints[] = scans[i].getDataPoints();
          for (int j = 0; j < dataPoints.length; j++) {
            if (mzRange.contains(dataPoints[j].getMZ())) {
              yValues[k] = yValues[k] + dataPoints[j].getIntensity();
            }
          }
        } else {
          continue;
        }
      }
    }
  }

  @Override
  public int getItemCount(int series) {
    return retentionTimes.size();
  }

  @Override
  public Number getX(int series, int item) {
    return xValues[item];
  }

  @Override
  public Number getY(int series, int item) {
    return yValues[item];
  }

  @Override
  public int getSeriesCount() {
    return 1;
  }

  public Comparable<?> getRowKey(int item) {
    return "XIC";
  }

  @Override
  public Comparable<?> getSeriesKey(int series) {
    return getRowKey(series);
  }

}

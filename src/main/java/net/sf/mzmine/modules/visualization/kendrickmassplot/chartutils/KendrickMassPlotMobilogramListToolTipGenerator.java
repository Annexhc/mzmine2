package net.sf.mzmine.modules.visualization.kendrickmassplot.chartutils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.jfree.chart.labels.XYZToolTipGenerator;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;
import net.sf.mzmine.datamodel.MobilogramListRow;

public class KendrickMassPlotMobilogramListToolTipGenerator
    implements XYZToolTipGenerator, PublicCloneable {

  private String xAxisLabel, yAxisLabel, zAxisLabel;
  private NumberFormat numberFormatX = new DecimalFormat("####0.0000");
  private NumberFormat numberFormatY = new DecimalFormat("0.000");
  private MobilogramListRow mobilogramListRows[];
  private String featureIdentity;

  public KendrickMassPlotMobilogramListToolTipGenerator(String xAxisLabel, String yAxisLabel,
      String zAxisLabel, MobilogramListRow rows[]) {
    super();
    this.xAxisLabel = xAxisLabel;
    this.yAxisLabel = yAxisLabel;
    this.zAxisLabel = zAxisLabel;
    this.mobilogramListRows = rows;
  }

  @Override
  public String generateToolTip(XYZDataset dataset, int series, int item) {
    if (mobilogramListRows[item].getPreferredMobilogramIdentity() != null) {
      featureIdentity = mobilogramListRows[item].getPreferredMobilogramIdentity().getName();
      return String.valueOf(featureIdentity + "\n" + xAxisLabel + ": "
          + numberFormatX.format(dataset.getXValue(series, item)) + " " + yAxisLabel + ": "
          + numberFormatY.format(dataset.getYValue(series, item)) + " " + zAxisLabel + ": "
          + numberFormatY.format(dataset.getZValue(series, item)));
    } else {
      return String
          .valueOf(xAxisLabel + ": " + numberFormatX.format(dataset.getXValue(series, item)) + " "
              + yAxisLabel + ": " + numberFormatY.format(dataset.getYValue(series, item)) + " "
              + zAxisLabel + ": " + numberFormatY.format(dataset.getZValue(series, item)));
    }
  }

  @Override
  public String generateToolTip(XYDataset dataset, int series, int item) {
    if (mobilogramListRows[item].getPreferredMobilogramIdentity() != null) {
      featureIdentity = mobilogramListRows[item].getPreferredMobilogramIdentity().getName();
      return String.valueOf(featureIdentity + "\n" + xAxisLabel + ": "
          + numberFormatX.format(dataset.getXValue(series, item)) + " " + yAxisLabel + ": "
          + numberFormatY.format(dataset.getYValue(series, item)));
    } else {
      return String
          .valueOf(xAxisLabel + ": " + numberFormatX.format(dataset.getXValue(series, item)) + " "
              + yAxisLabel + ": " + numberFormatY.format(dataset.getYValue(series, item)));
    }
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}

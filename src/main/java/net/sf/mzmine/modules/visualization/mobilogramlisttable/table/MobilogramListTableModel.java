/*
 * Copyright 2006-2018 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 2; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package net.sf.mzmine.modules.visualization.mobilogramlisttable.table;

import javax.swing.table.AbstractTableModel;
import net.sf.mzmine.datamodel.IMSFeature;
import net.sf.mzmine.datamodel.IMSFeature.IMSFeatureStatus;
import net.sf.mzmine.datamodel.MobilogramIdentity;
import net.sf.mzmine.datamodel.MobilogramList;
import net.sf.mzmine.datamodel.MobilogramListRow;
import net.sf.mzmine.datamodel.RawDataFile;

public class MobilogramListTableModel extends AbstractTableModel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private MobilogramList mobilogramList;

  /**
   * Constructor, assign given dataset to this table
   */
  public MobilogramListTableModel(MobilogramList mobilogramList) {
    this.mobilogramList = mobilogramList;

  }

  @Override
  public int getColumnCount() {
    return CommonColumnType.values().length
        + mobilogramList.getNumberOfRawDataFiles() * DataFileColumnType.values().length;
  }

  @Override
  public int getRowCount() {
    return mobilogramList.getNumberOfRows();
  }

  @Override
  public String getColumnName(int col) {
    return "column" + col;
  }

  @Override
  public Class<?> getColumnClass(int col) {

    if (isCommonColumn(col)) {
      CommonColumnType commonColumn = getCommonColumn(col);
      return commonColumn.getColumnClass();
    } else {
      DataFileColumnType dataFileColumn = getDataFileColumn(col);
      return dataFileColumn.getColumnClass();
    }

  }

  /**
   * This method returns the value at given coordinates of the dataset or null if it is a missing
   * value
   */

  @Override
  public Object getValueAt(int row, int col) {

    MobilogramListRow mobilogramListRow = mobilogramList.getRow(row);

    if (isCommonColumn(col)) {
      CommonColumnType commonColumn = getCommonColumn(col);

      switch (commonColumn) {
        case ROWID:
          return new Integer(mobilogramListRow.getID());
        case AVERAGEMZ:
          return new Double(mobilogramListRow.getAverageMZ());
        case AVERAGERT:
          if (mobilogramListRow.getAverageRT() <= 0)
            return null;
          return new Double(mobilogramListRow.getAverageRT());
        case AVERAGEMOBILITY:
          if (mobilogramListRow.getAverageMobility() <= 0)
            return null;
          return new Double(mobilogramListRow.getAverageMobility());
        case AVERAGECCS:
          if (mobilogramListRow.getAverageCcs() <= 0)
            return null;
          return new Double(mobilogramListRow.getAverageCcs());
        case COMMENT:
          return mobilogramListRow.getComment();
        case IDENTITY:
          return mobilogramListRow.getPreferredMobilogramIdentity();
        case PEAKSHAPE:
          return mobilogramListRow;
      }

    } else {

      DataFileColumnType dataFileColumn = getDataFileColumn(col);
      RawDataFile file = getColumnDataFile(col);
      IMSFeature mobilogram = mobilogramListRow.getMobilogram(file);

      if (mobilogram == null) {
        if (dataFileColumn == DataFileColumnType.STATUS)
          return IMSFeatureStatus.UNKNOWN;
        else
          return null;
      }

      switch (dataFileColumn) {
        case STATUS:
          return mobilogram.getIMSFeatureStatus();
        case PEAKSHAPE:
          return mobilogram;
        case MZ:
          return mobilogram.getMZ();
        case RT:
          if (mobilogram.getRT() <= 0)
            return null;
          return mobilogram.getRT();
        case MOBILITY:
          if (mobilogram.getMobility() <= 0)
            return null;
          return mobilogram.getMobility();
        case CCS:
          if (mobilogram.getCcs() <= 0)
            return null;
          return mobilogram.getCcs();
        case HEIGHT:
          if (mobilogram.getHeight() <= 0)
            return null;
          return mobilogram.getHeight();
        case AREA:
          return mobilogram.getArea();
        case DURATION:
          double rtLen = mobilogram.getRawIMSDataPointsRTRange().upperEndpoint()
              - mobilogram.getRawIMSDataPointsRTRange().lowerEndpoint();
          return rtLen;
        case CHARGE:
          if (mobilogram.getCharge() <= 0)
            return null;
          return new Integer(mobilogram.getCharge());
        case RT_START:
          return mobilogram.getRawIMSDataPointsRTRange().lowerEndpoint();
        case RT_END:
          return mobilogram.getRawIMSDataPointsRTRange().upperEndpoint();
        case DATAPOINTS:
          return mobilogram.getScanNumbers().length;
        case FWHM:
          return mobilogram.getFWHM();
        case TF:
          return mobilogram.getTailingFactor();
        case AF:
          return mobilogram.getAsymmetryFactor();
      }

    }

    return null;

  }

  @Override
  public boolean isCellEditable(int row, int col) {

    CommonColumnType columnType = getCommonColumn(col);

    return ((columnType == CommonColumnType.COMMENT) || (columnType == CommonColumnType.IDENTITY));

  }

  @Override
  public void setValueAt(Object value, int row, int col) {

    CommonColumnType columnType = getCommonColumn(col);

    MobilogramListRow mobilogramListRow = mobilogramList.getRow(row);

    if (columnType == CommonColumnType.COMMENT) {
      mobilogramListRow.setComment((String) value);
    }

    if (columnType == CommonColumnType.IDENTITY) {
      if (value instanceof MobilogramIdentity)
        mobilogramListRow.setPreferredMobilogramIdentity((MobilogramIdentity) value);
    }

  }

  boolean isCommonColumn(int col) {
    return col < CommonColumnType.values().length;
  }

  CommonColumnType getCommonColumn(int col) {

    CommonColumnType commonColumns[] = CommonColumnType.values();

    if (col < commonColumns.length)
      return commonColumns[col];

    return null;

  }

  DataFileColumnType getDataFileColumn(int col) {

    CommonColumnType commonColumns[] = CommonColumnType.values();
    DataFileColumnType dataFileColumns[] = DataFileColumnType.values();

    if (col < commonColumns.length)
      return null;

    // substract common columns from the index
    col -= commonColumns.length;

    // divide by number of data file columns
    col %= dataFileColumns.length;

    return dataFileColumns[col];

  }

  RawDataFile getColumnDataFile(int col) {

    CommonColumnType commonColumns[] = CommonColumnType.values();
    DataFileColumnType dataFileColumns[] = DataFileColumnType.values();

    if (col < commonColumns.length)
      return null;

    // substract common columns from the index
    col -= commonColumns.length;

    // divide by number of data file columns
    int fileIndex = (col / dataFileColumns.length);

    return mobilogramList.getRawDataFile(fileIndex);

  }

}

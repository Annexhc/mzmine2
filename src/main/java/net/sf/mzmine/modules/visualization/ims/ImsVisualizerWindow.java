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

package net.sf.mzmine.modules.visualization.ims;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import net.sf.mzmine.desktop.impl.WindowsMenu;

/**
 * Window for IMS visualization
 * 
 * @author Ansgar Korf (ansgar.korf@uni-muenster.de)
 */
public class ImsVisualizerWindow extends JFrame implements ActionListener {

  private static final long serialVersionUID = 1L;
  private ImsVisualizerToolBar toolBar;
  private JFreeChart chart;

  public ImsVisualizerWindow(JFreeChart chart) {

    this.chart = chart;
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setBackground(Color.white);

    // Add toolbar
    toolBar = new ImsVisualizerToolBar(this);
    add(toolBar, BorderLayout.EAST);

    // Add the Windows menu
    JMenuBar menuBar = new JMenuBar();
    menuBar.add(new WindowsMenu());
    setJMenuBar(menuBar);

    pack();
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    String command = event.getActionCommand();
    CombinedDomainXYPlot plot = (CombinedDomainXYPlot) chart.getXYPlot();

    @SuppressWarnings("unchecked")
    List<XYPlot> subPlots = plot.getSubplots();

    if (command.equals("TOGGLE_BACK_COLOR")) {
      if (subPlots.get(0).getBackgroundPaint() == Color.WHITE) {
        subPlots.get(0).setBackgroundPaint(Color.BLACK);
        subPlots.get(0).getRenderer().setSeriesPaint(0, Color.WHITE);
      } else {
        subPlots.get(0).setBackgroundPaint(Color.WHITE);
        subPlots.get(0).getRenderer().setSeriesPaint(0, Color.BLACK);
      }
    }
    for (XYPlot xyPlot : subPlots) {
      if (command.equals("TOGGLE_GRID")) {
        if (xyPlot.getBackgroundPaint() == Color.BLACK
            && xyPlot.isDomainGridlinesVisible() == false) {
          xyPlot.setDomainGridlinesVisible(true);
          xyPlot.setRangeGridlinesVisible(true);
          xyPlot.setDomainGridlinePaint(Color.WHITE);
          xyPlot.setRangeGridlinePaint(Color.WHITE);
        } else if (xyPlot.getBackgroundPaint() == Color.WHITE
            && xyPlot.isDomainGridlinesVisible() == false) {
          xyPlot.setDomainGridlinesVisible(true);
          xyPlot.setRangeGridlinesVisible(true);
          xyPlot.setDomainGridlinePaint(Color.BLACK);
          xyPlot.setRangeGridlinePaint(Color.BLACK);
        } else if (xyPlot.getBackgroundPaint() == Color.BLACK
            && xyPlot.isDomainGridlinesVisible() == true) {
          xyPlot.setDomainGridlinesVisible(false);
          xyPlot.setRangeGridlinesVisible(false);
          xyPlot.setDomainGridlinePaint(Color.BLACK);
          xyPlot.setRangeGridlinePaint(Color.BLACK);
        } else if (xyPlot.getBackgroundPaint() == Color.WHITE
            && xyPlot.isDomainGridlinesVisible() == true) {
          xyPlot.setDomainGridlinesVisible(false);
          xyPlot.setRangeGridlinesVisible(false);
          xyPlot.setDomainGridlinePaint(Color.WHITE);
          xyPlot.setRangeGridlinePaint(Color.WHITE);
        }
      }
    }

    if (command.equals("TOGGLE_ALPHA")) {
      if (subPlots.get(1).getForegroundAlpha() == 1.0f) {
        subPlots.get(1).setForegroundAlpha(0.1f);
      } else if (subPlots.get(1).getForegroundAlpha() == 0.1f) {
        subPlots.get(1).setForegroundAlpha(1.0f);
      }
    }

    if (command.equals("TOGGLE_LEGEND")) {
      if (chart.getSubtitle(1).isVisible() == true) {
        chart.getSubtitle(1).setVisible(false);
      } else {
        chart.getSubtitle(1).setVisible(true);
      }
    }

  }

}

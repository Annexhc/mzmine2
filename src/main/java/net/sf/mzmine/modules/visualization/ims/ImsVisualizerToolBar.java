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

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;
import net.sf.mzmine.util.GUIUtils;

/**
 * IMS visualizer toolbar class
 * 
 * @author Ansgar Korf (ansgar.korf@uni-muenster.de)
 */
public class ImsVisualizerToolBar extends JToolBar {

  private static final long serialVersionUID = 1L;
  static final Icon backColorIcon = new ImageIcon("icons/bgicon.png");
  static final Icon gridIcon = new ImageIcon("icons/gridicon.png");
  static final Icon legendIcon = new ImageIcon("icons/colorbaricon.png");

  public ImsVisualizerToolBar(ActionListener masterFrame) {

    super(JToolBar.VERTICAL);

    setFloatable(false);
    setFocusable(false);
    setMargin(new Insets(5, 5, 5, 5));
    setBackground(Color.white);

    GUIUtils.addButton(this, null, backColorIcon, masterFrame, "TOGGLE_BACK_COLOR",
        "Toggle background color white/black");

    addSeparator();

    GUIUtils.addButton(this, null, gridIcon, masterFrame, "TOGGLE_GRID", "Toggle grid");

    addSeparator();

    GUIUtils.addButton(this, null, gridIcon, masterFrame, "TOGGLE_ALPHA", "Toggle alpha");

    addSeparator();

    GUIUtils.addButton(this, null, legendIcon, masterFrame, "TOGGLE_LEGEND", "Toggle legend");

  }

}

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

package net.sf.mzmine.modules.mobilogramlistmethods.ordermobilogramlists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.swing.tree.DefaultMutableTreeNode;
import io.github.msdk.MSDKRuntimeException;
import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.MobilogramList;
import net.sf.mzmine.desktop.impl.MainWindow;
import net.sf.mzmine.desktop.impl.projecttree.MobilogramListTreeModel;
import net.sf.mzmine.desktop.impl.projecttree.ProjectTree;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.MZmineModuleCategory;
import net.sf.mzmine.modules.MZmineProcessingModule;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.project.impl.MZmineProjectImpl;
import net.sf.mzmine.taskcontrol.Task;
import net.sf.mzmine.util.ExitCode;

/**
 * This is a very simple module which reorders mobilogram lists alphabetically
 * 
 */
public class OrderMobilogramListsModule implements MZmineProcessingModule {

  private Logger logger = Logger.getLogger(this.getClass().getName());

  private static final String MODULE_NAME = "Order mobilogram lists";
  private static final String MODULE_DESCRIPTION = "Order selected mobilogram lists alphabetically";

  @Override
  public @Nonnull String getName() {
    return MODULE_NAME;
  }

  @Override
  public @Nonnull String getDescription() {
    return MODULE_DESCRIPTION;
  }

  @Override
  @Nonnull
  public ExitCode runModule(@Nonnull MZmineProject project, @Nonnull ParameterSet parameters,
      @Nonnull Collection<Task> tasks) {

    List<MobilogramList> mobilogramLists =
        Arrays.asList(parameters.getParameter(OrderMobilogramListsParameters.mobilogramLists)
            .getValue().getMatchingMobilogramLists());


    MobilogramListTreeModel model = null;
    if (project instanceof MZmineProjectImpl) {
      model = ((MZmineProjectImpl) project).getMobilogramListTreeModel();
    } else if (MZmineCore.getDesktop() instanceof MainWindow) {
      ProjectTree tree =
          ((MainWindow) MZmineCore.getDesktop()).getMainPanel().getMobilogramListTree();
      model = (MobilogramListTreeModel) tree.getModel();
    }

    if (model == null)
      throw new MSDKRuntimeException(
          "Cannot find mobilogram list tree model for sorting. Different MZmine project impl?");

    final DefaultMutableTreeNode rootNode = model.getRoot();

    // Get all tree nodes that represent selected mobilogram lists, and remove
    // them from
    final ArrayList<DefaultMutableTreeNode> selectedNodes = new ArrayList<DefaultMutableTreeNode>();

    for (int row = 0; row < rootNode.getChildCount(); row++) {
      DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) rootNode.getChildAt(row);
      Object selectedObject = selectedNode.getUserObject();
      if (mobilogramLists.contains(selectedObject)) {
        selectedNodes.add(selectedNode);
      }
    }

    // Get the index of the first selected item
    final ArrayList<Integer> positions = new ArrayList<Integer>();
    for (DefaultMutableTreeNode node : selectedNodes) {
      int nodeIndex = rootNode.getIndex(node);
      if (nodeIndex != -1)
        positions.add(nodeIndex);
    }
    if (positions.isEmpty())
      return ExitCode.ERROR;
    int insertPosition = Collections.min(positions);

    // Sort the mobilogram lists by name
    Collections.sort(selectedNodes, new Comparator<DefaultMutableTreeNode>() {
      @Override
      public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
        return o1.getUserObject().toString().compareTo(o2.getUserObject().toString());
      }
    });

    // Reorder the nodes in the tree model
    for (DefaultMutableTreeNode node : selectedNodes) {
      model.removeNodeFromParent(node);
      model.insertNodeInto(node, rootNode, insertPosition);
      insertPosition++;
    }

    return ExitCode.OK;
  }

  @Override
  public @Nonnull MZmineModuleCategory getModuleCategory() {
    return MZmineModuleCategory.MOBILOGRAMLIST;
  }

  @Override
  public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
    return OrderMobilogramListsParameters.class;
  }

}

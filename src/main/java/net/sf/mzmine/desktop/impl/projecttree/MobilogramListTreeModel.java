package net.sf.mzmine.desktop.impl.projecttree;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.MassList;
import net.sf.mzmine.datamodel.MobilogramList;
import net.sf.mzmine.datamodel.MobilogramListRow;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Scan;

public class MobilogramListTreeModel extends DefaultTreeModel {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static final String mobilogramListsNodeName = "Mobilogram lists";

  private Hashtable<Object, DefaultMutableTreeNode> treeObjects =
      new Hashtable<Object, DefaultMutableTreeNode>();

  private DefaultMutableTreeNode rootNode;

  public MobilogramListTreeModel(MZmineProject project) {

    super(new ProjectTreeNode(mobilogramListsNodeName));

    rootNode = (DefaultMutableTreeNode) super.getRoot();

  }

  /**
   * This method must be called from Swing thread
   */
  public void addObject(final Object object) {

    assert object != null;

    if (!SwingUtilities.isEventDispatchThread()) {
      throw new IllegalStateException("This method must be called from Swing thread");
    }

    // Create new node
    final DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(object);

    treeObjects.put(object, newNode);

    if (object instanceof MobilogramList) {
      int childCount = getChildCount(rootNode);
      insertNodeInto(newNode, rootNode, childCount);
      final MobilogramList MobilogramList = (MobilogramList) object;
      MobilogramListRow rows[] = MobilogramList.getRows();
      for (int i = 0; i < rows.length; i++) {
        DefaultMutableTreeNode rowNode = new DefaultMutableTreeNode(rows[i]);
        treeObjects.put(rows[i], rowNode);
        insertNodeInto(rowNode, newNode, i);
      }
    }

    if (object instanceof MassList) {
      Scan scan = ((MassList) object).getScan();

      final DefaultMutableTreeNode scNode = treeObjects.get(scan);
      assert scNode != null;

      int index = scNode.getChildCount();
      insertNodeInto(newNode, scNode, index);
    }

  }

  /**
   * This method must be called from Swing thread
   */
  public void removeObject(final Object object) {

    if (!SwingUtilities.isEventDispatchThread()) {
      throw new IllegalStateException("This method must be called from Swing thread");
    }

    final DefaultMutableTreeNode node = treeObjects.get(object);

    assert node != null;

    // Remove all children from treeObjects
    Enumeration<?> e = node.depthFirstEnumeration();
    while (e.hasMoreElements()) {
      DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement();
      Object nodeObject = childNode.getUserObject();
      treeObjects.remove(nodeObject);
    }

    // Remove the node from the tree, that also remove child
    // nodes
    removeNodeFromParent(node);

    // Remove the node object from treeObjects
    treeObjects.remove(object);

  }

  public synchronized MobilogramList[] getMobilogramLists() {
    int childrenCount = getChildCount(rootNode);
    MobilogramList result[] = new MobilogramList[childrenCount];
    for (int j = 0; j < childrenCount; j++) {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode) getChild(rootNode, j);
      result[j] = (MobilogramList) child.getUserObject();
    }
    return result;
  }

  @Override
  public void valueForPathChanged(TreePath path, Object value) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
    Object object = node.getUserObject();
    String newName = (String) value;
    if (object instanceof RawDataFile) {
      RawDataFile df = (RawDataFile) object;
      df.setName(newName);
    }
    if (object instanceof MobilogramList) {
      MobilogramList pl = (MobilogramList) object;
      pl.setName(newName);
    }
  }

  public void notifyObjectChanged(Object object, boolean structureChanged) {
    if (rootNode.getUserObject() == object) {
      if (structureChanged)
        nodeStructureChanged(rootNode);
      else
        nodeChanged(rootNode);
      return;
    }
    Enumeration<?> nodes = rootNode.breadthFirstEnumeration();
    while (nodes.hasMoreElements()) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) nodes.nextElement();

      if (node.getUserObject() == object) {
        if (structureChanged) {
          if (object instanceof MobilogramList) {
            node.removeAllChildren();
            MobilogramList MobilogramList = (MobilogramList) object;
            MobilogramListRow rows[] = MobilogramList.getRows();
            for (int i = 0; i < rows.length; i++) {
              DefaultMutableTreeNode rowNode = new DefaultMutableTreeNode(rows[i]);
              treeObjects.put(rows[i], rowNode);
              insertNodeInto(rowNode, node, i);
            }
          }
          nodeStructureChanged(node);
        } else
          nodeChanged(node);
        return;
      }
    }

  }

  @Override
  public DefaultMutableTreeNode getRoot() {
    return rootNode;
  }

}

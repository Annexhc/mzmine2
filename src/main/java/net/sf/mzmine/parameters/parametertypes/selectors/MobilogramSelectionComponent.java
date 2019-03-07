package net.sf.mzmine.parameters.parametertypes.selectors;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.StringParameter;
import net.sf.mzmine.parameters.parametertypes.ranges.IntRangeParameter;
import net.sf.mzmine.parameters.parametertypes.ranges.MZRangeParameter;
import net.sf.mzmine.parameters.parametertypes.ranges.MobilityRangeParameter;
import net.sf.mzmine.parameters.parametertypes.ranges.RTRangeParameter;
import net.sf.mzmine.util.ExitCode;
import net.sf.mzmine.util.GUIUtils;

public class MobilogramSelectionComponent extends JPanel implements ActionListener {

  private static final long serialVersionUID = 1L;

  private final DefaultListModel<MobilogramSelection> selectionListModel;
  private final JList<MobilogramSelection> selectionList;
  private final JButton addButton, removeButton, allButton, clearButton;

  public MobilogramSelectionComponent() {

    super(new BorderLayout());

    selectionListModel = new DefaultListModel<MobilogramSelection>();
    selectionList = new JList<>(selectionListModel);
    selectionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    selectionList.setPreferredSize(new Dimension(200, 50));
    JScrollPane scrollPane = new JScrollPane(selectionList);
    add(scrollPane, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
    addButton = GUIUtils.addButton(buttonPanel, "Add", null, this);
    removeButton = GUIUtils.addButton(buttonPanel, "Remove", null, this);
    allButton = GUIUtils.addButton(buttonPanel, "Set to all", null, this);
    clearButton = GUIUtils.addButton(buttonPanel, "Clear", null, this);

    add(buttonPanel, BorderLayout.EAST);
  }

  void setValue(List<MobilogramSelection> newValue) {
    selectionListModel.clear();
    for (MobilogramSelection ps : newValue)
      selectionListModel.addElement(ps);
  }

  public List<MobilogramSelection> getValue() {
    List<MobilogramSelection> items = Lists.newArrayList();
    ListModel<MobilogramSelection> model = selectionList.getModel();
    for (int i = 0; i < model.getSize(); i++)
      items.add(model.getElementAt(i));
    return items;
  }

  public void actionPerformed(ActionEvent event) {

    Object src = event.getSource();

    if (src == addButton) {
      final IntRangeParameter idParameter =
          new IntRangeParameter("ID", "Range of included peak IDs", false, null);
      final MZRangeParameter mzParameter = new MZRangeParameter(false);
      final RTRangeParameter rtParameter = new RTRangeParameter(false);
      final MobilityRangeParameter mobilityParameter = new MobilityRangeParameter(false);
      final StringParameter nameParameter =
          new StringParameter("Name", "Mobilogram identity name", null, false);
      SimpleParameterSet paramSet = new SimpleParameterSet(
          new Parameter[] {idParameter, mzParameter, rtParameter, nameParameter});
      Window parent = (Window) SwingUtilities.getAncestorOfClass(Window.class, this);
      ExitCode exitCode = paramSet.showSetupDialog(parent, true);
      if (exitCode == ExitCode.OK) {
        Range<Integer> idRange = paramSet.getParameter(idParameter).getValue();
        Range<Double> mzRange = paramSet.getParameter(mzParameter).getValue();
        Range<Double> rtRange = paramSet.getParameter(rtParameter).getValue();
        Range<Double> mobilityRange = paramSet.getParameter(mobilityParameter).getValue();
        String name = paramSet.getParameter(nameParameter).getValue();
        MobilogramSelection ps =
            new MobilogramSelection(idRange, mzRange, rtRange, mobilityRange, name);
        selectionListModel.addElement(ps);
      }
    }

    if (src == allButton) {
      MobilogramSelection ps = new MobilogramSelection(null, null, null, null, null);
      selectionListModel.clear();
      selectionListModel.addElement(ps);
    }

    if (src == removeButton) {
      for (MobilogramSelection p : selectionList.getSelectedValuesList()) {
        selectionListModel.removeElement(p);
      }
    }

    if (src == clearButton) {
      selectionListModel.clear();
    }

  }

  @Override
  public void setToolTipText(String toolTip) {
    selectionList.setToolTipText(toolTip);
  }

}

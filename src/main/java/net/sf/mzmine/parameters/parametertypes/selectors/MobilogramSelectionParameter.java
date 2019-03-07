package net.sf.mzmine.parameters.parametertypes.selectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import net.sf.mzmine.datamodel.MobilogramList;
import net.sf.mzmine.datamodel.MobilogramListRow;
import net.sf.mzmine.parameters.UserParameter;
import net.sf.mzmine.util.XMLUtils;

public class MobilogramSelectionParameter
    implements UserParameter<List<MobilogramSelection>, MobilogramSelectionComponent> {

  private final String name, description;
  private List<MobilogramSelection> value;

  public MobilogramSelectionParameter() {
    this("Mobilograms", "Select mobilograms that should be included.", null);
  }

  public MobilogramSelectionParameter(String name, String description,
      List<MobilogramSelection> defaultValue) {
    this.name = name;
    this.description = description;
    this.value = defaultValue;
  }

  @Override
  public List<MobilogramSelection> getValue() {
    return value;
  }

  @Override
  public void setValue(List<MobilogramSelection> newValue) {
    this.value = Lists.newArrayList(newValue);
  }

  @Override
  public MobilogramSelectionParameter cloneParameter() {
    MobilogramSelectionParameter copy =
        new MobilogramSelectionParameter(name, description, Lists.newArrayList(value));
    return copy;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public boolean checkValue(Collection<String> errorMessages) {
    if ((value == null) || (value.size() == 0)) {
      errorMessages.add("No mobilograms selected");
      return false;
    }
    return true;
  }

  @Override
  public void loadValueFromXML(Element xmlElement) {

    List<MobilogramSelection> newValue = Lists.newArrayList();
    NodeList selItems = xmlElement.getElementsByTagName("selection");
    for (int i = 0; i < selItems.getLength(); i++) {
      Element selElement = (Element) selItems.item(i);
      Range<Integer> idRange = XMLUtils.parseIntegerRange(selElement, "id");
      Range<Double> mzRange = XMLUtils.parseDoubleRange(selElement, "mz");
      Range<Double> rtRange = XMLUtils.parseDoubleRange(selElement, "rt");
      Range<Double> mobilityRange = XMLUtils.parseDoubleRange(selElement, "mobility");
      String name = XMLUtils.parseString(selElement, "name");
      MobilogramSelection ps =
          new MobilogramSelection(idRange, mzRange, rtRange, mobilityRange, name);
      newValue.add(ps);
    }
    this.value = newValue;
  }

  @Override
  public void saveValueToXML(Element xmlElement) {
    if (value == null)
      return;
    Document parentDocument = xmlElement.getOwnerDocument();

    for (MobilogramSelection ps : value) {
      Element selElement = parentDocument.createElement("selection");
      xmlElement.appendChild(selElement);
      XMLUtils.appendRange(selElement, "id", ps.getIDRange());
      XMLUtils.appendRange(selElement, "mz", ps.getMZRange());
      XMLUtils.appendRange(selElement, "rt", ps.getRTRange());
      XMLUtils.appendRange(selElement, "mobility", ps.getMobilityRange());
      XMLUtils.appendString(selElement, "name", ps.getName());
    }

  }

  @Override
  public MobilogramSelectionComponent createEditingComponent() {
    return new MobilogramSelectionComponent();
  }

  @Override
  public void setValueFromComponent(MobilogramSelectionComponent component) {
    value = component.getValue();
  }

  @Override
  public void setValueToComponent(MobilogramSelectionComponent component,
      List<MobilogramSelection> newValue) {
    component.setValue(newValue);
  }

  /**
   * Shortcut to set value based on mobilogram list rows
   */
  public void setValue(MobilogramListRow rows[]) {
    List<MobilogramSelection> newValue = Lists.newArrayList();
    for (MobilogramListRow row : rows) {
      Range<Integer> idRange = Range.singleton(row.getID());
      Range<Double> mzRange = Range.singleton(row.getAverageMZ());
      Range<Double> rtRange = Range.singleton(row.getAverageRT());
      Range<Double> mobilityRange = Range.singleton(row.getAverageMobility());
      MobilogramSelection ps =
          new MobilogramSelection(idRange, mzRange, rtRange, mobilityRange, null);
      newValue.add(ps);
    }
    setValue(newValue);
  }

  public MobilogramListRow[] getMatchingRows(MobilogramList mobilogramList) {

    final List<MobilogramListRow> matchingRows = new ArrayList<>();
    rows: for (MobilogramListRow row : mobilogramList.getRows()) {
      for (MobilogramSelection ps : value) {
        if (ps.checkMobilogramListRow(row)) {
          matchingRows.add(row);
          continue rows;
        }
      }
    }
    return matchingRows.toArray(new MobilogramListRow[matchingRows.size()]);

  }

}

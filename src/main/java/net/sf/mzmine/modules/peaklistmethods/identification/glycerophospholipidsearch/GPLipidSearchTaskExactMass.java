package net.sf.mzmine.modules.peaklistmethods.identification.glycerophospholipidsearch;

import java.util.logging.Logger;

import javax.print.attribute.standard.NumberOfDocuments;

import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.jmol.util.Elements;
import org.openscience.cdk.tools.SystemOutLoggingTool;

import com.google.common.collect.Range;

import net.sf.mzmine.datamodel.IonizationType;
import net.sf.mzmine.datamodel.PeakIdentity;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.datamodel.impl.SimplePeakList;
import net.sf.mzmine.datamodel.impl.SimplePeakListAppliedMethod;
import net.sf.mzmine.desktop.Desktop;
import net.sf.mzmine.desktop.impl.HeadLessDesktop;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.peaklistmethods.identification.glycerophospholipidsearch.GPLipidIdentities.GPLipidIdentity1Chain;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.parametertypes.tolerances.MZTolerance;
import net.sf.mzmine.parameters.parametertypes.tolerances.RTTolerance;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;
import uk.ac.ebi.beam.Element;

public class GPLipidSearchTaskExactMass extends AbstractTask {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private double finishedSteps, totalSteps;
    private PeakList peakList;

    private GPLipidType[] selectedLipids;
    private int minChainLength, maxChainLength, maxDoubleBonds, maxOxidationValue;
    private MZTolerance mzTolerance;
    private RTTolerance isotopeRtTolerance;
    private IonizationType ionizationType;
    private Boolean searchForIosotopes;
    private int relIsotopeIntensityTolerance;

    private ParameterSet parameters;

    /**
     * @param parameters
     * @param peakList
     */
    public GPLipidSearchTaskExactMass(ParameterSet parameters, PeakList peakList) {

        this.peakList = peakList;
        this.parameters = parameters;

        minChainLength = parameters.getParameter(
                GPLipidSearchParameters.minChainLength).getValue();
        maxChainLength = parameters.getParameter(
                GPLipidSearchParameters.maxChainLength).getValue();
        maxDoubleBonds = parameters.getParameter(
                GPLipidSearchParameters.maxDoubleBonds).getValue();
        maxOxidationValue = parameters.getParameter(
                GPLipidSearchParameters.maxOxidationValue).getValue();
        mzTolerance = parameters.getParameter(
                GPLipidSearchParameters.mzTolerance).getValue();
        selectedLipids = parameters.getParameter(
                GPLipidSearchParameters.lipidTypes).getValue();
        ionizationType = parameters.getParameter(
                GPLipidSearchParameters.ionizationMethod).getValue();
        searchForIosotopes = parameters.getParameter(
                GPLipidSearchParameters.searchForIsotopes).getValue();
        isotopeRtTolerance = parameters.getParameter(
                GPLipidSearchParameters.isotopeRetentionTimeTolerance).getValue();
        relIsotopeIntensityTolerance = parameters.getParameter(
                GPLipidSearchParameters.relativeIsotopeIntensityTolerance).getValue();
    }

    /**
     * @see net.sf.mzmine.taskcontrol.Task#getFinishedPercentage()
     */
    public double getFinishedPercentage() {
        if (totalSteps == 0)
            return 0;
        return ((double) finishedSteps) / totalSteps;
    }

    /**
     * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
     */
    public String getTaskDescription() {
        return "Identification of glycerophospholipids in " + peakList;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {

        setStatus(TaskStatus.PROCESSING);

        logger.info("Starting glycerophospholipid search in " + peakList);

        PeakListRow rows[] = peakList.getRows();

        // Calculate how many possible lipids we will try

        totalSteps = ((maxChainLength + 1)* (maxDoubleBonds + 1)*(maxOxidationValue+1))* selectedLipids.length;

        // Try all combinations of fatty acid lengths and double bonds
        for (GPLipidType lipidType : selectedLipids) {
            for (int fattyAcid1Length = 0; fattyAcid1Length <= maxChainLength; fattyAcid1Length++) {
                for (int fattyAcid1DoubleBonds = 0; fattyAcid1DoubleBonds <= maxDoubleBonds; fattyAcid1DoubleBonds++) {
                    for (int oxidationValue = 0; oxidationValue <= maxOxidationValue; oxidationValue++) {

                        // Task canceled?
                        if (isCanceled())
                            return;

                        // If we have non-zero fatty acid, which is shorter
                        // than minimal length, skip this lipid
                        if (((fattyAcid1Length > 0) && (fattyAcid1Length < minChainLength))){
                            finishedSteps++;
                            continue;
                        }

                        // If we have more double bonds than carbons, it
                        // doesn't make sense, so let's skip such lipids
                        if (((fattyAcid1DoubleBonds > 0) && (fattyAcid1DoubleBonds > fattyAcid1Length - 1))) {
                            finishedSteps++;
                            continue;
                        }

                        // Prepare a lipid instance
                        GPLipidIdentity1Chain lipidChain = new GPLipidIdentity1Chain(
                                lipidType, fattyAcid1Length,
                                fattyAcid1DoubleBonds, oxidationValue);

                        // Find all rows that match this lipid
                        findPossibleGPL1Chain(lipidChain, rows, oxidationValue);

                        finishedSteps++;
                    }
                }
            }
            // Add task description to peakList
            ((SimplePeakList) peakList)
            .addDescriptionOfAppliedTask(new SimplePeakListAppliedMethod(
                    "Identification of glycerophospholipids", parameters));

            // Repaint the window to reflect the change in the peak list
            Desktop desktop = MZmineCore.getDesktop();
            if (!(desktop instanceof HeadLessDesktop))
                desktop.getMainWindow().repaint();

            setStatus(TaskStatus.FINISHED);

            logger.info("Finished glycerophospholipid search in " + peakList);

        }


    }

    /**
     * Check if candidate peak may be a possible adduct of a given main peak
     * 
     * @param mainPeak
     * @param possibleFragment
     */
    private void findPossibleGPL1Chain(GPLipidIdentity1Chain lipid, PeakListRow rows[], int oxidationValue) {

        final double lipidIonMass = lipid.getMass()
                + ionizationType.getAddedMass()
                + oxidationValue*Elements.getAtomicMass(8);
        logger.finest("Searching for lipid " + lipid.getDescription() + ", "
                + lipidIonMass + " m/z");
        for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {

            if (isCanceled())
                return;

            Range<Double> mzTolRange12C = mzTolerance
                    .getToleranceRange(rows[rowIndex].getAverageMZ());

            if (mzTolRange12C.contains(lipidIonMass)) {
                rows[rowIndex].addPeakIdentity(lipid, false);
                rows[rowIndex].setComment(
                        "Ionization: "+ionizationType.getAdduct());

                //If search for isotopes is selected search for isotopes
                if(searchForIosotopes = true) {
                    for (int i = 0; i < rows.length; i++) {

                        Range<Double> mzTolRange13C = mzTolerance
                                .getToleranceRange(rows[i].getAverageMZ());

                        if (mzTolRange13C.contains(lipidIonMass+1.003355) &&
                                Math.abs(rows[i].getAverageRT()-rows[rowIndex].getAverageRT()) <= isotopeRtTolerance.getTolerance()) {
                            //Check if intensity of 13C fits calc intensity in  a range
                            int numberOfCAtoms = getNumberOfCAtoms(lipid.getFormula());
                            System.out.println(Math.abs((rows[i].getAverageHeight()/rows[rowIndex].getAverageHeight())*100-1.1*numberOfCAtoms));
                            if(Math.abs((rows[i].getAverageHeight()/rows[rowIndex].getAverageHeight())*100-1.1*numberOfCAtoms)
                                    <= relIsotopeIntensityTolerance) {              
                                rows[rowIndex].setComment(rows[rowIndex].getComment()+
                                        " found 13C isotope"); 
                                rows[i].setComment(" 13C isotope of Feautre with ID"+ rows[rowIndex].getID());
                            }
                        }
                    }
                }

                // Notify the GUI about the change in the project
                MZmineCore.getProjectManager().getCurrentProject()
                .notifyObjectChanged(rows[rowIndex], false);
            }
        }

    }

    private int getNumberOfCAtoms(String formula) {
        
        int numberOfCAtoms = 0;
        int counterC = 0;
        int counterH = 0;
        int indexFirstC = 0;
        int indexFirstH = 0;
        int indexSecondC = 0;
        int indexSecondH = 0;
        
        String firstCNumbers = null;
        String secondCNumbers = null;
        //Loop through every char and check for "C"
        for (int i = 0; i < formula.length(); i++) {
            //get first C
            if(formula.charAt(i) == 'C' && counterC == 0) {
                counterC++;
                if(counterC == 1) {
                    indexFirstC = i;
                }
                for (int j = 0; j < formula.length(); j++) {
                    if(formula.charAt(j) == 'H' && counterH == 0) {
                        counterH++;
                        if(counterH == 1) {
                            indexFirstH = j;
                        }
                       
                    }
                }
            }
                 //get second C
            if(formula.charAt(i) == 'C' && i != indexFirstC) {
                counterC++;
                if(counterC == 2) {
                    indexSecondC = i;
                }
                for (int j = 0; j < formula.length(); j++) {
                    if(formula.charAt(j) == 'H' && j != indexFirstH) {
                        counterH++;
                        if(counterH == 2) {
                            indexSecondH = j;
                        }
                       
                    }
                }
            }
        
        }
        
        //Combine to total number of C
        firstCNumbers = formula.substring(indexFirstC+1, indexFirstH);
        secondCNumbers = formula.substring(indexSecondC+1, indexSecondH);
        numberOfCAtoms = Integer.parseInt(firstCNumbers)+Integer.parseInt(secondCNumbers);
        System.out.println(numberOfCAtoms);
        return numberOfCAtoms;
    }

}

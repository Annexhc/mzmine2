package net.sf.mzmine.modules.peaklistmethods.identification.lipidprediction;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.jmol.util.Elements;

import com.google.common.collect.Range;

import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.IonizationType;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.datamodel.impl.SimplePeakList;
import net.sf.mzmine.datamodel.impl.SimplePeakListAppliedMethod;
import net.sf.mzmine.desktop.Desktop;
import net.sf.mzmine.desktop.impl.HeadLessDesktop;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.exactmass.ExactMassDetector;
import net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.exactmass.ExactMassDetectorParameters;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.parametertypes.tolerances.MZTolerance;
import net.sf.mzmine.parameters.parametertypes.tolerances.RTTolerance;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;
import net.sf.mzmine.util.FormulaUtils;
import net.sf.mzmine.modules.peaklistmethods.identification.lipidprediction.lipididentificationtools.FattyAcidTools;
import net.sf.mzmine.modules.peaklistmethods.identification.lipidprediction.lipididentificationtools.IsotopeLipidTools;
import net.sf.mzmine.modules.peaklistmethods.identification.lipidprediction.lipididentificationtools.MSMSLipidTools;
import net.sf.mzmine.modules.peaklistmethods.identification.lipidprediction.LipidIdentityChain;

public class LipidSearchTask extends AbstractTask {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private double finishedSteps, totalSteps;
    private PeakList peakList;

    private LipidType[] selectedLipids;
    private int minChainLength, maxChainLength, maxDoubleBonds, maxOxidationValue;
    private MZTolerance mzTolerance;
    private RTTolerance isotopeRtTolerance;
    private IonizationType ionizationType;
    private Boolean searchForIsotopes, searchForFAinMSMS;
    private int relIsotopeIntensityTolerance;
    private double noiseLevelMSMS;

    private ParameterSet parameters;

    /**
     * @param parameters
     * @param peakList
     */
    public LipidSearchTask(ParameterSet parameters, PeakList peakList) {

        this.peakList = peakList;
        this.parameters = parameters;

        minChainLength = parameters.getParameter(
                LipidSearchParameters.minChainLength).getValue();
        maxChainLength = parameters.getParameter(
                LipidSearchParameters.maxChainLength).getValue();
        maxDoubleBonds = parameters.getParameter(
                LipidSearchParameters.maxDoubleBonds).getValue();
        maxOxidationValue = parameters.getParameter(
                LipidSearchParameters.maxOxidationValue).getValue();
        mzTolerance = parameters.getParameter(
                LipidSearchParameters.mzTolerance).getValue();
        selectedLipids = parameters.getParameter(
                LipidSearchParameters.lipidTypes).getValue();
        ionizationType = parameters.getParameter(
                LipidSearchParameters.ionizationMethod).getValue();
        searchForIsotopes = parameters.getParameter(
                LipidSearchParameters.searchForIsotopes).getValue();
        isotopeRtTolerance = parameters.getParameter(
                LipidSearchParameters.isotopeRetentionTimeTolerance).getValue();
        relIsotopeIntensityTolerance = parameters.getParameter(
                LipidSearchParameters.relativeIsotopeIntensityTolerance).getValue();
        searchForFAinMSMS = parameters.getParameter(
                LipidSearchParameters.searchForFAinMSMS).getValue();
        noiseLevelMSMS = parameters.getParameter(
                LipidSearchParameters.noiseLevel).getValue();
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
        return "Prediction of lipids in " + peakList;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {

        setStatus(TaskStatus.PROCESSING);

        logger.info("Starting lipid predriction in " + peakList);

        PeakListRow rows[] = peakList.getRows();

        // Calculate how many possible lipids we will try

        totalSteps = ((maxChainLength + 1)* (maxDoubleBonds + 1)*(maxOxidationValue+1))* selectedLipids.length;

        // Try all combinations of fatty acid lengths and double bonds
        for (LipidType lipidType : selectedLipids) {
            for (int fattyAcidLength = 0; fattyAcidLength <= maxChainLength; fattyAcidLength++) {
                for (int fattyAcidDoubleBonds = 0; fattyAcidDoubleBonds <= maxDoubleBonds; fattyAcidDoubleBonds++) {
                    for (int oxidationValue = 0; oxidationValue <= maxOxidationValue; oxidationValue++) {

                        // Task canceled?
                        if (isCanceled())
                            return;

                        // If we have non-zero fatty acid, which is shorter
                        // than minimal length, skip this lipid
                        if (((fattyAcidLength > 0) && (fattyAcidLength < minChainLength))){
                            finishedSteps++;
                            continue;
                        }

                        // If we have more double bonds than carbons, it
                        // doesn't make sense, so let's skip such lipids
                        if (((fattyAcidDoubleBonds > 0) && (fattyAcidDoubleBonds > fattyAcidLength - 1))) {
                            finishedSteps++;
                            continue;
                        }

                        // Prepare a lipid instance
                        LipidIdentityChain lipidChain = new LipidIdentityChain(
                                lipidType, fattyAcidLength,
                                fattyAcidDoubleBonds, oxidationValue);

                        // Find all rows that match this lipid
                        findPossibleLipid(lipidChain, rows, oxidationValue);

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

            logger.info("Finished lipid prediction in " + peakList);

        }


    }

    /**
     * Check if candidate peak may be a possible adduct of a given main peak
     * 
     * @param mainPeak
     * @param possibleFragment
     */
    private void findPossibleLipid(LipidIdentityChain lipid, PeakListRow rows[], int oxidationValue) {

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
                if(searchForIsotopes == true) {
                    searchFor13CIsotope(rows, lipidIonMass, rowIndex, lipid);
                }
                //If search for FA in MSMS is selected search for FA
                if(searchForFAinMSMS == true) {
                    searchFAinMSMS(rows, lipidIonMass, rowIndex, lipid);
                }

                // Notify the GUI about the change in the project
                MZmineCore.getProjectManager().getCurrentProject()
                .notifyObjectChanged(rows[rowIndex], false);
            }
        }

    }

    private void searchFAinMSMS(PeakListRow rows[], double lipidIonMass, int rowIndex, LipidIdentityChain lipid) {
        ExactMassDetector massDetector = new ExactMassDetector();
        ExactMassDetectorParameters parametersMSMS = new ExactMassDetectorParameters();
        FattyAcidTools fattyAcidTools = new FattyAcidTools();
        parametersMSMS.noiseLevel.setValue(noiseLevelMSMS);

        //Create array of all possible FA masses based on lipid annotation
        ArrayList<String> fattyAcidFormulas = fattyAcidTools.calculateFattyAcidFormulas(4, 26,8, maxOxidationValue);
        ArrayList<String> fattyAcidNames = fattyAcidTools.getFattyAcidNames(4,26,8, maxOxidationValue);
        //Check if selected feature has MSMS spectra
        if(rows[rowIndex].getBestFragmentation() != null) {
            DataPoint[] massList = massDetector.getMassValues(rows[rowIndex].getBestFragmentation(), parametersMSMS);
            //Check if a mass in MSMS spectra fits a mass of a FA
            for (int j = 0; j < massList.length; j++) {
                Range<Double> mzTolRangeMSMS = mzTolerance.getToleranceRange(massList[j].getMZ());
                for (int i = 0; i < fattyAcidFormulas.size(); i++) {
                    if(mzTolRangeMSMS.contains(fattyAcidTools.getFAMass(FormulaUtils.
                            ionizeFormula(fattyAcidFormulas.get(i), IonizationType.NEGATIVE, 1)))) {
                        logger.info("Found "+fattyAcidFormulas.get(i)+" with m/z "+fattyAcidTools.getFAMass(FormulaUtils.
                                ionizeFormula(fattyAcidFormulas.get(i), IonizationType.NEGATIVE, 1)));
                        //Add masses to comment
                        if(rows[rowIndex].getComment().equals(null)) {
                            rows[rowIndex].setComment(" FA "+ fattyAcidNames.get(i)+
                                    " m/z "+ NumberFormat.getInstance().format(fattyAcidTools.getFAMass(FormulaUtils.
                                            ionizeFormula(fattyAcidFormulas.get(i), IonizationType.NEGATIVE, 1))));
                        }
                        else {
                            rows[rowIndex].setComment(rows[rowIndex].getComment()+";"+
                                    " FA "+ fattyAcidNames.get(i)+
                                    " m/z "+ NumberFormat.getInstance().format(fattyAcidTools.getFAMass(FormulaUtils.
                                            ionizeFormula(fattyAcidFormulas.get(i), IonizationType.NEGATIVE, 1))));
                        }
                    }
                }

                /**
                 * Check for lipid class specific fragment
                 *lipid class fragments are derived from lipid blast
                 */
                MSMSLipidTools msmsLipidTools = new MSMSLipidTools();
                ArrayList<String> listOfFragments = msmsLipidTools.checkForClassSpecificFragments(mzTolRangeMSMS,
                        rows[rowIndex].getPreferredPeakIdentity(),
                        lipidIonMass);
                if(listOfFragments.isEmpty() == false) {
                    for (int i = 0; i < listOfFragments.size(); i++) {
                        //Add masses to comment
                        System.out.println(listOfFragments.get(i)+"\n"+listOfFragments.size());
                        if(rows[rowIndex].getComment().equals(null)) {
                            rows[rowIndex].setComment(" "+listOfFragments.get(i));
                        }
                        else {
                            rows[rowIndex].setComment(rows[rowIndex].getComment()+";"+
                                    " "+listOfFragments.get(i));
                        }
                    }
                }
            }
        }
    }



    private void searchFor13CIsotope(PeakListRow rows[], double lipidIonMass, int rowIndex, LipidIdentityChain lipid) {
        for (int i = 0; i < rows.length; i++) {

            Range<Double> mzTolRange13C = mzTolerance
                    .getToleranceRange(rows[i].getAverageMZ());

            if (mzTolRange13C.contains(lipidIonMass+1.003355) &&
                    Math.abs(rows[i].getAverageRT()-rows[rowIndex].getAverageRT()) <= isotopeRtTolerance.getTolerance()) {
                //Check if intensity of 13C fits calc intensity in  a range
                IsotopeLipidTools isotopeLipidTools = new IsotopeLipidTools();
                int numberOfCAtoms = isotopeLipidTools.getNumberOfCAtoms(lipid.getFormula());
                if(Math.abs((rows[i].getAverageHeight()/rows[rowIndex].getAverageHeight())*100-1.1*numberOfCAtoms)
                        <= relIsotopeIntensityTolerance) {              
                    rows[rowIndex].setComment(rows[rowIndex].getComment()+";"+
                            " found 13C isotope"); 
                    rows[i].setComment(" 13C isotope of Feautre with ID"+ rows[rowIndex].getID());
                }
            }
        }
    }



}

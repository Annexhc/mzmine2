package net.sf.mzmine.modules.peaklistmethods.identification.lipidprediction.lipididentificationtools;

import java.util.ArrayList;

import com.google.common.collect.Range;

import net.sf.mzmine.datamodel.PeakIdentity;
import net.sf.mzmine.util.FormulaUtils;

public class MSMSLipidTools {

    public ArrayList<String> checkForClassSpecificFragments(
            Range<Double> mzTolRangeMSMS, PeakIdentity peakIdentity,
            double lipidIonMass) {

        ArrayList<String> listOfFragments = new ArrayList<String>();
        // load a MSMS fragment library
        MSMSLibrary msmsLibrary = loadMSMSLibrary(peakIdentity);
        // Get possible fatty acids
        FattyAcidTools fattyAcidTools = new FattyAcidTools();
        ArrayList<String> fattyAcidFormulas = fattyAcidTools
                .calculateFattyAcidFormulas(4, 26, 8, 0);
        ArrayList<String> fattyAcidNames = fattyAcidTools.getFattyAcidNames(4,
                26, 8, 0);

        // Loop through possible fragments
        for (int i = 0; i < msmsLibrary.getName().length; i++) {
            // Search for [M-H] - fatty acid
            if (msmsLibrary.getName()[i].equals("[M-H]-sn1")) {
                for (int j = 0; j < fattyAcidFormulas.size(); j++) {
                    if (mzTolRangeMSMS.contains(lipidIonMass - fattyAcidTools
                            .getFAMass((fattyAcidFormulas.get(j))))) {
                        listOfFragments
                                .add("[M-H]- FA" + fattyAcidNames.get(j));
                    }
                }
            }
            // Search for [M-H] - fatty acid -H2O
            if (msmsLibrary.getName()[i].equals("[M-H]-sn1-H2O")) {
                for (int j = 0; j < fattyAcidFormulas.size(); j++) {
                    if (mzTolRangeMSMS.contains(lipidIonMass - fattyAcidTools
                            .getFAMass((fattyAcidFormulas.get(j))))) {
                        listOfFragments
                                .add("[M-H]- FA" + fattyAcidNames.get(j));
                    }
                }
            }
            // Search for [M-H] - fatty acid - Sum Formula
            if (msmsLibrary.getName()[i].contains("[M-H]-sn1-C")) {
                for (int j = 0; j < fattyAcidFormulas.size(); j++) {
                    if (mzTolRangeMSMS.contains(lipidIonMass
                            - fattyAcidTools
                                    .getFAMass((fattyAcidFormulas.get(j)))
                            - FormulaUtils.calculateExactMass(msmsLibrary
                                    .getFormulaOfStaticFormula()[i]))) {
                        listOfFragments.add("[M-H]-FA" + fattyAcidNames.get(j)
                                + "-"
                                + msmsLibrary.getFormulaOfStaticFormula()[i]);
                    }
                }
            }
            // Search for "fragments"
            if (msmsLibrary.getName()[i].contains("sn1") == false) {
                if (mzTolRangeMSMS.contains(FormulaUtils.calculateExactMass(
                        msmsLibrary.getFormulaOfStaticFormula()[i]))) {
                    listOfFragments.add("Fragment: "
                            + msmsLibrary.getFormulaOfStaticFormula()[i]);
                }
            }
        }
        return listOfFragments;
    }

    private MSMSLibrary loadMSMSLibrary(PeakIdentity peakIdentity) {
        // Get lipid class name
        int endIndexSubstring = 0;
        for (int i = 0; i < peakIdentity.getName().length(); i++) {
            if (peakIdentity.getName().charAt(i) == '(') {
                endIndexSubstring = i;
            }
        }
        String lipidClass = peakIdentity.getName().substring(0,
                endIndexSubstring);
        MSMSLibrary msmsLibrary = MSMSLibrary.valueOf(lipidClass);
        return msmsLibrary;
    }

}

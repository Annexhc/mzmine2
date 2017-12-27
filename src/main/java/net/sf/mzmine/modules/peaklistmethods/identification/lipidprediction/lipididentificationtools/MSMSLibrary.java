package net.sf.mzmine.modules.peaklistmethods.identification.lipidprediction.lipididentificationtools;

/**
 *This enum contains specific fragments of a lipid class apart from fatty acids
 *If no specific fragment is listed in Lipid Maps, the enum is null
 *sn1 represents fatty acid chains
 */

public enum MSMSLibrary {

    PC(null, null, null), 
    PE(new String[] {"[M-H]-sn1", "[M-H]-sn1-H2O", null},
            new String[] {null, "H2O", null},
            new int[] {200, 50, 999}), 
    PI(new String[] {"[M-H]-sn1", "[M-H]-sn1-H2O", "[M-H]-sn1-C6H12O6"},
            new String[] {null, "H2O", "C6H12O6"},
            new int[] {999, 200, 400}), 
    PS(new String[] {"[M-H]-C3H5NO2", "[M-H]-C3H5NO2-sn1+H2O", "[M-H]-C3H5NO2-sn1", "sn1"},
            new String[] {"C3H5NO2", "C3H3NO", "C3H5NO2", null},
            new int[] {999, 200, 200, 100}), 
    PG(new String[] {"[M-H]-sn1", "[M-H]-sn1-H2O", "[M-H]-sn1-C3H8O3", "sn1"},
            new String[] {null, "H2O", "-C3H8O3", null},
            new int[] {200, 200, 200, 999}),
    BMP(null, null, null),
    CL(new String[] {"[M-H]-sn1","sn1+sn1+C6H11P2O8", "sn1+sn1+C6H10O5P",
            "sn1+sn1+C3H6PO4", "sn1+sn1+C6H11P2O8","sn1+C3H6PO4+H2O",
            "sn1+C3H6PO4","sn1"},
            new String[] {null, "C6H11P2O8", "C6H10O5P","C3H6PO4",
                    "C6H11P2O8", "C3H8PO5", "C3H6PO4", null},
            new int[] {50, 300, 100, 999, 300, 100, 200, 100}),
    DAG(null, null, null),
    TAG(null, null, null),
    MGDG(null, null, null),
    DGDG(null, null, null),
    SQDG(new String[] {"[M-H]-sn1", "sn1", "fragment C6H9O7S"},
            new String[] {null, null, "C6H9O7S"},
            new int[] {300, 100, 999});

    private final String[] name, formulaOfStaticFormula;
    private final int[] relativeIntensity;

    MSMSLibrary(String[] name,
            String[] formulaOfStaticFormula,
            int[] relativeIntensity) {
        
        this.name = name;
        this.formulaOfStaticFormula = formulaOfStaticFormula;
        this.relativeIntensity = relativeIntensity;
    }

    public String[] getFormulaOfStaticFormula() {
        return formulaOfStaticFormula;
    }

    public int[] getRelativeIntensity() {
        return relativeIntensity;
    }

    public String[] getName() {
        return this.name;
    }

}

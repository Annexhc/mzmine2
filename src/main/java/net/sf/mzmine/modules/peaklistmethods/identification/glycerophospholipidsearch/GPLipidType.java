/*
 * Copyright 2006-2015 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.peaklistmethods.identification.glycerophospholipidsearch;

public enum GPLipidType {

	PC("Phosphatidylcholine", "PC", "C8H17NO8P", 2), //
	PE("Phosphatidylethanolamine", "PE", "C5H11NO8P", 2), //
	PI("Phosphatidylinositol", "PI", "C9H16O13P", 2), //
	PS("Phosphatidylserine", "PS", "C6H11NO10P", 2),//
	PG("Phosphatidylglycerol", "PG", "C6H12O10P",2),//
	BMP("Bis(Monoacylglycero)Phosphate", "BMP", "C6H12O9P",2),//
	CL("Cardiolipine", "CL", "C9H15O17P2", 4),//
	DAG("Diacyglycerol", "DAG", "C3H5O5", 2),//
	TAG("Triacyglycerol", "TAG", "C3H3O6", 3),//
	MGDG("Monogalactosyldiacylglycerol", "MGDG", "C9H15O10",2),//
	DGDG("Digalactosyldiacylglycerol", "DGDG", "C15H25O15", 2),//
	SQDG("Sulfoquinovosyldiacylglyerol", "SQDG", "C9H15O12S" ,2);

	private final String name, abbr, formula;
	private final int numberOfChains;

	GPLipidType(String name, String abbr, String formula, int numberOfChains) {
		this.name = name;
		this.abbr = abbr;
		this.formula = formula;
		this.numberOfChains = numberOfChains;
	}

	public String getAbbr() {
		return abbr;
	}

	public String getFormula() {
		return formula;
	}

	public String getName() {
		return this.name;
	}

	public int getNumberOfChains() {
		return numberOfChains;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}

package org.crowdsourcedinterlinking.evaluation;

import java.util.Set;

import org.semanticweb.owl.align.Cell;

public class DiffMappings {

	private Set<Cell> falseNegatives;
	private Set<Cell> falsePositives;

	public DiffMappings(Set<Cell> falseN, Set<Cell> falseP) {
		this.falseNegatives = falseN;
		this.falsePositives = falseP;
	}

	public Set<Cell> getFalseNegatives() {
		return falseNegatives;
	}

	public void setFalseNegatives(Set<Cell> falseNegatives) {
		this.falseNegatives = falseNegatives;
	}

	public Set<Cell> getFalsePositives() {
		return falsePositives;
	}

	public void setFalsePositives(Set<Cell> falsePositives) {
		this.falsePositives = falsePositives;
	}
}

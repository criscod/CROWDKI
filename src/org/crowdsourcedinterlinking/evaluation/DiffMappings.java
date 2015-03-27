package org.crowdsourcedinterlinking.evaluation;

import org.semanticweb.owl.align.Cell;

import java.util.Set;

/**
 * @author: csarasua
 * Class to perform a confusion matrix analysis
 */
public class DiffMappings {

    /**
     * False negatives are cases that were labelled as negative when they are indeed positive (Ground Truth)
     * False positives are cases that were labelled as positives when they are indeed negatives (Ground Truth)
     */
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

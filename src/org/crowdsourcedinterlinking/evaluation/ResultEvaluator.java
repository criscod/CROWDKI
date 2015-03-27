package org.crowdsourcedinterlinking.evaluation;

/**
 * @author: csarasua
 * Interface for the evaluation of a crowd integration result
 */
public interface ResultEvaluator {
    /**
     * Method to calculate precision and recall.
     */
    public void evaluateResultsFromCrowdPR();

}

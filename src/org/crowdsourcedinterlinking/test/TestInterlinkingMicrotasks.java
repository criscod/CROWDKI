package org.crowdsourcedinterlinking.test;

import org.crowdsourcedinterlinking.expblogic.ExperimentManager;
import org.crowdsourcedinterlinking.model.Dataset;
import org.crowdsourcedinterlinking.model.TypeOfDatasetLocation;
import org.crowdsourcedinterlinking.model.TypeOfMappingGoal;
import org.junit.Test;

import java.io.File;
/**
 * @author csarasua
 */
public class TestInterlinkingMicrotasks {

    public static void main(String args[]) {


        //test_person11person12();
        test_person11person12_expertiseLinks();
        //testResu_person11person12();


    }

    @Test
    public static void test_person11person12_expertiseLinks() {

        ExperimentManager expBuild = new ExperimentManager();

        Dataset d1 = new Dataset("person11", TypeOfDatasetLocation.FILEDUMP, "datasets/person11.rdf", null, null, null);
        Dataset d2 = new Dataset("person12", TypeOfDatasetLocation.FILEDUMP, "datasets/person12.rdf", null, null, null);

        //imported ExpLinks into the allPoolLinks file
        File fInterlinkingExpandPool = new File("datasets/person11person12_expertiseLinks.nt");


        expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinkingExpandPool, TypeOfMappingGoal.VALIDATION, true);


    }

    public static void testResu_person11person12() {
        ExperimentManager expBuild = new ExperimentManager();

        Dataset d1 = new Dataset("person11", TypeOfDatasetLocation.FILEDUMP, "datasets/person11.rdf", null, null, null);
        Dataset d2 = new Dataset("person12", TypeOfDatasetLocation.FILEDUMP, "datasets/person12.rdf", null, null, null);

        expBuild.readResults(d1, d2);
    }

    public static void test_person11person12() {

        ExperimentManager expBuild = new ExperimentManager();

        Dataset d1 = new Dataset("person11", TypeOfDatasetLocation.FILEDUMP, "datasets/person11.rdf", null, null, null);
        Dataset d2 = new Dataset("person12", TypeOfDatasetLocation.FILEDUMP, "datasets/person12.rdf", null, null, null);

        //imported ExpLinks into the allPoolLinks file
        File fInterlinkingExpandPool = new File("datasets/person11person12_allPoolLinks.nt");


        expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinkingExpandPool, TypeOfMappingGoal.VALIDATION, true);


    }

    public static void testNYTDbpediaPeople_5050() {


        ExperimentManager expBuild = new ExperimentManager();

        Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/nyt1.rdf", null, null, null);
        Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/dbpedia4.rdf", null, null, null);
        File fInterlinking = new File("datasets/nytdbpediapeople_refinterlinking.nt");
        File fNoInterlinking = new File("datasets/nytdbpediapeople_refnointerlinking.nt");

        expBuild.runTrial5050LinksExperiment(d1, d2, fInterlinking, fNoInterlinking, TypeOfMappingGoal.VALIDATION, true);

    }

}

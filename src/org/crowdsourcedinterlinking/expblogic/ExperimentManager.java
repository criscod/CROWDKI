package org.crowdsourcedinterlinking.expblogic;

import org.crowdsourcedinterlinking.mgeneration.CwdfInterlinkingMicrotaskGeneratorImpl;
import org.crowdsourcedinterlinking.mgeneration.DirectLinksGeneratorImpl;
import org.crowdsourcedinterlinking.mgeneration.HCHILinksGeneratorImpl;
import org.crowdsourcedinterlinking.model.Dataset;
import org.crowdsourcedinterlinking.model.TypeOfMappingGoal;
import org.crowdsourcedinterlinking.mpublication.CwdfInterlinkingMicrotaskPublisherImpl;
import org.crowdsourcedinterlinking.rcollection.CwdfInterlinkingResultProcessorImpl;
import org.crowdsourcedinterlinking.rcollection.CwdfInterlinkingResultReaderImpl;

import java.io.File;

/**
 * @author:csarasua Class for managing the business logic of the interlinking validation / enhancement
 */
public class ExperimentManager {


    /**
     * Method for crowdsourcing a set of links by direct input
     * @param d1 source dataset
     * @param d2 target dataset
     * @param fInputInterlinking interlinking to be crowdsourced
     * @param mappingGoal type of mapping microtasks to be generated (e.g. validation or identification)
     * @param context with or without contextual information (was for testing purposes, but it always makes sense with context)
     */
    public void runTrialDirectLinksExperiment(Dataset d1, Dataset d2,
                                              File fInputInterlinking, TypeOfMappingGoal mappingGoal, boolean context) {

        try {

            DirectLinksGeneratorImpl directLinksGen = new DirectLinksGeneratorImpl(d1, d2, fInputInterlinking);


            CwdfInterlinkingMicrotaskGeneratorImpl microGen = new CwdfInterlinkingMicrotaskGeneratorImpl(
                    mappingGoal, context);

            CwdfInterlinkingMicrotaskPublisherImpl microPub = new CwdfInterlinkingMicrotaskPublisherImpl();

            InterlinkingMicrotaskManager microTaskManager = new InterlinkingMicrotaskManager(directLinksGen, microGen, microPub);
            microTaskManager.prepareListOfInterlinkingMicrotasks();


			
			/* from CrowdMAP
			 *  CwdfResultReaderImpl reader = new CwdfResultReaderImpl(o1, o2);
			  
			  CwdfResultProcessorImpl processor = new
			  CwdfResultProcessorImpl();
			  
			  
			  ResultManager resultManager = new ResultManager( reader,
			  processor); resultManager.analyseResultsOfTheCrowd(); 
			  */


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method for crowdsourcing a set of links created after (50% positive examples 50% negative examples) selecting from the ground truth
     * @param d1 source dataset
     * @param d2 target dataset
     * @param fReferenceInterlinking reference interlinking that provides the positive examples of the ground truth
     * @param fReferenceNoInterlinking reference interlinking that provides the negative examples of the ground truth
     * @param mappingGoal type of mapping microtasks to be generated (e.g. validation or identification)
     * @param context with or without contextual information (was for testing purposes, but it always makes sense with context)

     */
    public void runTrial5050LinksExperiment(Dataset d1, Dataset d2,
                                            File fReferenceInterlinking, File fReferenceNoInterlinking, TypeOfMappingGoal mappingGoal, boolean context) {

        try {


            HCHILinksGeneratorImpl halfhalfLinksGen = new HCHILinksGeneratorImpl(d1, d2, fReferenceInterlinking, fReferenceNoInterlinking);


            CwdfInterlinkingMicrotaskGeneratorImpl microGen = new CwdfInterlinkingMicrotaskGeneratorImpl(
                    mappingGoal, context);

            CwdfInterlinkingMicrotaskPublisherImpl microPub = new CwdfInterlinkingMicrotaskPublisherImpl();

            InterlinkingMicrotaskManager microTaskManager = new InterlinkingMicrotaskManager(halfhalfLinksGen, microGen, microPub);
            microTaskManager.prepareListOfInterlinkingMicrotasks();
			

			
			/* from CrowdMAP
			 *  CwdfResultReaderImpl reader = new CwdfResultReaderImpl(o1, o2);
			  
			  CwdfResultProcessorImpl processor = new
			  CwdfResultProcessorImpl();
			  
			  
			  ResultManager resultManager = new ResultManager( reader,
			  processor); resultManager.analyseResultsOfTheCrowd(); 
			  */


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method for crowdsourcing a set of links which are the output of an automatic interlinking tool (confident and uncertain candidate links)
     * @param d1 source dataset
     * @param d2 target dataset
     * @param fDiscoveredInterlinking set of links to be crowdsourced which were discovered with confidence by an automatic interlinking tool
     * @param fUnsureInterlinking set of links to be crowdsourced
     * @param mappingGoal type of mapping microtasks to be generated (e.g. validation or identification)
     * @param context with or without contextual information (was for testing purposes, but it always makes sense with context)
     */
    public void runTrialAlgLinksExperiment(Dataset d1, Dataset d2,
                                           File fDiscoveredInterlinking, File fUnsureInterlinking, TypeOfMappingGoal mappingGoal, boolean context) {

        try {


            DirectLinksGeneratorImpl discoveredLinksGen = new DirectLinksGeneratorImpl(d1, d2, fDiscoveredInterlinking);


            CwdfInterlinkingMicrotaskGeneratorImpl microGen = new CwdfInterlinkingMicrotaskGeneratorImpl(
                    TypeOfMappingGoal.VALIDATION, context);

            CwdfInterlinkingMicrotaskPublisherImpl microPub = new CwdfInterlinkingMicrotaskPublisherImpl();

            InterlinkingMicrotaskManager microTaskManager = new InterlinkingMicrotaskManager(discoveredLinksGen, microGen, microPub);
            microTaskManager.prepareListOfInterlinkingMicrotasks();


            DirectLinksGeneratorImpl unsureLinksGen = new DirectLinksGeneratorImpl(d1, d2, fUnsureInterlinking);


            CwdfInterlinkingMicrotaskGeneratorImpl microGen2 = new CwdfInterlinkingMicrotaskGeneratorImpl(
                    TypeOfMappingGoal.IDENTIFICATIONA, context);

            CwdfInterlinkingMicrotaskPublisherImpl microPub2 = new CwdfInterlinkingMicrotaskPublisherImpl();

            InterlinkingMicrotaskManager microTaskManager2 = new InterlinkingMicrotaskManager(unsureLinksGen, microGen2, microPub2);
            microTaskManager2.prepareListOfInterlinkingMicrotasks();

			
			/* from CrowdMAP
			 *  CwdfResultReaderImpl reader = new CwdfResultReaderImpl(o1, o2);
			  
			  CwdfResultProcessorImpl processor = new
			  CwdfResultProcessorImpl();
			  
			  
			  ResultManager resultManager = new ResultManager( reader,
			  processor); resultManager.analyseResultsOfTheCrowd(); 
			  */


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method for reading the results provided by the crowd on a particular set of interlinking microtasks
     * @param d1 source dataset
     * @param d2 target dataset
     * the job ids are read from the track files
     */
    public void readResults(Dataset d1, Dataset d2) {
        CwdfInterlinkingResultReaderImpl reader = new CwdfInterlinkingResultReaderImpl(d1, d2);

        CwdfInterlinkingResultProcessorImpl processor = new
                CwdfInterlinkingResultProcessorImpl();


        ResultManager resultManager = new ResultManager(reader,
                processor);
        resultManager.analyseResultsOfTheCrowd();
    }


}

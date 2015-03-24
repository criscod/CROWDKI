package org.crowdsourcedinterlinking.expblogic;

import org.crowdsourcedinterlinking.mgeneration.CwdfRelInterlinkingMicrotaskGeneratorImpl;
import org.crowdsourcedinterlinking.mgeneration.DirectInterlinkingChoicesGeneratorImpl;
import org.crowdsourcedinterlinking.mpublication.CwdfRelInterlinkingChoicesMicrotaskPublisherImpl;

import java.io.File;

/**
 * @author csarasua
 * Class to handle the experiment
 * analog to ExperimentManager
 */
public class RelevanceUseCaseManager {



    public void launchAssessmentDirectInterlinkingChoices(File choicesFile)
    {
        DirectInterlinkingChoicesGeneratorImpl choicesGen = new DirectInterlinkingChoicesGeneratorImpl(choicesFile);


        CwdfRelInterlinkingMicrotaskGeneratorImpl microGen = new CwdfRelInterlinkingMicrotaskGeneratorImpl();

        CwdfRelInterlinkingChoicesMicrotaskPublisherImpl microPub = new CwdfRelInterlinkingChoicesMicrotaskPublisherImpl();

        RelInterlinkingMicrotaskManager microTaskManager = new RelInterlinkingMicrotaskManager(choicesGen,
                microGen, microPub);
        microTaskManager.prepareListOfRelInterlinkingChoicesMicrotasks();


    }

    public void readAssessmentDirectInterlinkingChoices()
    {




		/*
		 CwdfResultReaderImpl reader = new CwdfResultReaderImpl(o1, o2);

		  CwdfResultProcessorImpl processor = new
		  CwdfResultProcessorImpl();


		  ResultManager resultManager = new ResultManager( reader,
		  processor); resultManager.analyseResultsOfTheCrowd();
    */
    }

}

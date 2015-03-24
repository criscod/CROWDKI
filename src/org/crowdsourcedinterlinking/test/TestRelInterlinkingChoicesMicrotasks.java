package org.crowdsourcedinterlinking.test;

import org.crowdsourcedinterlinking.expblogic.RelevanceUseCaseManager;
import org.junit.Test;

import java.io.File;

/**
 * @author csarasua
 */
public class TestRelInterlinkingChoicesMicrotasks {

    @Test
    public void testFakeChoicesGeneration()
    {
        RelevanceUseCaseManager manager = new RelevanceUseCaseManager();
        String workingDir = System.getProperty("user.dir");
        String workingDirForFileName = workingDir.replace("\\","/");
        manager.launchAssessmentDirectInterlinkingChoices(new File(workingDirForFileName+"/datasets/choices.txt"));
    }
}

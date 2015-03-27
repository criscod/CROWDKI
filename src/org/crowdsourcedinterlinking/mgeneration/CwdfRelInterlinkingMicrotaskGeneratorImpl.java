package org.crowdsourcedinterlinking.mgeneration;

import com.google.common.io.Files;
import org.crowdsourcedinterlinking.model.InterlinkingChoices;
import org.crowdsourcedinterlinking.model.Microtask;
import org.crowdsourcedinterlinking.model.RelInterlinkingChoicesJobMicrotaskImpl;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author csarasua
 */
public class CwdfRelInterlinkingMicrotaskGeneratorImpl implements RelInterlinkingChoicesMicrotaskGenerator {

    String workingDir = System.getProperty("user.dir");
    String workingDirForFileName = workingDir.replace("\\", "/");

    public CwdfRelInterlinkingMicrotaskGeneratorImpl() {
        //no config
    }

    public Set<Microtask> createMicrotasks(InterlinkingChoices interlinkingChoices) {
        Set<Microtask> result = new HashSet<Microtask>();

        try {


            RelInterlinkingChoicesJobMicrotaskImpl job = new RelInterlinkingChoicesJobMicrotaskImpl();

            job.setTitle("Assess the relevance of this information within a particular context");

            //Read the instructions from the file
            File fileInstructions = new File(workingDirForFileName + "/CML/interlinkingchoices_instructions.txt");
            List<String> linesInstructions = Files.readLines(fileInstructions, Charset.defaultCharset());
            String instructions = new String();
            for (String lineInstr : linesInstructions) {
                instructions = instructions + lineInstr;
            }


            job.setInstructions(instructions);
            job.createUI(interlinkingChoices);
            result.add(job);

            /*
            validationJob
							.setInstructions(instructions);
					validationJob.setLanguage("en");
					// it is a 2 possible answers microtask
					validationJob.setJudgmentsPerUnit(ConfigurationManager
							.getInstance().getjudgmentsPerUnitTwoOptions());
					validationJob.setMaxJudgmentsPerWorker(ConfigurationManager
							.getInstance().getMaxJudgmentsPerWorker());
					validationJob.setPagesPerAssignment(ConfigurationManager
							.getInstance().getPagerPerAssignment());
					// validationJob.setUnitsPerAssignment(ConfigurationManager.getInstance().getUnitsPerAssignment());
					validationJob.setUnitsPerAssignment(realUnitsPerAssignment);
					validationJob.setGoldPerAssignment(ConfigurationManager
							.getInstance().getGoldPerAssignment());
					validationJob.setListOfUnits(listOfUnits);
					validationJob.setGoldenUnits(goldenUnits);
					validationJob.setGoldenUnitsSourcePos(goldenUnitsPos);
					validationJob.setGoldenUnitsSourceNeg(goldenUnitsNeg);

					validationJob.createUI();
             */

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}

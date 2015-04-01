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
 * Class for generating all the microtasks of type interlinking relevance assessment.
 */
public class CwdfRelInterlinkingMicrotaskGeneratorImpl implements RelInterlinkingChoicesMicrotaskGenerator {

    String workingDir = System.getProperty("user.dir");
    String workingDirForFileName = workingDir.replace("\\", "/");

    public CwdfRelInterlinkingMicrotaskGeneratorImpl() {
        //no config
    }

    /**
     *
     * @param interlinkingChoices all the interlinking possibilities to assess in these microtasks
     * @return set of generated relevance assessment microtasks
     */
    public Set<Microtask> createMicrotasks(InterlinkingChoices interlinkingChoices) {
        Set<Microtask> result = new HashSet<Microtask>();

        try {


            // the microtasks are created with survey-style. Therefore, in contrast to other microtasks (e.g. interlinking valkidation) no units need to be uploaded as units.
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



        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}

package org.crowdsourcedinterlinking.expblogic;

import org.crowdsourcedinterlinking.mgeneration.InterlinkingChoicesGenerator;
import org.crowdsourcedinterlinking.mgeneration.RelInterlinkingChoicesMicrotaskGenerator;
import org.crowdsourcedinterlinking.model.InterlinkingChoices;
import org.crowdsourcedinterlinking.model.Microtask;
import org.crowdsourcedinterlinking.mpublication.CwdfService;
import org.crowdsourcedinterlinking.mpublication.MicrotaskPublisher;
import org.crowdsourcedinterlinking.util.ConfigurationManager;

import java.util.Set;
import java.util.UUID;

/**
 * @author csarasua
 * Class to handle all the process related to generating and
 * publishing microtasks for assessing the relevance of interlinking choices
 */
public class RelInterlinkingMicrotaskManager {

    private InterlinkingChoicesGenerator choicesGenerator;


    private RelInterlinkingChoicesMicrotaskGenerator microtaskGenerator;
    private MicrotaskPublisher microtaskPublisher;

    private InterlinkingChoices interlinkingChoices;

    private Set<Microtask> setOfMicrotasks;


    public RelInterlinkingMicrotaskManager(InterlinkingChoicesGenerator choicesGen,
                                        RelInterlinkingChoicesMicrotaskGenerator microtaskGen, MicrotaskPublisher microtaskPub) {
        this.choicesGenerator = choicesGen;
        this.microtaskGenerator = microtaskGen;
        this.microtaskPublisher = microtaskPub;

        UUID id = UUID.randomUUID();
        String trackFilePath = ConfigurationManager.getInstance()
                .getListOfOnlineJobsToAnalyseFile() + id.toString() + ".txt";
        ConfigurationManager.getInstance().setCurrentTrackFile(trackFilePath);
    }



    public void prepareListOfRelInterlinkingChoicesMicrotasks() {
        try {

            this.interlinkingChoices = this.choicesGenerator.generateChoices();
System.out.println("trace");
            this.setOfMicrotasks = this.microtaskGenerator.createMicrotasks(this.interlinkingChoices );
            this.uploadMicrotasksToCwdf();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void uploadMicrotasksToCwdf() {
        try {
            CwdfService s = new CwdfService();

            for (Microtask m : this.setOfMicrotasks) {
                String idGeneratedJob = this.microtaskPublisher
                        .uploadMicrotask(m, s);
                if (idGeneratedJob != null) {
                    // this.microtaskPublisher.orderMicrotask(idGeneratedJob,
                    // s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

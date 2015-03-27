package org.crowdsourcedinterlinking.expblogic;

import org.crowdsourcedinterlinking.mgeneration.InterlinkingMicrotaskGenerator;
import org.crowdsourcedinterlinking.mgeneration.LinksGenerator;
import org.crowdsourcedinterlinking.model.Interlinking;
import org.crowdsourcedinterlinking.model.Microtask;
import org.crowdsourcedinterlinking.mpublication.CwdfService;
import org.crowdsourcedinterlinking.mpublication.MicrotaskPublisher;
import org.crowdsourcedinterlinking.util.ConfigurationManager;

import java.util.Set;
import java.util.UUID;

/**
 * @author:csarasua Manager for the interlinking validation microtasks
 */
public class InterlinkingMicrotaskManager {

    private LinksGenerator linksGenerator;


    private InterlinkingMicrotaskGenerator microtaskGenerator;
    private MicrotaskPublisher microtaskPublisher;

    private Interlinking candidates;

    private Set<Microtask> setOfMicrotasks;

    // when creating the specific PairsGenerato, first create alignment to
    // insert the two ontologies and then set the pairs inside the

    public InterlinkingMicrotaskManager(LinksGenerator linksGen,
                                        InterlinkingMicrotaskGenerator microtaskGen, MicrotaskPublisher microtaskPub) {
        this.linksGenerator = linksGen;
        this.microtaskGenerator = microtaskGen;
        this.microtaskPublisher = microtaskPub;

        UUID id = UUID.randomUUID();
        String trackFilePath = ConfigurationManager.getInstance()
                .getListOfOnlineJobsToAnalyseFile() + id.toString() + ".txt";
        ConfigurationManager.getInstance().setCurrentTrackFile(trackFilePath);
    }


    public void prepareListOfInterlinkingMicrotasks() {
        try {

            this.candidates = this.linksGenerator.generateLinks();

            this.setOfMicrotasks = this.microtaskGenerator
                    .createMicrotasks(this.candidates);
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

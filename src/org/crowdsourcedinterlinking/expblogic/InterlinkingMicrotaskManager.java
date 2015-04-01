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

    //Links generator (which can be based on Cartesian Product / DirectInput / 5050)
    private LinksGenerator linksGenerator;

    //Generator of interlinking microtasks (their UI and data content)
    private InterlinkingMicrotaskGenerator microtaskGenerator;
    // Microtask publisher (e.g. with the CrowdFlower service)
    private MicrotaskPublisher microtaskPublisher;

    // Set of links included in the microtasks
    private Interlinking candidates;
    // Set of microtasks generated
    private Set<Microtask> setOfMicrotasks;

    // when creating the specific PairsGenerator, first create alignment to
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

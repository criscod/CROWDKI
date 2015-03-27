package org.crowdsourcedinterlinking.mgeneration;

import org.crowdsourcedinterlinking.model.Dataset;
import org.crowdsourcedinterlinking.model.Interlink;
import org.crowdsourcedinterlinking.model.Interlinking;
import org.crowdsourcedinterlinking.model.InterlinkingParser;

import java.io.File;
import java.util.Set;

/**
 * @author csarasua
 */
public class HCHILinksGeneratorImpl extends LinksGeneratorImpl {

    private File fReferenceInterlinkig;
    private File fReferenceNoInterlinking;

    public HCHILinksGeneratorImpl(Dataset d1, Dataset d2, File referenceInterlinking, File referenceNoInterlinking) {
        //This generator is used both for 50%links correct and 50%links incorrect, and for the sure ones of the algorithm and the unsure ones - it is just a matter of sending one file or  the other

        this.setDataset1(d1);
        this.setDataset2(d2);

        this.fReferenceInterlinkig = referenceInterlinking;
        this.fReferenceNoInterlinking = referenceNoInterlinking;
    }


    public Interlinking generateLinks() {
        Interlinking result = null;
        Interlinking noInterlinking = null;

        try {
            this.registerDatasetsInTrackFile();


            InterlinkingParser parser = new InterlinkingParser(this.fReferenceInterlinkig);
            parser.parseInterlinking(this.getDataset1(), this.getDataset2());
            result = parser.getInterlinking();
            System.out.println("size of interlinks " + result.getSetOfInterLinks().size());

            InterlinkingParser parser2 = new InterlinkingParser(this.fReferenceNoInterlinking);
            parser2.parseInterlinking(this.getDataset1(), this.getDataset2());
            noInterlinking = parser2.getInterlinking();
            Set<Interlink> setNoInterlinks = noInterlinking.getSetOfInterLinks();
            System.out.println("size of no interlinks " + setNoInterlinks.size());

            result.getSetOfInterLinks().addAll(setNoInterlinks);
            System.out.println("size of interlinks (after adding in theroy the set of not interlinks " + result.getSetOfInterLinks().size());


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}

package org.crowdsourcedinterlinking.model;

import java.util.List;
import java.util.Set;
/**
 * @author csarasua
 */
public class Interlinking {

    private Dataset dataset1;
    private Dataset dataset2;

    private Set<Interlink> setOfInterLinks;
    private List<Interlink> orderedListOfInterlinks;

    public Interlinking(Dataset d1, Dataset d2, Set<Interlink> setL) {
        this.dataset1 = d1;
        this.dataset2 = d2;
        this.setOfInterLinks = setL;
    }

    public Interlinking(Dataset d1, Dataset d2, List<Interlink> listL) {
        this.dataset1 = d1;
        this.dataset2 = d2;
        this.orderedListOfInterlinks = listL;
    }

    public Dataset getDataset1() {
        return dataset1;
    }

    public void setDataset1(Dataset dataset1) {
        this.dataset1 = dataset1;
    }

    public Dataset getDataset2() {
        return dataset2;
    }

    public void setDataset2(Dataset dataset2) {
        this.dataset2 = dataset2;
    }

    public Set<Interlink> getSetOfInterLinks() {
        return setOfInterLinks;
    }

    public void setSetOfInterLinks(Set<Interlink> setOfLinks) {
        this.setOfInterLinks = setOfLinks;
    }

    public List<Interlink> getOrderedListOfInterlinks() {
        return orderedListOfInterlinks;
    }

    public void setOrderedListOfInterlinks(List<Interlink> orderedListOfInterlinks) {
        this.orderedListOfInterlinks = orderedListOfInterlinks;
    }


}

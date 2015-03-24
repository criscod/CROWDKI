package org.crowdsourcedinterlinking.model;

/**
 * @author csarasua
 * Class to keep an individual interlinking choice or possibility.
 * It describes the type of objects to be connected and the predicate used in the links.
 */
public class InterlinkingChoice {

    /**
     * Uri, local name and human label of the type of objects to connect and the predicate to use in the typed links
     */
    private String uriClass1;
    private String labelClass1;

    private String uriClass2;
    private String labelClass2;

    private String uriPredicate;
    private String labelPredicate;

    /**
     * Datasets from which the choices come. It is not relevant for the user, but it can be useful for keeping trails of the choices and for using it afterwards for a recommendation.
     */
    private Dataset d1;
    private Dataset d2;

    public InterlinkingChoice(Dataset d1, Dataset d2, String uri1, String labeluri1, String predicate, String labelPredicate, String uri2, String labeluri2)
    {
        this.d1 = d1;
        this.d2 = d2;

        this.uriClass1 = uri1;
        this.labelClass1 = labeluri1;

        this.uriClass2 = uri2;
        this.labelClass2 = labeluri2;

        this.uriPredicate = predicate;
        this.labelPredicate = labelPredicate;




    }

    public String getUriClass1() {
        return uriClass1;
    }

    public void setUriClass1(String uriClass1) {
        this.uriClass1 = uriClass1;
    }



    public String getLabelClass1() {
        return labelClass1;
    }

    public void setLabelClass1(String labelClass1) {
        this.labelClass1 = labelClass1;
    }

    public String getUriClass2() {
        return uriClass2;
    }

    public void setUriClass2(String uriClass2) {
        this.uriClass2 = uriClass2;
    }


    public String getLabelClass2() {
        return labelClass2;
    }

    public void setLabelClass2(String labelClass2) {
        this.labelClass2 = labelClass2;
    }

    public String getUriPredicate() {
        return uriPredicate;
    }

    public void setUriPredicate(String uriPredicate) {
        this.uriPredicate = uriPredicate;
    }



    public String getLabelPredicate() {
        return labelPredicate;
    }

    public void setLabelPredicate(String labelPredicate) {
        this.labelPredicate = labelPredicate;
    }

    public Dataset getD1() {
        return d1;
    }

    public void setD1(Dataset d1) {
        this.d1 = d1;
    }

    public Dataset getD2() {
        return d2;
    }

    public void setD2(Dataset d2) {
        this.d2 = d2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InterlinkingChoice that = (InterlinkingChoice) o;

        if (!d1.equals(that.d1)) return false;
        if (!d2.equals(that.d2)) return false;
        if (!labelClass1.equals(that.labelClass1)) return false;
        if (!labelClass2.equals(that.labelClass2)) return false;
        if (!labelPredicate.equals(that.labelPredicate)) return false;
        if (!uriClass1.equals(that.uriClass1)) return false;
        if (!uriClass2.equals(that.uriClass2)) return false;
        if (!uriPredicate.equals(that.uriPredicate)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uriClass1.hashCode();
        result = 31 * result + labelClass1.hashCode();
        result = 31 * result + uriClass2.hashCode();
        result = 31 * result + labelClass2.hashCode();
        result = 31 * result + uriPredicate.hashCode();
        result = 31 * result + labelPredicate.hashCode();
        result = 31 * result + d1.hashCode();
        result = 31 * result + d2.hashCode();
        return result;
    }
}

package org.crowdsourcedinterlinking.model;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import java.io.File;

/**
 * @author csarasua
 */
public class DatasetDescription {

    /*
     * Name for identifying the dataset
     */
    private String datasetId;
    /**
     * description containing DCAT, VoiD statements and example resources (described completely) in RDF/XML
     * only have one description per dataset
     */
    private Model description;

    public DatasetDescription(String id, File desc) {
        this.datasetId = id;

        this.description = ModelFactory.createDefaultModel();
        this.description.read("file:///" + desc.getAbsolutePath(), "RDF/XML");

    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public Model getDescription() {
        return description;
    }

    public void setDescription(Model description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatasetDescription that = (DatasetDescription) o;

        if (!datasetId.equals(that.datasetId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return datasetId.hashCode();
    }
}

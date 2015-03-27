package org.crowdsourcedinterlinking.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author csarasua
 */
public class InterlinkingChoices {

    Set<InterlinkingChoice> choices;
    Map<String, DatasetDescription> datasetDescriptions;

    public InterlinkingChoices() {
        this.choices = new HashSet<InterlinkingChoice>();
        this.datasetDescriptions = new HashMap<String, DatasetDescription>();
    }

    public void addChoice(InterlinkingChoice choice) {
        this.choices.add(choice);
    }

    public void addDatasetDescription(DatasetDescription desc) {
        this.datasetDescriptions.put(desc.getDatasetId(), desc);
    }

    public Set<InterlinkingChoice> getChoices() {
        return choices;
    }

    public void setChoices(Set<InterlinkingChoice> choices) {
        this.choices = choices;
    }

    public Map<String, DatasetDescription> getDatasetDescriptions() {
        return datasetDescriptions;
    }

    public void setDatasetDescriptions(Map<String, DatasetDescription> datasetDescriptions) {
        this.datasetDescriptions = datasetDescriptions;
    }
}

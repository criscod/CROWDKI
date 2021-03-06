package org.crowdsourcedinterlinking.model;

import org.crowdsourcedinterlinking.util.URIutils;
/**
 * @author csarasua
 */
public class InterlinkIdentificationUnitDataEntryImpl extends UnitDataEntryImpl {

    //I don�t use this class

    protected String elementA;
    protected String elementB;
    protected String relation;

    protected Dataset datasetA;
    protected Dataset datasetB;

    /*protected String commentA;
    protected String commentB;	*/
    protected String labelA;
    protected String labelB;


    //protected ObjectMapping oMap;

    public InterlinkIdentificationUnitDataEntryImpl(String elA, String elB,
                                                    Dataset dA, Dataset dB) {
        this.elementA = elA;
        this.elementB = elB;

        this.datasetA = dA;
        this.datasetB = dB;

        this.setGoldenUnit(false);
        this.labelA = URIutils.getDefaultLabel(elA);
        this.labelB = URIutils.getDefaultLabel(elB);
        /*

		this.commentA = new String("not available");
		this.commentB = new String("not available");

		
		
			if (ConfigurationManager
					.getInstance().getAlignmentElements().equals("classes"))
					{
						this.oMap = ObjectMapping.CLASSES;
					}
					else if (ConfigurationManager
							.getInstance().getAlignmentElements().equals("properties"))
					{
						this.oMap = ObjectMapping.PROPERTIES;
					}*/

    }


    public void loadInfo() {

        // load commentsA and commentsB
		/*try {
			if (!this.isGoldenUnit()) {

				Model modelA = this.ontologyA.getModel();
				Model modelB = this.ontologyB.getModel();

				String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?comment WHERE { <"
						+ this.elementA
						+ "> rdfs:comment ?comment . FILTER ( lang(?comment) = \"en\" ) } ";

				QueryExecution qe = QueryExecutionFactory.create(queryString,
						modelA);
				ResultSet results = qe.execSelect();
				QuerySolution qs = null;

				while (results.hasNext()) {
					qs = results.nextSolution();
					String comment = qs.getLiteral("comment").getString();

					this.commentA = comment;

				}

				String queryString2 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?comment2 WHERE { <"
						+ this.elementB
						+ "> rdfs:comment ?comment2 . FILTER ( lang(?comment2) = \"en\" ) } ";

				qe = QueryExecutionFactory.create(queryString2, modelB);
				results = qe.execSelect();

				while (results.hasNext()) {
					qs = results.nextSolution();

					String comment2 = qs.getLiteral("comment2").getString();

					this.commentB = comment2;
				}

				// getlabels

				queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?label WHERE { <"
						+ this.elementA
						+ "> rdfs:label ?label . FILTER ( lang(?label) = \"en\" ) } ";

				qe = QueryExecutionFactory.create(queryString, modelA);
				results = qe.execSelect();

				while (results.hasNext()) {
					qs = results.nextSolution();
					String label = qs.getLiteral("label").getString();

					this.labelA = label;

				}

				queryString2 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?label2 WHERE { <"
						+ this.elementB
						+ "> rdfs:label ?label2 . FILTER ( lang(?label2) = \"en\" ) } ";

				qe = QueryExecutionFactory.create(queryString2, modelB);
				results = qe.execSelect();

				while (results.hasNext()) {
					qs = results.nextSolution();

					String label2 = qs.getLiteral("label2").getString();

					this.labelB = label2;
				}

			} // end if(!this.isGoldenUnit())

		} catch (Exception e) {
			e.printStackTrace();
		}*/
    }

    public String getElementA() {
        return elementA;
    }

    public void setElementA(String elementA) {
        this.elementA = elementA;
    }

    public String getElementB() {
        return elementB;
    }

    public void setElementB(String elementB) {
        this.elementB = elementB;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }


    public Dataset getDatasetA() {
        return datasetA;
    }


    public void setDatasetA(Dataset datasetA) {
        this.datasetA = datasetA;
    }


    public Dataset getDatasetB() {
        return datasetB;
    }


    public void setDatasetB(Dataset datasetB) {
        this.datasetB = datasetB;
    }

    /*public String getCommentA() {
        return commentA;
    }

    public void setCommentA(String commentA) {
        this.commentA = commentA;
    }

    public String getCommentB() {
        return commentB;
    }

    public void setCommentB(String commentB) {
        this.commentB = commentB;
    }

    public Ontology getOntologyA() {
        return ontologyA;
    }

    public void setOntologyA(Ontology ontologyA) {
        this.ontologyA = ontologyA;
    }

    public Ontology getOntologyB() {
        return ontologyB;
    }

    public void setOntologyB(Ontology ontologyB) {
        this.ontologyB = ontologyB;
    }
    */
    public String getLabelA() {
        return labelA;
    }

    public void setLabelA(String labelA) {
        this.labelA = labelA;
    }

    public String getLabelB() {
        return labelB;
    }

    public void setLabelB(String labelB) {
        this.labelB = labelB;
    }


}

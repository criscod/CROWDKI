package org.crowdsourcedinterlinking.model;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.Constants;

import java.util.ArrayList;
import java.util.List;
/**
 * @author csarasua
 */
public class InterlinkValidationWithFullContextUnitDataEntryImpl extends
        InterlinkValidationUnitDataEntryImpl {


    public List<FeatureTextValue> getListOfFeaturesA() {
        return listOfFeaturesA;
    }

    public void setListOfFeaturesA(List<FeatureTextValue> listOfFeaturesA) {
        this.listOfFeaturesA = listOfFeaturesA;
    }

    public List<FeatureTextValue> getListOfFeaturesB() {
        return listOfFeaturesB;
    }

    public void setListOfFeaturesB(List<FeatureTextValue> listOfFeaturesB) {
        this.listOfFeaturesB = listOfFeaturesB;
    }

    private List<FeatureTextValue> listOfFeaturesA = new ArrayList<FeatureTextValue>();
    private List<FeatureTextValue> listOfFeaturesB = new ArrayList<FeatureTextValue>();

    private String firstWordA = new String();
    private String firstWordB = new String();

    /*//private String superClassA = new String("not available");
    private Set<String> superClassesA = new HashSet<String>();
    private Set<String> siblingsA = new HashSet<String>();
    private Set<String> subClassesA = new HashSet<String>();
    private Set<String> instancesA = new HashSet<String>(); // IMPROVEMENTS
    //private String superClassB = new String("not available");
    private Set<String> superClassesB = new HashSet<String>();
    private Set<String> siblingsB = new HashSet<String>();
    private Set<String> subClassesB = new HashSet<String>();
    private Set<String> instancesB = new HashSet<String>(); // IMPROVEMENTS
*/
    public InterlinkValidationWithFullContextUnitDataEntryImpl(String elA,
                                                               String elB, String rel, Dataset dA, Dataset dB) {
        super(elA, elB, rel, dA, dB);
    }

    public void loadInfo() {
        try {
            if (!this.isGoldenUnit()) {

				/*
				String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?label WHERE { <"
						+ this.elementA
						+ "> rdfs:label ?label . OPTIONAL {FILTER ( lang(?label) = \"en\" )} } ";
*/

                String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX dc: <http://purl.org/dc/elements/1.1/> PREFIX foaf: <http://xmlns.com/foaf/0.1/> PREFIX skos: <http://www.w3.org/2004/02/skos/core#> SELECT ?label WHERE { {<"
                        + this.elementA
                        + "> rdfs:label ?label } UNION {<"
                        + this.elementA
                        + "> dc:title ?label } UNION {<"
                        + this.elementA
                        + "> foaf:name ?label }UNION {<"
                        + this.elementA
                        //+ "> skos:prefLabel ?label } OPTIONAL {FILTER ( lang(?label) = \"en\" || lang(?label)=\"\")} } ";
                        + "> skos:prefLabel ?label } FILTER (lang(?label) = \"\" || langMatches(lang(?label),\"en\")) } ";


                ResultSet results = null;
                QuerySolution qs;


                if (this.datasetA.getTypeOfLocation().equals(TypeOfDatasetLocation.SPARQLENDPOINT)) {


                    Query query = QueryFactory.create(queryString);
                    QueryExecution qexec = QueryExecutionFactory.sparqlService(this.datasetA.getLocation(), query);

                    results = qexec.execSelect();
                }
                //If the data set has access via the model (File)
                else if (this.datasetA.getTypeOfLocation().equals(TypeOfDatasetLocation.FILEDUMP)) {
                    QueryExecution qexec2 = QueryExecutionFactory.create(queryString, this.datasetA.getModel());
                    results = qexec2.execSelect();

                }


                while (results.hasNext()) {
                    qs = results.nextSolution();
                    String label = qs.getLiteral("label").getString();

                    this.labelA = label;

                }
				
								

				/*String queryString2 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?label2 WHERE { <"
						+ this.elementB
						+ "> rdfs:label ?label2 . OPTIONAL {FILTER ( lang(?label2) = \"en\" )} } ";
				*/

                String queryString2 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX dc: <http://purl.org/dc/elements/1.1/> PREFIX foaf: <http://xmlns.com/foaf/0.1/> PREFIX skos: <http://www.w3.org/2004/02/skos/core#> SELECT ?label2 WHERE { {<"
                        + this.elementB
                        + "> rdfs:label ?label2 } UNION {<"
                        + this.elementB
                        + "> dc:title ?label2 } UNION {<"
                        + this.elementB
                        + "> foaf:name ?label2 }UNION {<"
                        + this.elementB
                        //+ "> skos:prefLabel ?label2} OPTIONAL {FILTER ( lang(?label2) = \"en\" || lang(?label2)=\"\")} } ";
                        + "> skos:prefLabel ?label2} FILTER (lang(?label2) = \"\" || langMatches(lang(?label2),\"en\")) } ";


                if (this.datasetB.getTypeOfLocation().equals(TypeOfDatasetLocation.SPARQLENDPOINT)) {


                    Query query = QueryFactory.create(queryString2);
                    QueryExecution qexec = QueryExecutionFactory.sparqlService(this.datasetB.getLocation(), query);

                    results = qexec.execSelect();
                }
                //If the data set has access via the model (File)
                else if (this.datasetB.getTypeOfLocation().equals(TypeOfDatasetLocation.FILEDUMP)) {
                    QueryExecution qexec2 = QueryExecutionFactory.create(queryString2, this.datasetB.getModel());
                    results = qexec2.execSelect();

                }


                while (results.hasNext()) {
                    qs = results.nextSolution();


                    String label2 = qs.getLiteral("label2").getString();
                    this.labelB = label2;


                }
//the template is checked with value, so feature does not need to show "not available by default"

                //Load the features to be loaded from the configuration file

                String listOfFeatureNamesA = ConfigurationManager.getInstance().getListOfFeaturesA(); //URI
                String[] featuresA = listOfFeatureNamesA.split(";");

                String messagesStringA = ConfigurationManager.getInstance().getListOfMessagesFeaturesAForUI();
                String[] messagesA = messagesStringA.split(";");

                //featuresA.length == messagesA.length (otherwise error log)
                for (int i = 0; i < featuresA.length; i++) {


                    //Find the value for feature s of elemA
                    String valueOfSA = findValueOfElementA(featuresA[i]);
                    if (valueOfSA.equals("''")) {
                        valueOfSA = new String("not available");
                    }

                    String valueOfSA2 = new String("not available");
                    FeatureTextValue fVA;
// 					if (!valueOfSA.equals("not available") && valueOfSA.length()>50)
//					{
// 						if(valueOfSA.length()<60)
// 						{
// 							valueOfSA2 = valueOfSA.substring(0, valueOfSA.length()-1)+"...";
// 						}
// 						else
// 						{
//						valueOfSA2 = valueOfSA.substring(0, 60)+"...";
// 						}
//						fVA = new FeatureTextValue(featuresA[i], messagesA[i],valueOfSA2);
//					}
// 					else
// 					{
                    fVA = new FeatureTextValue(featuresA[i], messagesA[i], valueOfSA);
//				}


                    listOfFeaturesA.add(fVA);
					
					/*String valueOfSA= findValueOfElementA(featuresA[i]);
					
									
					FeatureTextValue fVA = new FeatureTextValue(featuresA[i], messagesA[i],valueOfSA);
					listOfFeaturesA.add(fVA);*/
                }


                String listOfFeatureNamesB = ConfigurationManager.getInstance().getListOfFeaturesB(); //URI
                String[] featuresB = listOfFeatureNamesB.split(";");

                String messagesStringB = ConfigurationManager.getInstance().getListOfMessagesFeaturesBForUI();
                String[] messagesB = messagesStringB.split(";");

                for (int j = 0; j < featuresB.length; j++) {


                    //Find the value for feature s of elemB


                    String valueOfSB = findValueOfElementB(featuresB[j]);
                    if (valueOfSB.equals("''")) {
                        valueOfSB = new String("not available");
                    }
                    String valueOfSB2 = new String("not available");
                    FeatureTextValue fVB;
//					if (!valueOfSB.equals("not available") && valueOfSB.length()>50)
//					{
//						if(valueOfSB.length()<60)
// 						{
// 							valueOfSB2 = valueOfSB.substring(0, valueOfSB.length()-1)+"...";
// 						}
// 						else
// 						{
//						valueOfSB2=valueOfSB.substring(0, 60)+"...";
// 						}
//						fVB = new FeatureTextValue(featuresB[j],messagesB[j], valueOfSB2);
//					}
//					else
//					{


                    fVB = new FeatureTextValue(featuresB[j], messagesB[j], valueOfSB);
//					}


                    listOfFeaturesB.add(fVB);
					
					/*String valueOfSB= findValueOfElementB(featuresB[j]);
				
					
					FeatureTextValue fVB = new FeatureTextValue(featuresB[j],messagesB[j], valueOfSB);
					listOfFeaturesB.add(fVB);*/
                }


                //get the value of the first property
                String firstValueA = listOfFeaturesA.get(0).getValue();
                String firstValueB = listOfFeaturesB.get(0).getValue();
                String tempA = firstValueA;
                String a = null;
                String tempB = firstValueB;
                String b = null;


                if (tempA.startsWith(" <br> <b> Text snippet1</b>: ... ")) {
                    a = tempA.substring(33);
                    tempA = a;
                }

                while (tempA.startsWith(" ")) {
                    a = tempA.substring(1, tempA.length() - 1);
                    tempA = a;
                }
                while (tempB.startsWith(" ")) {
                    b = tempB.substring(1, tempB.length() - 1);
                    tempB = b;
                }

                String[] wordsA = tempA.split(" ");
                String[] wordsB = tempB.split(" ");
                // It contains "'" in the beginning

                if (tempA.startsWith("'")) {
                    //this.firstWordA = wordsA[0].substring(1);
                    this.firstWordA = wordsA[0].substring(1, wordsA[0].length() - 1);
                } else {
                    this.firstWordA = wordsA[0];
                }
                if (tempB.startsWith("'")) {

                    this.firstWordB = wordsB[0].substring(1, wordsB[0].length() - 1);
                } else {

                    this.firstWordB = wordsB[0];
                }

                //System.out.println(".");
				

/*
				Model modelA = this.ontologyA.getModel();
				// System.out.println("modelA @MappingValidationWithFullContextUnitDataEntryImpl: "+this.ontologyA.getUri()+" ; "+this.ontologyA.getLocation()+" ; "+this.ontologyA.getName());
				Model modelB = this.ontologyB.getModel();
				// System.out.println("modelB @MappingValidationWithFullContextUnitDataEntryImpl: "+this.ontologyB.getUri()+" ; "+this.ontologyB.getLocation()+" ; "+this.ontologyB.getName());

				// getComments
				String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?comment WHERE { <"
						+ this.elementA
						+ "> rdfs:comment ?comment . OPTIONAL {FILTER ( lang(?comment) = \"en\" )} } ";

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
						+ "> rdfs:comment ?comment2 . OPTIONAL {FILTER ( lang(?comment2) = \"en\" )} } ";

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
						+ "> rdfs:label ?label . OPTIONAL {FILTER ( lang(?label) = \"en\" )} } ";

				qe = QueryExecutionFactory.create(queryString, modelA);
				results = qe.execSelect();

				while (results.hasNext()) {
					qs = results.nextSolution();
					String label = qs.getLiteral("label").getString();

					this.labelA = label;

				}

				queryString2 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?label2 WHERE { <"
						+ this.elementB
						+ "> rdfs:label ?label2 . OPTIONAL {FILTER ( lang(?label2) = \"en\" )} } ";

				qe = QueryExecutionFactory.create(queryString2, modelB);
				results = qe.execSelect();

				while (results.hasNext()) {
					qs = results.nextSolution();

					String label2 = qs.getLiteral("label2").getString();

					this.labelB = label2;
				}

// getsuperclasses
				
				if (oMap.equals(ObjectMapping.CLASSES))
				{	
				queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?superclass ?superclassr WHERE { {<"
						+ this.elementA
						+ "> rdfs:subClassOf ?superclassr . OPTIONAL {?superclassr rdfs:label ?superclass .} ?superclassr rdf:type owl:Class} UNION {<"
						+ this.elementA
						+ "> rdfs:subClassOf ?superclassr . OPTIONAL{?superclassr rdfs:label ?superclass .} ?superclassr rdf:type rdfs:Class} OPTIONAL {FILTER ( lang(?superclass) = \"en\")}  }";
				}
				else if (oMap.equals(ObjectMapping.PROPERTIES))
				{
					queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?superclass ?superclassr WHERE { {<"
						+ this.elementA
						+ "> rdfs:subPropertyOf ?superclassr . OPTIONAL {?superclassr rdfs:label ?superclass .} ?superclassr rdf:type owl:DatatypeProperty} UNION {<"
						+ this.elementA
						+ "> rdfs:subPropertyOf ?superclassr . OPTIONAL{?superclassr rdfs:label ?superclass .} ?superclassr rdf:type owl:ObjectProperty} UNION {<"
						+ this.elementA
						+ "> rdfs:subPropertyOf ?superclassr . OPTIONAL{?superclassr rdfs:label ?superclass .} ?superclassr rdf:type rdf:Property} OPTIONAL {FILTER ( lang(?superclass) = \"en\")}  }";
				}
				
				// separate by ; and if it is owl thing don�t add
				qe = QueryExecutionFactory.create(queryString, modelA);
				results = qe.execSelect();

				// if there are several ones we get only one - version 1
				
				int numberOfSuperClasses = 0;
				while (results.hasNext()) {
					qs = results.nextSolution();
					String superclass = null;
					if (!qs.getResource("superclassr").isAnon())
					{
					if (qs.getLiteral("superclass") != null) {
						superclass = qs.getLiteral("superclass").getString();
					} else {
						
						superclass = URIutils.getDefaultLabel(qs.getResource(
								"superclassr").getURI());
						
					}
					if (numberOfSuperClasses < 5) {
						this.superClassesA.add(superclass);
						numberOfSuperClasses = numberOfSuperClasses + 1;
					}
					}
				}

				if (oMap.equals(ObjectMapping.CLASSES))
				{
				queryString2 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?superclass2 ?superclass2r WHERE { {<"
						+ this.elementB
						+ "> rdfs:subClassOf ?superclass2r . OPTIONAL {?superclass2r rdfs:label ?superclass2 .} ?superclass2r rdf:type owl:Class} UNION {<"
						+ this.elementB
						+ "> rdfs:subClassOf ?superclass2r . OPTIONAL {?superclass2r rdfs:label ?superclass2 .} ?superclass2r rdf:type rdfs:Class} OPTIONAL{FILTER ( lang(?superclass2) = \"en\")}  }";
				}
				else if (oMap.equals(ObjectMapping.PROPERTIES))
				{
					queryString2 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?superclass2 ?superclass2r WHERE { {<"
						+ this.elementB
						+ "> rdfs:subPropertyOf ?superclass2r . OPTIONAL {?superclass2r rdfs:label ?superclass2 .} ?superclass2r rdf:type owl:DatatypeProperty} UNION {<"
						+ this.elementB
						+ "> rdfs:subPropertyOf ?superclass2r . OPTIONAL {?superclass2r rdfs:label ?superclass2 .} ?superclass2r rdf:type owl:ObjectProperty} UNION {<"
						+ this.elementB
						+ "> rdfs:subPropertyOf ?superclass2r . OPTIONAL {?superclass2r rdfs:label ?superclass2 .} ?superclass2r rdf:type rdf:Property} OPTIONAL{FILTER ( lang(?superclass2) = \"en\")}  }";
				}
				
				// separate by ; and if it is owl thing don�t add
				qe = QueryExecutionFactory.create(queryString2, modelB);
				results = qe.execSelect();

				// if there are several ones we get only one
				int numberOfSuperClasses2 = 0;
				while (results.hasNext()) {
					qs = results.nextSolution();
					String superclass2 = null;
					if (!qs.getResource("superclass2r").isAnon())
					{
					if (qs.getLiteral("superclass2") != null) {
						superclass2 = qs.getLiteral("superclass2").getString();
					} else {
						
						superclass2 = URIutils.getDefaultLabel(qs.getResource(
								"superclass2r").getURI());
						
					}
					if (numberOfSuperClasses < 5) {
						this.superClassesB.add(superclass2);
						numberOfSuperClasses2 = numberOfSuperClasses2 + 1;
					}
					}
				}


				// getsiblings

				if (oMap.equals(ObjectMapping.CLASSES))
				{
				
					queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?sibling ?siblingr WHERE { {<"
						+ this.elementA
						+ "> rdfs:subClassOf ?superclassr . ?superclassr rdf:type owl:Class . ?siblingr rdfs:subClassOf ?superclassr . OPTIONAL {?siblingr rdfs:label ?sibling } } UNION {<"
						+ this.elementA
						+ "> rdfs:subClassOf ?superclassr . ?superclassr rdf:type rdfs:Class . ?siblingr rdfs:subClassOf ?superclassr . OPTIONAL {?siblingr rdfs:label ?sibling} } OPTIONAL {FILTER ( lang(?sibling) = \"en\" )} }";
				
					
					}
				else if (oMap.equals(ObjectMapping.PROPERTIES))
				{
					queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?sibling ?siblingr WHERE { {<"
						+ this.elementA
						+ "> rdfs:subPropertyOf ?superclassr . ?superclassr rdf:type owl:DatatypeProperty . ?siblingr rdfs:subPropertyOf ?superclassr . OPTIONAL {?siblingr rdfs:label ?sibling } } UNION {<"
						+ this.elementA
						+ "> rdfs:subPropertyOf ?superclassr . ?superclassr rdf:type rdfs:ObjectProperty . ?siblingr rdfs:subPropertyOf ?superclassr . OPTIONAL {?siblingr rdfs:label ?sibling} } UNION {<"
						+ this.elementA
						+ "> rdfs:subPropertyOf ?superclassr . ?superclassr rdf:type rdf:Property . ?siblingr rdfs:subPropertyOf ?superclassr . OPTIONAL {?siblingr rdfs:label ?sibling} }  OPTIONAL {FILTER ( lang(?sibling) = \"en\" )} }";
				}
				
				// separate by ; and if it is owl thing don�t add
				qe = QueryExecutionFactory.create(queryString, modelA);
				results = qe.execSelect();

				int numberOfSiblings = 0;
				// if there are several ones we get only one
				while (results.hasNext()) {
					qs = results.nextSolution();
					String sibling = null;
					if (!qs.getResource("siblingr").isAnon())
					{
					if (qs.getLiteral("sibling") != null) {
						sibling = qs.getLiteral("sibling").getString();
					} else {
						
						sibling = URIutils.getDefaultLabel(qs.getResource(
								"siblingr").getURI());
					}

					if (numberOfSiblings < 3
							&& !sibling.equals(URIutils
									.getDefaultLabel(this.elementA))) {
						this.siblingsA.add(sibling);
						numberOfSiblings = numberOfSiblings + 1;
					}
					}
				}

				if (oMap.equals(ObjectMapping.CLASSES))
				{
				queryString2 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?sibling2 ?sibling2r WHERE {{<"
						+ this.elementB
						+ "> rdfs:subClassOf ?superclass2r . ?superclass2r rdf:type owl:Class . ?sibling2r rdfs:subClassOf ?superclass2r . OPTIONAL {?sibling2r rdfs:label ?sibling2} } UNION {<"
						+ this.elementB
						+ "> rdfs:subClassOf ?superclass2r . ?superclass2r rdf:type rdfs:Class . ?sibling2r rdfs:subClassOf ?superclass2r . OPTIONAL {?sibling2r rdfs:label ?sibling2}  } OPTIONAL {FILTER ( lang(?sibling2) = \"en\")} }";
				
				}
				else if (oMap.equals(ObjectMapping.PROPERTIES))
				{
					queryString2 = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?sibling2 ?sibling2r WHERE {{<"
						+ this.elementB
						+ "> rdfs:subPropertyOf ?superclass2r . ?superclass2r rdf:type owl:DatatypeProperty . ?sibling2r rdfs:subPropertyOf ?superclass2r . OPTIONAL {?sibling2r rdfs:label ?sibling2} } UNION {<"
						+ this.elementB
						+ "> rdfs:subPropertyOf ?superclass2r . ?superclass2r rdf:type owl:ObjectProperty . ?sibling2r rdfs:subPropertyOf ?superclass2r . OPTIONAL {?sibling2r rdfs:label ?sibling2}  } UNION {<"
						+ this.elementB
						+ "> rdfs:subPropertyOf ?superclass2r . ?superclass2r rdf:type rdf:Property . ?sibling2r rdfs:subPropertyOf ?superclass2r . OPTIONAL {?sibling2r rdfs:label ?sibling2}  }  OPTIONAL {FILTER ( lang(?sibling2) = \"en\")} }";
				
				
				}
				
				qe = QueryExecutionFactory.create(queryString2, modelB);
				results = qe.execSelect();

				int numberOfSiblings2 = 0;
				// if there are several ones we get only one
				while (results.hasNext()) {
					qs = results.nextSolution();
					String sibling2 = null;
					if (!qs.getResource("sibling2r").isAnon())
					{
					if (qs.getLiteral("sibling2") != null) {
						sibling2 = qs.getLiteral("sibling2").getString();
					} else {
						sibling2 = URIutils.getDefaultLabel(qs.getResource(
								"sibling2r").getURI());
					}

					if (numberOfSiblings2 < 3
							&& !sibling2.equals(URIutils
									.getDefaultLabel(this.elementB))) {
						this.siblingsB.add(sibling2);
						numberOfSiblings2 = numberOfSiblings2 + 1;
					}
					}
				}

				// getsubclasses

				if (oMap.equals(ObjectMapping.CLASSES))
				{
				queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?subclass ?subclassr WHERE {{?subclassr rdfs:subClassOf <"
						+ this.elementA
						+ "> . ?subclassr rdf:type owl:Class . OPTIONAL {?subclassr rdfs:label ?subclass} } UNION {?subclassr rdfs:subClassOf <"
						+ this.elementA
						+ "> . ?subclassr rdf:type rdfs:Class . OPTIONAL {?subclassr rdfs:label ?subclass} } OPTIONAL {FILTER ( lang(?subclass) = \"en\") } }";
				}
				else if (oMap.equals(ObjectMapping.PROPERTIES))
				{
					
					queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?subclass ?subclassr WHERE {{?subclassr rdfs:subPropertyOf <"
						+ this.elementA
						+ "> . ?subclassr rdf:type owl:DatatypeProperty . OPTIONAL {?subclassr rdfs:label ?subclass} } UNION {?subclassr rdfs:subPropertyOf <"
						+ this.elementA
						+ "> . ?subclassr rdf:type owl:ObjectProperty . OPTIONAL {?subclassr rdfs:label ?subclass} } UNION {?subclassr rdfs:subPropertyOf <"
						+ this.elementA
						+ "> . ?subclassr rdf:type rdf:Property . OPTIONAL {?subclassr rdfs:label ?subclass} } OPTIONAL {FILTER ( lang(?subclass) = \"en\") } }";
				
				}
				
				qe = QueryExecutionFactory.create(queryString, modelA);
				results = qe.execSelect();

				int numberOfSubClasses = 0;
				// if there are several ones we get only one
				while (results.hasNext()) {
					qs = results.nextSolution();
					String subclass = null;
					if (!qs.getResource("subclassr").isAnon())
					{
					if (qs.getLiteral("subclass") != null) {
						subclass = qs.getLiteral("subclass").getString();
					} else {
						subclass = URIutils.getDefaultLabel(qs.getResource(
								"subclassr").getURI());
					}

					if (numberOfSubClasses < 5) {
						this.subClassesA.add(subclass);
						numberOfSubClasses = numberOfSubClasses + 1;
					}
					}
				}

				if (oMap.equals(ObjectMapping.CLASSES))
				{
				queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?subclass2 ?subclass2r WHERE { {?subclass2r rdfs:subClassOf <"
						+ this.elementB
						+ "> . ?subclass2r rdf:type owl:Class . OPTIONAL {?subclass2r rdfs:label ?subclass2 } } UNION {?subclass2r rdfs:subClassOf <"
						+ this.elementB
						+ "> . ?subclass2r rdf:type rdfs:Class . OPTIONAL {?subclass2r rdfs:label ?subclass2} } OPTIONAL {FILTER ( lang(?subclass2) = \"en\")} }";
				}
				else if (oMap.equals(ObjectMapping.PROPERTIES))
				{
					queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?subclass2 ?subclass2r WHERE { {?subclass2r rdfs:subPropertyOf <"
						+ this.elementB
						+ "> . ?subclass2r rdf:type owl:DatatypeProperty . OPTIONAL {?subclass2r rdfs:label ?subclass2 } } UNION {?subclass2r rdfs:subPropertyOf <"
						+ this.elementB
						+ "> . ?subclass2r rdf:type owl:ObjectProperty . OPTIONAL {?subclass2r rdfs:label ?subclass2} } UNION {?subclass2r rdfs:subPropertyOf <"
						+ this.elementB
						+ "> . ?subclass2r rdf:type rdf:Property . OPTIONAL {?subclass2r rdfs:label ?subclass2} }  OPTIONAL {FILTER ( lang(?subclass2) = \"en\")} }";
				}
				
				qe = QueryExecutionFactory.create(queryString, modelB);
				results = qe.execSelect();

				int numberOfSubClasses2 = 0;
				// if there are several ones we get only one
				while (results.hasNext()) {
					qs = results.nextSolution();
					String subclass2 = null;
					if (!qs.getResource("subclass2r").isAnon())
					{
					if (qs.getLiteral("subclass2") != null) {
						subclass2 = qs.getLiteral("subclass2").getString();
					} else {
						subclass2 = URIutils.getDefaultLabel(qs.getResource(
								"subclass2r").getURI());
					}
					if (numberOfSubClasses2 < 5) {
						this.subClassesB.add(subclass2);
						numberOfSubClasses2 = numberOfSubClasses2 + 1;
					}
					}
				}
				*/

            }// end if (!this.isGoldenUnit())
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public String getFirstWordA() {
        return firstWordA;
    }

    public void setFirstWordA(String firstWordA) {
        this.firstWordA = firstWordA;
    }

    public String getFirstWordB() {
        return firstWordB;
    }

    public void setFirstWordB(String firstWordB) {
        this.firstWordB = firstWordB;
    }

    public void loadInfoFromString(List<FeatureTextValue> listOfFeaturesA, List<FeatureTextValue> listOfFeaturesB, String goldData) {
        try {
            List<FeatureTextValue> listFeaturesA = new ArrayList<FeatureTextValue>();
            List<FeatureTextValue> listFeaturesB = new ArrayList<FeatureTextValue>();

            //Parse the string - labela, labelb, property, values..., validationreadiolabel
            String[] pairsOfData = goldData.split(";");
            //Label, Label
            String[] labelA = pairsOfData[0].split(",");
            if (labelA[0].startsWith("labela")) {
                System.out.println(labelA[1]);
                this.labelA = labelA[1];
                this.setElementA(Constants.NS_CROWD + this.labelA); //invented
            }

            String[] labelB = pairsOfData[1].split(",");
            if (labelB[0].startsWith("labelb")) {
                this.labelB = labelB[1];
                this.setElementB(Constants.NS_CROWD + this.labelB);
            }


            int sizeA = listOfFeaturesA.size();
            int sizeB = listOfFeaturesB.size();

            int lastIndex = 0;
            for (int i = 0; i <= sizeA - 1; i++) {
                String[] attributeValue = pairsOfData[(i * 2) + 2].split(",");
                String attribute = attributeValue[0];
                String value = attributeValue[1];
                FeatureTextValue ftv;
                if (attribute.startsWith("value")) {

                    ftv = new FeatureTextValue(listOfFeaturesA.get(i).getFeature(), listOfFeaturesA.get(i).getMessageText(), value);
                    if (attribute.endsWith("a")) {
                        this.listOfFeaturesA.add(ftv);
                        if (((i * 2) + 2) > lastIndex) {
                            lastIndex = (i * 2) + 2;
                        }
                    }


                } else {
                    //finished parsing the values
                    break;
                }

            }

            for (int j = 0; j <= sizeB - 1; j++) {
                String[] attributeValue = pairsOfData[(j * 2) + 3].split(",");
                String attribute = attributeValue[0];
                String value = attributeValue[1];
                FeatureTextValue ftv;
                if (attribute.startsWith("value")) {

                    ftv = new FeatureTextValue(listOfFeaturesB.get(j).getFeature(), listOfFeaturesB.get(j).getMessageText(), value);
                    if (attribute.endsWith("b")) {
                        this.listOfFeaturesB.add(ftv);
                        if (((j * 2) + 3) > lastIndex) {
                            lastIndex = (j * 2) + 3;
                        }
                    }


                } else {
                    //finished parsing the values
                    break;
                }

            }
			/*int lastIndex=0;
			for (int i=0; i<listOfFeaturesA.size()*2;i++)
			{
				int divNum= listOfFeaturesA.size();
				int indexFeature = (i%divNum);
				lastIndex=2+(divNum*2);
				String[] attributeValue=pairsOfData[i+2].split(",");
				String attribute = attributeValue[0];
				String value = attributeValue[1];
				FeatureTextValue ftv;
				if (attribute.startsWith("value"))
				{
					
					ftv = new FeatureTextValue(listOfFeaturesA.get(indexFeature).getFeature(),listOfFeaturesA.get(indexFeature).getMessageText(), value);
					if(attribute.endsWith("a"))
					{
					this.listOfFeaturesA.add(ftv);
					}
					
					
				}
				else
				{
					//finished parsing the values
					break;
				}
			}
			
			lastIndex=0;
			for (int i=0; i<listOfFeaturesB.size()*2;i++)
			{
				int divNum= listOfFeaturesB.size();
				int indexFeature = (i%divNum);
				lastIndex=2+(divNum*2);
				String[] attributeValue=pairsOfData[i+2].split(",");
				String attribute = attributeValue[0];
				String value = attributeValue[1];
				FeatureTextValue ftv;
				if (attribute.startsWith("value"))
				{
					
					ftv = new FeatureTextValue(listOfFeaturesB.get(indexFeature).getFeature(),listOfFeaturesB.get(indexFeature).getMessageText(), value);
					if (attribute.endsWith("b"))
					{
						this.listOfFeaturesB.add(ftv);
					}
					
					
				}
				else
				{
					//finished parsing the values
					break;
				}
			}
			
			*/
            int index = 0;
            if (sizeA > sizeB) {
                index = (sizeA * 2) + 2;
            } else //if(sizeA<sizeB) OR (sizeA==sizeB)
            {
                index = (sizeB * 2) + 2;
            }


            //here we ignore the property if there is one at the end
            String[] property = pairsOfData[index].split("&");
            if (property[0].startsWith("property")) {
                this.relation = property[1];
                System.out.println("prop: " + this.relation);

                //the validationradiolabel is written in the configuration.properties file like validationradiolabelsameas - sameas is the localName or the URI of the property
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //	public String getSuperClassA() {
//		return superClassA;
//	}
//
//	public void setSuperClassA(String superClassA) {
//		this.superClassA = superClassA;
//	}

	/*public Set<String> getSiblingsA() {
		return siblingsA;
	}

	public void setSiblingsA(Set<String> siblingsA) {
		this.siblingsA = siblingsA;
	}

	public Set<String> getSubClassesA() {
		return subClassesA;
	}

	public void setSubClassesA(Set<String> subClassesA) {
		this.subClassesA = subClassesA;
	}

	public Set<String> getInstancesA() {
		return instancesA;
	}

	public void setInstancesA(Set<String> instancesA) {
		this.instancesA = instancesA;
	}

//	public String getSuperClassB() {
//		return superClassB;
//	}
//
//	public void setSuperClassB(String superClassB) {
//		this.superClassB = superClassB;
//	}

	public Set<String> getSiblingsB() {
		return siblingsB;
	}

	public void setSiblingsB(Set<String> siblingsB) {
		this.siblingsB = siblingsB;
	}

	public Set<String> getSubClassesB() {
		return subClassesB;
	}

	public void setSubClassesB(Set<String> subClassesB) {
		this.subClassesB = subClassesB;
	}

	public Set<String> getInstancesB() {
		return instancesB;
	}

	public void setInstancesB(Set<String> instancesB) {
		this.instancesB = instancesB;
	}

	public String getStringSuperClassesA() {
		String result = new String(" ");
		try {

			for (String s : this.superClassesA) {
				result = result + "'";
				result = result + s;
				result = result + "'";
				result = result + " ";
			}
			if (this.superClassesA.size() == 0 && result.equals(" ")) {
				result = new String("not available");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public String getStringSuperClassesB() {
		String result = new String(" ");
		try {

			for (String s : this.superClassesB) {
				result = result + "'";
				result = result + s;
				result = result + "'";
				result = result + " ";
			}
			if (this.superClassesB.size() == 0 && result.equals(" ")) {
				result = new String("not available");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	
	public String getStringSiblingsA() {
		String result = new String(" ");
		try {

			for (String s : this.siblingsA) {
				result = result + "'";
				result = result + s;
				result = result + "'";
				result = result + " ";
			}
			if (this.siblingsA.size() == 0 && result.equals(" ")) {
				result = new String("not available");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getStringSiblingsB() {
		String result = new String(" ");
		try {

			for (String s : this.siblingsB) {
				result = result + "'";
				result = result + s;
				result = result + "'";
				result = result + " ";
			}
			if (this.siblingsB.size() == 0 && result.equals(" ")) {
				result = new String("not available");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getStringSubClassesA() {
		String result = new String(" ");
		try {

			for (String s : this.subClassesA) {
				result = result + "'";
				result = result + s;
				result = result + "'";
				result = result + " ";
			}
			if (this.subClassesA.size() == 0 && result.equals(" ")) {
				result = new String("not available");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getStringSubClassesB() {
		String result = new String(" ");
		try {

			for (String s : this.subClassesB) {
				result = result + "'";
				result = result + s;
				result = result + "'";
				result = result + " ";
			}
			if (this.subClassesB.size() == 0 && result.equals(" ")) {
				result = new String("not available");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getStringInstancesA() {
		String result = new String(" ");
		try {

			for (String s : this.instancesA) {
				result = result + "'";
				result = result + s;
				result = result + "'";
				result = result + " ";
			}
			if (this.instancesA.size() == 0 && result.equals(" ")) {
				result = new String("not available");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getStringInstancesB() {
		String result = new String(" ");
		try {

			for (String s : this.instancesB) {
				result = result + "'";
				result = result + s;
				result = result + "'";
				result = result + " ";
			}
			if (this.instancesB.size() == 0 && result.equals(" ")) {
				result = new String("not available");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Set<String> getSuperClassesA() {
		return superClassesA;
	}

	public void setSuperClassesA(Set<String> superClassesA) {
		this.superClassesA = superClassesA;
	}

	public Set<String> getSuperClassesB() {
		return superClassesB;
	}

	public void setSuperClassesB(Set<String> superClassesB) {
		this.superClassesB = superClassesB;
	}
*/

    // could be with String a or b and do it, but maybe in the feature I would like to treat differently the source and the target so I separate
    private String findValueOfElementA(String feature) {
        String result = new String(" ");
        String queryString;
        ResultSet results = null;
        QuerySolution qs;
        String[] subFeatures;
        try {
            if (feature.contains(" ")) //it is a "complex" feature. For example I need the rdfs:label of the related lode:atPlace location. Firt jump into the location resource and then get the label of such resource
            {
                subFeatures = feature.split(" "); // I define I have only two levels - otherwise update here
                String feature1 = subFeatures[0];
                String feature2 = subFeatures[1];

                //queryString = "SELECT ?value WHERE { <"+this.elementA+"> <"+feature1+"> ?valuefeature1 . ?valuefeature1 <"+feature2+"> ?value FILTER ( lang(?value)=\"en\" || lang(?value)=\"\" )}";
                if (feature1.equals("http://linkedevents.org/ontology/involvedAgent") || feature1.equals("http://linkedevents.org/ontology/atPlace")) {
                    queryString = "SELECT ?value WHERE { <" + this.elementA + "> <" + feature1 + "> ?valuefeature1 . ?valuefeature1 <http://www.w3.org/2002/07/owl#sameAs> <" + this.elementB + "> . ?valuefeature1 <" + feature2 + "> ?value FILTER (lang(?value) = \"\" || langMatches(lang(?value),\"en\")) } ";

                } else {
                    queryString = "SELECT ?value WHERE { <" + this.elementA + "> <" + feature1 + "> ?valuefeature1 . ?valuefeature1 <" + feature2 + "> ?value FILTER (lang(?value) = \"\" || langMatches(lang(?value),\"en\")) } ";

                }

            } else {
                if (feature.equals("http://data.nytimes.com/elements/topicPage")) {
                    queryString = "SELECT ?value WHERE { <" + this.elementA + "> <" + feature + "> ?value }";
                } else {
                    //queryString = "SELECT ?value WHERE { <"+this.elementA+"> <"+feature+"> ?value FILTER ( lang(?value)=\"en\" || lang(?value)=\"\" )}";
                    queryString = "SELECT ?value WHERE { <" + this.elementA + "> <" + feature + "> ?value FILTER (lang(?value) = \"\" || langMatches(lang(?value),\"en\")) }";
                }
            }


            //System.out.println("the query to load: "+queryString);
            //If the data set has access via SPARQL endpoint

            if (this.datasetA.getTypeOfLocation().equals(TypeOfDatasetLocation.SPARQLENDPOINT)) {


                Query query = QueryFactory.create(queryString);
                QueryExecution qexec = QueryExecutionFactory.sparqlService(this.datasetA.getLocation(), query);

                results = qexec.execSelect();
            }
            //If the data set has access via the model (File)
            else if (this.datasetA.getTypeOfLocation().equals(TypeOfDatasetLocation.FILEDUMP)) {
                QueryExecution qexec2 = QueryExecutionFactory.create(queryString, this.datasetA.getModel());
                results = qexec2.execSelect();

            }
            int count = 1;
            while (results.hasNext()) {
                qs = results.nextSolution();
                RDFNode valueNode = qs.get("value");
                //check, but the query should always look for literal values
                if (count < 6) {
                    if (valueNode instanceof Literal) {

                        if (feature.equals("http://www.gesis.org/infolis#textSnippet")) // make it more general
                        {

                            result = result + "<br> <b> Text snippet" + count + "</b>: ... ";
                            result = result + valueNode.asLiteral().getString();
                            result = result + " ... ";

                        } else {
                            result = result + "'";
                            result = result + valueNode.asLiteral().getString();
                            result = result + "'";
                            result = result + " ";
                        }
                    } else {
                        if (feature.equals("http://data.nytimes.com/elements/topicPage") || feature.equals("http://www.w3.org/2003/01/geo/wgs84_pos#lat") || feature.equals("http://www.w3.org/2003/01/geo/wgs84_pos#long")) {
                            result = result + valueNode.asResource().getURI().toString();
                        } else {
                            result = result + "'";
                            result = result + valueNode.asResource().getURI().toString();
                            result = result + "'";
                            result = result + " ";
                        }
                    }
                } else {
                    break;
                }
                count = count + 1;
            }
            if (result == null || result.equals(" ")) {
                result = new String("not available");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("uriA: " + this.elementA);
        System.out.println("feature: " + feature);
        System.out.println("value: " + result);
        return result;
    }

    private String findValueOfElementB(String feature) {
        String result = new String(" ");
        String queryString;
        ResultSet results = null;
        QuerySolution qs;
        String[] subFeatures;
        try {
            if (feature.contains(" ")) //it is a "complex" feature. For example I need the rdfs:label of the related lode:atPlace location. Firt jump into the location resource and then get the label of such resource
            {
                subFeatures = feature.split(" "); // I define I have only two levels - otherwise update here
                String feature1 = subFeatures[0];
                String feature2 = subFeatures[1];

                //	queryString = "SELECT ?value WHERE { <"+this.elementB+"> <"+feature1+"> ?valuefeature1 . ?valuefeature1 <"+feature2+"> ?value FILTER ( lang(?value)=\"en\" || lang(?value)=\"\" )}";
                queryString = "SELECT ?value WHERE { <" + this.elementB + "> <" + feature1 + "> ?valuefeature1 . ?valuefeature1 <" + feature2 + "> ?value FILTER (lang(?value) = \"\" || langMatches(lang(?value),\"en\")) }";

            } else {

                //queryString = "SELECT ?value WHERE { <"+this.elementB+"> <"+feature+"> ?value FILTER ( lang(?value)=\"en\" || lang(?value)=\"\" )}";
                queryString = "SELECT ?value WHERE { <" + this.elementB + "> <" + feature + "> ?value  FILTER (lang(?value) = \"\" || langMatches(lang(?value),\"en\")) }";
            }


            //System.out.println("the query to load: "+queryString);
            //If the data set has access via SPARQL endpoint

            if (this.datasetB.getTypeOfLocation().equals(TypeOfDatasetLocation.SPARQLENDPOINT)) {


                Query query = QueryFactory.create(queryString);
                QueryExecution qexec = QueryExecutionFactory.sparqlService(this.datasetB.getLocation(), query);

                results = qexec.execSelect();
            }
            //If the data set has access via the model (File)
            else if (this.datasetB.getTypeOfLocation().equals(TypeOfDatasetLocation.FILEDUMP)) {
                QueryExecution qexec2 = QueryExecutionFactory.create(queryString, this.datasetB.getModel());
                results = qexec2.execSelect();

            }

            while (results.hasNext()) {
                qs = results.nextSolution();
                RDFNode valueNode = qs.get("value");
                //check, but the query should always look for literal values
                if (valueNode instanceof Literal) {
                    result = result + "'";
                    result = result + valueNode.asLiteral().getString();
                    result = result + "'";
                    result = result + " ";
                } else {
                    if (feature.equals("http://www.w3.org/2003/01/geo/wgs84_pos#long") || feature.equals("http://www.w3.org/2003/01/geo/wgs84_pos#lat")) {
                        result = result + valueNode.asResource().getURI().toString();
                    } else {

                        result = result + "'";
                        result = result + valueNode.asResource().getURI().toString();
                        result = result + "'";
                        result = result + " ";
                    }
                }

            }
            if (result == null || result.equals(" ")) {
                result = new String("not available");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("uriB: " + this.elementB);
        System.out.println("feature: " + feature);
        System.out.println("value: " + result);
        return result;
    }


}

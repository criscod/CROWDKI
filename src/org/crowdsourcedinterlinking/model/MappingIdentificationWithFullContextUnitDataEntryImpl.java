package org.crowdsourcedinterlinking.model;

import java.util.HashSet;
import java.util.Set;

import org.crowdsourcedinterlinking.util.ObjectMapping;
import org.crowdsourcedinterlinking.util.URIutils;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class MappingIdentificationWithFullContextUnitDataEntryImpl extends
		MappingIdentificationUnitDataEntryImpl {
	//private String superClassA = new String("not available");
	private Set<String> superClassesA = new HashSet<String>();
	private Set<String> siblingsA = new HashSet<String>();
	private Set<String> subClassesA = new HashSet<String>();
	private Set<String> instancesA = new HashSet<String>();; // IMPROVEMENTS
	//private String superClassB = new String("not available");
	private Set<String> superClassesB = new HashSet<String>();
	private Set<String> siblingsB = new HashSet<String>();
	private Set<String> subClassesB = new HashSet<String>();
	private Set<String> instancesB = new HashSet<String>();; // IMPROVEMENTS

	public MappingIdentificationWithFullContextUnitDataEntryImpl(String elA,
			String elB, Ontology oA, Ontology oB) {
		super(elA, elB, oA, oB);

	}

	public void loadInfo() {
		try {

			if (!this.isGoldenUnit()) {
				Model modelA = this.ontologyA.getModel();
				Model modelB = this.ontologyB.getModel();

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
				
				// separate by ; and if it is owl thing don«t add
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
				
				// separate by ; and if it is owl thing don«t add
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
				
				// separate by ; and if it is owl thing don«t add
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

			}// end if (!this.isGoldenUnit())
		}

		 catch (Exception e) {
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
	

	public Set<String> getSiblingsA() {
		return siblingsA;
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

}

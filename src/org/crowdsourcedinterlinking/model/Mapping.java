package org.crowdsourcedinterlinking.model;

import com.hp.hpl.jena.rdf.model.Resource;

public class Mapping {

	private String id;

	private Resource elementA;
	private Resource elementB;
	private Relation relation;

	private double measure;

	private boolean invented;

	public Mapping(Resource elemA, Resource elemB, Relation rel, double m) {
		this.elementA = elemA;
		this.elementB = elemB;
		this.relation = rel;
		this.measure = m;

		this.id = elemA.getLocalName() + elemB.getLocalName();
		this.invented = false;

	}

	public Mapping(Resource elemA, Resource elemB, Relation rel) {
		this.elementA = elemA;
		this.elementB = elemB;
		this.relation = rel;

		this.id = elemA.getLocalName() + elemB.getLocalName();
		this.invented = false;
	}

	public boolean isInvented() {
		return invented;
	}

	public void setInvented(boolean invented) {
		this.invented = invented;
	}

	public Resource getElementA() {
		return elementA;
	}

	public void setElementA(Resource elementA) {
		this.elementA = elementA;
	}

	public Resource getElementB() {
		return elementB;
	}

	public void setElementB(Resource elementB) {
		this.elementB = elementB;
	}

	public String getRelation() {

		String resultRelation = "=";

		switch (this.relation) {
		case SIMILAR:
			resultRelation = "=";
			break;
		case GENERAL:
			// resultRelation=">";
			resultRelation = "&gt;";
			break;
		case SPECIFIC:
			// resultRelation="<";
			resultRelation = "&lt;";
			break;
		case UNKNOWN: // this is only for generatingPairs case Cartesian and
						// Algorithm (without the relation)
			resultRelation = "";
			break;
		}

		return resultRelation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public String getMeasure() {
	
		
		return String.valueOf(measure);
	}

	public void setMeasure(double measure) {
		this.measure = measure;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/*
	 * @Override public int hashCode() {
	 * 
	 * return this.relation.toString().hashCode() * 31 +
	 * (this.elementA.getURI().hashCode() * this.elementB.getURI().hashCode());
	 * 
	 * 
	 * }
	 * 
	 * @Override public boolean equals(Object o) {
	 * 
	 * 
	 * 
	 * if(!(o instanceof Mapping) ){ return false; }
	 * 
	 * Mapping map = (Mapping)o;
	 * 
	 * 
	 * if( this.elementA.getURI().equals(map.elementA.getURI()) &&
	 * this.elementB.getURI().equals(map.elementB.getURI()) &&
	 * this.relation.toString().equals(map.relation.toString())) { return true ;
	 * } else {
	 * 
	 * return false; }
	 * 
	 * 
	 * 
	 * 
	 * }
	 */

}

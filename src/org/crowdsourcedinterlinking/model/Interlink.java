package org.crowdsourcedinterlinking.model;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class Interlink {

	private String id;

	private Resource elementA;
	private Resource elementB;
	private Property relation;

	private double measure;

	private boolean invented;

	public Interlink(Resource elemA, Resource elemB, Property rel, double m) {
		this.elementA = elemA;
		this.elementB = elemB;
		this.relation = rel;
		this.measure = m;

		this.id = elemA.getLocalName() + elemB.getLocalName();
		this.invented = false;

	}

	public Interlink(Resource elemA, Resource elemB, Property rel) {
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

	public Property getRelation() {

		return this.relation;
	}

	public void setRelation(Property relation) {
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Interlink interlink = (Interlink) o;

		if (!elementA.equals(interlink.elementA)) return false;
		if (!elementB.equals(interlink.elementB)) return false;
		if (!id.equals(interlink.id)) return false;
		if (!relation.equals(interlink.relation)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + elementA.hashCode();
		result = 31 * result + elementB.hashCode();
		result = 31 * result + relation.hashCode();
		return result;
	}
}

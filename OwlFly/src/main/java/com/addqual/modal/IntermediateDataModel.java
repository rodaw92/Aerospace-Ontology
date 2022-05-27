package com.addqual.modal;

import java.util.ArrayList;
import java.util.List;

public class IntermediateDataModel {
	String xmlns;
	String resource;
	String uri;
	
	List<IdmAttribute> dataProperties;
	List<IdmPredicate> predicates;
	
	public IntermediateDataModel() {
		dataProperties = new ArrayList<IdmAttribute>();
		predicates = new ArrayList<IdmPredicate>();
	}

	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public List<IdmAttribute> getDataProperties() {
		return dataProperties;
	}

	public void setDataProperties(List<IdmAttribute> dataProperties) {
		this.dataProperties = dataProperties;
	}

	public List<IdmPredicate> getPredicates() {
		return predicates;
	}

	public void setPredicates(List<IdmPredicate> predicates) {
		this.predicates = predicates;
	}
	
}

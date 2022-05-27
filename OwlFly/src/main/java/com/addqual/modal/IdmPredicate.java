package com.addqual.modal;

import java.util.List;

public class IdmPredicate {
	String xmlns;
	String name;
	boolean direction = true; //. True [outgoing edge], and False [incoming edge]
	
	String objectResource;
	String objectUri;
	String objectXmlns;
	List<IdmAttribute> objDataProperties;
	
	public IdmPredicate() {
		
	}
	
	
	public IdmPredicate(String xmlns, String name, boolean direction, String objectResource, String objectUri,
			String objectXmlns) {
		super();
		this.xmlns = xmlns;
		this.name = name;
		this.direction = direction;
		this.objectResource = objectResource;
		this.objectUri = objectUri;
		this.objectXmlns = objectXmlns;
	}


	public String getXmlns() {
		return xmlns;
	}
	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean getDirection() {
		return direction;
	}
	public void setDirection(boolean direction) {
		this.direction = direction;
	}
	public String getObjectResource() {
		return objectResource;
	}
	public void setObjectResource(String objectResource) {
		this.objectResource = objectResource;
	}
	public String getObjectUri() {
		return objectUri;
	}
	public void setObjectUri(String objectUri) {
		this.objectUri = objectUri;
	}
	public String getObjectXmlns() {
		return objectXmlns;
	}
	public void setObjectXmlns(String objectXmlns) {
		this.objectXmlns = objectXmlns;
	}


	public List<IdmAttribute> getObjDataProperties() {
		return objDataProperties;
	}

	public void setObjDataProperties(List<IdmAttribute> objDataProperties) {
		this.objDataProperties = objDataProperties;
	}
	
	
	
}

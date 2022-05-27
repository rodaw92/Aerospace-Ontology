package com.addqual.modal;

public class IdmAttribute {
	String xmlns;
	String attributeName;
	String attributeValue;
	String dataType;
	
	public IdmAttribute() {
		
	}

	public IdmAttribute(String xmlns, String attributeName, String attributeValue, String dataType) {
		super();
		this.xmlns = xmlns;
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
		this.dataType = dataType;
	}



	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
}

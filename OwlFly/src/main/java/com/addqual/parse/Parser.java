package com.addqual.parse;

public abstract class Parser {
	//. add generic methods and constructors here
	//. connect(uri)
	//. parse();
	//. IMD
	public abstract void connect() throws ClassNotFoundException, java.sql.SQLException ;
	public abstract void DbLoadIndividuals();
}

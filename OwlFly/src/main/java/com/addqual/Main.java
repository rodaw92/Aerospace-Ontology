package com.addqual;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.addqual.modal.IdmAttribute;
import com.addqual.modal.IdmPredicate;
import com.addqual.modal.IntermediateDataModel;
import com.addqual.parse.PostgresParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
	static String xml_ns =  "http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual" + "#";
	static String prefix = "ontaddqual";
	static String owlFile = "OwlAddQual.owl";
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		PostgresParser pgp = new PostgresParser(owlFile, xml_ns, prefix);
		pgp.setOutputFile("addqualdb_populated.owl");
		pgp.connect();
		pgp.DbLoadIndividuals();
	}

}
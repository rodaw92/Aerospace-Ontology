package com.addqual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.OWL2;

import com.addqual.modal.IdmAttribute;
import com.addqual.modal.IdmPredicate;
import com.addqual.modal.IntermediateDataModel;

public class AddQualOwlModel {
	private String owlFile;
	private String NAME_SPACE;
	private String ONT_PREFIX;

	private OntModel owlModel;

	public AddQualOwlModel(String owlFile, String nameSpace, String ontPrefix) {
		this.owlFile = owlFile;
		this.NAME_SPACE = nameSpace; // . name space includes # sign at the end of the string.
		this.ONT_PREFIX = ontPrefix;

		loadOntology();
	}

	private void loadOntology() {
		owlModel = ModelFactory.createOntologyModel();
		// . InputStream in = FileManager.get().open(owlFile); //. deprecated!
		// . InputStream in = StreamManager.get().open(owlFile); //. works
		InputStream in = RDFDataMgr.open(owlFile);
		if (in == null) {
			throw new IllegalArgumentException("File: not found");
		}
		// read the RDF/XML file
		owlModel.read(in, null);
		// write it to standard out
		owlModel.write(System.out);

		owlModel.setNsPrefix(ONT_PREFIX, NAME_SPACE);
	}

	private void loadOntology(OntModelSpec ont_spec) {

		owlModel = ModelFactory.createOntologyModel(ont_spec);
		InputStream in = RDFDataMgr.open(owlFile);

		if (in == null) {
			throw new IllegalArgumentException("File: not found");
		}
		// read the RDF/XML file
		owlModel.read(in, null);
		// write it to standard out
		owlModel.write(System.out);

		owlModel.setNsPrefix(ONT_PREFIX, NAME_SPACE);
	}

	public AddQualOwlModel populateOntology(List<IntermediateDataModel> dataIndividuals) {
		// . create subject individuals if not exists.
		// --- loadIndividualsIfNotExists(dataIndividuals);

		// . create and overwrite subject individuals.
		loadAndOverwriteIndividuals(dataIndividuals);

		int indCounter = 0;
		for (IntermediateDataModel dataInd : dataIndividuals) {

			Individual subject = owlModel.getIndividual(dataInd.getXmlns() + dataInd.getUri());
			if (subject == null) {
				// .System.err.println("Individual [" + dataInd.getUri() + "] not found!");
				System.err.println("Individual  [" + dataInd.getUri() + "] not found!!");
				System.err.println("Exiting...");
				System.exit(0);
			}

			for (IdmPredicate dataPred : dataInd.getPredicates()) {

				Individual obj = owlModel.getIndividual(dataPred.getObjectXmlns() + dataPred.getObjectUri());
				if (obj == null) {
					System.err.println("Individual [" + dataPred.getObjectUri() + "] not found!");
					System.err.println("Creating Individual [" + dataPred.getObjectUri() + "] without attributes!");

					obj = owlModel.createIndividual(dataPred.getObjectXmlns() + dataPred.getObjectUri(),
							owlModel.createResource(dataPred.getObjectXmlns() + dataPred.getObjectResource()));
					obj.addRDFType(OWL2.NamedIndividual);

					// . Add Datatype Properties
					for (IdmAttribute objDataAttr : dataPred.getObjDataProperties()) {
						DatatypeProperty dataProp = owlModel
								.getDatatypeProperty(objDataAttr.getXmlns() + objDataAttr.getAttributeName());
						obj.addProperty(dataProp, objDataAttr.getAttributeValue());
					}

					owlModel.write(System.out);
				}

				ObjectProperty predicate = owlModel.getObjectProperty(dataPred.getXmlns() + dataPred.getName());

				// . if direction = true : outgoing relation else incoming relation
				if (dataPred.getDirection()) {
					// . outgoing relation : subject -[predicate]-> object
					subject.addProperty(predicate, obj);
				} else {
					// . incoming relation : object -[predicate]-> subject
					obj.addProperty(predicate, subject);
					owlModel.write(System.out);
				}

			}

			owlModel.write(System.out);
			
		}

		return this;
	}

	public boolean save(String out_owl_file){
		File file = new File(out_owl_file);
		PrintStream stream;
		boolean saved = true;
		try {
			stream = new PrintStream(file);
			// System.out.println("From now on "+file.getAbsolutePath()+" will be your
			// console");
			System.setOut(stream);

			owlModel.write(stream); ////////////////////////////////
			stream.close();
			stream.flush();
			// System.setOut(stream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block  
			e.printStackTrace();
			saved = false;
		}

		return saved;
	}

	public int loadIndividualsIfNotExists(List<IntermediateDataModel> dataIndividuals) {
		int indCounter = 0;

		for (IntermediateDataModel dataInd : dataIndividuals) {

			Individual subject = owlModel.getIndividual(dataInd.getXmlns() + dataInd.getUri());
			if (subject == null) {
				// .System.err.println("Individual [" + dataInd.getUri() + "] not found!");
				System.out.println("Creating Individual [" + dataInd.getUri() + "]");

				subject = owlModel.createIndividual(dataInd.getXmlns() + dataInd.getUri(),
						owlModel.createResource(dataInd.getXmlns() + dataInd.getResource()));
				subject.addRDFType(OWL2.NamedIndividual);

				// . Add Datatype Properties
				for (IdmAttribute dataAttr : dataInd.getDataProperties()) {
					DatatypeProperty dataProp = owlModel
							.getDatatypeProperty(dataAttr.getXmlns() + dataAttr.getAttributeName());
					subject.addProperty(dataProp, dataAttr.getAttributeValue());
				}

				owlModel.write(System.out);
			}

			indCounter++;
		}

		System.err.println("=====================================================");
		System.err.println("===== Created " + indCounter + " Individuals!  ======");
		System.err.println("===== Now Adding Predicates =========================");
		System.err.println();

		return indCounter;
	}

	public int loadAndOverwriteIndividuals(List<IntermediateDataModel> dataIndividuals) {

		int indCounter = 0;

		for (IntermediateDataModel dataInd : dataIndividuals) {

			Individual subject = owlModel.getIndividual(dataInd.getXmlns() + dataInd.getUri());
			if (subject == null) {
				// .System.err.println("Individual [" + dataInd.getUri() + "] not found!");
				System.out.println("Creating Individual [" + dataInd.getUri() + "]");
				System.out.println("xml: " + dataInd.getXmlns());
				System.out.print("resource: " + dataInd.getResource());

				subject = owlModel.createIndividual(dataInd.getXmlns() + dataInd.getUri(),
						owlModel.createResource(dataInd.getXmlns() + dataInd.getResource()));
				subject.addRDFType(OWL2.NamedIndividual);

			}

			// . Add Datatype Properties
			for (IdmAttribute dataAttr : dataInd.getDataProperties()) {
				DatatypeProperty dataProp = owlModel
						.getDatatypeProperty(dataAttr.getXmlns() + dataAttr.getAttributeName());
				subject.addProperty(dataProp, dataAttr.getAttributeValue());
			}

			owlModel.write(System.out);

			indCounter++;
		}

		System.err.println("=====================================================");
		System.err.println("===== Created " + indCounter + " Individuals!  ======");
		System.err.println("===== Now Adding Predicates =========================");
		System.err.println();

		return indCounter;
	}

	public String getOwlFile() {
		return owlFile;
	}

	public String getNAME_SPACE() {
		return NAME_SPACE;
	}

	public OntModel getOwlModel() {
		return owlModel;
	}

}

package com.addqual.parse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.addqual.AddQualOwlModel;
import com.addqual.modal.IdmAttribute;
import com.addqual.modal.IdmPredicate;
import com.addqual.modal.IntermediateDataModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.jena.sparql.pfunction.library.version;

public class PostgresParser extends Parser {
	// . 1. connect to the database
	// . 2. read the contents
	// . 3. parse them into IMD

	String url = "jdbc:postgresql://localhost:5432/addqualdb";
	String user = "postgres";
	String password = "xl@010101";
	static String prefix = "ontaddqual";
	static String owlFile = "OwlAddQual.owl";

	List<IntermediateDataModel> idmArr = new ArrayList<IntermediateDataModel>();
	private String xml_ns = "http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#";
	private Connection conn;
	private String outputFile;

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	/**
	 * 
	 * @param owlFile
	 * @param xml_ns
	 * @param prefix
	 */
	public PostgresParser(String owlFile, String xml_ns, String prefix) {
		this.owlFile = owlFile;
		this.xml_ns = xml_ns;
		this.prefix = prefix;

	}

	public void connect() throws ClassNotFoundException {

		Class.forName("org.postgresql.Driver");

		try {
			conn = DriverManager.getConnection(url, user, password);

		} catch (SQLException e) {
			e.printStackTrace();

		}

	}

	public void DbLoadIndividuals() {

		try {

			 ClientSenderOfPackageHasKeyContact();
			 //EmployerHasKeyContact();
			 ClientKeyContactCommunicatorOfEmployer();
			 EmployerKeyContactCommunicatorOfClient();
			 BatchHasPart();
			 GRNAssignedToPackage();
			 JobCardPartOfOperator();
			 StatsGeneratedByScanner();
			 OperatorFuctions();
			 GomCreatorOfLazerScanner();
			 PackageSentByClientHasBatch();
			PartMeasuredByMeasurementTool();
			 LazerScanningStatsByLazerScanner();
			 VisualInspectionImagePartOperator();
			LazerScannerScanningSerialisedStats();

			conn.close();

			(new AddQualOwlModel(owlFile,
					xml_ns,
					prefix)).populateOntology(idmArr).save(this.outputFile);

			/*
			 * ObjectMapper objMapper1 = new ObjectMapper();
			 * 
			 * try {
			 * PrintStream o = new PrintStream(new File("Json.json"));
			 * PrintStream console = System.out;
			 * System.setOut(o);
			 * System.out.println(objMapper1.writeValueAsString(idmArr));
			 * } catch (FileNotFoundException e) {
			 * // TODO Auto-generated catch block
			 * e.printStackTrace();
			 * 
			 * }
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void ClientSenderOfPackageHasKeyContact() {
		try {

			Statement stmt = conn.createStatement();

			String sqlClient = "SELECT client_id, company_name, industry, country, post_code FROM table_client";
			ResultSet rs = stmt.executeQuery(sqlClient);
			int i = 1;
			while (rs.next()) {
				String client_id = rs.getString("client_id");
				String company_name = rs.getString("company_name");
				String industry = rs.getString("industry");
				String country = rs.getString("country");
				String post_code = rs.getString("post_code");
				List<IdmAttribute> dataProperties = new ArrayList<IdmAttribute>();
				List<IdmPredicate> predicates = new ArrayList<IdmPredicate>();

				IntermediateDataModel idm = new IntermediateDataModel();
				idm.setXmlns(xml_ns);
				idm.setUri(NODES.Client.name() + client_id);
				idm.setResource(NODES.Client.name());

				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.clientId.name(), client_id, "String"));
				dataProperties.add(new IdmAttribute("http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
						ATTRIBUTES.companyName.name(), company_name, "String"));
				dataProperties.add(new IdmAttribute("http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
						ATTRIBUTES.industry.name(), industry, "String"));
				dataProperties.add(new IdmAttribute("http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
						ATTRIBUTES.country.name(), country, "String"));
				dataProperties.add(new IdmAttribute("http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
						ATTRIBUTES.postCode.name(), post_code, "String"));

				idm.setDataProperties(dataProperties);
				// String grnQuery = "SELECT project,po,coc,comments,timestamp FROM table_grn
				// WHERE client_id='"
				// + client_id.trim() + "'";
				String serialised = "SELECT  sr.serial_no,gr.type ,db_id , development , description ,part_number, quantity, weight, scale FROM table_serialised sr, table_jobcard jc, table_grn gr where gr.grn_id = jc.grn_id and sr.grn_id = gr.grn_id and gr.client_id ='"
						+ client_id.trim() + "'";
				Statement stmt2 = conn.createStatement();

				ResultSet rs2 = stmt2.executeQuery(serialised);
				int j = 1;
				while (rs2.next()) {

					// String project = rs.getString("project");
					String type = rs2.getString("type");
					String serial_no = rs2.getString("serial_no");
					String db_id = rs2.getString("db_id");
					String development = rs2.getString("development");
					String description = rs2.getString("description");
					String part_number = rs2.getString("part_number");
					// String quantity = rs2.getString("quantity");
					// String weight = rs2.getString("weight");
					// String scale = rs2.getString("scale");

					List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.partType.name(), type, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.serialNo.name(), serial_no, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.databaseId.name(), db_id, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.development.name(), development, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.description.name(), description, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.partNumber.name(), part_number, "String"));
					// objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.quantity.name(),
					// quantity, "String"));
					// objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.weight.name(), weight,
					// "String"));
					// objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.scale.name(), scale,
					// "String"));

					IdmPredicate predicate = new IdmPredicate(xml_ns,
							PREDICATES.isSenderOf.name(), true, NODES.Serialised.name(),
							NODES.Serialised.name() + serial_no,
							"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
					predicate.setObjDataProperties(objDataProps);
					predicates.add(predicate);

					idm.setPredicates(predicates);
					j++;

				}
				String KeyContact = "SELECT Key_contact_name, email   FROM table_client where client_id='"
						+ client_id.trim() + "'";

				Statement stmt3 = conn.createStatement();

				ResultSet rs3 = stmt3.executeQuery(KeyContact);

				while (rs3.next()) {

					String name = rs3.getString("Key_contact_name");

					List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.name.name(), name, "String"));

					IdmPredicate predicate = new IdmPredicate(xml_ns,
							PREDICATES.hasPart.name(), true, NODES.KeyContact.name(), NODES.KeyContact.name() + name,
							xml_ns);
					predicate.setObjDataProperties(objDataProps);
					predicates.add(predicate);

					idm.setPredicates(predicates);
				}
				i++;
				idmArr.add(idm);

			}
			/*
			 * ObjectMapper objMapper1 = new ObjectMapper();
			 * try {
			 * System.out.println(objMapper1.writeValueAsString(idmArr));
			 * } catch (JsonProcessingException e) {
			 * // TODO Auto-generated catch block
			 * e.printStackTrace();
			 * }
			 */

			// (new AddQualOwlModel("OwlAddQual.owl",
			// "http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
			// "ontaddqual")).populateOntology(idmArr);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/*
	 * private void EmployerHasKeyContact() {
	 * try {
	 * 
	 * String company_name = "AddQual";
	 * String industry = "Aerospace manufactoring";
	 * String country = "UK";
	 * String post_code = "DE24 9FU";
	 * List<IdmAttribute> dataProperties = new ArrayList<IdmAttribute>();
	 * List<IdmPredicate> predicates = new ArrayList<IdmPredicate>();
	 * 
	 * IntermediateDataModel idm = new IntermediateDataModel();
	 * idm.setXmlns(xml_ns);
	 * idm.setUri(NODES.Employer.name());
	 * idm.setResource(NODES.Employer.name());
	 * 
	 * dataProperties.add(new IdmAttribute(xml_ns,
	 * ATTRIBUTES.companyName.name(), company_name, "String"));
	 * // dataProperties.add(new
	 * // IdmAttribute(
	 * "http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
	 * // "industry", industry, "String"));
	 * // dataProperties.add(new
	 * // IdmAttribute(
	 * "http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
	 * // "country", country, "String"));
	 * // dataProperties.add(new
	 * // IdmAttribute(
	 * "http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
	 * // "post_code", post_code, "String"));
	 * 
	 * idm.setDataProperties(dataProperties);
	 * 
	 * String Key_contact_name = "Ben Anderson";
	 * 
	 * List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();
	 * 
	 * objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.name.name(),
	 * Key_contact_name, "String"));
	 * 
	 * IdmPredicate predicate = new IdmPredicate(xml_ns,
	 * PREDICATES.hasPart.name(), true, NODES.KeyContact.name(), "keyContact",
	 * "http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
	 * predicate.setObjDataProperties(objDataProps);
	 * predicates.add(predicate);
	 * 
	 * idm.setPredicates(predicates);
	 * 
	 * idmArr.add(idm);
	 * 
	 * // (new AddQualOwlModel("OwlAddQual.owl",
	 * // "http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
	 * // "ontaddqual")).populateOntology(idmArr);
	 * 
	 * } catch (Exception e) {
	 * 
	 * e.printStackTrace();
	 * }
	 * }
	 */
	private void BatchHasPart() {
		try {

			Statement stmt = conn.createStatement();

			String batch = "SELECT grn_id, 'batch no' as batch_no, element, sigma_ , mean_ , usl ,lsl , cp , cpk ,data_sam   FROM table_batch";
			ResultSet rs = stmt.executeQuery(batch);

			while (rs.next()) {
				String batchNumber = rs.getString("batch_no");
				String grn_id = rs.getString("grn_id");
				String element = rs.getString("element");
				String sigma_ = rs.getString("sigma_");
				String mean_ = rs.getString("mean_");
				String usl = rs.getString("usl");
				String lsl = rs.getString("lsl");
				String cp = rs.getString("cp");
				String cpk = rs.getString("cpk");
				String data_sam = rs.getString("data_sam");

				List<IdmAttribute> dataProperties = new ArrayList<IdmAttribute>();
				List<IdmPredicate> predicates = new ArrayList<IdmPredicate>();

				IntermediateDataModel idm1 = new IntermediateDataModel();
				idm1.setXmlns(xml_ns);
				idm1.setUri(NODES.Batch.name() + batchNumber);
				idm1.setResource(NODES.Batch.name());

				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.batchNumber.name(), batchNumber, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.element.name(), element, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.sigma_.name(), sigma_, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.mean_.name(), mean_, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.usl.name(), usl, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.lsl.name(), lsl, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.cp.name(), cp, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.cpk.name(), cpk, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.data_sam.name(), data_sam, "String"));

				idm1.setDataProperties(dataProperties);

				String SerialItem = "SELECT  sr.serial_no,gr.type ,db_id , development , description ,part_number FROM 	table_serialised sr, table_jobcard jc, table_grn gr where gr.grn_id = jc.grn_id and sr.grn_id = gr.grn_id and gr.grn_id ='"
						+ grn_id + "'";

				Statement stmt2 = conn.createStatement();

				ResultSet rs2 = stmt2.executeQuery(SerialItem);

				while (rs2.next()) {

					String db_id = rs2.getString("db_id");
					String partType = rs2.getString("type");
					String serial_no = rs2.getString("serial_no");
					String development = rs2.getString("development");
					String description = rs2.getString("description");
					String part_number = rs2.getString("part_number");
					List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.databaseId.name(), db_id, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.serialNo.name(), serial_no, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.partType.name(), partType, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.development.name(), development, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.description.name(), description, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.partNumber.name(), part_number, "String"));

					IdmPredicate predicate = new IdmPredicate(xml_ns,
							PREDICATES.hasPart.name(), true, NODES.Serialised.name(),
							NODES.Serialised.name() + serial_no,
							"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
					predicate.setObjDataProperties(objDataProps);
					predicates.add(predicate);

					idm1.setPredicates(predicates);

				}
				String operatorQuery = "SELECT operator from table_stats st, table_serialised sr where st.db_id = sr.db_id and sr.grn_id = (SELECT  gr.grn_id  FROM table_grn gr, table_batch bt where gr.grn_id ='"
						+ grn_id + "')";

				Statement stmt3 = conn.createStatement();

				ResultSet rs3 = stmt3.executeQuery(operatorQuery);

				while (rs3.next()) {

					// String project = rs.getString("project");
					String Operator = rs3.getString("operator");

					List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.operator.name(), Operator, "String"));

					IdmPredicate predicate = new IdmPredicate(xml_ns,
							PREDICATES.isDefinedBy.name(), true, NODES.Operator.name(),
							NODES.Operator.name() + Operator,
							"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
					predicate.setObjDataProperties(objDataProps);
					predicates.add(predicate);

					idm1.setPredicates(predicates);

				}

				idmArr.add(idm1);

			}

			// (new AddQualOwlModel("OwlAddQual.owl",
			// "http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
			// "ontaddqual")).populateOntology(idmArr);
			/*
			 * ObjectMapper objMapper1 = new ObjectMapper();
			 * try {
			 * System.out.println(objMapper1.writeValueAsString(idmArr));
			 * } catch (JsonProcessingException e) {
			 * // TODO Auto-generated catch block
			 * e.printStackTrace();
			 * }
			 */

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void ClientKeyContactCommunicatorOfEmployer() {
		try {

			Statement stmt = conn.createStatement();

			String KeyContact = "SELECT client_id,Key_contact_name, email   FROM table_client";
			ResultSet rs = stmt.executeQuery(KeyContact);
			int i = 1;
			while (rs.next()) {
				String name = rs.getString("Key_contact_name");
				String client_id = rs.getString("client_id");
				String email = rs.getString("email");
				List<IdmAttribute> dataProperties = new ArrayList<IdmAttribute>();
				List<IdmPredicate> predicates = new ArrayList<IdmPredicate>();

				IntermediateDataModel idm1 = new IntermediateDataModel();
				idm1.setXmlns(xml_ns);
				idm1.setUri(NODES.KeyContact.name() + name);
				idm1.setResource(NODES.KeyContact.name());

				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.name.name(), name, "String"));

				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.clientId.name(), client_id, "String"));

				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.email.name(), email, "String"));

				idm1.setDataProperties(dataProperties);

				String company_name = "AddQual";

				List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

				objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.name.name(), company_name, "String"));

				IdmPredicate predicate = new IdmPredicate(xml_ns,
						PREDICATES.isCommunicatorOf.name(), true, NODES.Employer.name(),
						NODES.Employer.name() + company_name,
						"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
				predicate.setObjDataProperties(objDataProps);

				predicates.add(predicate);

				List<IdmAttribute> objDataProps1 = new ArrayList<IdmAttribute>();

				objDataProps1.add(new IdmAttribute(xml_ns, ATTRIBUTES.clientId.name(), client_id, "String"));

				IdmPredicate predicate1 = new IdmPredicate(xml_ns,
						PREDICATES.isWorkerFor.name(), true, NODES.Client.name(), NODES.Client.name() + client_id,
						"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
				predicate1.setObjDataProperties(objDataProps1);
				predicates.add(predicate1);

				idm1.setPredicates(predicates);

				i++;
				idmArr.add(idm1);

			}

			// (new AddQualOwlModel("OwlAddQual.owl",
			// "http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
			// "ontaddqual")).populateOntology(idmArr);
			/*
			 * ObjectMapper objMapper1 = new ObjectMapper();
			 * try {
			 * System.out.println(objMapper1.writeValueAsString(idmArr));
			 * } catch (JsonProcessingException e) {
			 * // TODO Auto-generated catch block
			 * e.printStackTrace();
			 * }
			 */

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void EmployerKeyContactCommunicatorOfClient() {
		try {

			String name = "AddQual";

			List<IdmAttribute> dataProperties = new ArrayList<IdmAttribute>();
			List<IdmPredicate> predicates = new ArrayList<IdmPredicate>();

			IntermediateDataModel idm1 = new IntermediateDataModel();
			idm1.setXmlns(xml_ns);
			idm1.setUri(NODES.KeyContact.name() + "Ben Anderson");
			idm1.setResource(NODES.KeyContact.name());

			dataProperties.add(new IdmAttribute(xml_ns,
					ATTRIBUTES.name.name(), name, "String"));

			idm1.setDataProperties(dataProperties);

			String sqlClient = "SELECT client_id, company_name, industry, country, post_code FROM table_client";
			Statement stmt2 = conn.createStatement();

			ResultSet rs2 = stmt2.executeQuery(sqlClient);
			int j = 1;
			while (rs2.next()) {
				String client_id = rs2.getString("client_id");
				String company_name = rs2.getString("company_name");
				String industry = rs2.getString("industry");
				String country = rs2.getString("country");
				String post_code = rs2.getString("post_code");

				List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.clientId.name(), client_id, "String"));
				dataProperties.add(new IdmAttribute("http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
						ATTRIBUTES.companyName.name(), company_name, "String"));
				dataProperties.add(new IdmAttribute("http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
						ATTRIBUTES.industry.name(), industry, "String"));
				dataProperties.add(new IdmAttribute("http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
						ATTRIBUTES.country.name(), country, "String"));
				dataProperties.add(new IdmAttribute("http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
						ATTRIBUTES.postCode.name(), post_code, "String"));

				IdmPredicate predicate = new IdmPredicate(xml_ns,
						PREDICATES.isCommunicatorOf.name(), true, NODES.Client.name(), NODES.Client.name() + client_id,
						"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
				predicate.setObjDataProperties(objDataProps);
				predicates.add(predicate);

				idm1.setPredicates(predicates);
				j++;
			}

			List<IdmAttribute> objDataProps1 = new ArrayList<IdmAttribute>();

			objDataProps1.add(new IdmAttribute(xml_ns, ATTRIBUTES.companyName.name(), "AddQual", "String"));

			IdmPredicate predicate1 = new IdmPredicate(xml_ns,
					PREDICATES.isWorkerFor.name(), true, NODES.Employer.name(), NODES.Employer.name() + name,
					"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
			predicate1.setObjDataProperties(objDataProps1);
			predicates.add(predicate1);

			idmArr.add(idm1);

		}

		// (new AddQualOwlModel("OwlAddQual.owl",
		// "http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
		// "ontaddqual")).populateOntology(idmArr);
		/*
		 * ObjectMapper objMapper1 = new ObjectMapper();
		 * try {
		 * System.out.println(objMapper1.writeValueAsString(idmArr));
		 * } catch (JsonProcessingException e) {
		 * // TODO Auto-generated catch block
		 * e.printStackTrace();
		 * }
		 */

		catch (

		Exception e) {

			e.printStackTrace();
		}
	}

	private void GRNAssignedToPackage() {
		try {

			Statement stmt = conn.createStatement();

			String grn = "SELECT grn_id, project, po, coc, comments ,timestamp   FROM table_grn";
			ResultSet rs = stmt.executeQuery(grn);

			while (rs.next()) {
				String grn_id = rs.getString("grn_id");
				String project = rs.getString("project");
				String po = rs.getString("po");
				String coc = rs.getString("coc");
				String comments = rs.getString("comments");
				String timestamp = rs.getString("timestamp");

				List<IdmAttribute> dataProperties = new ArrayList<IdmAttribute>();
				List<IdmPredicate> predicates = new ArrayList<IdmPredicate>();

				IntermediateDataModel idm = new IntermediateDataModel();
				idm.setXmlns(xml_ns);
				idm.setUri(NODES.GRN.name() + grn_id);
				idm.setResource(NODES.GRN.name());

				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.goodReceivedNotesId.name(), grn_id, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.project.name(), project, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.PO.name(), po, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.coc.name(), coc, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.comments.name(), comments, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.timestamp.name(), timestamp, "String"));

				idm.setDataProperties(dataProperties);

				String serialised = "SELECT serial_no ,db_id , development ,type, description ,part_number FROM table_serialised sr, table_jobcard jc, table_grn gr where gr.grn_id = jc.grn_id and sr.grn_id = gr.grn_id and gr.grn_id ='"
						+ grn_id + "'";

				Statement stmt3 = conn.createStatement();

				ResultSet rs3 = stmt3.executeQuery(serialised);

				while (rs3.next()) {

					String part_number = rs3.getString("part_number");
					String db_id = rs3.getString("db_id");
					String serial_no = rs3.getString("serial_no");
					String development = rs3.getString("development");
					String type = rs3.getString("type");
					String description = rs3.getString("description");

					List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.partNumber.name(), part_number, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.databaseId.name(), db_id, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.serialNo.name(), serial_no, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.development.name(), development, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.partType.name(), type, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.description.name(), description, "String"));

					IdmPredicate predicate = new IdmPredicate(xml_ns,
							PREDICATES.isAssignedTo.name(), true, NODES.Serialised.name(),
							NODES.Serialised.name() + serial_no,
							xml_ns);
					predicate.setObjDataProperties(objDataProps);
					predicates.add(predicate);

					idm.setPredicates(predicates);

				}

				idmArr.add(idm);

			}
			/*
			 * ObjectMapper objMapper1 = new ObjectMapper();
			 * try {
			 * System.out.println(objMapper1.writeValueAsString(idmArr));
			 * } catch (JsonProcessingException e) {
			 * // TODO Auto-generated catch block
			 * e.printStackTrace();
			 * }
			 */

			// (new AddQualOwlModel("OwlAddQual.owl",
			// "http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
			// "ontaddqual")).populateOntology(idmArr);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void JobCardPartOfOperator() {
		try {

			Statement stmt = conn.createStatement();

			String jc = "SELECT jc_id,grn_id FROM table_jobcard";
			ResultSet rs = stmt.executeQuery(jc);

			while (rs.next()) {
				String jc_id = rs.getString("jc_id");
				String grn_id = rs.getString("grn_id");
				List<IdmAttribute> dataProperties = new ArrayList<IdmAttribute>();
				List<IdmPredicate> predicates = new ArrayList<IdmPredicate>();

				IntermediateDataModel idm = new IntermediateDataModel();
				idm.setXmlns(xml_ns);
				idm.setUri(NODES.JobCard.name() + jc_id);
				idm.setResource(NODES.JobCard.name());

				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.jobCardId.name(), jc_id, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.goodReceivedNotesId.name(), grn_id, "String"));

				idm.setDataProperties(dataProperties);

				String operatorQuery = "SELECT operator from table_stats st, table_serialised sr where st.db_id = sr.db_id and sr.grn_id in (SELECT  gr.grn_id  FROM table_grn gr, table_jobcard jc where gr.grn_id = jc.grn_id and jc.jc_id ='"
						+ jc_id.trim() + "')";

				Statement stmt3 = conn.createStatement();

				ResultSet rs3 = stmt3.executeQuery(operatorQuery);
				int j = 1;
				while (rs3.next()) {

					String Operator = rs3.getString("operator");

					List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.operator.name(), Operator, "String"));

					IdmPredicate predicate = new IdmPredicate(xml_ns,
							PREDICATES.isPartOf.name(), true, NODES.Operator.name(), NODES.Operator.name() + Operator,
							"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
					predicate.setObjDataProperties(objDataProps);
					predicates.add(predicate);

					idm.setPredicates(predicates);
					j++;
				}

				idmArr.add(idm);

			}
			/*
			 * ObjectMapper objMapper1 = new ObjectMapper();
			 * try {
			 * System.out.println(objMapper1.writeValueAsString(idmArr));
			 * } catch (JsonProcessingException e) {
			 * // TODO Auto-generated catch block
			 * e.printStackTrace();
			 * }
			 */

			// (new AddQualOwlModel("OwlAddQual.owl",
			// "http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
			// "ontaddqual")).populateOntology(idmArr);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void StatsGeneratedByScanner() {
		try {

			Statement stmt = conn.createStatement();

			String stats = "SELECT db_id, start_time, temperature, total_time, photogrammetry_cycle_time , evaluation_time , digit_cycle_time , overall_digit_alig_residual , photogrammetry_avg_scale_dev , photogrammetry_avg_img_dev , calibration_proj_dev , calibration_dev  FROM table_stats";
			ResultSet rs = stmt.executeQuery(stats);

			while (rs.next()) {
				String databaseId = rs.getString("db_id");
				String start_time = rs.getString("start_time");
				String temperature = rs.getString("temperature");
				String total_time = rs.getString("total_time");
				String photogrammetry_cycle_time = rs.getString("photogrammetry_cycle_time");
				String evaluation_time = rs.getString("evaluation_time");
				String digit_cycle_time = rs.getString("digit_cycle_time");
				String overall_digit_alig_residual = rs.getString("overall_digit_alig_residual");
				String calibration_dev = rs.getString("calibration_dev");
				String calibration_proj_dev = rs.getString("calibration_proj_dev");
				String photogrammetry_avg_img_dev = rs.getString("photogrammetry_avg_img_dev");
				String photogrammetry_avg_scale_dev = rs.getString("photogrammetry_avg_scale_dev");

				List<IdmAttribute> dataProperties = new ArrayList<IdmAttribute>();
				List<IdmPredicate> predicates = new ArrayList<IdmPredicate>();

				IntermediateDataModel idm = new IntermediateDataModel();
				idm.setXmlns(xml_ns);
				idm.setUri(NODES.Stats.name() + databaseId);
				idm.setResource(NODES.Stats.name());

				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.databaseId.name(), databaseId, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.temperature.name(), temperature, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.startTime.name(), start_time, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.totalTime.name(), total_time, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.photogrammetryCycleTime.name(), photogrammetry_cycle_time, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.evaluationTime.name(), evaluation_time, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.digitCycleTime.name(), digit_cycle_time, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.overallDigitAligResidual.name(), overall_digit_alig_residual, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.calibrationDev.name(), calibration_dev, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.calibrationProjDev.name(), calibration_proj_dev, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.photogrammetryAvgImgDev.name(), photogrammetry_avg_img_dev, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.photogrammetryAvgScaleDev.name(), photogrammetry_avg_scale_dev, "String"));
				idm.setDataProperties(dataProperties);

				String LazerScanner = "GOM";

				List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

				objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.scannerId.name(), LazerScanner, "String"));

				IdmPredicate predicate = new IdmPredicate(xml_ns,
						PREDICATES.isGeneratedBy.name(), true, NODES.LazerScanner.name(),
						NODES.LazerScanner.name() + LazerScanner,
						"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
				predicate.setObjDataProperties(objDataProps);
				predicates.add(predicate);

				idm.setPredicates(predicates);

				idmArr.add(idm);

			}
			/*
			 * ObjectMapper objMapper1 = new ObjectMapper();
			 * try {
			 * System.out.println(objMapper1.writeValueAsString(idmArr));
			 * } catch (JsonProcessingException e) {
			 * // TODO Auto-generated catch block
			 * e.printStackTrace();
			 * }
			 */

			// (new AddQualOwlModel("OwlAddQual.owl",
			// "http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
			// "ontaddqual")).populateOntology(idmArr);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void OperatorFuctions() {
		try {

			Statement stmt = conn.createStatement();

			String Operator = "SELECT operator,db_id from table_stats";
			ResultSet rs = stmt.executeQuery(Operator);
			int i = 1;
			while (rs.next()) {
				String operator = rs.getString("operator");
				String db_id = rs.getString("db_id");

				List<IdmAttribute> dataProperties = new ArrayList<IdmAttribute>();
				List<IdmPredicate> predicates = new ArrayList<IdmPredicate>();

				IntermediateDataModel idm = new IntermediateDataModel();
				idm.setXmlns(xml_ns);
				idm.setUri(NODES.Operator.name() + operator);
				idm.setResource(NODES.Operator.name());

				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.operator.name(), operator, "String"));

				idm.setDataProperties(dataProperties);

				String jobcard = "select jc.jc_id, db_id from table_jobcard jc , table_jcserial jos where jc.jc_id = jos.jc_id and jos.db_id = ( select sr.db_id from table_serialised sr where sr.db_id ='"
						+ db_id.trim() + "')";

				Statement stmt2 = conn.createStatement();

				ResultSet rs2 = stmt2.executeQuery(jobcard);
				int j = 1;
				while (rs2.next()) {

					String db_id1 = rs.getString("db_id");
					String jc_id = rs2.getString("jc_id");
					// String coc = rs.getString("coc");
					// String comments = rs.getString("comments");
					// String timestamp = rs.getString("timestamp");
					List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.jobCardId.name(), jc_id, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.databaseId.name(), db_id1, "String"));

					IdmPredicate predicate = new IdmPredicate(xml_ns,
							PREDICATES.hasPart.name(), true, NODES.JobCard.name(), NODES.JobCard.name() + jc_id,
							"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
					predicate.setObjDataProperties(objDataProps);
					predicates.add(predicate);

					idm.setPredicates(predicates);
					j++;
				}

				String grn = "select gr.grn_id, project, po, coc, comments ,timestamp  from table_grn gr, table_serialised sr where gr.grn_id = sr.grn_id and sr.db_id = '"
						+ db_id.trim() + "'";

				Statement stmt3 = conn.createStatement();

				ResultSet rs3 = stmt3.executeQuery(grn);
				int j1 = 1;
				while (rs3.next()) {
					String grn_id = rs3.getString("grn_id");
					String project = rs3.getString("project");
					String po = rs3.getString("po");
					String coc = rs3.getString("coc");
					String comments = rs3.getString("comments");
					String timestamp = rs3.getString("timestamp");

					List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.goodReceivedNotesId.name(), grn_id, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.project.name(), project, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.PO.name(), po, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.coc.name(), coc, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.comments.name(), comments, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.timestamp.name(), timestamp, "String"));

					IdmPredicate predicate = new IdmPredicate(xml_ns,
							PREDICATES.isAssignerOf.name(), true, NODES.GRN.name(), NODES.GRN.name() + grn_id,
							"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
					predicate.setObjDataProperties(objDataProps);
					predicates.add(predicate);

					idm.setPredicates(predicates);
					j1++;
				}
				String batch = "select gr.grn_id, 'batch no' as batch_no, element, sigma_ , mean_ , usl ,lsl , cp , cpk ,data_sam from table_grn gr, table_serialised sr, table_batch bt where gr.grn_id = sr.grn_id AND gr.grn_id = bt.grn_id and sr.db_id ='"
						+ db_id.trim() + "'";

				Statement stmt4 = conn.createStatement();

				ResultSet rs4 = stmt4.executeQuery(batch);

				while (rs4.next()) {

					String batchNumber = rs4.getString("batch_no");
					String grn_id = rs4.getString("grn_id");
					String element = rs4.getString("element");
					String sigma_ = rs4.getString("sigma_");
					String mean_ = rs4.getString("mean_");
					String usl = rs4.getString("usl");
					String lsl = rs4.getString("lsl");
					String cp = rs4.getString("cp");
					String cpk = rs4.getString("cpk");
					String data_sam = rs4.getString("data_sam");

					List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.batchNumber.name(), batchNumber, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.element.name(), element, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.sigma_.name(), sigma_, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.mean_.name(), mean_, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.usl.name(), usl, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.lsl.name(), lsl, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.cp.name(), cp, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.cpk.name(), cpk, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.data_sam.name(), data_sam, "String"));

					IdmPredicate predicate = new IdmPredicate(xml_ns,
							PREDICATES.isDefinerOf.name(), true, NODES.Batch.name(), NODES.Batch.name() + batchNumber,
							"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
					predicate.setObjDataProperties(objDataProps);
					predicates.add(predicate);

					idm.setPredicates(predicates);

				}

				String Visual_Inspection = "SELECT seria_no, db_id,broken, greasy , dirty , reject_to_scan, comments FROM table_serialised sr where sr.db_id='"
						+ db_id.trim() + "'";

				Statement stmt5 = conn.createStatement();

				ResultSet rs5 = stmt5.executeQuery(Visual_Inspection);

				while (rs5.next()) {
					String seria_no = rs5.getString("seria_no");
					String broken = rs5.getString("broken");
					String db_id1 = rs5.getString("db_id");
					String greasy = rs5.getString("greasy");
					String dirty = rs5.getString("dirty");
					String rejectToScan = rs5.getString("reject_to_scan");
					String comments = rs5.getString("comments");

					List<IdmAttribute> objDataProps1 = new ArrayList<IdmAttribute>();

					objDataProps1.add(new IdmAttribute(xml_ns, ATTRIBUTES.broken.name(), broken, "String"));
					objDataProps1.add(new IdmAttribute(xml_ns, ATTRIBUTES.databaseId.name(), db_id1, "String"));
					objDataProps1.add(new IdmAttribute(xml_ns, ATTRIBUTES.greasy.name(), greasy, "String"));
					objDataProps1.add(new IdmAttribute(xml_ns, ATTRIBUTES.dirty.name(), dirty, "String"));
					objDataProps1.add(new IdmAttribute(xml_ns, ATTRIBUTES.rejectToScan.name(), rejectToScan, "String"));
					objDataProps1.add(new IdmAttribute(xml_ns, ATTRIBUTES.comments.name(), comments, "String"));
					objDataProps1.add(new IdmAttribute(xml_ns, ATTRIBUTES.serialNo.name(), seria_no, "String"));

					IdmPredicate predicate1 = new IdmPredicate(xml_ns,
							PREDICATES.isVisualizerOf.name(), true, NODES.VisualInspection.name(),
							NODES.VisualInspection.name() + seria_no,
							"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
					predicate1.setObjDataProperties(objDataProps1);
					predicates.add(predicate1);

					idm.setPredicates(predicates);

				}
				String LazerScanner = "GOM";

				List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

				objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.scannerId.name(), LazerScanner, "String"));

				IdmPredicate predicate = new IdmPredicate(xml_ns,
						PREDICATES.isRunnerOf.name(), true, NODES.LazerScanner.name(),
						NODES.LazerScanner.name() + LazerScanner,
						"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
				predicate.setObjDataProperties(objDataProps);
				predicates.add(predicate);

				idm.setPredicates(predicates);

				String Employer = "AddQual";

				List<IdmAttribute> objDataProps2 = new ArrayList<IdmAttribute>();

				objDataProps2.add(new IdmAttribute(xml_ns, ATTRIBUTES.name.name(), Employer, "String"));

				IdmPredicate predicate2 = new IdmPredicate(xml_ns,
						PREDICATES.isWorkerFor.name(), true, NODES.Employer.name(), NODES.Employer.name() + Employer,
						"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
				predicate2.setObjDataProperties(objDataProps2);
				predicates.add(predicate2);

				idm.setPredicates(predicates);

				i++;
				idmArr.add(idm);
			}

			/*
			 * ObjectMapper objMapper1 = new ObjectMapper();
			 * try {
			 * System.out.println(objMapper1.writeValueAsString(idmArr));
			 * } catch (JsonProcessingException e) {
			 * // TODO Auto-generated catch block
			 * e.printStackTrace();
			 * }
			 */
			// (new AddQualOwlModel("OwlAddQual.owl",
			// "http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
			// "ontaddqual")).populateOntology(idmArr);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void GomCreatorOfLazerScanner() {
		try {

			String GOM = "GOM";
			List<IdmPredicate> predicates = new ArrayList<IdmPredicate>();

			IntermediateDataModel idm1 = new IntermediateDataModel();
			idm1.setXmlns(xml_ns);
			idm1.setUri(NODES.GOM.name());
			idm1.setResource(NODES.GOM.name());

			String LazerScannerId = "LazerScanner";

			List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

			objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.scannerId.name(), LazerScannerId, "String"));

			IdmPredicate predicate = new IdmPredicate(xml_ns,
					PREDICATES.isCreatorOf.name(), true, NODES.LazerScanner.name(), NODES.LazerScanner.name(),
					"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
			predicate.setObjDataProperties(objDataProps);
			predicates.add(predicate);

			idm1.setPredicates(predicates);

			idmArr.add(idm1);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void PackageSentByClientHasBatch() {
		try {

			Statement stmt = conn.createStatement();

			String serialised = "SELECT serial_no ,db_id , development ,type, description ,part_number, jc.grn_id, gr.client_id FROM table_serialised sr, table_jobcard jc, table_grn gr where gr.grn_id = jc.grn_id and sr.grn_id = gr.grn_id";
			ResultSet rs = stmt.executeQuery(serialised);
			int i = 1;
			while (rs.next()) {
				String part_number = rs.getString("part_number");
				String type = rs.getString("type");
				String grn_id = rs.getString("grn_id");
				String client_id = rs.getString("client_id");
				String serial_no = rs.getString("serial_no");
				String development = rs.getString("development");
				String description = rs.getString("description");
				String db_id = rs.getString("db_id");

				List<IdmAttribute> dataProperties = new ArrayList<IdmAttribute>();
				List<IdmPredicate> predicates = new ArrayList<IdmPredicate>();

				IntermediateDataModel idm1 = new IntermediateDataModel();
				idm1.setXmlns(xml_ns);
				idm1.setUri(NODES.Serialised.name() + serial_no);
				idm1.setResource(NODES.Serialised.name());

				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.partNumber.name(), part_number, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.partType.name(), type, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.development.name(), development, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.description.name(), description, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.databaseId.name(), db_id, "String"));

				idm1.setDataProperties(dataProperties);

				String batch = "SELECT grn_id,'batch no' as batch_no , element, sigma_ , mean_ , usl ,lsl , cp , cpk ,data_sam   FROM table_batch where grn_id='"
						+ grn_id + "'";

				Statement stmt2 = conn.createStatement();

				ResultSet rs2 = stmt2.executeQuery(batch);

				while (rs2.next()) {

					String batch_no = rs2.getString("batch_no");
					String grn_id1 = rs2.getString("grn_id");
					String element = rs2.getString("element");
					String sigma_ = rs2.getString("sigma_");
					String mean_ = rs2.getString("mean_");
					String usl = rs2.getString("usl");
					String lsl = rs2.getString("lsl");
					String cp = rs2.getString("cp");
					String cpk = rs2.getString("cpk");
					String data_sam = rs2.getString("data_sam");

					List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.batchNumber.name(), batch_no, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.element.name(), element, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.sigma_.name(), sigma_, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.mean_.name(), mean_, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.usl.name(), usl, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.lsl.name(), lsl, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.cp.name(), cp, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.cpk.name(), cpk, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.data_sam.name(), data_sam, "String"));

					IdmPredicate predicate = new IdmPredicate(xml_ns,
							PREDICATES.hasPart.name(), true, NODES.Batch.name(), NODES.Batch.name() + batch_no,
							"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
					predicate.setObjDataProperties(objDataProps);
					predicates.add(predicate);

					idm1.setPredicates(predicates);

				}
				String sqlClient = "SELECT client_id, company_name, industry, country, post_code FROM table_client where client_id = '"
						+ client_id.trim() + "'";

				Statement stmt3 = conn.createStatement();

				ResultSet rs3 = stmt3.executeQuery(sqlClient);

				List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

				int j = 1;
				while (rs3.next()) {

					String client_id1 = rs3.getString("client_id");
					String company_name = rs3.getString("company_name");
					String industry = rs3.getString("industry");
					String country = rs3.getString("country");
					String post_code = rs3.getString("post_code");

					List<IdmAttribute> objDataProps1 = new ArrayList<IdmAttribute>();

					objDataProps1.add(new IdmAttribute(xml_ns, ATTRIBUTES.clientId.name(), client_id1, "String"));

					objDataProps1.add(new IdmAttribute("http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
							ATTRIBUTES.companyName.name(), company_name, "String"));
					objDataProps1.add(new IdmAttribute("http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
							ATTRIBUTES.industry.name(), industry, "String"));
					objDataProps1.add(new IdmAttribute("http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
							ATTRIBUTES.country.name(), country, "String"));
					objDataProps1.add(new IdmAttribute("http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#",
							ATTRIBUTES.postCode.name(), post_code, "String"));

					IdmPredicate predicate1 = new IdmPredicate(xml_ns,
							PREDICATES.isSentBy.name(), true, NODES.Client.name(), NODES.Client.name() + client_id1,
							"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
					predicate1.setObjDataProperties(objDataProps1);
					predicates.add(predicate1);

					idm1.setPredicates(predicates);
					j++;
				}
				i++;
				idmArr.add(idm1);

			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void PartMeasuredByMeasurementTool() {
		try {

			Statement stmt = conn.createStatement();

			String serialised = "SELECT serial_no ,db_id ,gr.grn_id, development ,type, description ,part_number FROM table_serialised sr, table_jobcard jc, table_grn gr where gr.grn_id = jc.grn_id and sr.grn_id = gr.grn_id";
			ResultSet rs = stmt.executeQuery(serialised);

			while (rs.next()) {
				String part_number = rs.getString("part_number");
				String type = rs.getString("type");
				String grn_id = rs.getString("grn_id");
				// String client_id = rs.getString("client_id");
				String serial_no = rs.getString("serial_no");
				String development = rs.getString("development");
				String description = rs.getString("description");
				String db_id = rs.getString("db_id");

				List<IdmAttribute> dataProperties = new ArrayList<IdmAttribute>();
				List<IdmPredicate> predicates = new ArrayList<IdmPredicate>();

				IntermediateDataModel idm1 = new IntermediateDataModel();
				idm1.setXmlns(xml_ns);
				idm1.setUri(NODES.Serialised.name() + serial_no);
				idm1.setResource(NODES.Serialised.name());

				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.partNumber.name(), part_number, "String"));
						dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.serialNo.name(), serial_no, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.partType.name(), type, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.development.name(), development, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.description.name(), description, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.databaseId.name(), db_id, "String"));

				idm1.setDataProperties(dataProperties);

				String Tool = "LazerScanner";

				List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

				objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.scannerId.name(), Tool, "String"));

				IdmPredicate predicate1 = new IdmPredicate(xml_ns,
						PREDICATES.isMeasuredBy.name(), true, NODES.LazerScanner.name(), "LazerScanner",
						"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
				predicate1.setObjDataProperties(objDataProps);
				predicates.add(predicate1);

				idm1.setPredicates(predicates);

				idmArr.add(idm1);
			}
		}

		catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void LazerScanningStatsByLazerScanner() {
		try {

			Statement stmt = conn.createStatement();

			String LazerScnning = "SELECT scan_id,element, datum, property,nominal, actual,tol_n, tol_p,dev,check_r,db_id, out  FROM table_gom";
			ResultSet rs = stmt.executeQuery(LazerScnning);
			int i = 1;
			while (rs.next()) {
				String scan_id = rs.getString("scan_id");
				String db_id = rs.getString("db_id");
				String element = rs.getString("element");
				String datum = rs.getString("datum");
				String property = rs.getString("property");
				String nominal = rs.getString("nominal");
				String actual = rs.getString("actual");
				String tol_n = rs.getString("tol_n");
				String tol_p = rs.getString("tol_p");
				String dev = rs.getString("dev");
				String check_r = rs.getString("check_r");
				String out = rs.getString("out");

				List<IdmAttribute> dataProperties = new ArrayList<IdmAttribute>();
				List<IdmPredicate> predicates = new ArrayList<IdmPredicate>();

				IntermediateDataModel idm1 = new IntermediateDataModel();
				idm1.setXmlns(xml_ns);
				idm1.setUri(NODES.LaserScanning.name() + scan_id);
				idm1.setResource(NODES.LaserScanning.name());

				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.scanId.name(), scan_id, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.databaseId.name(), db_id, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.element.name(), element, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.datum.name(), datum, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.property.name(), property, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.nominal.name(), nominal, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.actual.name(), actual, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.tolN.name(), tol_n, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.tolP.name(), tol_p, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.dev.name(), dev, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.out.name(), out, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.checkR.name(), check_r, "String"));
				idm1.setDataProperties(dataProperties);

				String stats = "SELECT db_id, start_time, temperature, total_time, photogrammetry_cycle_time , evaluation_time , digit_cycle_time , overall_digit_alig_residual , photogrammetry_avg_scale_dev , photogrammetry_avg_img_dev , calibration_proj_dev , calibration_dev  FROM table_stats where db_id ='"
						+ db_id + "'";

				Statement stmt2 = conn.createStatement();

				ResultSet rs2 = stmt2.executeQuery(stats);

				while (rs2.next()) {

					String databaseId = rs2.getString("db_id");
					String start_time = rs2.getString("start_time");
					String temperature = rs2.getString("temperature");
					String total_time = rs2.getString("total_time");
					String photogrammetry_cycle_time = rs2.getString("photogrammetry_cycle_time");
					String evaluation_time = rs2.getString("evaluation_time");
					String digit_cycle_time = rs2.getString("digit_cycle_time");
					String overall_digit_alig_residual = rs2.getString("overall_digit_alig_residual");
					String calibration_dev = rs2.getString("calibration_dev");
					String calibration_proj_dev = rs2.getString("calibration_proj_dev");
					String photogrammetry_avg_img_dev = rs2.getString("photogrammetry_avg_img_dev");
					String photogrammetry_avg_scale_dev = rs2.getString("photogrammetry_avg_scale_dev");
					List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.databaseId.name(), databaseId, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.temperature.name(), temperature, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.startTime.name(), start_time, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.totalTime.name(), total_time, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.photogrammetryCycleTime.name(), photogrammetry_cycle_time, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.evaluationTime.name(), evaluation_time, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.digitCycleTime.name(), digit_cycle_time, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.overallDigitAligResidual.name(), overall_digit_alig_residual, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.calibrationDev.name(), calibration_dev, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.calibrationProjDev.name(), calibration_proj_dev, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.photogrammetryAvgImgDev.name(), photogrammetry_avg_img_dev, "String"));
					objDataProps.add(new IdmAttribute(xml_ns,
							ATTRIBUTES.photogrammetryAvgScaleDev.name(), photogrammetry_avg_scale_dev, "String"));

					IdmPredicate predicate = new IdmPredicate(xml_ns,
							PREDICATES.hasPart.name(), true, NODES.Stats.name(), NODES.Stats.name() + databaseId,
							"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
					predicate.setObjDataProperties(objDataProps);
					predicates.add(predicate);

					idm1.setPredicates(predicates);

				}

				String LazerScanner = "LazerScanner";

				List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

				objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.scannerId.name(), LazerScanner, "String"));

				IdmPredicate predicate = new IdmPredicate(xml_ns,
						PREDICATES.isInitiatedBy.name(), true, NODES.LazerScanner.name(), "LazerScanner",
						"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
				predicate.setObjDataProperties(objDataProps);
				predicates.add(predicate);

				idm1.setPredicates(predicates);

				i++;
				idmArr.add(idm1);

			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void VisualInspectionImagePartOperator() {
		try {

			Statement stmt = conn.createStatement();

			String Visual_Inspection = "SELECT  serial_no,grn_id,db_id,broken, greasy , dirty , reject_to_scan, comments FROM table_serialised";
			ResultSet rs = stmt.executeQuery(Visual_Inspection);
			int i = 1;
			while (rs.next()) {
				String broken = rs.getString("broken");
			    String greasy = rs.getString("greasy");
				String dirty = rs.getString("dirty");
				String reject_to_scan = rs.getString("reject_to_scan");
				String comments = rs.getString("comments");
				String db_id = rs.getString("db_id");
				String grn_id = rs.getString("grn_id");
				String serial_no = rs.getString("serial_no");
				List<IdmAttribute> dataProperties = new ArrayList<IdmAttribute>();
				List<IdmPredicate> predicates = new ArrayList<IdmPredicate>();

				IntermediateDataModel idm1 = new IntermediateDataModel();
				idm1.setXmlns(xml_ns);
				idm1.setUri(NODES.VisualInspection.name() + serial_no);
				idm1.setResource(NODES.VisualInspection.name());

				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.broken.name(), broken, "String"));
				dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.databaseId.name(), db_id, "String"));
						dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.greasy.name(), greasy, "String"));
						dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.dirty.name(), dirty, "String"));
						dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.rejectToScan.name(), reject_to_scan, "String"));
						dataProperties.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.comments.name(), comments, "String"));

				idm1.setDataProperties(dataProperties);

				String imageqry = "SELECT image_name, image_url  FROM table_insp_images where db_id='" + db_id + "'";

				Statement stmt2 = conn.createStatement();

				ResultSet rs2 = stmt2.executeQuery(imageqry);

				while (rs2.next()) {
					int j = 1;
					String imageName = rs2.getString("image_name");
					String imageURL = rs2.getString("image_url");
					List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.imageName.name(), imageName, "String"));
					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.imageUrl.name(), imageURL, "String"));

					IdmPredicate predicate = new IdmPredicate(xml_ns,
							PREDICATES.hasPart.name(), true, NODES.Image.name(), NODES.Image.name() + imageName,
							"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
					predicate.setObjDataProperties(objDataProps);
					predicates.add(predicate);

					idm1.setPredicates(predicates);
					j++;
				}
				String serialised = "SELECT serial_no ,db_id , development ,type, description ,part_number FROM table_serialised sr, table_jobcard jc, table_grn gr where gr.grn_id = jc.grn_id and sr.grn_id = gr.grn_id and sr.db_id ='"
						+ db_id + "'";

				Statement stmt3 = conn.createStatement();

				ResultSet rs3 = stmt3.executeQuery(serialised);
				int j = 1;
				while (rs3.next()) {

					String serial_no1 = rs3.getString("serial_no");

					List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.serialNo.name(), serial_no1, "String"));

					IdmPredicate predicate = new IdmPredicate(xml_ns,
							PREDICATES.isAppliedTo.name(), true, NODES.Serialised.name(),
							NODES.Serialised.name() + serial_no,
							"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
					predicate.setObjDataProperties(objDataProps);
					predicates.add(predicate);

					idm1.setPredicates(predicates);
					j++;
				}

				String operatorQuery = "SELECT operator from table_stats st, table_serialised sr where st.db_id = sr.db_id and sr.grn_id = (SELECT  gr.grn_id  FROM table_grn gr, table_batch bt where gr.grn_id ='"
						+ grn_id + "')";

				Statement stmt4 = conn.createStatement();

				ResultSet rs4 = stmt4.executeQuery(operatorQuery);
				int j1 = 1;
				while (rs4.next()) {

					String operator = rs4.getString("operator");

					List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

					objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.operator.name(), operator, "String"));

					IdmPredicate predicate = new IdmPredicate(xml_ns,
							PREDICATES.isVisualizedBy.name(), true, NODES.Operator.name(),
							NODES.Operator.name() + operator,
							"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
					predicate.setObjDataProperties(objDataProps);
					predicates.add(predicate);

					idm1.setPredicates(predicates);
					j1++;
				}

				i++;
				idmArr.add(idm1);

			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void LazerScannerScanningSerialisedStats() {
		try {

			String LazerScanner = "LazerScanner";

			List<IdmAttribute> dataProperties = new ArrayList<IdmAttribute>();
			List<IdmPredicate> predicates = new ArrayList<IdmPredicate>();

			IntermediateDataModel idm1 = new IntermediateDataModel();
			idm1.setXmlns(xml_ns);
			idm1.setUri(NODES.LazerScanner.name());
			idm1.setResource(NODES.LazerScanner.name());

			dataProperties.add(new IdmAttribute(xml_ns,
					ATTRIBUTES.scannerId.name(), LazerScanner, "String"));

			idm1.setDataProperties(dataProperties);

			String serialised = "SELECT gr.grn_id,serial_no ,db_id , development ,type, description ,part_number, quantity, weight, scale FROM table_serialised sr, table_jobcard jc, table_grn gr where sr.grn_id = jc.grn_id and gr.grn_id = sr.grn_id";

			Statement stmt2 = conn.createStatement();

			ResultSet rs2 = stmt2.executeQuery(serialised);
			int j = 1;
			while (rs2.next()) {

				String part_number = rs2.getString("part_number");
				String type = rs2.getString("type");
				String grn_id = rs2.getString("grn_id");
				String serial_no = rs2.getString("serial_no");
				String development = rs2.getString("development");
				String description = rs2.getString("description");
				String db_id = rs2.getString("db_id");

				List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.partNumber.name(), part_number, "String"));
				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.partType.name(), type, "String"));
				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.development.name(), development, "String"));
				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.description.name(), description, "String"));
				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.databaseId.name(), db_id, "String"));

				IdmPredicate predicate = new IdmPredicate(xml_ns,
						PREDICATES.isMeasurerOf.name(), true, NODES.Serialised.name(),
						NODES.Serialised.name() + serial_no,
						"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
				predicate.setObjDataProperties(objDataProps);
				predicates.add(predicate);

				idm1.setPredicates(predicates);
				j++;
			}

			String stats = "SELECT db_id, start_time, temperature, total_time, photogrammetry_cycle_time , evaluation_time , digit_cycle_time , overall_digit_alig_residual , photogrammetry_avg_scale_dev , photogrammetry_avg_img_dev , calibration_proj_dev , calibration_dev  FROM table_stats";

			Statement stmt3 = conn.createStatement();

			ResultSet rs3 = stmt3.executeQuery(stats);
			int j1 = 1;
			while (rs3.next()) {

				String databaseId = rs3.getString("db_id");
				String start_time = rs3.getString("start_time");
				String temperature = rs3.getString("temperature");
				String total_time = rs3.getString("total_time");
				String photogrammetry_cycle_time = rs3.getString("photogrammetry_cycle_time");
				String evaluation_time = rs3.getString("evaluation_time");
				String digit_cycle_time = rs3.getString("digit_cycle_time");
				String overall_digit_alig_residual = rs3.getString("overall_digit_alig_residual");
				String calibration_dev = rs3.getString("calibration_dev");
				String calibration_proj_dev = rs3.getString("calibration_proj_dev");
				String photogrammetry_avg_img_dev = rs3.getString("photogrammetry_avg_img_dev");
				String photogrammetry_avg_scale_dev = rs3.getString("photogrammetry_avg_scale_dev");
				// String temperature = rs3.getString("temperature");

				List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.databaseId.name(), databaseId, "String"));
				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.temperature.name(), temperature, "String"));
				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.startTime.name(), start_time, "String"));
				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.totalTime.name(), total_time, "String"));
				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.photogrammetryCycleTime.name(), photogrammetry_cycle_time, "String"));
				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.evaluationTime.name(), evaluation_time, "String"));
				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.digitCycleTime.name(), digit_cycle_time, "String"));
				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.overallDigitAligResidual.name(), overall_digit_alig_residual, "String"));
				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.calibrationDev.name(), calibration_dev, "String"));
				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.calibrationProjDev.name(), calibration_proj_dev, "String"));
				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.photogrammetryAvgImgDev.name(), photogrammetry_avg_img_dev, "String"));
				objDataProps.add(new IdmAttribute(xml_ns,
						ATTRIBUTES.photogrammetryAvgScaleDev.name(), photogrammetry_avg_scale_dev, "String"));

				IdmPredicate predicate = new IdmPredicate(xml_ns,
						PREDICATES.isGeneratorOf.name(), true, NODES.Stats.name(), NODES.Stats.name() + databaseId,
						"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
				predicate.setObjDataProperties(objDataProps);
				predicates.add(predicate);

				idm1.setPredicates(predicates);
				j1++;
			}

			String LazerScnning = "SELECT scan_id,element, datum, property,nominal, actual,tol_n, tol_p,dev,check_r, out  FROM table_gom";

			Statement stmt4 = conn.createStatement();

			ResultSet rs4 = stmt4.executeQuery(LazerScnning);
			int j2 = 1;
			while (rs4.next()) {

				String scan_id = rs4.getString("scan_id");

				List<IdmAttribute> objDataProps = new ArrayList<IdmAttribute>();

				objDataProps.add(new IdmAttribute(xml_ns, ATTRIBUTES.scanId.name(), scan_id, "String"));

				IdmPredicate predicate = new IdmPredicate(xml_ns,
						PREDICATES.isInitiatorOf.name(), false, NODES.LaserScanning.name(),
						NODES.LaserScanning.name() + scan_id,
						"http://www.semanticweb.org/rod22/ontologies/2022/1/ontaddqual#");
				predicate.setObjDataProperties(objDataProps);
				predicates.add(predicate);

				idm1.setPredicates(predicates);
				j2++;
			}

			idmArr.add(idm1);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void loadIndividualsFromDB() {

		List<IntermediateDataModel> dind = new ArrayList<IntermediateDataModel>();

		// . individuals for client

		String sqlClient = "SELECT client_id, company_name, industry, country, post_code FROM table_client";
		String employer = "SELECT receiver_name FROM table_grn";
		String LazerScnning = "SELECT scan_id,element, datum, property,nominal, actual,tol_n, tol_p,dev,check_r, out  FROM table_gom";
		String image = "SELECT image_name, image_url  FROM image";
		String stats = "SELECT db_id, start_time, temperature, total_time, photogrammetry_cycle_time , evaluation_time , digit_cycle_time , overall_digit_alig_residual , photogrammetry_avg_scale_dev , photogrammetry_avg_img_dev , calibration_proj_dev , calibration_dev  FROM table_stats";
		String batch = "SELECT batchNumber, element, sigma_ , mean_ , usl ,lsl , cp , cpk ,data_sam   FROM table_batch";
		String serialised = "SELECT Serial_no ,db_id , development   FROM table_serialised   INNER JOIN table_jobcard ON table_serialised.grn_id = table_jobcard.grn_id";
		String part = "SELECT type, description ,part_number, quantity, weight, scale  FROM table_grn";
		String grn = "SELECT grn_id, project, po, coc, comments ,timestamp   FROM table_grn";
		String KeyContact = "SELECT Key_contact_name, email   FROM table_client";
		String jc = "SELECT jc_id FROM table_jobcard";
		String Visual_Inspection = "SELECT  broken, greasy , dirty , reject_to_scan, comments FROM table_serialised";

		// (new AddQualOwlModel()).populateOntology(dind);

	}
}

enum NODES {
	Batch, Business, Client, Employer, Document, Note, DeliveryNote, GRN, ProcessDetails, JobCard, Stats, Human,
	Employee, KeyContact, Operator, Image, JobInstructions, Manufacturer, GOM, Package, Part, Serialised, Process,
	Metrology, LaserScanning, Visual, VisualInspection, Tool, MeasurementTool, Calliper, CMMScanner, LazerScanner,
	Micrometer, Ruler
}

enum PREDICATES {
	hasPart, isAppliedTo, isAssignedTo, isAssignerOf, isCommunicatorOf, isCreatorOf, isDefinedBy, isDefinerOf,
	isGeneratedBy, isGeneratorOf, isInitiatedBy, isInitiatorOf, isMeasuredBy, isMeasurerOf, isPartOf, isRunnerOf,
	isSenderOf, isSentBy, isVisualizedBy, isVisualizerOf, isWorkerFor

}

enum ATTRIBUTES {
	goodReceivedNotesId, jobCardId, partType, visualizationId, serialNo, scannerId, scanId,
	receiverName, partNumber, packageId, industry, operator, imageUrl, imageName, employerId, databaseId, employeeId,
	deliveryId, department, companyName, clientId, batchNumber, broken, email, name, country, postCode,
	scale, weight, quantity, description, development,
	sigma_, mean_, usl, lsl, cp, cpk, data_sam,
	project, PO, coc, comments, timestamp,
	startTime, temperature, totalTime, photogrammetryCycleTime, evaluationTime, digitCycleTime,
	overallDigitAligResidual, calibrationDev, calibrationProjDev,
	photogrammetryAvgImgDev, photogrammetryAvgScaleDev,
	greasy, dirty, rejectToScan,
	element, datum, property, nominal, actual, tolN, tolP, dev, checkR, out

}
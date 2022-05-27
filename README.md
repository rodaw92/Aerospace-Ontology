# Knowledge-Graph

![Parts](https://github.com/rodaw92/Aerospace-Ontology/blob/main/bloom%20client.png)

---------
## Project Structure
- `OwlFly` is a tool created for populating an ontology
  - `Source` the source files for the ontology population tool
  - `Target` the target executable files for the ontology. Note. this directory may be empty. Once the repository is cloned and executed on the local machine then the executables are generated in this directory
  - `populated_ontology.owl` once the ontology tool is successfully executed it produces a populated ontology e.g., an ontology with all the data loaded from the database into this ontology. 
- `Json.Json` is a json structure of the Intermedidate Data Model (IMD) of the populated ontology. This file contains the data from the populated ontology in triplet format. It can easily be transfered between APIs and can be easily adapted to load in any graph database like Neo4j.
- `README` file the file containing instructions for the project. e.g., the project structure, running and deploying the project.

## Ontology Modeling

## How to Run
This project contains the following two parts
1.  Ontology Population
2.  Ontology Deployment
These are discussed one by one in the following section.

### Ontology Population
3. Clone the repositogy on the local machine
  - `$ git clone https://github.com/rodaw92/Aerospace-Ontology`
  - OR alternatively you can click on the `download zip` from the top right `code` button dropdown menu.
4.  Project Configurations.
  - Open `OwlFly/src/main/java/com/addqual/parse/PostgresParser.java`
    - update the database credentials.
    - update the namespace for the ontology
    - update the location for the ontology schema e.g., `OwlAddQual.owl`
5. Run `OwlFly/src/main/java/com/addqual/Main.java`
  - It will load all the data from the database and will generate the following two files:
    - `Json.Json` the intermediate data model (IMD) representation of the loaded ontology
    - `populated_ontology.owl` the fully loaded ontology.
    
### Ontology Deployment
Here in this section the ontology deployment on the `Neo4j Graph Database` using `Neosemantics` is explained. The process requires the installation of the `Neo4j Desktop` and the plugin `Neosemantics`

6. Install the `Neo4j Desktop` latest version
  - Following the instructions from the link below to install the `Neo4j Desktop` latest version.
  - [Neo4j Desktop] (https://neo4j.com/)
  - Neo4j Desktop includes `Neo4j Browser`, and `Neo4j Bloom`. We will be using these tools to extract and visualise knowledge from the loaded ontology.

7. Install Neosemantics plugin
8. On `Neo4j Desktop`: select your database, and in the plugins section click on the install button to install `neosemantics`
  - Note: the plugin `neosemantic` needs to be installed for every new database.
9. add the following line to the config file
  - `dbms.unmanaged_extension_classes=n10s.endpoint=/rdf`
    - In the desktop you'll be able to do this by clicking on the three dots `...` to the right hand side of your database and then select `settings`. You can add the fragment at the end of the file.
 10. create a unique constraint
  - `CREATE CONSTRAINT n10s_unique_uri FOR (r:Resource) REQUIRE r.uri IS UNIQUE` 
    - On Neo4j Browser run the above query
 11. create a graph config with all defaults - run the following on Neo4j Browser
  - `call n10s.graphconfig.init()`
 12. load ontology into Neo4j Database
  - `call n10s.rdf.import.fetch("file:///C:/.../addqualdb_populated.owl","RDF/XML")`

## Example Cypher Queries - Information Retrieval
Following are the example cypher queries to return visual and tabular information from the Ontology.
Open Neo4j Browser and type in the following cypher queries one by one.

### Visual 
1.  show all serialised items returned by a client
`MATCH p=()-[r:ns0__isSentBy]->() RETURN p LIMIT 18`
2.  show all items sent by client 001
`MATCH p=()-[r:ns0__isSentBy]->({ns0__clientId:'001'}) RETURN p LIMIT 5`
3.  show key contacts
`MATCH p=()-[r:ns0__isCommunicatorOf]->() RETURN p LIMIT 25`
4.  retrieve multi-level information
`MATCH p=()-[r1:ns0__isSenderOf]->()<-[r2:ns0__isAssignedTo]-() RETURN p LIMIT 25`

### Tabular
5.  How many times each part number is sent.
`Match (grn:ns0__GRN)- [REL:ns0__isAssignedTo]->(ser:ns0__Serialised) RETURN ser.ns0__partNumber, count(ser) as count`
6.  Number of parts sent by specific clients
`MATCH (client001:ns0__Client)-[rel:ns0__isSenderOf]->(ser:ns0__Serialised) return client001.ns0__companyName, count(ser) as NumOfSer`
7.  Count by part type
`MATCH (ser:ns0__Serialised) return ser.ns0__partType, count(ser.ns0__partType)`
8.  Number of parts and serialised items by ckient001
`MATCH (client:ns0__Client)-[rel:ns0__isSenderOf]-> (ser:ns0__Serialised) return client.ns0__companyName, ser.ns0__partType, count(ser.ns0__partType) as NumOfSerialised`


## References
1.  https://github.com/neo4j-labs/neosemantics
2.  https://neo4j.com/labs/neosemantics/4.0/
3.  https://neo4j.com/labs/neosemantics/4.0/config/
4.  https://neo4j.com/labs/neosemantics/4.0/import/
5.  https://medium.com/@hazzindu/import-ontology-to-neo4j-d3524f5e47dd


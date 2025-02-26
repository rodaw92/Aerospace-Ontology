OwlFly Ontology Population Tool!

## Ontology Deployment on Neo4j using neosemantics

Loading Ontology into Neo4j
- Install Neo4j Desktop
- Neo4j Desktop includes Neo4j Browser

1. Install Neo4j Desktop
2. On Neo4j Desktop: select your database, and in the plugins section click on the install button to install neo-semantics
3. add the following line to the config file
 
 3.1. dbms.unmanaged_extension_classes=n10s.endpoint=/rdf
    
    3.1.1. In the desktop you'll be able to do this by clicking on the three dots to the right-hand side of your database and then selecting settings. You can add the fragment at the end of the file.
4. create a unique constraint
  4.1. CREATE CONSTRAINT n10s_unique_uri FOR (r:Resource) REQUIRE r.uri IS UNIQUE 
    4.1.1. On Neo4j Browser run the above query
5. create a graph config with all defaults - run the following on Neo4j Browser
  5.1. call n10s.graphconfig.init()
6. load ontology into the Neo4j Database
  6.1. call n10s.rdf.import.fetch("file:///C:/.../addqualdb_populated.owl","RDF/XML")

References:
https://github.com/neo4j-labs/neosemantics
https://neo4j.com/labs/neosemantics/4.0/
https://neo4j.com/labs/neosemantics/4.0/config/
https://neo4j.com/labs/neosemantics/4.0/import/
https://medium.com/@hazzindu/import-ontology-to-neo4j-d3524f5e47dd

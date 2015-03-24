package org.crowdsourcedinterlinking.model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashSet;
import java.util.Set;

import org.crowdsourcedinterlinking.util.ConfigurationManager;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.StoreFactory;
import com.hp.hpl.jena.util.FileManager;

public class Ontology {

	private String uri;
	private String location;
	private String name;

	private Model model;
	private String nameSpace;

	// private OntModel model;
	
 

	public Ontology(String uri, String location, String name) {
		try {

			this.uri = uri;
			this.location = location;
			this.name = name;

			model = ModelFactory.createDefaultModel();
			boolean file=false; 
			
			File f = new File(location);
			
			long length =f.length();
			
			
			
			if (length< 5000000)
			{
			
			
			// OntModel model = ModelFactory.createOntologyModel();

			if (location.startsWith("http://")) {
				model.read(location);
			} else {
				
				
				//model.read("file:///" + location);
				
				
				 InputStream in = new FileInputStream(location);
				  in = new BufferedInputStream(in);
				
			/*	InputStream in2 = FileManager.get().open(location);
				RDFReader bigFileReader = model.getReader("RDF/XML");
				bigFileReader.setProperty("WARN_REDEFINITION_OF_ID","EM_IGNORE");*/
				
				
				model.read(in, null); //
				
				
			}
			
				
			}
			else
			{
				
				
				
				Store store = com.hp.hpl.jena.sdb.SDBFactory.connectStore("file:///C:/Users/ASUS12DE/workspace/ISWC2012experiment/sdb.ttl");
				
				
				
				if (store != null) {

						model = SDBFactory.connectDefaultModel(store);
						//Dataset ds = SDBFactory.connectDataset(store);
						
						
						/* only first time
						InputStream in = FileManager.get().open(location); 
						model.read(in, ""); */
						
						
						// ds.getDefaultModel().add(model.read(in, ""));
					}
					
				store.close();
				
				
				
//				Store store = com.hp.hpl.jena.sdb.SDBFactory.connectStore("file:///C:/Users/csarasua/workspace_PHD/ISWC2012experiment/sdb.ttl");
//				
//				
//				
//				if (store != null) {
//
//						model = SDBFactory.connectDefaultModel(store);
//						Dataset ds = SDBFactory.connectDataset(store);
//						
//						InputStream in = FileManager.get().open(location); 
//						
//						model.read(in, "");
//						// ds.getDefaultModel().add(model.read(in, ""));
//					}
//					
//				store.close();
				
//				 Store store = StoreFactory.create("file:///C:/Users/csarasua/workspace_PHD/ISWC2012experiment/sdb.ttl") ;
//			        Model model = SDBFactory.connectDefaultModel(store) ;
//			        
//			        StmtIterator sIter = model.listStatements() ;
//			        for ( ; sIter.hasNext() ; )
//			        {
//			            Statement stmt = sIter.nextStatement() ;
//			            System.out.println(stmt) ;
//			        }
//			        sIter.close() ;
//			        store.close() ;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Model getModel() {
		
		return model;
		
	}

	public void setModel(Model model) {
		this.model = model;
	}

	/*
	 * public void setModel(OntModel model) { this.model = model; }
	 */

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Set<Resource> listClassesToBeMapped() {
		Set<Resource> setOfClasses = new HashSet<Resource>();

		try {

			String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?c WHERE {  { ?c rdf:type owl:Class } UNION { ?c rdf:type rdfs:Class }  }";

			QueryExecution qe = QueryExecutionFactory.create(queryString,
					this.model);
			ResultSet results = qe.execSelect();
			QuerySolution qs = null;

			while (results.hasNext()) {
				qs = results.nextSolution();
				Resource c = qs.getResource("c");

				// delete AORB collectioni type owl:Class?

				if ((c.getURI() != null)
						&& (!c.getURI()
								.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#List"))) {
					setOfClasses.add(c);
				} else {
					if (c.getURI() == null) {
						System.out.println("Descartada: NULL c.getURI()");
					} else {
						System.out.println("Descartada: " + c.getURI());
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Resource res : setOfClasses) {
			System.out.println(res.getURI());
		}

		return setOfClasses;
	}
	
	public Set<Resource> listDatatypePropertiesToBeMapped() {
		Set<Resource> setOfDatatypeProperties = new HashSet<Resource>();

		try {

			String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?p WHERE { { ?p rdf:type owl:DatatypeProperty}  }";

			QueryExecution qe = QueryExecutionFactory.create(queryString,
					this.model);
			ResultSet results = qe.execSelect();
			QuerySolution qs = null;

			while (results.hasNext()) {
				qs = results.nextSolution();
				Resource p = qs.getResource("p");


				if ((p.getURI() != null)
						&& (!p.getURI()
								.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#List"))) {
					setOfDatatypeProperties.add(p);
				} else {
					if (p.getURI() == null) {
						System.out.println("Descartada: NULL"+ p.getURI());
					} else {
						System.out.println("Descartada: " + p.getURI());
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Resource res : setOfDatatypeProperties) {
			System.out.println(res.getURI());
		}

		return setOfDatatypeProperties;
		
		//RDFS:Property should be added
	}
	
	public Set<Resource> listObjectPropertiesToBeMapped() {
		Set<Resource> setOfObjectProperties = new HashSet<Resource>();

		try {

			String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?p WHERE { { ?p rdf:type owl:ObjectProperty}  }";

			QueryExecution qe = QueryExecutionFactory.create(queryString,
					this.model);
			ResultSet results = qe.execSelect();
			QuerySolution qs = null;

			while (results.hasNext()) {
				qs = results.nextSolution();
				Resource p = qs.getResource("p");


				if ((p.getURI() != null)
						&& (!p.getURI()
								.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#List"))) {
					setOfObjectProperties.add(p);
				} else {
					if (p.getURI() == null) {
						System.out.println("Descartada: NULL"+ p.getURI());
					} else {
						System.out.println("Descartada: " + p.getURI());
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Resource res : setOfObjectProperties) {
			System.out.println(res.getURI());
		}

		return setOfObjectProperties;
		
		//RDFS:Property should be added
	}
	
}

package org.crowdsourcedinterlinking.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.util.FileManager;

public class Dataset {

	private String title;
	private TypeOfDatasetLocation typeOfLocation;
	private String location; // can be the Path of a file or a SPARQL endpoint -
								// distinguished by file:/// if local otherwise
								// add tyoe of location

	private String uriSpace;
	private String vocabulary; //locator
	private String nameSpace;
	
	private Model model;
	

	public Dataset(String title, TypeOfDatasetLocation typeLoc,
			String loc, String uriSpa, String vocab, String ns) {
		try {

			this.title = title;
			this.typeOfLocation = typeLoc;
			this.location = loc;
			this.uriSpace = uriSpa;
			this.vocabulary = vocab;
			this.nameSpace = ns;

			
			
			
			Model vocabularyModel = ModelFactory.createDefaultModel();
			// Load the input data set into a model(file-based or DB-backend) only if it is a File, otherwise the queries are executed against the SPARQL Endpoint

			if (this.typeOfLocation.equals(TypeOfDatasetLocation.FILEDUMP)) {

				this.model = ModelFactory.createDefaultModel();
				
				File f = new File(location);
				long length = f.length();

				if (length < 5000000) {

					//The data set dump file can be imported to an in-memory model

					if (location.startsWith("http://")) {
						model.read(location);
					} else {

						// model.read("file:///" + location);

						InputStream in = new FileInputStream(location);
						in = new BufferedInputStream(in);

						
						model.read(in, null); 

					}

				} else {
					
					//The data set dump file is too big to handle it in an in-meomry model, therefore it is stored in a database - TO REVISE (From ontology)

					Store store = com.hp.hpl.jena.sdb.SDBFactory
							.connectStore("file:///C:/Users/csarasua/workspace_PHD/CrowdMAP-CrowdLINK/sdb.ttl");

					if (store != null) {

						model = SDBFactory.connectDefaultModel(store);
						// Dataset ds = SDBFactory.connectDataset(store);

						/*
						 * only first time */
						 InputStream in = FileManager.get().open(location); model.read(in, "");
						 

						// ds.getDefaultModel().add(model.read(in, ""));
					}

					store.close();

					// Store store =
					// com.hp.hpl.jena.sdb.SDBFactory.connectStore("file:///C:/Users/csarasua/workspace_PHD/ISWC2012experiment/sdb.ttl");
					//
					//
					//
					// if (store != null) {
					//
					// model = SDBFactory.connectDefaultModel(store);
					// Dataset ds = SDBFactory.connectDataset(store);
					//
					// InputStream in = FileManager.get().open(location);
					//
					// model.read(in, "");
					// // ds.getDefaultModel().add(model.read(in, ""));
					// }
					//
					// store.close();

					// Store store =
					// StoreFactory.create("file:///C:/Users/csarasua/workspace_PHD/ISWC2012experiment/sdb.ttl")
					// ;
					// Model model = SDBFactory.connectDefaultModel(store) ;
					//
					// StmtIterator sIter = model.listStatements() ;
					// for ( ; sIter.hasNext() ; )
					// {
					// Statement stmt = sIter.nextStatement() ;
					// System.out.println(stmt) ;
					// }
					// sIter.close() ;
					// store.close() ;
				}

			}
			
			//load the vocabulary -- should be done like ebfore if too big store it in Db
			
			if (vocabulary!=null)
			{
			if (vocabulary.startsWith("http://")) {
				vocabularyModel.read(vocabulary);
			} else {

				// model.read("file:///" + location);

				InputStream in = new FileInputStream(vocabulary);
				in = new BufferedInputStream(in);

				
				vocabularyModel.read(in, null); 

			}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public TypeOfDatasetLocation getTypeOfLocation() {
		return typeOfLocation;
	}


	public void setTypeOfLocation(TypeOfDatasetLocation typeOfLocation) {
		this.typeOfLocation = typeOfLocation;
	}


	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public String getUriSpace() {
		return uriSpace;
	}


	public void setUriSpace(String uriSpace) {
		this.uriSpace = uriSpace;
	}


	public String getVocabulary() {
		return vocabulary;
	}


	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}


	public String getNameSpace() {
		return nameSpace;
	}


	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}


	public Model getModel() {
		return model;
	}


	public void setModel(Model model) {
		this.model = model;
	}

}

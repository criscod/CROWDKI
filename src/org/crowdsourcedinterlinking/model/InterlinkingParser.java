package org.crowdsourcedinterlinking.model;

import com.google.common.io.Files;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.*;
import org.crowdsourcedinterlinking.util.Constants;
import org.crowdsourcedinterlinking.util.ParseLinksInOrder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * @author csarasua
 */
public class InterlinkingParser {

    private File fInterlinking;
    private Interlinking interlinking;

    private String format = "RDF/XML";

    public InterlinkingParser(File interlinking) {

        this.fInterlinking = interlinking;
        if (fInterlinking.getAbsolutePath().endsWith(".nt")) {
            this.format = "N-TRIPLES";
        }
    }


    public void parseInterlinking(Dataset ds1, Dataset ds2) {
        Set<Interlink> setOfInterlinks = new HashSet<Interlink>();
        List<Interlink> listOfInterlinks = new ArrayList<Interlink>();
        String queryString;
        String entity1, entity2;
        String relation = null;
        Resource resourceE1, resourceE2;
        RDFNode objectNode;
        Property property;
        Interlink interlink = null;

        Model modelTemp = ModelFactory.createDefaultModel();
        //works with nt triples, but not with e.g. RDF/XML statements
        if (fInterlinking.getAbsolutePath().endsWith(".nt") && org.crowdsourcedinterlinking.util.ConfigurationManager.getInstance().getLinksInOrder().equals(ParseLinksInOrder.Lists)) {

            try {
                List<String> allLines = Files.readLines(fInterlinking, Charset.defaultCharset());
                for (String line : allLines) {
                    String[] lineElements = line.split(" ");
                    //<uri> --> uri
                    String uri1 = lineElements[0].substring(1, lineElements[0].length() - 1);
                    resourceE1 = modelTemp.createResource(uri1);
                    System.out.println("NTriple parsed substring: " + uri1);
                    //<uri> --> uri
                    String uri2 = lineElements[1].substring(1, lineElements[1].length() - 1);
                    property = modelTemp.createProperty(uri2);
                    System.out.println("NTriple parsed substring: " + uri2);
                    //<uri> (has deleted the . with the space split --> uri
                    String uri3 = lineElements[2].substring(1, lineElements[2].length() - 1);
                    resourceE2 = modelTemp.createResource(uri3);
                    System.out.println("NTriple parsed substring: " + uri3);

                    //if((this.elementInDataset(resourceE1.getURI(), ds2)) && (this.elementInDataset(resourceE2.getURI(), ds1)) && property.equals(Constants.NS_OWL+"sameAs"))
                    //{
                    // interlink = new Interlink(resourceE2, resourceE1, property);

                    //}
                    //else if((this.elementInDataset(resourceE1.getURI(), ds1))&&(this.elementInDataset(resourceE2.getURI(), ds2)))
                    //{
                    interlink = new Interlink(resourceE1, resourceE2, property);

                    //	 }
                    // else
                    //{
                    // System.out.println("not added: "+resourceE1 + " " +resourceE2 );
                    //}


                    listOfInterlinks.add(interlink);


                }


            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            this.interlinking = new Interlinking(ds1, ds2, listOfInterlinks);
        } else {

            Model model = ModelFactory.createDefaultModel();
            Model resultModel = ModelFactory.createDefaultModel();

            model.read("file:///" + fInterlinking.getAbsolutePath(), this.format);

            StmtIterator iter = model.listStatements();

            System.out.println("----------");
            // print out the predicate, subject and object of each statement
            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement();
                resourceE1 = stmt.getSubject();
                property = stmt.getPredicate();
                resourceE2 = stmt.getObject().asResource(); // in the interlinks the objects will be resources, like in Ontology Alignment

                System.out.println("r1: " + resourceE1);
                System.out.println("property: " + property);
                System.out.println("r2: " + resourceE2);


                // check that source if from dataset1 and target is from dataset2, just in case and when it is not in the correct order turn around -- it is only possible with owl:sameAs though. In principle should not happen, this might mean that there was an error.

                if ((this.elementInDataset(resourceE1.getURI(), ds2)) && (this.elementInDataset(resourceE2.getURI(), ds1)) && property.equals(Constants.NS_OWL + "sameAs")) {
                    interlink = new Interlink(resourceE2, resourceE1, property);

                } else if ((this.elementInDataset(resourceE1.getURI(), ds1)) && (this.elementInDataset(resourceE2.getURI(), ds2))) {
                    interlink = new Interlink(resourceE1, resourceE2, property);

                } else {
                    System.out.println("not added: " + resourceE1 + " " + resourceE2);
                }


                setOfInterlinks.add(interlink);
            }
            System.out.println("----------");

            this.interlinking = new Interlinking(ds1, ds2, setOfInterlinks);
        }


    }

    public File getfInterlinking() {
        return fInterlinking;
    }

    public void setfInterlinking(File fInterlinking) {
        this.fInterlinking = fInterlinking;
    }

    public Interlinking getInterlinking() {
        return interlinking;
    }

    public void setInterlinking(Interlinking interlinking) {
        this.interlinking = interlinking;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    private boolean elementInDataset(String element, Dataset dataset) {

        boolean result = false;

        try {

            if (dataset.getTypeOfLocation().equals(
                    TypeOfDatasetLocation.FILEDUMP)) {

                Model model = dataset.getModel();
                String queryString = "SELECT ?p ?o WHERE { { <" + element + "> ?p ?o } UNION { ?s ?p <" + element + "> } }";
                QueryExecution qe = QueryExecutionFactory.create(queryString,
                        model);
                ResultSet results = qe.execSelect();
                if (results.hasNext()) {
                    result = true;

                }
            } else if (dataset.getTypeOfLocation().equals(
                    TypeOfDatasetLocation.SPARQLENDPOINT)) {
                // tbc - against endpoint

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }
}

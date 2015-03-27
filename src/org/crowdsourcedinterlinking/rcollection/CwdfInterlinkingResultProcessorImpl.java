package org.crowdsourcedinterlinking.rcollection;

import com.google.common.io.Files;
import com.hp.hpl.jena.rdf.model.*;
import org.crowdsourcedinterlinking.model.Dataset;
import org.crowdsourcedinterlinking.model.Interlink;
import org.crowdsourcedinterlinking.model.Interlinking;
import org.crowdsourcedinterlinking.util.ConfigurationManager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;
/**
 * @author csarasua
 */
public class CwdfInterlinkingResultProcessorImpl implements InterlinkingResultProcessor {


    public CwdfInterlinkingResultProcessorImpl() {

    }

    public void serialiseInterlinkingToNTriples(Interlinking crowdInterlinking) {
        Dataset d1 = crowdInterlinking.getDataset1();
        Dataset d2 = crowdInterlinking.getDataset2();

        Model crowdModel = ModelFactory.createDefaultModel();

        Set<Interlink> setOfLinks = crowdInterlinking.getSetOfInterLinks();

        // File f = new File(confManager.getAlignmentResultFile());
        File f = new File(ConfigurationManager.getInstance().getCrowdInterlinkingsDirectory()
                + ConfigurationManager.getInstance().getCrowdBaseFileName()
                + d1.getTitle() + d2.getTitle() + ".rdf");
        f.delete();
        String ls = System.getProperty("line.separator");


        for (Interlink i : setOfLinks) {
            Resource r1 = crowdModel.createResource(i.getElementA().getURI());
            Resource r2 = crowdModel.createResource(i.getElementB().getURI());
            Property p = crowdModel.createProperty(i.getRelation().getURI());
            Statement st = crowdModel.createStatement(r1, p, r2);
            crowdModel.add(st);
            String ntriplesLine = "<" + r1.getURI() + "> <" + p.getURI() + "> <" + r2.getURI() + "> .";

            try {
                Files.append(ntriplesLine, f, Charset.defaultCharset());
                Files.append(ls, f, Charset.defaultCharset());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

	/*
    public void serialiseSelectedAlignmentToAlignmentAPIFormat(
			Interlinking crowdAlignment) {

		try {
			Model model = ModelFactory.createDefaultModel();
			model.getWriter("RDF/XML-ABBREV").setProperty("showXmlDeclaration",
					true);
			model.setNsPrefix("", Constants.NS_ALIGN);


			Dataset onto1 = crowdAlignment.getDataset1();
			Resource onto1Resource = model.createResource(onto1.getUriSpace());
			Dataset onto2 = crowdAlignment.getDataset2();
			Resource onto2Resource = model.createResource(onto2.getUriSpace());
			Set<Interlink> setOfMapCells = crowdAlignment.getSetOfInterLinks();

			
			// File f = new File(confManager.getAlignmentResultFile());
			File f = new File(ConfigurationManager.getInstance()
					.getCrowdAlignmentsDirectory()
					+ ConfigurationManager.getInstance().getCrowdBaseFileName()
					+onto1.getTitle() + onto2.getTitle() + ".rdf");
			// System.out.println("confManager.getResultFile: "+confManager.getAlignmentResultFile());
			// System.out.println("the file for resulst: "+f.getAbsolutePath());

			Resource alignmentResource = model.createResource();
			Resource alignment = model.createResource(Constants.NS_ALIGN
					+ "Alignment");
			alignmentResource.addProperty(RDF.type, alignment);
			Property pXml = model.createProperty(Constants.NS_ALIGN + "xml");
			alignmentResource.addProperty(pXml, "yes");

			Property pOnto1 = model
					.createProperty(Constants.NS_ALIGN + "onto1");
			Resource ontology = model.createResource(Constants.NS_ALIGN
					+ "Ontology");
			onto1Resource.addProperty(RDF.type, ontology);
			Property pLocation = model.createProperty(Constants.NS_ALIGN
					+ "location");
			onto1Resource.addProperty(pLocation, onto1.getLocation());
			alignmentResource.addProperty(pOnto1, onto1Resource);

			Property pOnto2 = model
					.createProperty(Constants.NS_ALIGN + "onto2");
			alignmentResource.addProperty(pOnto2, onto2Resource);
			onto2Resource.addProperty(RDF.type, ontology);
			onto2Resource.addProperty(pLocation, onto2.getLocation());
			alignmentResource.addProperty(pOnto1, onto1Resource);

			Property pMap = model.createProperty(Constants.NS_ALIGN + "map");
			Resource cell = model.createResource(Constants.NS_ALIGN + "Cell");
			Property pEntity1 = model.createProperty(Constants.NS_ALIGN
					+ "entity1");
			Property pEntity2 = model.createProperty(Constants.NS_ALIGN
					+ "entity2");
			Property pRelation = model.createProperty(Constants.NS_ALIGN
					+ "relation");
			Property pMeasure = model.createProperty(Constants.NS_ALIGN + "measure");
			// Property pMeasure =
			// model.createProperty(Constants.NS_ALIGN+"measure");

			for (Interlink mp : setOfMapCells) {

				Resource cellResource = model.createResource(Constants.NS_CROWD
						+ mp.getId());
				cellResource.addProperty(RDF.type, cell);

				// If the relation is GENERAL it must not be serialised as
				// general, but as specific instead and changing the order of
				// elementA and elementB
				if (mp.getRelation().equals("&gt;")) // general
				{
					cellResource.addProperty(pEntity1, mp.getElementB());
					cellResource.addProperty(pEntity2, mp.getElementA());
					// cellResource.addProperty(pRelation, "&lt;");
					cellResource.addProperty(pRelation, "&lt;");
					//when without measure delete the next instruction
					cellResource.addProperty(pMeasure, mp.getMeasure(), new XSDDatatype("float"));
				} else {
					cellResource.addProperty(pEntity1, mp.getElementA());
					cellResource.addProperty(pEntity2, mp.getElementB());
					cellResource.addProperty(pRelation, mp.getRelation());
					//when without measure delete the next instruction
					cellResource.addProperty(pMeasure, mp.getMeasure(), new XSDDatatype("float"));
				}

				alignmentResource.addProperty(pMap, cellResource);

			}

			OutputStream out = new FileOutputStream(f);

			PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
			writer.println("<?xml version='1.0' encoding='utf-8' standalone='no'?>");
			writer.flush();

			model.write(writer, "RDF/XML-ABBREV");

			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/


}

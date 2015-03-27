package org.crowdsourcedinterlinking.mgeneration;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import org.crowdsourcedinterlinking.model.Dataset;
import org.crowdsourcedinterlinking.model.Interlink;
import org.crowdsourcedinterlinking.model.Interlinking;
import org.crowdsourcedinterlinking.util.ConfigurationManager;

import java.util.HashSet;
import java.util.Set;

/**
 * @author csarasua
 */
public class CartesianLinksGeneratorImpl extends LinksGeneratorImpl {

    // datasets in parent class

    public CartesianLinksGeneratorImpl(Dataset d1, Dataset d2) {
        try {
            this.setDataset1(d1);
            this.setDataset2(d2);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Interlinking generateLinks() {
        Interlinking result = null;
        Set<Interlink> setOfInterlinks = new HashSet<Interlink>();

        Interlink map;


        Model model = ModelFactory.createDefaultModel();
        Property defaultProp = model.createProperty(ConfigurationManager.getInstance().getInterlinkDefaultProperty());
        try {
            this.registerDatasetsInTrackFile();


            String ls = System.getProperty("line.separator");


            Set<Resource> setElementsD1 = this.getDataset1().listResourcesToBeLinked();
            Set<Resource> setElementsD2 = this.getDataset2().listResourcesToBeLinked();


            for (Resource class1 : setElementsD1) {

                for (Resource class2 : setElementsD2) {

					/*
                     * Files.append("Class1: "+class1.getURI(), resultsFile,
					 * Charset.defaultCharset()); Files.append(ls, resultsFile,
					 * Charset.defaultCharset());
					 */

					/*
					 * Files.append("Class2: "+class2.getURI(), resultsFile,
					 * Charset.defaultCharset()); Files.append(ls, resultsFile,
					 * Charset.defaultCharset());
					 */

                    map = new Interlink(class1, class2, defaultProp);
                    setOfInterlinks.add(map);
                }
            }


            result = new Interlinking(this.getDataset1(), this.getDataset2(),
                    setOfInterlinks);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;

    }

}

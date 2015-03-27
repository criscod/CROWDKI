package org.crowdsourcedinterlinking.mgeneration;

import com.google.common.io.Files;
import org.crowdsourcedinterlinking.model.*;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author csarasua
 *         Class to generate the interlinking choices from parsing a .csv file
 */
public class DirectInterlinkingChoicesGeneratorImpl extends InterlinkingChoicesGeneratorImpl {

    private File fChoices;

    public DirectInterlinkingChoicesGeneratorImpl(File choices) {
        this.fChoices = choices;
    }


    public InterlinkingChoices generateChoices() {
        /* for each read line from the choices csv file "D1,D2,uri1,label1,uripred,labelpred,uri2,label2"
         *
         * create datasets
         * find in directory ("descDx"), read and create descriptions of each dataset, add InterlinkingChoices in the map that can be looked up
         *
         */
        InterlinkingChoices result = new InterlinkingChoices();

        String workingDir = System.getProperty("user.dir");
        String workingDirForFileName = workingDir.replace("\\", "/");

        InterlinkingChoice choice;

        try {
            //read the file
            List<String> lines = Files.readLines(this.fChoices, Charset.defaultCharset());
            String header = new String(lines.get(0));
            lines.remove(0);

            //create InterlinkingChoices
            for (String line : lines) {
                String[] lineElements = line.split("\t");
                //lines are of shape "D1	D2	uri1	labeluri1	predicate	labelpredicate	uri2	labeluri2"
                String idD1 = lineElements[0];
                String idD2 = lineElements[1];
                String uri1 = lineElements[2];
                String labelUri1 = lineElements[3];
                String predicate = lineElements[4];
                String labelPredicate = lineElements[5];
                String uri2 = lineElements[6];
                String labelUri2 = lineElements[7];

                Dataset d1 = new Dataset(idD1, TypeOfDatasetLocation.SPARQLENDPOINT, null, null, null, null);
                Dataset d2 = new Dataset(idD2, TypeOfDatasetLocation.SPARQLENDPOINT, null, null, null, null);

                File fDescD1 = new File(workingDirForFileName + "/datasets/desc" + d1.getTitle() + ".rdf");
                DatasetDescription descD1 = new DatasetDescription(d1.getTitle(), fDescD1);

                //add the corresponding dataset descriptions if they are not already in the Map

                if (result.getDatasetDescriptions().get(d1.getTitle()) == null) {
                    // result.getDatasetDescriptions().put(d1.getTitle(),descD1);
                    result.addDatasetDescription(descD1);
                }

                File fDescD2 = new File(workingDirForFileName + "/datasets/desc" + d2.getTitle() + ".rdf");
                DatasetDescription descD2 = new DatasetDescription(d2.getTitle(), fDescD2);

                if (result.getDatasetDescriptions().get(d2.getTitle()) == null) {
                    // result.getDatasetDescriptions().put(d2.getTitle(),descD2);
                    result.addDatasetDescription(descD2);
                }


                choice = new InterlinkingChoice(d1, d2, uri1, labelUri1, predicate, labelPredicate, uri2, labelUri2);
                //add the choice to the InterlinkingChoices
                result.addChoice(choice);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }
}

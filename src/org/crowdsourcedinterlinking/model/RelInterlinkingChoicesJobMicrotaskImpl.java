package org.crowdsourcedinterlinking.model;

import com.google.common.io.Files;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

/**
 * @author csarasua
 */
public class RelInterlinkingChoicesJobMicrotaskImpl extends JobMicrotaskImpl {

    public void createUI() {
    }

    public void createUI(InterlinkingChoices choices) {

        String workingDir = System.getProperty("user.dir");
        String workingDirForFileName = workingDir.replace("\\", "/");


        //build the cml file D1D2 section and all the questions


        try {
            String cmlCode = new String();
            File cmlFile = new File(workingDirForFileName + "/CML/interlinkingchoices.txt");
            cmlFile.delete();

            String ls = System.getProperty("line.separator");
            //Files.append(ls, cmlFile, Charset.defaultCharset());
            //read the CML block to be repeated
            File fileRepeatedBlock = new File(workingDirForFileName + "/CML/interlinkingchoices_repeated.txt");
            List<String> linesRepeated = Files.readLines(fileRepeatedBlock, Charset.defaultCharset());
            String repeatedBlock = new String();
            for (String lineRep : linesRepeated) {
                repeatedBlock = repeatedBlock + lineRep;
            }

            File fileDescBlock = new File(workingDirForFileName + "/CML/interlinkingchoices_datasetdesc.txt");
            List<String> linesDesc = Files.readLines(fileDescBlock, Charset.defaultCharset());
            String descBlock = new String();
            for (String lineDesc : linesDesc) {
                descBlock = descBlock + lineDesc;
            }


            Set<InterlinkingChoice> choicesSet = choices.getChoices();
            for (InterlinkingChoice choicei : choicesSet) {

                String idD1 = choicei.getD1().getTitle();
                DatasetDescription desc = choices.getDatasetDescriptions().get(idD1);
                String text = this.getTextFromDatsetDescription(desc);
                String descI = new String(descBlock);
                descI = descI.replaceAll("XXX", text);
                Files.append(descI, cmlFile, Charset.defaultCharset());
                //         Files.append(ls, cmlFile, Charset.defaultCharset());


                String idD2 = choicei.getD2().getTitle();
                DatasetDescription desc2 = choices.getDatasetDescriptions().get(idD2);
                String text2 = this.getTextFromDatsetDescription(desc2);
                String descI2 = new String(descBlock);
                descI2 = descI2.replaceAll("XXX", text2);
                Files.append(descI2, cmlFile, Charset.defaultCharset());
                //  Files.append(ls, cmlFile, Charset.defaultCharset());

                //statement part
                String textSt = new String();
                textSt = textSt + choicei.getLabelClass1();
                textSt = textSt + " ";
                textSt = textSt + choicei.getLabelPredicate();
                textSt = textSt + " ";
                textSt = textSt + choicei.getLabelClass2();
                textSt = textSt + " ";
                String repeatedBlock2 = repeatedBlock.replaceAll("XXX", textSt);
                Files.append(repeatedBlock2, cmlFile, Charset.defaultCharset());
                //  Files.append(ls, cmlFile, Charset.defaultCharset());

            }


            //issue CrowdFlower with sending code directly from string
            List<String> lines = Files.readLines(cmlFile,
                    Charset.defaultCharset());
            for (String s : lines) {
                cmlCode = cmlCode + s;

            }
            this.setCml(cmlCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void serialiseUnitsIntoCVSFile() {

    }

    //in unit data jobs in link validation jena RDF access is not at this level - refactor
    private String getTextFromDatsetDescription(DatasetDescription desc) {
        String resultText = new String();
        Model descModelD1 = desc.getDescription();
        String query = "SELECT ?desc ?keyword ?subject ?res ?property WHERE {?s <http://purl.org/dc/terms/description> ?desc . ?s <http://www.w3.org/ns/dcat#keyword> ?keyword . ?s <http://purl.org/dc/terms/subject> ?subject . ?s <http://rdfs.org/ns/void#exampleResource> ?eres . ?eres ?property ?object . ?eres <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?res }";
        ResultSet results = null;
        QuerySolution qs;

        QueryExecution qexec = QueryExecutionFactory.create(query, descModelD1);
        results = qexec.execSelect();
        boolean first = true;
        Resource prevResource = null;
        while (results.hasNext()) {
            qs = results.nextSolution();
            if (first) {
                String descText = qs.getLiteral("desc").getString();
                String keywordText = qs.getLiteral("keyword").getString();
                String subjectText = qs.getLiteral("subject").getString();

                resultText = resultText + "The first dataset is about " + keywordText + " and " + subjectText + " . Description:" + descText + " . ";
                first = false;

                Resource r = qs.getResource("res");

                Resource p = qs.getResource("property");

                resultText = resultText + " " + r.getLocalName() + "s are described with " + p.getLocalName();
                prevResource = r;
            } else {
                Resource exResource = qs.getResource("res");
                Resource prop = qs.getResource("property");
                if (exResource.getLocalName().equals(prevResource.getLocalName())) {
                    resultText = resultText + ", " + prop.getLocalName();
                } else {
                    resultText = resultText + ". " + exResource.getLocalName() + "s are described with " + prop.getLocalName();
                }
            }
        }
        return resultText;
    }
}

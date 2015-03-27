package org.crowdsourcedinterlinking.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.URIref;
import com.hp.hpl.jena.vocabulary.RDF;


import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
/**
 * @author csarasua
 */
public class GesisXSLX2RDF {


    //Transform the excel file to NT notation
    private File excelFile;
    private File rdfLinks;
    private File rdfFilePubs;
    private File rdfFileStuds;
    private RDFFormat format;
    private String ontology;

    private final String NS_DDI = "http://rdf-vocabulary.ddialliance.org/discovery#";
    private final String NS_DATACITE = "http://purl.org/spar/datacite#";


    public GesisXSLX2RDF(File fIn, File fOutLinks, File fOut1, File fOut2, RDFFormat format, String ontoPath) {
        this.excelFile = fIn;
        this.rdfLinks = fOutLinks;
        this.rdfFilePubs = fOut1;
        this.rdfFileStuds = fOut2;
        this.format = format;
        this.ontology = ontoPath;

    }

    //this method transforms the kind of Excel file provided by Daniel for use case 3 - adhoc
    public void transformfile3() {

        Model model = ModelFactory.createDefaultModel();
        File fOntology = new File(this.ontology);
        model.read("file:///" + fOntology.getAbsolutePath());

        Model modelLinks = ModelFactory.createDefaultModel();
        Model modelPublications = ModelFactory.createDefaultModel();

        Model modelStudies = ModelFactory.createDefaultModel();


        try {
            FileInputStream file = new FileInputStream(this.excelFile);


            //Get the workbook instance for XLS file
            HSSFWorkbook workbook = new HSSFWorkbook(file);

            //Get second sheet from the workbook
            //HSSFSheet sheet = workbook.getSheetAt(1);
            HSSFSheet sheet = workbook.getSheetAt(0);

            //prepare the classes and properties of the ontology

			
		
			
			
			/*Property creatorProperty = model.getProperty(Constants.NS_DC_TERMS + "creator");
			Property dateProperty = model.getProperty(Constants.NS_DC_TERMS + "date");
			Property titleProperty = model.getProperty(Constants.NS_DC_TERMS + "title");
			Property abstractProperty = model.getProperty(Constants.NS_DC_TERMS + "abstract");
			
			Resource discoStudy = model.getResource(this.NS_DDI + "Study");
			
			
			// I can also load FOAF + datacite + NEPOMUK, but it is not really interesting - would be merged in the ontology
			Resource foafDocument = model.createResource(Constants.NS_FOAF + "Document");
			Resource foafPerson = model.createResource(Constants.NS_FOAF + "Person");
			Property foafNameProperty = model.createProperty(Constants.NS_FOAF + "name");
			Property doiProperty = model.createProperty(this.NS_DATACITE + "doi");
			Property affiliation = model.createProperty("http://www.semanticdesktop.org/ontologies/nco/#hasAffiliation");
			*/
            Property creatorPropertyP = modelPublications.createProperty(Constants.NS_DC_TERMS + "creator");
            Property creatorPropertyS = modelStudies.createProperty(Constants.NS_DC_TERMS + "creator");

            Property datePropertyP = modelPublications.createProperty(Constants.NS_DC_TERMS + "date");
            Property datePropertyS = modelStudies.createProperty(Constants.NS_DC_TERMS + "date");

            Property titlePropertyP = modelPublications.createProperty(Constants.NS_DC_TERMS + "title");
            Property titlePropertyS = modelPublications.createProperty(Constants.NS_DC_TERMS + "title");

            Property abstractPropertyP = modelPublications.createProperty(Constants.NS_DC_TERMS + "abstract");
            Property abstractPropertyS = modelStudies.createProperty(Constants.NS_DC_TERMS + "abstract");

            Resource discoStudy = model.getResource(this.NS_DDI + "Study");


            // I can also load FOAF + datacite + NEPOMUK, but it is not really interesting - would be merged in the ontology
            Resource foafDocumentP = modelPublications.createResource(Constants.NS_FOAF + "Document");
//			Resource foafDocumentS = modelStudies.createResource(Constants.NS_FOAF + "Document");

            Resource foafPersonP = modelPublications.createResource(Constants.NS_FOAF + "Person");
            Resource foafPersonS = modelStudies.createResource(Constants.NS_FOAF + "Person");

            Property foafNamePropertyP = modelPublications.createProperty(Constants.NS_FOAF + "name");
            Property foafNamePropertyS = modelStudies.createProperty(Constants.NS_FOAF + "name");

            Property doiPropertyS = modelStudies.createProperty(this.NS_DATACITE + "doi");

            Property affiliationP = modelPublications.createProperty("http://www.semanticdesktop.org/ontologies/nco/#hasAffiliation");
            Property affiliationS = modelStudies.createProperty("http://www.semanticdesktop.org/ontologies/nco/#hasAffiliation");


            Property dctermsRelation = modelLinks.createProperty("http://purl.org/dc/terms/relation");


            //Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = sheet.iterator();


            int rowCount = 1;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                boolean correctContent = true;

                if (rowCount == 2) {
                    correctContent = check3(row);
                }

                // [GOLD] else if( correctContent && ( (rowCount>2 && rowCount<25)))
                // [DATA]  else if( correctContent && ( (rowCount>2 && rowCount<103)))
                else if (correctContent && ((rowCount > 2 && rowCount < 103)))
                // else if( correctContent && ( (rowCount>2)))

                {


                    //I add URIs not to leave it empty - but they are not real and actually, I don�t really access them

                    Resource publicationResource = modelPublications.createResource(URIref.encode("http://www.gesis.org/data/publication" + (rowCount - 2)));    //EMPTY URI!
                    publicationResource.addProperty(RDF.type, foafDocumentP);

                    Resource studyResource = modelStudies.createResource(URIref.encode("http://www.gesis.org/data/study" + (rowCount - 2)));    //EMPTY URI!
                    studyResource.addProperty(RDF.type, discoStudy);

                    Resource publicationResourceL = modelLinks.createResource(URIref.encode("http://www.gesis.org/data/publication" + (rowCount - 2)));    //EMPTY URI!

                    Resource studyResourceL = modelLinks.createResource(URIref.encode("http://www.gesis.org/data/study" + (rowCount - 2)));    //EMPTY URI!

                    publicationResourceL.addProperty(dctermsRelation, studyResourceL);

                    //For each row, iterate through each columns
                    Iterator<Cell> cellIterator = row.cellIterator();

                    int columnCount = 1;
                    while (cellIterator.hasNext()) {

                        Cell cell = cellIterator.next();
                        String cellValue = "";

                        if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                            Double doubleValue = new Double(cell.getNumericCellValue());
                            int integerValue = doubleValue.intValue();
                            cellValue = Integer.toString(integerValue);
                            // cellValue=Double.toString(cell.getNumericCellValue());

                        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            cellValue = cell.getStringCellValue();
			    			  /* HSSFDataFormatter formatter = new HSSFDataFormatter(); 
			    			   formatter.formatCellValue(cell);*/
                        }

                        if ((!cellValue.equals(" ")) && (!cellValue.equals("BB")) && (!cellValue.equals("[]"))) {
                            if (columnCount == 2) {
                                publicationResource.addProperty(titlePropertyP, cellValue);
                            } else if (columnCount == 3) {

                                //Split authors
                                String[] authorsArray = cellValue.split(";");

                                //for each author get the name and the affiliation
                                for (String s : authorsArray) {
                                    String[] nameElements;

                                    String surname = "";
                                    String firstname = "";
                                    String nameText = "";
                                    if (s.contains("(")) {
                                        nameText = s.substring(0, s.indexOf("("));


                                    } else {

                                        nameText = s;

                                    }

                                    Resource personResourceP;
                                    if (nameText.contains(",")) {
                                        nameElements = nameText.split(",");
                                        surname = nameElements[0];
                                        firstname = nameElements[1].substring(1, nameElements[1].length()); //there is a space before and after the name
                                        UUID id = UUID.randomUUID();
                                        personResourceP = modelPublications.createResource(URIref.encode("http://www.gesis.org/data/person" + firstname + surname + id));

                                        personResourceP.addProperty(foafNamePropertyP, firstname + " " + surname);
                                    } else {
                                        firstname = nameText;
                                        UUID id = UUID.randomUUID();
                                        personResourceP = modelPublications.createResource(URIref.encode("http://www.gesis.org/data/person" + firstname + id));
                                        personResourceP.addProperty(foafNamePropertyP, firstname);
                                    }
                                    personResourceP.addProperty(RDF.type, foafPersonP);


                                    if (s.contains("(")) {
                                        String affil = s.substring(s.indexOf("(") + 1, s.length() - 1);


                                        personResourceP.addProperty(affiliationP, affil);

                                    }
                                    publicationResource.addProperty(creatorPropertyP, personResourceP);
                                }


                            } else if (columnCount == 4) {
                                publicationResource.addProperty(datePropertyP, cellValue);
                            } else if (columnCount == 5) {
                                publicationResource.addProperty(abstractPropertyP, cellValue);
                            } else if (columnCount == 6) {
                                studyResource.addProperty(titlePropertyS, cellValue);
                            } else if (columnCount == 7) {
                                //Split authors
                                String[] authorsArray = cellValue.split(";");

                                //for each author get the name and the affiliation
                                for (String s : authorsArray) {
                                    String[] nameElements;

                                    String surname = "";
                                    String firstname = "";
                                    String nameText = "";
                                    if (s.contains("(")) {
                                        nameText = s.substring(0, s.indexOf("("));


                                    } else {

                                        nameText = s;

                                    }

                                    Resource personResourceS;
                                    if (nameText.contains(",")) {
                                        nameElements = nameText.split(",");
                                        surname = nameElements[0];
                                        firstname = nameElements[1].substring(1, nameElements[1].length()); //there is a space before and after the name
                                        UUID id = UUID.randomUUID();
                                        personResourceS = modelStudies.createResource(URIref.encode("http://www.gesis.org/data/person" + firstname + surname + id));

                                        personResourceS.addProperty(foafNamePropertyS, firstname + " " + surname);
                                    } else {
                                        firstname = nameText;
                                        UUID id = UUID.randomUUID();
                                        personResourceS = modelStudies.createResource(URIref.encode("http://www.gesis.org/data/person" + firstname + id));
                                        personResourceS.addProperty(foafNamePropertyS, firstname);
                                    }
                                    personResourceS.addProperty(RDF.type, foafPersonS);


                                    if (s.contains("(")) {
                                        String affil = s.substring(s.indexOf("(") + 1, s.length() - 1);


                                        personResourceS.addProperty(affiliationS, affil);

                                    }
                                    studyResource.addProperty(creatorPropertyS, personResourceS);

                                }
                            } else if (columnCount == 8) {
                                studyResource.addProperty(datePropertyS, cellValue);
                            } else if (columnCount == 9) {
                                studyResource.addProperty(doiPropertyS, cellValue);
                            }
                        }

                        columnCount = columnCount + 1;

                    }
                }
                //if the correctContent is false then the model will be empty
                else if (!correctContent) {
                    System.out.println("incorrect format of excel columns");
                }

                rowCount = rowCount + 1;
            }
			 
			 /*
			 OutputStream out = new FileOutputStream(this.rdfFilePubs);
			 
			 
			 RDFDataMgr.write(out, modelPublications, Lang.RDFXML) ;*/


            //Serialise the file (ask about the format)
            OutputStream outPubs = new FileOutputStream(this.rdfFilePubs);
            modelPublications.write(outPubs, "RDF/XML");

            OutputStream outStuds = new FileOutputStream(this.rdfFileStuds);
            modelStudies.write(outStuds, "RDF/XML");

            OutputStream outLinks = new FileOutputStream(this.rdfLinks);
            modelLinks.write(outLinks, "N-TRIPLE");

			 
			/* OutputStream outPubs = new FileOutputStream(this.rdfFilePubs);

				PrintWriter writer1 = new PrintWriter(new OutputStreamWriter(outPubs));
				writer1.println("<?xml version='1.0' encoding='utf-8' standalone='no'?>");
				writer1.flush();

				modelPublications.write(writer1, "RDF/XML");

				writer1.close();
				
				OutputStream outStuds = new FileOutputStream(this.rdfFileStuds);

				
					PrintWriter writer2 = new PrintWriter(new OutputStreamWriter(outStuds));
					writer2.println("<?xml version='1.0' encoding='utf-8' standalone='no'?>");
					writer2.flush();

					modelStudies.write(writer2, "RDF/XML");

					writer2.close();
					
					OutputStream outLinks = new FileOutputStream(this.rdfLinks);

					PrintWriter writer3 = new PrintWriter(new OutputStreamWriter(outLinks));
					
					writer3.flush();

					modelLinks.write(writer3, "N-TRIPLE");

					writer3.close();*/

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }


    }

    public void transformfile1() {

        Model model = ModelFactory.createDefaultModel();
        File fOntology = new File(this.ontology);
        model.read("file:///" + fOntology.getAbsolutePath());

        Model modelLinks = ModelFactory.createDefaultModel();
        Model modelPublications = ModelFactory.createDefaultModel();

        Model modelStudies = ModelFactory.createDefaultModel();


        try {
            FileInputStream file = new FileInputStream(this.excelFile);


            //Get the workbook instance for XLS file
            HSSFWorkbook workbook = new HSSFWorkbook(file);

            //Get second sheet from the workbook
            HSSFSheet sheet = workbook.getSheetAt(0);

            //prepare the classes and properties of the ontology


            Property snippetPropertyP = modelPublications.createProperty(Constants.NS_INFOLIS + "textSnippet");

            //Property creatorPropertyP = modelPublications.createProperty(Constants.NS_DC_TERMS + "creator");
            Property creatorPropertyS = modelStudies.createProperty(Constants.NS_DC_TERMS + "creator");

            //Property datePropertyP = modelPublications.createProperty(Constants.NS_DC_TERMS + "date");
            Property datePropertyS = modelStudies.createProperty(Constants.NS_DC_TERMS + "date");

            //Property titlePropertyP = modelPublications.createProperty(Constants.NS_DC_TERMS + "title");
            Property titlePropertyS = modelPublications.createProperty(Constants.NS_DC_TERMS + "title");

            //Property abstractPropertyP = modelPublications.createProperty(Constants.NS_DC_TERMS + "abstract");
            Property abstractPropertyS = modelStudies.createProperty(Constants.NS_DC_TERMS + "abstract");

            Resource discoStudy = model.getResource(this.NS_DDI + "Study");


            // I can also load FOAF + datacite + NEPOMUK, but it is not really interesting - would be merged in the ontology
            Resource foafDocumentP = modelPublications.createResource(Constants.NS_FOAF + "Document");

            //Resource foafPersonP = modelPublications.createResource(Constants.NS_FOAF + "Person");
            Resource foafPersonS = modelStudies.createResource(Constants.NS_FOAF + "Person");

            Resource foafAgentS = modelStudies.createResource(Constants.NS_FOAF + "Agent");

            //	Property foafNamePropertyP = modelPublications.createProperty(Constants.NS_FOAF + "name");
            Property foafNamePropertyS = modelStudies.createProperty(Constants.NS_FOAF + "name");

            Property doiPropertyS = modelStudies.createProperty(this.NS_DATACITE + "doi");

            //Property affiliationP = modelPublications.createProperty("http://www.semanticdesktop.org/ontologies/nco/#hasAffiliation");
            Property affiliationS = modelStudies.createProperty("http://www.semanticdesktop.org/ontologies/nco/#hasAffiliation");

            Property dctermsDateS = modelStudies.createProperty(Constants.NS_DC_TERMS + "date");
            Property dctermsDescriptionS = modelStudies.createProperty(Constants.NS_DC_TERMS + "description");


            Property dctermsRelation = modelLinks.createProperty("http://purl.org/dc/terms/relation");


            Resource agentResourceS = null;


            //Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = sheet.iterator();


            int rowCount = 1;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                boolean correctContent = true;

                //  if(rowCount==2)
                if (rowCount == 1) {
                    correctContent = check1(row);
                }

                // else if( correctContent && ( (rowCount>2 && rowCount<70)||(rowCount > 71 && rowCount<141)))
                else if (correctContent && ((rowCount > 1 && rowCount < 454))) {


                    //I add URIs not to leave it empty - but they are not real and actually, I don�t really access them

                    Resource publicationResource = modelPublications.createResource(URIref.encode("http://www.gesis.org/data/publication" + (rowCount - 1)));    //EMPTY URI!
                    publicationResource.addProperty(RDF.type, foafDocumentP);

                    Resource studyResource = modelStudies.createResource(URIref.encode("http://www.gesis.org/data/study" + (rowCount - 1)));    //EMPTY URI!
                    studyResource.addProperty(RDF.type, discoStudy);


                    //link
                    Resource publicationResourceL = modelLinks.createResource(URIref.encode("http://www.gesis.org/data/publication" + (rowCount - 1)));    //EMPTY URI!

                    Resource studyResourceL = modelLinks.createResource(URIref.encode("http://www.gesis.org/data/study" + (rowCount - 1)));    //EMPTY URI!

                    publicationResourceL.addProperty(dctermsRelation, studyResourceL);

                    //For each row, iterate through each columns
                    Iterator<Cell> cellIterator = row.cellIterator();

                    int columnCount = 1;
                    while (cellIterator.hasNext()) {

                        Cell cell = cellIterator.next();
                        String cellValue = "";

                        if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                            Double doubleValue = new Double(cell.getNumericCellValue());
                            int integerValue = doubleValue.intValue();
                            cellValue = Integer.toString(integerValue);
                        } else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            cellValue = cell.getStringCellValue();

                        }

                        // System.out.println("cellValue: "+cellValue);


                        if ((!cellValue.equals(" ")) && (!cellValue.equals("BB")) && (!cellValue.equals("[]"))) {

                            if (columnCount == 3) {
                                //id of pub we forget about it at the moment
                            } else if (columnCount == 4) {

                                studyResource.addProperty(doiPropertyS, cellValue);

                            } else if (columnCount == 5) {
                                studyResource.addProperty(titlePropertyS, cellValue);
                            } else if (columnCount == 6) {
                                //we ignore is the extracted reference
                            } else if (columnCount == 7 || columnCount == 8 || columnCount == 9 || columnCount == 10 || columnCount == 11) {
                                //if ((!cellValue.equals(" ")) && (!cellValue.equals(" BB")) )
                                //{
                                publicationResource.addProperty(snippetPropertyP, cellValue); //include @de?
                                //}
                            } else if (columnCount == 19 || columnCount == 21) //19-publicationYear 21-publicationDate
                            {
                                studyResource.addProperty(dctermsDateS, cellValue);
                            } else if (columnCount == 22 || columnCount == 23) //creator with Agent
                            {

                                String affiliation = "";
                                String patternText = null;
                                if (columnCount == 22) {
                                    patternText = "'(affiliation_name:) (.+?)',";
                                } else if (columnCount == 23) {
                                    patternText = "'(name:) (.+?)',";
                                }
                                //String patternText ="'(.+?)', (.+?)'affiliation_name: (.+?)', ('affiliantion_name_language: de)'";

                                Pattern pattern = Pattern.compile(patternText);
                                List<String> list = new ArrayList<String>();
                                Matcher matcher = pattern.matcher(cellValue);

                                while (matcher.find()) {

                                    affiliation = matcher.group(2);
                                    int size = list.size();
                                    if ((size == 0) || ((size != 0) && (!affiliation.equals(list.get(size - 1))))) // I get one in German and one in English but they are actually the same text
                                    {
                                        list.add(matcher.group(2));
                                    }
                                }

                                for (String nameInstitution : list) {
                                    UUID id = UUID.randomUUID();
                                    agentResourceS = modelStudies.createResource(URIref.encode("http://www.gesis.org/data/institution" + id));
                                    agentResourceS.addProperty(RDF.type, foafAgentS);
                                    agentResourceS.addProperty(foafNamePropertyS, nameInstitution);
                                    studyResource.addProperty(creatorPropertyS, agentResourceS);
                                    System.out.println("study" + (rowCount - 1) + " creator: " + nameInstitution);
                                }


                            }
			            	
			            	/*else if (columnCount==24) //deleted because it is too long!
			            	{
			            		studyResource.addProperty(dctermsDescriptionS, cellValue);
			            	}
			            	*/
                        }

                        columnCount = columnCount + 1;

                    }
                }
                //if the correctContent is false then the model will be empty
                else if (!correctContent) {
                    System.out.println("incorrect format of excel columns");
                }

                rowCount = rowCount + 1;
            }
			 
			 /*
			 OutputStream out = new FileOutputStream(this.rdfFilePubs);
			 
			 
			 RDFDataMgr.write(out, modelPublications, Lang.RDFXML) ;*/


            //Serialise the file (ask about the format)
            OutputStream outPubs = new FileOutputStream(this.rdfFilePubs);
            modelPublications.write(outPubs, "RDF/XML");

            OutputStream outStuds = new FileOutputStream(this.rdfFileStuds);
            modelStudies.write(outStuds, "RDF/XML");

            OutputStream outLinks = new FileOutputStream(this.rdfLinks);
            modelLinks.write(outLinks, "N-TRIPLE");


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }


    }


    private boolean check3(Row r) {
        boolean result = true;


        Iterator<Cell> cellIterator = r.cellIterator();
        int columnCount = 1;
        while (cellIterator.hasNext()) {
            Cell c = cellIterator.next();
            String cellValue = "";
            if (columnCount > 1) {


                if (c.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                    cellValue = Double.toString(c.getNumericCellValue());
                } else if (c.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                    cellValue = c.getStringCellValue();
                }


                if ((columnCount == 2 && !cellValue.equals("LitTitle")) || (columnCount == 3 && !cellValue.equals("LitAuthors")) || (columnCount == 4 && !cellValue.equals("LitYear")) || (columnCount == 5 && !cellValue.equals("LitAbstract")) || (columnCount == 6 && !cellValue.equals("StudyTitle")) || (columnCount == 7 && !cellValue.equals("StudyAuthors (Affiliation)")) || (columnCount == 8 && !cellValue.equals("StudyYear")) || (columnCount == 9 && !cellValue.equals("StudyDOI"))) {
                    result = false;
                }
            }


            columnCount = columnCount + 1;
        }


        return result;

    }

    private boolean check1(Row r) {
        boolean result = true;


        Iterator<Cell> cellIterator = r.cellIterator();
        int columnCount = 1;
        while (cellIterator.hasNext()) {
            Cell c = cellIterator.next();
            String cellValue = "";
            if (columnCount > 1) {


                if (c.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                    cellValue = Double.toString(c.getNumericCellValue());
                } else if (c.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                    cellValue = c.getStringCellValue();
                }


                if ((columnCount == 3 && !cellValue.equals("ID der Publikation (URN)")) || (columnCount == 4 && !cellValue.equals("ID des zugewie�enen Datensatzes (DOI)")) || (columnCount == 5 && !cellValue.equals("Titel des Datensatzes")) || (columnCount == 6 && !cellValue.equals("aus Publikation extrahierte Referenz des Datensatzes")) || (columnCount == 7 && !cellValue.equals("Textsnippet 1")) || (columnCount == 8 && !cellValue.equals("Textsnippet 2")) || (columnCount == 9 && !cellValue.equals("Textsnippet 3")) || (columnCount == 10 && !cellValue.equals("Textsnippet 4") || (columnCount == 11 && !cellValue.equals("Textsnippet 5")) || (columnCount == 12 && !cellValue.equals("DOI")) || (columnCount == 13 && !cellValue.equals("title_de")) || (columnCount == 14 && !cellValue.equals("title_en")) || (columnCount == 15 && !cellValue.equals("temporalCoverage")) || (columnCount == 16 && !cellValue.equals("temporalCoverage_monthYear")) || (columnCount == 17 && !cellValue.equals("temporalCoverage_year")) || (columnCount == 18 && !cellValue.equals("temporalCoverage_date")) || (columnCount == 19 && !cellValue.equals("publication_year")) || (columnCount == 20 && !cellValue.equals("publication_monthYear")) || (columnCount == 21 && !cellValue.equals("publication_date")) || (columnCount == 22 && !cellValue.equals("principalInvestigators_persons")) || (columnCount == 23 && !cellValue.equals("principalInvestigators_institutions")) || (columnCount == 24 && !cellValue.equals("description_de")) || (columnCount == 25 && !cellValue.equals("description_en")) || (columnCount == 26 && !cellValue.equals("error_type")))) {
                    result = false;
                }
            }


            columnCount = columnCount + 1;
        }


        return result;

    }
}

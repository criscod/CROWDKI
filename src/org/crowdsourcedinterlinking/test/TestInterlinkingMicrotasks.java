package org.crowdsourcedinterlinking.test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import org.crowdsourcedinterlinking.expblogic.ExperimentManager;
import org.crowdsourcedinterlinking.model.Dataset;
import org.crowdsourcedinterlinking.model.TypeOfDatasetLocation;
import org.crowdsourcedinterlinking.model.TypeOfMappingGoal;

import java.io.File;

public class TestInterlinkingMicrotasks {
	
	public static void main(String args[]) {
		
		
		test_person11person12();
		
		


	}
	public static void test_person11person12()
	{
		
			
			
			
			
			ExperimentManager expBuild = new ExperimentManager();

			Dataset d1 = new Dataset("person11", TypeOfDatasetLocation.FILEDUMP, "datasets/person11.rdf", null, null, null);
			Dataset d2 = new Dataset("person12", TypeOfDatasetLocation.FILEDUMP, "datasets/person12.rdf", null, null, null);
			
			//imported ExpLinks into the allPoolLinks file 
			File fInterlinkingExpandPool = new File("datasets/person11person12_allPoolLinks.nt");

			
			expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinkingExpandPool, TypeOfMappingGoal.VALIDATION, true);

		
	}
	
	public static void test_a626837()
	{
		
		Model modelTemp = ModelFactory.createDefaultModel();
		//read ref nt --> merge the two job refs?
		
		//Model refBasedOnSilk = ModelFactory.createDefaultModel();
		//refBasedOnSilk.read("file:///", "N-TRIPLES");
		
		
		//read refWorkedlinks.nt
		
		Model refWorkedLinks = ModelFactory.createDefaultModel();
		refWorkedLinks.read("file:///C:/Users/csarasua/Documents/WeST/SENSE4US/KO_meeting/refWorkedLinks.nt", "N-TRIPLES");
		
		
		//read total_silklinks.nt - count correct, incorrect and print
		
		Model total_silklinks = ModelFactory.createDefaultModel();
		total_silklinks.read("file:///C:/Users/csarasua/Documents/WeST/SENSE4US/KO_meeting/total_silklinks.nt", "N-TRIPLES");
		
		StmtIterator itTotal = total_silklinks.listStatements();
		int countTotalLinks = 0;
		int countCorrectLinks = 0;
		int countIncorrectLinks = 0;
		while (itTotal.hasNext())
		{
			Statement st = itTotal.nextStatement();
			if (refWorkedLinks.contains(st))
			{
				countCorrectLinks = countCorrectLinks + 1;
			}
			else
			{
				countIncorrectLinks = countIncorrectLinks + 1;
			}
			
			countTotalLinks = countTotalLinks + 1;
		}
		
		System.out.println("Silk classified "+countTotalLinks+ " candidate links as links. "+countCorrectLinks+" links were CORRECT, and "+countIncorrectLinks+" links were NOT CORRECT.");
		
		//read res_a626837.nt - count correct, count incorrect and print
		
		Model resagg = ModelFactory.createDefaultModel();
		resagg.read("file:///C:/Users/csarasua/Documents/WeST/SENSE4US/KO_meeting/resagg.nt", "N-TRIPLES");
				
		StmtIterator itAgg = resagg.listStatements();
		int countAggLinks = 0;
		int countAggCorrectLinks = 0;
		int countAggIncorrectLinks = 0;
		while (itAgg.hasNext())
		{
			Statement st = itAgg.nextStatement();
			if (refWorkedLinks.contains(st))
			{
				countAggCorrectLinks = countAggCorrectLinks + 1;
			}
			else
			{
				countAggIncorrectLinks = countAggIncorrectLinks + 1;
			}
			
			countAggLinks = countAggLinks + 1;
		}
		
		System.out.println("From the links identified by Silk, the crowd decided that "+countAggLinks+ " were links. "+countAggCorrectLinks+" links were CORRECTly identified by the crowd, and "+countAggIncorrectLinks+" links were NOT CORRECTly identified by the crowd.");
		
		
		
	}
	

public static void test_gesissense4us1()
{
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/nace_r2.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/AFA_isic3.rdf", null, null, null);
	//File fInterlinking = new File("datasets/nacer2-afaisic3_silkalllinks.nt"); //0.75
	
	File fInterlinking = new File("datasets/nacer2-afaisic3_alllinks06.nt");
	
	//File fInterlinking = new File("datasets/nacer2-afaisic3_silkalllinks_t08.nt");
	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.VALIDATION, true);
	
	
}


public static void test_gesissense4us2()
{
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/AFA_var.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/indic_bt.rdf", null, null, null);
	File fInterlinking = new File("datasets/afavar-indicbt_silkalllinks.nt");
	
	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.VALIDATION, true);
	
	
}
	
	
	public static void test29042014()
	{
		
		ExperimentManager expBuild = new ExperimentManager();

		Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/test29042014/nyt1.rdf", null, null, null);
		Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/test29042014/dbpedia4.rdf", null, null, null);
		File fInterlinking = new File("datasets/test29042014/imaginary.nt");

		expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.VALIDATION, true);
		
		
	}
	
	public static void testsense4usjune()
	{
		
		ExperimentManager expBuild = new ExperimentManager();

		Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/wbld/modelSourceS_datalinks_UTF8.rdf", null, null, null);
		Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/wbld/modelSourceT_datalinks_UTF8.rdf", null, null, null);
		File fInterlinking = new File("datasets/wbld/datalinks.nt");

		expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.VALIDATION, true);
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	public static void testFoafTestData() {
		
		
		
		
		ExperimentManager expBuild = new ExperimentManager();

		Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "testdata/source1.rdf", null, null, null);
		Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "testdata/source2.rdf", null, null, null);
		File fInterlinking = new File("testdata/interlinks.nt");

		expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.VALIDATION, true);

	}
public static void testFoafTestData2() {
		
		
		
		
		ExperimentManager expBuild = new ExperimentManager();

		Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "testdata/source1.rdf", null, null, null);
		Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "testdata/source2.rdf", null, null, null);
		File fInterlinking = new File("testdata/interlinks.nt");

		expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.IDENTIFICATIONA, true);

	}

//All the data sets are FILEDUMP, since I have subsets in files. 

public static void testEventMediaDbpediaEvents_5050() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/eventmedia1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/dbpedia1.rdf", null, null, null);
	File fInterlinking = new File("datasets/eventmediadbpediaevents_refinterlinking.nt");
	File fNoInterlinking = new File("datasets/eventmediadbpediaevents_refnointerlinking.nt");

	expBuild.runTrial5050LinksExperiment(d1, d2, fInterlinking, fNoInterlinking, TypeOfMappingGoal.VALIDATION, true);

}

public static void testEventMediaDbpediaPeople_5050() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/eventmedia2.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/dbpedia2.rdf", null, null, null);
	File fInterlinking = new File("datasets/eventmediadbpediapeople_refinterlinking.nt");
	File fNoInterlinking = new File("datasets/eventmediadbpediapeople_refnointerlinking.nt");

	expBuild.runTrial5050LinksExperiment(d1, d2, fInterlinking, fNoInterlinking, TypeOfMappingGoal.VALIDATION, true);

}

public static void testEventMediaDbpediaLocations_5050() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/eventmedia3.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/dbpedia3.rdf", null, null, null);
	File fInterlinking = new File("datasets/eventmediadbpedialocations_refinterlinking.nt");
	File fNoInterlinking = new File("datasets/eventmediadbpedialocations_refnointerlinking.nt");

	expBuild.runTrial5050LinksExperiment(d1, d2, fInterlinking, fNoInterlinking, TypeOfMappingGoal.VALIDATION, true);

}


public static void testNYTDbpediaPeople_5050() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/nyt1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/dbpedia4.rdf", null, null, null);
	File fInterlinking = new File("datasets/nytdbpediapeople_refinterlinking.nt");
	File fNoInterlinking = new File("datasets/nytdbpediapeople_refnointerlinking.nt");

	expBuild.runTrial5050LinksExperiment(d1, d2, fInterlinking, fNoInterlinking, TypeOfMappingGoal.VALIDATION, true);

}

public static void testNYTDbpediaOrganizations_5050() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/nyt2.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/dbpedia5.rdf", null, null, null);
	File fInterlinking = new File("datasets/nytdbpediaorganizations_refinterlinking.nt");
	File fNoInterlinking = new File("datasets/nytdbpediaorganizations_refnointerlinking.nt");

	expBuild.runTrial5050LinksExperiment(d1, d2, fInterlinking, fNoInterlinking, TypeOfMappingGoal.VALIDATION, true);

}

public static void testMyDBworkersKnowledge__5050() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/dataworkers_d1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/dataworkers_d2.rdf", null, null, null);
	File fInterlinking = new File("datasets/workers_refinterlinking.nt");
	File fNoInterlinking = new File("datasets/workers_refnointerlinking.nt");

	expBuild.runTrial5050LinksExperiment(d1, d2, fInterlinking, fNoInterlinking, TypeOfMappingGoal.VALIDATION, true);

}
public static void testNYTDbpediaLocations_5050() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/nyt3.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/dbpedia6.rdf", null, null, null);
	File fInterlinking = new File("datasets/nytdbpedialocations_refinterlinking.nt");
	File fNoInterlinking = new File("datasets/nytdbpedialocations_refnointerlinking.nt");

	expBuild.runTrial5050LinksExperiment(d1, d2, fInterlinking, fNoInterlinking, TypeOfMappingGoal.VALIDATION, true);

}





public static void testEventMediaDbpedia_alg1() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/eventmedia1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/dbpedia1.rdf", null, null, null);
	
	File fInterlinking = new File("datasets/silk_links.nt");

	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.VALIDATION, true);

}



public static void testNYTDBpediaPeople_Silk(){
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/nyt1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/dbpedia4.rdf", null, null, null);
	File fInterlinking = new File("datasets/alllinks_people.nt");

	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.VALIDATION, true);

}

public static void testNYTDBpediaOrganizations_Silk(){
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/nyt2.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/dbpedia5.rdf", null, null, null);
	File fInterlinking = new File("datasets/alllinks_organizations.nt");

	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.VALIDATION, true);

}

/*

//-----------------------------------------------

public static void testEventMediaDbpedia_alg2() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "testdata/source1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "testdata/source2.rdf", null, null, null);
	File fInterlinking = new File("testdata/interlinks.nt");

	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.IDENTIFICATIONA, true);

}





public static void NytDBpediaAlg1() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "testdata/source1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "testdata/source2.rdf", null, null, null);
	File fInterlinking = new File("testdata/interlinks.nt");

	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.IDENTIFICATIONA, true);

}

public static void NytDBpediaAlg2() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "testdata/source1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "testdata/source2.rdf", null, null, null);
	File fInterlinking = new File("testdata/interlinks.nt");

	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.IDENTIFICATIONA, true);

}
*/
public static void testNYTGeonamesLocations5050() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "testdata/source1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "testdata/source2.rdf", null, null, null);
	File fInterlinking = new File("testdata/interlinks.nt");

	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.IDENTIFICATIONA, true);

}
/*public static void testNytGeonamesAlg1() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "testdata/source1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "testdata/source2.rdf", null, null, null);
	File fInterlinking = new File("testdata/interlinks.nt");

	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.IDENTIFICATIONA, true);

}

public static void testNytGeonamesAlg2() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "testdata/source1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "testdata/source2.rdf", null, null, null);
	File fInterlinking = new File("testdata/interlinks.nt");

	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.IDENTIFICATIONA, true);

}*/
public static void testIIMB5050() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "testdata/source1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "testdata/source2.rdf", null, null, null);
	File fInterlinking = new File("testdata/interlinks.nt");

	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.IDENTIFICATIONA, true);

}
/*public static void testIIMBAlg1() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "testdata/source1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "testdata/source2.rdf", null, null, null);
	File fInterlinking = new File("testdata/interlinks.nt");

	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.IDENTIFICATIONA, true);

}

public static void testIIMBAlg2() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "testdata/source1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "testdata/source2.rdf", null, null, null);
	File fInterlinking = new File("testdata/interlinks.nt");

	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.IDENTIFICATIONA, true);

}*/
public static void testFBDbpedia5050() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "testdata/source1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "testdata/source2.rdf", null, null, null);
	File fInterlinking = new File("testdata/interlinks.nt");

	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.IDENTIFICATIONA, true);

}

/*public static void testFBDbpediaAlg1() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "testdata/source1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "testdata/source2.rdf", null, null, null);
	File fInterlinking = new File("testdata/interlinks.nt");

	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.IDENTIFICATIONA, true);

}

public static void testFBDbpediaAlg2() {
	
	
	
	
	ExperimentManager expBuild = new ExperimentManager();

	Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "testdata/source1.rdf", null, null, null);
	Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "testdata/source2.rdf", null, null, null);
	File fInterlinking = new File("testdata/interlinks.nt");

	expBuild.runTrialDirectLinksExperiment(d1, d2, fInterlinking, TypeOfMappingGoal.IDENTIFICATIONA, true);

}*/

// Experiments for testing workers profile R3 and R4




}

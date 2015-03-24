package org.crowdsourcedinterlinking.mgeneration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

import org.crowdsourcedinterlinking.model.Alignment;
import org.crowdsourcedinterlinking.model.AlignmentParser;
import org.crowdsourcedinterlinking.model.Dataset;
import org.crowdsourcedinterlinking.model.Interlink;
import org.crowdsourcedinterlinking.model.Interlinking;
import org.crowdsourcedinterlinking.model.InterlinkingParser;
import org.crowdsourcedinterlinking.model.Mapping;
import org.crowdsourcedinterlinking.model.Ontology;
import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.Time;

import com.google.common.io.Files;

public class HCHILinksGeneratorImpl  extends LinksGeneratorImpl {
	
	private File fReferenceInterlinkig;
	private File fReferenceNoInterlinking;
	
	public HCHILinksGeneratorImpl(Dataset d1, Dataset d2, File referenceInterlinking, File referenceNoInterlinking)
	{
		//This generator is used both for 50%links correct and 50%links incorrect, and for the sure ones of the algorithm and the unsure ones - it is just a matter of sending one file or  the other
		
		this.setDataset1(d1);
		this.setDataset2(d2);
		
		this.fReferenceInterlinkig=referenceInterlinking; 
		this.fReferenceNoInterlinking=referenceNoInterlinking; 
	}
	
	
	
	
	public Interlinking generateLinks()
	{
		Interlinking result=null;
		Interlinking noInterlinking=null;
		
		try
		{
		this.registerDatasetsInTrackFile();

		
		
			InterlinkingParser parser = new InterlinkingParser(this.fReferenceInterlinkig);
			parser.parseInterlinking(this.getDataset1(), this.getDataset2());
			result = parser.getInterlinking();
		System.out.println("size of interlinks "+result.getSetOfInterLinks().size());
			
			InterlinkingParser parser2 = new InterlinkingParser(this.fReferenceNoInterlinking);
			parser2.parseInterlinking(this.getDataset1(), this.getDataset2());
			noInterlinking = parser2.getInterlinking();
			Set<Interlink> setNoInterlinks = noInterlinking.getSetOfInterLinks();
			System.out.println("size of no interlinks "+setNoInterlinks.size());

			result.getSetOfInterLinks().addAll(setNoInterlinks);
			System.out.println("size of interlinks (after adding in theroy the set of not interlinks "+result.getSetOfInterLinks().size());
			
			
			//for printing
			
			File resultsFile = new File(
					"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/testLinksGeneration.txt");
			Files.write("ALGORITHM " + Time.currentTime(), resultsFile,
					Charset.defaultCharset());
			String ls = System.getProperty("line.separator");
			Files.append(ls, resultsFile, Charset.defaultCharset());

			
			
			for (Interlink intl : result.getSetOfInterLinks()) {

				Files.append(
						"Link: elem1: " + intl.getElementA().getURI()
								+ " elem2: " + intl.getElementB().getURI()
								+ " rel: " + intl.getRelation(), resultsFile,
						Charset.defaultCharset());
				Files.append(ls, resultsFile, Charset.defaultCharset());
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result; 
	}
	
	
	
	/*
	 * 
	 * private File algorithmAlignment;

	public AlgorithmPairsGeneratorImpl(Ontology o1, Ontology o2,
			File algorithmAlignment) {
		try {
			this.setOntology1(o1);
			this.setOntology2(o2);

			this.algorithmAlignment = algorithmAlignment;
			this.loadAlignmentElements();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Alignment generatePairs() {
		Alignment result = null;

		try {
			this.registerOntologiesInTrackFile();

			AlignmentParser p = new AlignmentParser(this.algorithmAlignment, oMap);
			p.parseAlignment(this.getOntology1(), this.getOntology2());
			// they will contain relations, but when generating the type of
			// Microtask, the relation should be hidden
			result = p.getAlignment();

			File resultsFile = new File(
					"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/testPairsGeneration.txt");
			Files.write("ALGORITHM " + Time.currentTime(), resultsFile,
					Charset.defaultCharset());
			String ls = System.getProperty("line.separator");
			Files.append(ls, resultsFile, Charset.defaultCharset());

			for (Mapping m : result.getSetOfMappings()) {

				Files.append(
						"Mapping: elem1: " + m.getElementA().getURI()
								+ " elem2: " + m.getElementB().getURI()
								+ " rel: " + m.getRelation(), resultsFile,
						Charset.defaultCharset());
				Files.append(ls, resultsFile, Charset.defaultCharset());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	 */

}

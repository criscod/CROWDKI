package org.crowdsourcedinterlinking.mgeneration;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import org.crowdsourcedinterlinking.model.*;
import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.Constants;
import org.crowdsourcedinterlinking.util.ParseLinksInOrder;
import org.crowdsourcedinterlinking.util.TypeOfMicrotask;

import java.io.File;
import java.util.*;

public class CwdfInterlinkingMicrotaskGeneratorImpl implements InterlinkingMicrotaskGenerator {

	// If a page is a HIT then I have to create a Job with 50 units
	// That is, create as many jobs as totalMappings-Pairs*6(units per HIT)/50
	// for each TEST

	private Interlinking candidateInterlinking;
	

	private TypeOfMappingGoal mappingGoal;
	private boolean context;

	private int numberOfMinimumGold = 1;
	private int numberOfJobs;
	// because it is necessary to know the real units in each job -- there are
	// jobs with less units
	private int numberOfRealUnitsOfJob;
	private int numberOfAssignmentsInJob;

	private int lastAvailableIndex = 0;
	private Set<Interlink> setOfLinksFromRDFFile = new HashSet<Interlink>();

	private Set<Interlink> setOfExperiseLinks= new HashSet<Interlink>();
	private Set<Interlink> goldenUnitsPos = new HashSet<Interlink>();
	private Set<Interlink> goldenUnitsNeg = new HashSet<Interlink>();

	private static int counter = Integer.parseInt(ConfigurationManager
			.getInstance().getJobTitleCounter());

	private int jobNumber;

	// so that addGoldUnits also can access it

	public CwdfInterlinkingMicrotaskGeneratorImpl(TypeOfMappingGoal mappingGoal,
			boolean context) {

		this.mappingGoal = mappingGoal; //Validation or Identification
		this.context = context;

	}

	/*
	 * private void adjustUnitsPerAssignmentAndCalculateMinimumGold(int
	 * numberOfNormalUnits) {
	 * 
	 * //this.realUnitsPerAssignment has value the default from the file:
	 * this.realUnitsPerAssignment
	 * =ConfigurationManager.getInstance().getUnitsPerAssignment();
	 * 
	 * try { this.numberOfPagesInTheJob =
	 * (int)(numberOfNormalUnits/(this.realUnitsPerAssignment-1));
	 * this.numberOfPagesInTheJob = this.numberOfPagesInTheJob+1;
	 * 
	 * 
	 * 
	 * //this.numberOfMinimumGold = (int)(this.numberOfPagesInTheJob/4); int
	 * modMultipleOf4 = this.numberOfPagesInTheJob%4;
	 * 
	 * //if (this.numberOfMinimumGold<1) //{ //adjust until at least one gold
	 * can be achieved while(modMultipleOf4 != 0) { this.realUnitsPerAssignment
	 * = this.realUnitsPerAssignment-1; this.numberOfPagesInTheJob =
	 * (int)(numberOfNormalUnits/(this.realUnitsPerAssignment-1));
	 * this.numberOfPagesInTheJob = this.numberOfPagesInTheJob+1; modMultipleOf4
	 * = this.numberOfPagesInTheJob%4; } //in principle it is ok - is it
	 * probable that it happens? if (this.numberOfPagesInTheJob>50) { throw new
	 * Exception("numberOfPagesInTheJob is now more than 50"); } // it is
	 * multiple of 4
	 * 
	 * this.numberOfMinimumGold = this.numberOfPagesInTheJob/4; //} //else //{
	 * //if(modMultipleOf4!=0) //{ // this.numberOfMinimumGold =
	 * this.numberOfMinimumGold+1; //} //otherwise it is already calculated //}
	 * 
	 * 
	 * } catch(Exception e) { e.printStackTrace(); }
	 * 
	 * }
	 */

	public Set<Microtask> createMicrotasks(Interlinking intl) {
		Set<Microtask> result = new HashSet<Microtask>();
		this.candidateInterlinking = intl;

		try {
			
			
			
			/*
			 * The Alignment defines the pairs that have to be used in the
			 * microtasks. If the typeOfMappingGoal is validation, then the
			 * interesting information of the mappings in the alignment is
			 * Element1, Element2, Relation If the typeOfMappingGoal is
			 * identification, then the interesting information of the mappings
			 * in the alignment is Element1, Element 2 (Relation will be UNKNOWN
			 * or SIMILAR/GENERAL/SPECIFIC depending on the method for
			 * generating the pairs that has been applied)
			 * 
			 * Depending on the typeOfMappingGoal and the context, the specific
			 * jobs to be created will be different One experiment initiates
			 * this, so all the microtasks in this method will be created in the
			 * same wa Several experiments, each of them with different types of
			 * specific jobs
			 * 
			 * 
			 * MappingValidationJobMicrotaskImpl: TypeOfMappingGoal VALIDATION
			 * Context FALSE MappingValidationWithFullContextJobMicrotaskImpl:
			 * TypeOfMappingGoal VALIDATION Context TRUE
			 * 
			 * MappingIdentificationJobMicrotaskImpl: TypeOfMappingGoal
			 * IDENTIFICATION Context FALSE
			 * MappingIdentificationWithFullContextJobMicrotaskImpl:
			 * TypeOFMappingGoal IDENTIFICATION Context TRUE
			 */

			// division of Units/Jobs for defining Batches

			// associate the Ontology when creating the units
			List<Interlink> interlinks=null; 
			if(ConfigurationManager.getInstance().getLinksInOrder().equals(ParseLinksInOrder.Lists))
			{ interlinks= new ArrayList<Interlink>(
					this.candidateInterlinking.getOrderedListOfInterlinks());}
			else
			{
				interlinks = new ArrayList<Interlink>(
						this.candidateInterlinking.getSetOfInterLinks());
			}
			

			int sizeOfSetOfInterlinks = interlinks.size();
			// (int) Math.ceil(sizeOfSetOfMappings /..)

			// ConfigurationManager.getInstance().getBatch()*ConfigurationManager.getInstance().getUnitsPerAssignment()

			// this method will calcultae & store this.numberOfJobs and
			// this.numberOfMinimuGold in the object
			// calculateMinimumGold(sizeOfSetOfMappings,
			// ConfigurationManager.getInstance().getUnitsPerAssignment());

			// this.numberOfJobs = (int)(sizeOfSetOfMappings /
			// ConfigurationManager.getInstance().getBatch()*(ConfigurationManager.getInstance().getUnitsPerAssignment()-1));
			// this.numberOfJobs = this.numberOfJobs +1; //lo hab�a quitado ????
			int realUnitsPerAssignment = ConfigurationManager.getInstance()
					.getUnitsPerAssignment();
			int normalUnitsPerAssignment = realUnitsPerAssignment
					- ConfigurationManager.getInstance().getGoldPerAssignment();

			// it should be -1 ?
			// this.numberOfJobs = (int) (sizeOfSetOfMappings /
			// (ConfigurationManager.getInstance().getBatch()*normalUnitsPerAssignment));
			this.numberOfJobs = (int) (sizeOfSetOfInterlinks / (ConfigurationManager
					.getInstance().getBatch() * normalUnitsPerAssignment));
			numberOfJobs = numberOfJobs + 1;

			// TEST?-------

			System.out
					.println("number of Jobs to create: " + this.numberOfJobs);
			System.out.println("batch: "
					+ ConfigurationManager.getInstance().getBatch()
					+ " sizeInterlinks: " + sizeOfSetOfInterlinks);

			//TODO: refactor and update now changed due to requisite of links in order - split
			//Set<UnitDataEntryImpl> setOfUnits = new HashSet<UnitDataEntryImpl>();
			List<UnitDataEntryImpl> listOfUnits = new ArrayList<UnitDataEntryImpl>();
			Set<UnitDataEntryImpl> goldenUnits = new HashSet<UnitDataEntryImpl>();



			// int unitsPerJob =
			// (int)(ConfigurationManager.getInstance().getBatch()*(this.realUnitsPerAssignment-1));

			int unitsPerJob = (int) (ConfigurationManager.getInstance()
					.getBatch() * (ConfigurationManager.getInstance()
					.getUnitsPerAssignment() - 1));

			// At least 2 jobs ALWAYS --- this should dissapear
			/*
			 * if (numberOfJobs==1) { numberOfJobs=2; unitsPerJob =
			 * (int)sizeOfSetOfMappings/2; }
			 */

			// if(numberOfAssignmentsInJob<(ConfigurationManager.getInstance().getCwdfRestrictionGolden()*ConfigurationManager.getInstance().get)
			// {
			// the unitsPerAssignment must be decreased until the restriction in
			// number of golden is reached (since we have 1 golden unit)
			// }

			// int job = 1;
			int minIndex = 0;
			int maxIndex = 0;
			// while (job <=numberOfJobs){
			for (int job = 1; maxIndex < interlinks.size() - 1; job++) {

				// create jobs with related units inside based on the
				// setOfMappings + gold if needed form goldenMappings
				/*
				 * correct one
				 * 
				 * //50*6==300 minIndex=(unitsPerJob*job)-unitsPerJob; maxIndex
				 * = (unitsPerJob*job)-1; //if we are in the last job to build,
				 * then the maxIndex is the last possible index, which might not
				 * be (50*job)-1 if (job==numberOfJobs) { maxIndex =
				 * mappings.size()-1;
				 * //adjustUnitsPerAssignmentAndCalculateMinimumGold
				 * (mappings.size()); }
				 */

				if ((job % 2 == 1 && maxIndex + 2 * unitsPerJob < interlinks
						.size())
						|| (job % 2 == 0 && maxIndex + unitsPerJob < interlinks
								.size())) {
					if (job % 2 == 1) { // odd
						minIndex = unitsPerJob * 2 * (int) (job / 2 + 1)
								- unitsPerJob * 2;
						maxIndex = unitsPerJob * 2 * (int) (job / 2 + 1)
								- unitsPerJob - 1;
					} else { // even
						minIndex = unitsPerJob * 2 * (int) ((job) / 2)
								- unitsPerJob;
						maxIndex = unitsPerJob * 2 * (int) ((job) / 2) - 1;
					}
				} else {
					if (job % 2 == 1) {
						minIndex = unitsPerJob * 2 * (int) (job / 2 + 1)
								- unitsPerJob * 2;
						maxIndex = interlinks.size()
								- (interlinks.size() % (2 * unitsPerJob)) / 2 - 1;
					} else { // even
						minIndex = interlinks.size()
								- (interlinks.size() % (2 * unitsPerJob)) / 2;
						maxIndex = interlinks.size() - 1;
					}
				}

				/*
				 * int job = 1; int maxIndex ; while (job <=numberOfJobs) {
				 * //create jobs with related units inside based on the
				 * setOfMappings + gold if needed form goldenMappings
				 * //50*6==300 (unitsPerJob)
				 * 
				 * //in the beginning unitsPerAssignment is the one by default
				 * this
				 * .realUnitsPerAssignment=ConfigurationManager.getInstance()
				 * .getUnitsPerAssignment();
				 * 
				 * 
				 * //if we are in the last job to build, then the maxIndex is
				 * the last possible index, which might not be (50*job)-1
				 * 
				 * adjustUnitsPerAssignmentAndCalculateMinimumGold(mappings.size(
				 * )); int unitsPerJob =
				 * (int)(ConfigurationManager.getInstance()
				 * .getBatch()*(this.realUnitsPerAssignment-1));
				 * 
				 * if (job==numberOfJobs) { maxIndex = mappings.size()-1;
				 * 
				 * 
				 * } else { maxIndex = (unitsPerJob*job)-1; }
				 * 
				 * //calcular num minimum gold
				 */

				//setOfUnits = new HashSet<UnitDataEntryImpl>();
				goldenUnits = new HashSet<UnitDataEntryImpl>();
				
				UnitDataEntryImpl sampleUnitData = null; 

				// we create and add the golden to the set of units to be
				// included in the job. By default validation HIT without
				// context
				/*
				 * for (Mapping gMap: goldenMappings) {
				 * 
				 * //create the appropriate units
				 * MappingValidationUnitDataEntryImpl gMapVal = new
				 * MappingValidationUnitDataEntryImpl
				 * (gMap.getElementA().getURI(), gMap.getElementB().getURI(),
				 * gMap.getRelation(),this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2()); // here we don't
				 * need to loadInfo - not even the label or comments which is
				 * what we show on HITs without context, because I invented the
				 * mapping element of the golden HITS - gMapVal.loadInfo();
				 * gMapVal.setDifficulty(1); setOfUnits.add(gMapVal); }
				 */

				/*
				 * The real number of units in the job is calculated by
				 * counting:
				 * 
				 * lastIndex - firstIndex +1
				 * 
				 * in the code lastIndex-->maxIndex
				 * firstIndex-->(unitsPerJob*job)-unitsPerJob
				 * 
				 * For example: 510 units ==> job1: 300 units (0..299) ; job2:
				 * 210 units (300 .. 509) this.numberOfRealUnitsOfJob: 299 -
				 * ((300*1)-300)+1 509 - ((300*2)-300) +1 299-0+1 509 - 300 +1
				 * 300 210
				 */
				// this.numberOfRealUnitsOfJob = maxIndex
				// -((unitsPerJob*job)-unitsPerJob) +1;
				this.numberOfRealUnitsOfJob = maxIndex - minIndex + 1;
				realUnitsPerAssignment = ConfigurationManager.getInstance()
						.getUnitsPerAssignment();
				normalUnitsPerAssignment = realUnitsPerAssignment
						- ConfigurationManager.getInstance()
								.getGoldPerAssignment();

				// calculate number of golden nunits and adjust number of golden
				// if necessary (<2)
				numberOfAssignmentsInJob = (int) numberOfRealUnitsOfJob
						/ normalUnitsPerAssignment;
				if (numberOfRealUnitsOfJob % normalUnitsPerAssignment != 0) {
					numberOfAssignmentsInJob = numberOfAssignmentsInJob + 1;
				}
				this.numberOfMinimumGold = numberOfAssignmentsInJob
						* ConfigurationManager.getInstance()
								.getGoldPerAssignment();

				while (this.numberOfMinimumGold < ConfigurationManager
						.getInstance().getCwdfRestrictionGolden()) {
					// adjust numberOfAssignmentsInJob
					realUnitsPerAssignment = realUnitsPerAssignment - 1;
					normalUnitsPerAssignment = realUnitsPerAssignment
							- ConfigurationManager.getInstance()
									.getGoldPerAssignment();

					numberOfAssignmentsInJob = (int) numberOfRealUnitsOfJob
							/ normalUnitsPerAssignment;
					if (numberOfRealUnitsOfJob % normalUnitsPerAssignment != 0) {
						numberOfAssignmentsInJob = numberOfAssignmentsInJob + 1;
					}
					this.numberOfMinimumGold = numberOfAssignmentsInJob
							* ConfigurationManager.getInstance()
									.getGoldPerAssignment();

					// extract in method
				}

				if (context
						&& (this.mappingGoal
								.equals(TypeOfMappingGoal.VALIDATION))) {

					// goldenUnits = addGoldStandardMappings(1,
					// numberOfRealUnitsOfJob, job);
					
					
 					//lastAvailableIndex = lastAvailableIndex+ (this.numberOfMinimumGold*2);
 					//lastAvailableIndex = lastAvailableIndex+ this.numberOfMinimumGold;
					for (int i = minIndex; i <= maxIndex; i++) {

						Interlink interlink = interlinks.get(i);

						// create the appropriate units
						InterlinkValidationWithFullContextUnitDataEntryImpl intlValCont = new InterlinkValidationWithFullContextUnitDataEntryImpl(
								interlink.getElementA().getURI(), interlink
										.getElementB().getURI(),
								interlink.getRelation().getURI(),
								this.candidateInterlinking.getDataset1(),
								this.candidateInterlinking.getDataset2());
						intlValCont.loadInfo();
						intlValCont.setDifficulty(2);
						listOfUnits.add(intlValCont);
						
						if(sampleUnitData==null)
						{
							sampleUnitData =intlValCont;
						}

					}

				} else {
					if (!context
							&& (this.mappingGoal
									.equals(TypeOfMappingGoal.VALIDATION))) {

						// goldenUnits = addGoldStandardMappings(2,
						// numberOfRealUnitsOfJob, job);
						
						/*lastAvailableIndex = lastAvailableIndex
								+ (this.numberOfMinimumGold*2);*/
						//lastAvailableIndex = lastAvailableIndex+ this.numberOfMinimumGold;
						for (int i = minIndex; i <= maxIndex; i++) {

							Interlink interlink = interlinks.get(i);
							// create the appropriate units
							InterlinkValidationUnitDataEntryImpl intlVal = new InterlinkValidationUnitDataEntryImpl(
									interlink.getElementA().getURI(), interlink
											.getElementB().getURI(),
											interlink.getRelation().getURI(),
									this.candidateInterlinking.getDataset1(),
									this.candidateInterlinking.getDataset2());
							intlVal.setDifficulty(2);
							intlVal.loadInfo();
							listOfUnits.add(intlVal);
							
							if(sampleUnitData==null)
							{
								sampleUnitData =intlVal;
							}

						}
					} else {
						if (context
								&& ((this.mappingGoal
										.equals(TypeOfMappingGoal.IDENTIFICATIONA)) || (this.mappingGoal
										.equals(TypeOfMappingGoal.IDENTIFICATIONB)))) {
							// add two golden units

							// numberOfRealUnitsOfJob = 25;
							// goldenUnits = addGoldStandardMappings(3,
							// numberOfRealUnitsOfJob, job);
							
						/*lastAvailableIndex = lastAvailableIndex+ (this.numberOfMinimumGold*2); */
						//	lastAvailableIndex = lastAvailableIndex+ this.numberOfMinimumGold;

							for (int i = minIndex; i <= maxIndex; i++) {

								Interlink interlink = interlinks.get(i);
								// create the appropriate units
								InterlinkIdentificationWithFullContextUnitDataEntryImpl intlIdCont = new InterlinkIdentificationWithFullContextUnitDataEntryImpl(
										interlink.getElementA().getURI(), interlink
												.getElementB().getURI(),
										this.candidateInterlinking.getDataset1(),
										this.candidateInterlinking.getDataset2());
								intlIdCont .setDifficulty(2);
								intlIdCont .loadInfo();
								listOfUnits.add(intlIdCont );
								
								if(sampleUnitData==null)
								{
									sampleUnitData =intlIdCont;
								}

							}

							/*
							 * //only for the small pilot------------ int
							 * first=0; int last=0; if (job==1) { first=0;
							 * last=24; } else { if (job==2) { first=25;
							 * last=49; } } for (int i=first; i<=last; i++) {
							 * 
							 * Mapping mapping = mappings.get(i); //create the
							 * appropriate units
							 * MappingIdentificationWithFullContextUnitDataEntryImpl
							 * mapIdCont = new
							 * MappingIdentificationWithFullContextUnitDataEntryImpl
							 * (mapping.getElementA().getURI(),
							 * mapping.getElementB().getURI(),
							 * this.candidateInterlinking.getOntology1(),
							 * this.candidateInterlinking.getOntology2());
							 * mapIdCont.setDifficulty(2); mapIdCont.loadInfo();
							 * setOfUnits.add(mapIdCont);
							 * 
							 * 
							 * 
							 * } //end of small pilot-------------------
							 */
						} else {
							if (!context
									&& ((this.mappingGoal
											.equals(TypeOfMappingGoal.IDENTIFICATIONA)) || (this.mappingGoal
											.equals(TypeOfMappingGoal.IDENTIFICATIONB)))) {

								// goldenUnits = addGoldStandardMappings(4,
								// numberOfRealUnitsOfJob, job);
								
								/*lastAvailableIndex = lastAvailableIndex
										+ (this.numberOfMinimumGold*2);*/
							//	lastAvailableIndex = lastAvailableIndex+ this.numberOfMinimumGold;

								for (int i = minIndex; i <= maxIndex; i++) {

									Interlink interlink = interlinks.get(i);
									// create the appropriate units
									InterlinkIdentificationUnitDataEntryImpl intlId = new InterlinkIdentificationUnitDataEntryImpl(
											interlink.getElementA().getURI(),
											interlink.getElementB().getURI(),
											this.candidateInterlinking.getDataset1(),
											this.candidateInterlinking.getDataset2());
									intlId.setDifficulty(2);
									intlId.loadInfo();
									listOfUnits.add(intlId);
									
									if(sampleUnitData==null)
									{
										sampleUnitData =intlId;
									}

								}
							}

						}
						
					
						}
						
					}
				Dataset d1 = new Dataset("person11", TypeOfDatasetLocation.FILEDUMP, "datasets/person11.rdf", null, null, null);
				Dataset d2 = new Dataset("person12", TypeOfDatasetLocation.FILEDUMP, "datasets/person12.rdf", null, null, null);
				
				File fInterlinkingPos = new File("datasets/person11person12_posgoldLinks.nt");
				File fInterlinkingNeg=new File("datasets/person11person12_neggoldLinks.nt");
				//File fInterlinkingExp=new File("datasets/person11person12_expertiseLinks.nt");


				goldenUnits = addGoldStandardMappingsFromRDFFile(job, sampleUnitData, d1, d2, fInterlinkingPos, fInterlinkingNeg);
			//	this.setOfLinksFromRDFFile= new HashSet<Interlink>();
				//addExpertiseLinksFromRDFFile(job,sampleUnitData,d1,d2,fInterlinkingExp);
				
				Iterator<UnitDataEntryImpl> it = goldenUnits.iterator();
				while (it.hasNext())
				{
					UnitDataEntryImpl ud = it.next();
					if (ud instanceof InterlinkValidationWithFullContextUnitDataEntryImpl)
					{
						InterlinkValidationWithFullContextUnitDataEntryImpl iv = (InterlinkValidationWithFullContextUnitDataEntryImpl)ud;
						
					}
					else if (ud instanceof InterlinkIdentificationWithFullContextUnitDataEntryImpl)
					{
						InterlinkIdentificationWithFullContextUnitDataEntryImpl ii = (InterlinkIdentificationWithFullContextUnitDataEntryImpl)ud;
					}
				}
				// the value for this.numberOfMinimumGold should have been set
				// within the specific cases, where the addGOldStandardMappings
				// has been invoked -- that method is the one filling in the
				// info of the minum number of golden units
				// ConfigurationManager.getInstance().setMaxJudgmentsPerWorker(this.numberOfMinimumGold*ConfigurationManager.getInstance().getUnitsPerAssignment());
				ConfigurationManager.getInstance().setMaxJudgmentsPerWorker(
						this.numberOfMinimumGold * realUnitsPerAssignment);

				// ConfigurationManager.getInstance().setMaxJudgmentsPerWorker(setOfUnits.size()+goldenUnits.size());

				// System.out.println("NUMBEROFJOBS: "+this.numberOfJobs+
				// "REALUNITSPERASSIGNMENT: "+this.realUnitsPerAssignment+
				// "PAGES/HITS: "+this.numberOfPagesInTheJob+"MINIMUM GOLD: "+this.numberOfMinimumGold);

				/*
				 * The jobs will be created with not turned / turned UI (CML)
				 * depending on the number of the job. Always jobs that are
				 * multiple of 2 will be turned and the ones that are not
				 * multiple of 2 will be not turned
				 * 
				 * So if two jobs must be created: the first one will be not
				 * turned and the second turned If three jobs must be created:
				 * the first one will be not turned and the second turned
				 */
				String instructions, titleBasis = null;
				

				if (context
						&& (this.mappingGoal
								.equals(TypeOfMappingGoal.VALIDATION))) {
					
					instructions = ConfigurationManager.getInstance().getJobInstructions(TypeOfMicrotask.Validation);
							titleBasis = ConfigurationManager.getInstance().getJobTitleBasis(TypeOfMicrotask.Validation);

					InterlinkValidationWithFullContextJobMicrotaskImpl validationJob = null;

					if (job % 2 != 0) {
						validationJob = new InterlinkValidationWithFullContextJobMicrotaskImpl(
								false);
					} else if (job % 2 == 0) {
						validationJob = new InterlinkValidationWithFullContextJobMicrotaskImpl(
								true);

					}
					jobNumber = ++counter;
					// validationJob.setTitle("Validate the relation between two elements - Part "+jobNumber+"B");
					
					
					
					validationJob
							.setTitle(titleBasis
									+ jobNumber);
					validationJob
							.setInstructions(instructions);
					validationJob.setLanguage("en");
					// it is a 2 possible answers microtask
					validationJob.setJudgmentsPerUnit(ConfigurationManager
							.getInstance().getjudgmentsPerUnitTwoOptions());
					validationJob.setMaxJudgmentsPerWorker(ConfigurationManager
							.getInstance().getMaxJudgmentsPerWorker());
					validationJob.setPagesPerAssignment(ConfigurationManager
							.getInstance().getPagerPerAssignment());
					// validationJob.setUnitsPerAssignment(ConfigurationManager.getInstance().getUnitsPerAssignment());
					validationJob.setUnitsPerAssignment(realUnitsPerAssignment);
					validationJob.setGoldPerAssignment(ConfigurationManager
							.getInstance().getGoldPerAssignment());
					validationJob.setListOfUnits(listOfUnits);
					validationJob.setGoldenUnits(goldenUnits);
					validationJob.setGoldenUnitsSourcePos(goldenUnitsPos);
					validationJob.setGoldenUnitsSourceNeg(goldenUnitsNeg);

					validationJob.createUI();

					result.add(validationJob);
				} else {
					if (!context
							&& (this.mappingGoal
									.equals(TypeOfMappingGoal.VALIDATION))) {
						
						instructions = ConfigurationManager.getInstance().getJobInstructions(TypeOfMicrotask.Validation);
						titleBasis = ConfigurationManager.getInstance().getJobTitleBasis(TypeOfMicrotask.Validation);
						InterlinkValidationJobMicrotaskImpl validationJob = null;

						if (job % 2 != 0) {
							validationJob = new InterlinkValidationJobMicrotaskImpl(
									false);
						} else if (job % 2 == 0) {
							validationJob = new InterlinkValidationJobMicrotaskImpl(
									true);

						}
						jobNumber = ++counter;
						// validationJob.setTitle("Validate the relation between two elements - Part "+jobNumber+"B");
						validationJob
								.setTitle(titleBasis
										+ jobNumber);
						validationJob
								.setInstructions(instructions);
						validationJob.setLanguage("en");
						// it is a 2 possible answers microtask
						validationJob.setJudgmentsPerUnit(ConfigurationManager
								.getInstance().getjudgmentsPerUnitTwoOptions());
						validationJob
								.setMaxJudgmentsPerWorker(ConfigurationManager
										.getInstance()
										.getMaxJudgmentsPerWorker());
						validationJob
								.setPagesPerAssignment(ConfigurationManager
										.getInstance().getPagerPerAssignment());
						// validationJob.setUnitsPerAssignment(ConfigurationManager.getInstance().getUnitsPerAssignment());
						validationJob
								.setUnitsPerAssignment(realUnitsPerAssignment);
						validationJob.setGoldPerAssignment(ConfigurationManager
								.getInstance().getGoldPerAssignment());
						validationJob.setListOfUnits(listOfUnits);
						validationJob.setGoldenUnits(goldenUnits);
						validationJob.setGoldenUnitsSourcePos(goldenUnitsPos);
						validationJob.setGoldenUnitsSourceNeg(goldenUnitsNeg);
						validationJob.createUI();

						result.add(validationJob);
					} else {
						if (context
								&& (this.mappingGoal
										.equals(TypeOfMappingGoal.IDENTIFICATIONA))) {
							
							instructions = ConfigurationManager.getInstance().getJobInstructions(TypeOfMicrotask.Identification);
							titleBasis = ConfigurationManager.getInstance().getJobTitleBasis(TypeOfMicrotask.Identification);
							InterlinkIdentificationWithFullContextJobMicrotaskImpl ideaJob = null;
							if (job % 2 != 0) {
								ideaJob = new InterlinkIdentificationWithFullContextJobMicrotaskImpl(
										true, false);
							} else if (job % 2 == 0) {
								ideaJob = new InterlinkIdentificationWithFullContextJobMicrotaskImpl(
										true, true);

							}
							jobNumber = ++counter;
							ideaJob.setTitle(titleBasis
									+ jobNumber);
							ideaJob.setInstructions(instructions);
							ideaJob.setLanguage("en");
							// it is a 4 possible answers microtask
							ideaJob.setJudgmentsPerUnit(ConfigurationManager
									.getInstance()
									.getjudgmentsPerUnitFourOptions());
							ideaJob.setMaxJudgmentsPerWorker(ConfigurationManager
									.getInstance().getMaxJudgmentsPerWorker());
							ideaJob.setPagesPerAssignment(ConfigurationManager
									.getInstance().getPagerPerAssignment());
							// ideaJob.setUnitsPerAssignment(ConfigurationManager.getInstance().getUnitsPerAssignment());
							ideaJob.setUnitsPerAssignment(realUnitsPerAssignment);
							ideaJob.setGoldPerAssignment(ConfigurationManager
									.getInstance().getGoldPerAssignment());
							ideaJob.setListOfUnits(listOfUnits);
							ideaJob.setGoldenUnits(goldenUnits);
							ideaJob.setGoldenUnitsSourcePos(goldenUnitsPos);
							ideaJob.setGoldenUnitsSourceNeg(goldenUnitsNeg);
							ideaJob.createUI();

							result.add(ideaJob);
						} else {
							if (!context
									&& (this.mappingGoal
											.equals(TypeOfMappingGoal.IDENTIFICATIONA))) {

								
								instructions = ConfigurationManager.getInstance().getJobInstructions(TypeOfMicrotask.Identification);
								titleBasis = ConfigurationManager.getInstance().getJobTitleBasis(TypeOfMicrotask.Identification);
								
								InterlinkIdentificationJobMicrotaskImpl ideaJob = null;
								if (job % 2 != 0) {
									ideaJob = new InterlinkIdentificationJobMicrotaskImpl(
											true, false);
								} else if (job % 2 == 0) {
									ideaJob = new InterlinkIdentificationJobMicrotaskImpl(
											true, true);

								}
								jobNumber = ++counter;
								ideaJob.setTitle(titleBasis
										+ jobNumber);
								ideaJob.setInstructions(instructions);
								ideaJob.setLanguage("en");
								// it is a 4 possible answers microtask
								ideaJob.setJudgmentsPerUnit(ConfigurationManager
										.getInstance()
										.getjudgmentsPerUnitFourOptions());
								ideaJob.setMaxJudgmentsPerWorker(ConfigurationManager
										.getInstance()
										.getMaxJudgmentsPerWorker());
								ideaJob.setPagesPerAssignment(ConfigurationManager
										.getInstance().getPagerPerAssignment());
								// ideaJob.setUnitsPerAssignment(ConfigurationManager.getInstance().getUnitsPerAssignment());
								ideaJob.setUnitsPerAssignment(realUnitsPerAssignment);
								ideaJob.setGoldPerAssignment(ConfigurationManager
										.getInstance().getGoldPerAssignment());
								ideaJob.setListOfUnits(listOfUnits);
								ideaJob.setGoldenUnits(goldenUnits);
								ideaJob.setGoldenUnitsSourcePos(goldenUnitsPos);
								ideaJob.setGoldenUnitsSourceNeg(goldenUnitsNeg);
								ideaJob.createUI();

								result.add(ideaJob);
							} else {
								if (context
										&& (this.mappingGoal
												.equals(TypeOfMappingGoal.IDENTIFICATIONB))) {

									InterlinkIdentificationWithFullContextJobMicrotaskImpl idebJob = null;
									if (job % 2 != 0) {
										idebJob = new InterlinkIdentificationWithFullContextJobMicrotaskImpl(
												false, false);

									} else if (job % 2 == 0) {
										idebJob = new InterlinkIdentificationWithFullContextJobMicrotaskImpl(
												false, true);

									}
									jobNumber = ++counter;
									idebJob.setTitle("Identify the relation between two elements - Part "
											+ jobNumber);
									idebJob.setInstructions("Please tell us whether you think these two elements are related to each other.");
									idebJob.setLanguage("en");
									// it is a 2 possible answers microtask
									idebJob.setJudgmentsPerUnit(ConfigurationManager
											.getInstance()
											.getjudgmentsPerUnitTwoOptions());
									idebJob.setMaxJudgmentsPerWorker(ConfigurationManager
											.getInstance()
											.getMaxJudgmentsPerWorker());
									idebJob.setPagesPerAssignment(ConfigurationManager
											.getInstance()
											.getPagerPerAssignment());
									// idebJob.setUnitsPerAssignment(ConfigurationManager.getInstance().getUnitsPerAssignment());
									idebJob.setUnitsPerAssignment(realUnitsPerAssignment);
									idebJob.setGoldPerAssignment(ConfigurationManager
											.getInstance()
											.getGoldPerAssignment());
									idebJob.setListOfUnits(listOfUnits);
									idebJob.setGoldenUnits(goldenUnits);
									idebJob.setGoldenUnitsSourcePos(goldenUnitsPos);
									idebJob.setGoldenUnitsSourceNeg(goldenUnitsNeg);

									idebJob.createUI();

									result.add(idebJob);
								} else {
									if (!context
											&& (this.mappingGoal
													.equals(TypeOfMappingGoal.IDENTIFICATIONB))) {
										InterlinkIdentificationJobMicrotaskImpl idebJob = null;
										if (job % 2 != 0) {
											idebJob = new InterlinkIdentificationJobMicrotaskImpl(
													false, false);
										} else if (job % 2 == 0) {
											idebJob = new InterlinkIdentificationJobMicrotaskImpl(
													false, true);

										}
										jobNumber = ++counter;
										idebJob.setTitle("Identify the relation between two elements - Part "
												+ jobNumber);
										idebJob.setInstructions("Please tell us whether you think these two elements are related to each other.");
										idebJob.setLanguage("en");
										// it is a 2 possible answers microtask
										idebJob.setJudgmentsPerUnit(ConfigurationManager
												.getInstance()
												.getjudgmentsPerUnitTwoOptions());
										idebJob.setMaxJudgmentsPerWorker(ConfigurationManager
												.getInstance()
												.getMaxJudgmentsPerWorker());
										idebJob.setPagesPerAssignment(ConfigurationManager
												.getInstance()
												.getPagerPerAssignment());
										// idebJob.setUnitsPerAssignment(ConfigurationManager.getInstance().getUnitsPerAssignment());
										idebJob.setUnitsPerAssignment(realUnitsPerAssignment);
										idebJob.setGoldPerAssignment(ConfigurationManager
												.getInstance()
												.getGoldPerAssignment());
										idebJob.setListOfUnits(listOfUnits);
										idebJob.setGoldenUnits(goldenUnits);
										idebJob.setGoldenUnitsSourcePos(goldenUnitsPos);
										idebJob.setGoldenUnitsSourceNeg(goldenUnitsNeg);
										idebJob.createUI();

										result.add(idebJob);
									}
								}
							}
						}
					}
				}

				
				
			
				// job = job +1;

			}

			/*for testing printing
			 * for (Microtask microt : result) {

				microt.serialiseUnitsIntoCVSFile();
				// microt.createUI();
			}*/

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*private Set<UnitDataEntryImpl> addGoldStandardMappingsFromRDFFile(
			int jobIndex, UnitDataEntryImpl sampleUnitData, Dataset d1, Dataset d2, File fInterlinking) {
		Set<UnitDataEntryImpl> goldenUnits = new HashSet<UnitDataEntryImpl>();
		
		
		try{
			
			//int index = this.lastAvailableIndex;
			int index = this.lastAvailableIndex;
			
			
			
					
			
			//File fileGolden = new File(ConfigurationManager.getInstance().getGoldenUnitsFilePath());
			
			//int numGoldenUnitsAvailable = ConfigurationManager.getInstance().getMaxGoldenUnitsAvaible(); ONLY IS OK WHEN WE HAVE FROM THE FILE ADHOC ISWC2013
			
			
			
			
			//instead of using the DirectLinksGeneratorImpl through the experiment manager i can use it directly as this is not the place from which to call the experiment manager - should be separated
			
			//Improve this tunning - configuration.properties
			
			
			
			
			//Use case1

			*//*Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/gesis_pubs_uc1_g.rdf", null, null, null);
			Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/gesis_studs_uc1_g.rdf", null, null, null);
			
			File fInterlinking = new File("datasets/gesis_links_uc1_g.nt");*//*
			
			
			//Use case 3
			*//*Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/gesis_pubs_uc3_g.rdf", null, null, null);
			Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/gesis_studs_uc3_g.rdf", null, null, null);
			
			File fInterlinking = new File("datasets/gesis_links_uc3_g.nt");*//*
			
			
			
			*//*File rdfFilePubs = new File ("datasets/gesis_pubs_uc1_g.rdf");
			File rdfFileStuds = new File ("datasets/gesis_studs_uc1_g.rdf");
			File rdfLinksFile = new File ("datasets/gesis_links_uc1_g.nt");*//*
			
			//Use case 1
			
			//Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/gesis_pubs_uc1_g.rdf", null, null, null);
			//Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/gesis_studs_uc1_g.rdf", null, null, null);
			
		
		
			
			
			
			
			if (this.setOfLinksFromRDFFile.size()==0)
			{
			
			DirectLinksGeneratorImpl directLinksGen = new DirectLinksGeneratorImpl(d1, d2, fInterlinking);
			Interlinking interlinking = directLinksGen.generateLinks();
			
			if(ConfigurationManager.getInstance().getLinksInOrder().equals(ParseLinksInOrder.Lists))
				// TODO: refactor - using the DIrectLinksgenerator which if the config is in list - for the non gold unit links - is getting links from a file into the ordered list and not in the set
		
			{			this.setOfLinksFromRDFFile = new HashSet<Interlink>(interlinking.getOrderedListOfInterlinks());
}
			else
			{			this.setOfLinksFromRDFFile = interlinking.getSetOfInterLinks();
}
			
			}
			
			int numGoldenUnitsAvailable = setOfLinksFromRDFFile.size();
			
			*//*System.out.println("number of gold units loaded from RDF file: "+setOfLinksFromRDFFile.size());
			
			
			Iterator<Interlink> it = setOfLinksFromRDFFile.iterator();
			while (it.hasNext())
			{
				System.out.println("gold link: "+ it.next().getId());
			}*//*
			
			
			//List<String> listOfLinesWithGold=Files.readLines(fileGolden,Charsets.UTF_8);
			

			//repeated -> update! - should be with a call to the superclass
			Iterator<Interlink> itLinks =setOfLinksFromRDFFile.iterator();
			ArrayList<Interlink> listLinks = new ArrayList<Interlink>();
			while (itLinks.hasNext())
			{
				listLinks.add(itLinks.next());
			}

			*//*for (int i = 0; i <= this.numberOfMinimumGold-1; i++) {

				if (index > (numGoldenUnitsAvailable - 1))
						//|| index + 1 > (numGoldenUnitsAvailable - 1))
				{
					index = 0;
					this.lastAvailableIndex = 0;
				}

				goldenLinkI = listLinks.get(index);*//*
	for (Interlink goldenLinkI: listLinks) {
				if (sampleUnitData instanceof InterlinkValidationWithFullContextUnitDataEntryImpl)
				{
					
									
						
					InterlinkValidationWithFullContextUnitDataEntryImpl sampleIntValUnit = (InterlinkValidationWithFullContextUnitDataEntryImpl)sampleUnitData; 
					List<FeatureTextValue> listOfFeaturesA = sampleIntValUnit.getListOfFeaturesA();
					List<FeatureTextValue> listOfFeaturesB = sampleIntValUnit.getListOfFeaturesB();//just as reference list of features
					
//					InterlinkValidationWithFullContextUnitDataEntryImpl goldIntValUnit = new InterlinkValidationWithFullContextUnitDataEntryImpl("", "", "", null, null); 
//					goldIntValUnit.loadInfoFromString(listOfFeaturesA, listOfFeaturesB,data);
					
				InterlinkValidationWithFullContextUnitDataEntryImpl goldIntValUnit = new InterlinkValidationWithFullContextUnitDataEntryImpl(goldenLinkI.getElementA().getURI(), goldenLinkI.getElementB().getURI(),goldenLinkI.getRelation().getURI(), d1, d2); 
				goldIntValUnit.loadInfo();
					
					
					goldIntValUnit.setDifficulty(1);
					goldIntValUnit.setGoldenUnit(true);
					goldenUnits.add(goldIntValUnit);
					
					
				}
				else if(sampleUnitData instanceof InterlinkIdentificationWithFullContextUnitDataEntryImpl)
				{
					
					
					InterlinkIdentificationWithFullContextUnitDataEntryImpl sampleIntIdentifUnit = (InterlinkIdentificationWithFullContextUnitDataEntryImpl)sampleUnitData; 
					List<FeatureTextValue> listOfFeaturesA = sampleIntIdentifUnit.getListOfFeaturesA(); //just as reference list of features
					List<FeatureTextValue> listOfFeaturesB = sampleIntIdentifUnit.getListOfFeaturesB();//just as reference list of features
					
					InterlinkIdentificationWithFullContextUnitDataEntryImpl goldIntIdentifUnit = new InterlinkIdentificationWithFullContextUnitDataEntryImpl(goldenLinkI.getElementA().getURI(), goldenLinkI.getElementB().getURI(), d1, d2); 
					goldIntIdentifUnit.loadInfo();
					
					goldIntIdentifUnit.setDifficulty(1);
					goldIntIdentifUnit.setGoldenUnit(true);
					goldenUnits.add(goldIntIdentifUnit);
				
					
					
					
				}
				
				
				
			}
		
			
			//Should  be done with the MOD in case it is done this.lastAvailableIndex=this.lastAvailableIndex+this.numberOfMinimumGold;
				

			
			
			
			
		}
		catch(Exception e)
		{e.printStackTrace();}
		return goldenUnits;
	}
		
	*/
	/*private void addExpertiseLinksFromRDFFile(
			int jobIndex, UnitDataEntryImpl sampleUnitData, Dataset d1, Dataset d2, File fInterlinkingExp)
	{
		Set<UnitDataEntryImpl> expertiseLinks = new HashSet<UnitDataEntryImpl>();
		if (this.setOfExperiseLinks.size()==0)
		{
			DirectLinksGeneratorImpl directLinksGen = new DirectLinksGeneratorImpl(d1, d2, fInterlinkingExp);
			Interlinking interlinkingPos = directLinksGen.generateLinks();
			if(ConfigurationManager.getInstance().getLinksInOrder().equals(ParseLinksInOrder.Lists))
			{
				this.setOfExperiseLinks= new HashSet<Interlink>(interlinkingPos.getOrderedListOfInterlinks());

			}
			else
			{
				this.setOfExperiseLinks = interlinkingPos.getSetOfInterLinks();}
		}


	}*/

	private Set<UnitDataEntryImpl> addGoldStandardMappingsFromRDFFile(
			int jobIndex, UnitDataEntryImpl sampleUnitData, Dataset d1, Dataset d2, File fInterlinkingPos, File fInterlinkingNeg) {
		Set<UnitDataEntryImpl> goldenUnits = new HashSet<UnitDataEntryImpl>();
		
		
		try{
			
			int index = this.lastAvailableIndex;
			
			
			
			
					
			
			//File fileGolden = new File(ConfigurationManager.getInstance().getGoldenUnitsFilePath());
			
			//int numGoldenUnitsAvailable = ConfigurationManager.getInstance().getMaxGoldenUnitsAvaible(); ONLY IS OK WHEN WE HAVE FROM THE FILE ADHOC ISWC2013
			
			
			
			
			//instead of using the DirectLinksGeneratorImpl through the experiment manager i can use it directly as this is not the place from which to call the experiment manager - should be separated
			
			//Improve this tunning - configuration.properties
			
			
			
			
			//Use case1

			/*Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/gesis_pubs_uc1_g.rdf", null, null, null);
			Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/gesis_studs_uc1_g.rdf", null, null, null);
			
			File fInterlinking = new File("datasets/gesis_links_uc1_g.nt");*/
			
			
			//Use case 3
			/*Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/gesis_pubs_uc3_g.rdf", null, null, null);
			Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/gesis_studs_uc3_g.rdf", null, null, null);
			
			File fInterlinking = new File("datasets/gesis_links_uc3_g.nt");*/
			
			
			
			/*File rdfFilePubs = new File ("datasets/gesis_pubs_uc1_g.rdf");
			File rdfFileStuds = new File ("datasets/gesis_studs_uc1_g.rdf");
			File rdfLinksFile = new File ("datasets/gesis_links_uc1_g.nt");*/
			
			//Use case 1
			
			//Dataset d1 = new Dataset("source1", TypeOfDatasetLocation.FILEDUMP, "datasets/gesis_pubs_uc1_g.rdf", null, null, null);
			//Dataset d2 = new Dataset("source2", TypeOfDatasetLocation.FILEDUMP, "datasets/gesis_studs_uc1_g.rdf", null, null, null);
			
		
		
			
			
			//to be sure it can still be retrieved and treated as sets
			if (this.setOfLinksFromRDFFile.size()==0)
			{
			
			DirectLinksGeneratorImpl directLinksGenPos = new DirectLinksGeneratorImpl(d1, d2, fInterlinkingPos);
			Interlinking interlinkingPos = directLinksGenPos.generateLinks();
				if(ConfigurationManager.getInstance().getLinksInOrder().equals(ParseLinksInOrder.Lists))
				{
					this.goldenUnitsPos = new HashSet<Interlink>(interlinkingPos.getOrderedListOfInterlinks());

			}
				else
				{
				this.goldenUnitsPos = interlinkingPos.getSetOfInterLinks();}

				DirectLinksGeneratorImpl directLinksGenNeg = new DirectLinksGeneratorImpl(d1, d2, fInterlinkingNeg);
				Interlinking interlinkingNeg = directLinksGenNeg.generateLinks();
				if(ConfigurationManager.getInstance().getLinksInOrder().equals(ParseLinksInOrder.Lists))
				{
					this.goldenUnitsNeg = new HashSet<Interlink>(interlinkingNeg.getOrderedListOfInterlinks());
				}
				else {
					this.goldenUnitsNeg = interlinkingNeg.getSetOfInterLinks();
				}
			
			this.setOfLinksFromRDFFile.addAll(this.goldenUnitsPos);
				this.setOfLinksFromRDFFile.addAll(this.goldenUnitsNeg);

					List<Interlink> listAll=new ArrayList<Interlink>(this.setOfLinksFromRDFFile);
				Collections.shuffle(listAll);
				this.setOfLinksFromRDFFile=new HashSet<Interlink>(listAll);

			}
			
			int numGoldenUnitsAvailable = setOfLinksFromRDFFile.size();
			
			/*System.out.println("number of gold units loaded from RDF file: "+setOfLinksFromRDFFile.size());
			
			
			Iterator<Interlink> it = setOfLinksFromRDFFile.iterator();
			while (it.hasNext())
			{
				System.out.println("gold link: "+ it.next().getId());
			}*/
			
			
			//List<String> listOfLinesWithGold=Files.readLines(fileGolden,Charsets.UTF_8);
			

			//repeated -> update! - should be with a call to the superclass
			Iterator<Interlink> itLinks =setOfLinksFromRDFFile.iterator();
			ArrayList<Interlink> listLinks = new ArrayList<Interlink>();
			
			while (itLinks.hasNext())
			{
				listLinks.add(itLinks.next());
			}
			

			Interlink goldenLinkI=null; 
			
			//int ri = r;
			for (int i = 0; i <= this.numberOfMinimumGold-1; i++) {
				// check that ri is not bigger than size-1 otherwise go to
				// the beginning
				
				//index = this.lastAvailableIndex+i;
				System.out.println(" ");
				if (index > (numGoldenUnitsAvailable - 1))
						/*|| index + 1 > (numGoldenUnitsAvailable - 1))*/ 
				{
					index = 0;
					this.lastAvailableIndex = 0;
				}

				goldenLinkI = listLinks.get(index);
//				String dataLine = listOfLinesWithGold.get(index);
//				String[] dataElements = dataLine.split("=");
//				String data = dataElements[1];
				
				/*if(itLinks.hasNext())
				{
				goldenLinkI = itLinks.next(); 
					
					
				}*/
				
				if (sampleUnitData instanceof InterlinkValidationWithFullContextUnitDataEntryImpl)
				{
					
									
						
					InterlinkValidationWithFullContextUnitDataEntryImpl sampleIntValUnit = (InterlinkValidationWithFullContextUnitDataEntryImpl)sampleUnitData; 
					List<FeatureTextValue> listOfFeaturesA = sampleIntValUnit.getListOfFeaturesA();
					List<FeatureTextValue> listOfFeaturesB = sampleIntValUnit.getListOfFeaturesB();//just as reference list of features
					
//					InterlinkValidationWithFullContextUnitDataEntryImpl goldIntValUnit = new InterlinkValidationWithFullContextUnitDataEntryImpl("", "", "", null, null); 
//					goldIntValUnit.loadInfoFromString(listOfFeaturesA, listOfFeaturesB,data);
					
				InterlinkValidationWithFullContextUnitDataEntryImpl goldIntValUnit = new InterlinkValidationWithFullContextUnitDataEntryImpl(goldenLinkI.getElementA().getURI(), goldenLinkI.getElementB().getURI(),goldenLinkI.getRelation().getURI(), d1, d2); 
				goldIntValUnit.loadInfo();
					
					
					goldIntValUnit.setDifficulty(1);
					goldIntValUnit.setGoldenUnit(true);
					goldenUnits.add(goldIntValUnit);
					index = index +1;
					
				}
				else if(sampleUnitData instanceof InterlinkIdentificationWithFullContextUnitDataEntryImpl)
				{
					
					
					InterlinkIdentificationWithFullContextUnitDataEntryImpl sampleIntIdentifUnit = (InterlinkIdentificationWithFullContextUnitDataEntryImpl)sampleUnitData; 
					List<FeatureTextValue> listOfFeaturesA = sampleIntIdentifUnit.getListOfFeaturesA(); //just as reference list of features
					List<FeatureTextValue> listOfFeaturesB = sampleIntIdentifUnit.getListOfFeaturesB();//just as reference list of features
					
					InterlinkIdentificationWithFullContextUnitDataEntryImpl goldIntIdentifUnit = new InterlinkIdentificationWithFullContextUnitDataEntryImpl(goldenLinkI.getElementA().getURI(), goldenLinkI.getElementB().getURI(), d1, d2); 
					goldIntIdentifUnit.loadInfo();
					
					goldIntIdentifUnit.setDifficulty(1);
					goldIntIdentifUnit.setGoldenUnit(true);
					goldenUnits.add(goldIntIdentifUnit);
					
					index = index+1;
					
					
					
				}
				//we don�t have the case without context 
				
				
				
			}
			this.lastAvailableIndex = index+1;
			
			//Should  be done with the MOD in case it is done this.lastAvailableIndex=this.lastAvailableIndex+this.numberOfMinimumGold;
				

			
			
			
			
		}
		
		catch(Exception e)
		{e.printStackTrace();}
		return goldenUnits;
	}
		
	
	
	private Set<UnitDataEntryImpl> addGoldStandardMappingsFromFile(
			int jobIndex, UnitDataEntryImpl sampleUnitData) {
		Set<UnitDataEntryImpl> goldenUnits = new HashSet<UnitDataEntryImpl>();
		
		
		try{
			
			int index = this.lastAvailableIndex;
			
			
			
			
					
			
			File fileGolden = new File(ConfigurationManager.getInstance().getGoldenUnitsFilePath());
			
			int numGoldenUnitsAvailable = ConfigurationManager.getInstance().getMaxGoldenUnitsAvaible();
			
			List<String> listOfLinesWithGold=Files.readLines(fileGolden,Charsets.UTF_8);
			

			//repeated -> update! - should be with a call to the superclass
			

			
			//int ri = r;
			for (int i = 0; i <= this.numberOfMinimumGold-1; i++) {
				// check that ri is not bigger than size-1 otherwise go to
				// the beginning
				
				//index = this.lastAvailableIndex+i;
				System.out.println(" ");
				if (index > (numGoldenUnitsAvailable - 1))
						/*|| index + 1 > (numGoldenUnitsAvailable - 1))*/ 
				{
					index = 0;
					this.lastAvailableIndex = 0;
				}

				String dataLine = listOfLinesWithGold.get(index);
				String[] dataElements = dataLine.split("=");
				String data = dataElements[1];
				
				if (sampleUnitData instanceof InterlinkValidationWithFullContextUnitDataEntryImpl)
				{
					
									
						
					InterlinkValidationWithFullContextUnitDataEntryImpl sampleIntValUnit = (InterlinkValidationWithFullContextUnitDataEntryImpl)sampleUnitData; 
					List<FeatureTextValue> listOfFeaturesA = sampleIntValUnit.getListOfFeaturesA();
					List<FeatureTextValue> listOfFeaturesB = sampleIntValUnit.getListOfFeaturesB();//just as reference list of features
					
					InterlinkValidationWithFullContextUnitDataEntryImpl goldIntValUnit = new InterlinkValidationWithFullContextUnitDataEntryImpl("", "", "", null, null); 
					goldIntValUnit.loadInfoFromString(listOfFeaturesA, listOfFeaturesB,data);
					
					goldIntValUnit.setDifficulty(1);
					goldIntValUnit.setGoldenUnit(true);
					goldenUnits.add(goldIntValUnit);
					index = index +1;
					
				}
				else if(sampleUnitData instanceof InterlinkIdentificationWithFullContextUnitDataEntryImpl)
				{
					
					
					InterlinkIdentificationWithFullContextUnitDataEntryImpl sampleIntIdentifUnit = (InterlinkIdentificationWithFullContextUnitDataEntryImpl)sampleUnitData; 
					List<FeatureTextValue> listOfFeaturesA = sampleIntIdentifUnit.getListOfFeaturesA(); //just as reference list of features
					List<FeatureTextValue> listOfFeaturesB = sampleIntIdentifUnit.getListOfFeaturesB();//just as reference list of features
					
					InterlinkIdentificationWithFullContextUnitDataEntryImpl goldIntIdentifUnit = new InterlinkIdentificationWithFullContextUnitDataEntryImpl("","",null,null); 
					goldIntIdentifUnit.loadInfoFromString(listOfFeaturesA, listOfFeaturesB,data);
					
					goldIntIdentifUnit.setDifficulty(1);
					goldIntIdentifUnit.setGoldenUnit(true);
					goldenUnits.add(goldIntIdentifUnit);
					
					index = index+1;
					
					
					
				}
				//we don�t have the case without context 
				
				
				
			}
			
			
			//Should  be done with the MOD in case it is done this.lastAvailableIndex=this.lastAvailableIndex+this.numberOfMinimumGold;
				

			
			
			/*
			switch (caseId) {
			case 1: // context + validation

				int ri = r;
				for (int i = 1; i <= this.numberOfMinimumGold; i++) {
					// check that ri is not bigger than size-1 otherwise go to
					// the beginning
					
					System.out.println(" ");
					if (ri > (setOfResources.size() - 1)
							|| ri + 1 > (setOfResources.size() - 1)) {
						ri = 0;
						this.lastAvailableIndex = 0;
					}

					InterlinkValidationWithFullContextUnitDataEntryImpl goldIntlValCont = new InterlinkValidationWithFullContextUnitDataEntryImpl(
							setOfResources.get(ri).getLocalName(),
							setOfResources.get(ri + 1).getLocalName(), "=",
							this.candidateInterlinking.getDataset1(),
							this.candidateInterlinking.getDataset2());
					ri = ri + 2;
					goldIntlValCont.setDifficulty(1);
					goldIntlValCont.loadGoldenInfo();
					goldIntlValCont.setGoldenUnit(true);
					goldenUnits.add(goldIntlValCont);
				}
				break;
			case 2: // !context + validation
				
			case 3:// context + identification
			case 4:// !context + identification
			*/
			
		}
		
		catch(Exception e)
		{e.printStackTrace();}
		return goldenUnits;
	}
		
		
		
	private Set<UnitDataEntryImpl> addGoldStandardMappings(int caseId,
			int jobIndex) {
		Set<UnitDataEntryImpl> goldenUnits = new HashSet<UnitDataEntryImpl>();

		// already calculated in the calculateMinimumGold() method

		// what is called assignment in CrowdFlower will be a HIT in MTurk

		// this.numberOfMinimumGold = numberOfAssignmentsInJob;

		ArrayList<Resource> setOfResources = new ArrayList<Resource>();

		try {
			// In order to avoid Spam, it is a good thing to use Gold Standard
			// units.
			Model model = ModelFactory.createDefaultModel();

			Resource r1 = model
					.createResource(Constants.NS_CROWD + "NEWSPAPER");
			Resource r2 = model
					.createResource(Constants.NS_CROWD + "NEWSPAPEr");
			setOfResources.add(r1);
			setOfResources.add(r2);
			// Relation rel = Relation.SIMILAR;

			Resource r3 = model.createResource(Constants.NS_CROWD + "Book");
			Resource r4 = model.createResource(Constants.NS_CROWD + "book");
			setOfResources.add(r3);
			setOfResources.add(r4);
			// Relation rel2 = Relation.SIMILAR;

			Resource r5 = model.createResource(Constants.NS_CROWD
					+ "CONFERENCE");
			Resource r6 = model.createResource(Constants.NS_CROWD
					+ "CONFERENCE");
			setOfResources.add(r5);
			setOfResources.add(r6);
			// Relation rel3 = Relation.SIMILAR;

			Resource r7 = model.createResource(Constants.NS_CROWD
					+ "Researcher");
			Resource r8 = model.createResource(Constants.NS_CROWD
					+ "researcher");
			setOfResources.add(r7);
			setOfResources.add(r8);
			// Relation rel4 = Relation.SIMILAR;

			Resource r9 = model.createResource(Constants.NS_CROWD + "day");
			Resource r10 = model.createResource(Constants.NS_CROWD + "day");
			setOfResources.add(r9);
			setOfResources.add(r10);
			// Relation rel = Relation.SIMILAR;

			Resource r11 = model.createResource(Constants.NS_CROWD + "person");
			Resource r12 = model.createResource(Constants.NS_CROWD + "person");
			setOfResources.add(r11);
			setOfResources.add(r12);
			// Relation rel2 = Relation.SIMILAR;

			Resource r13 = model.createResource(Constants.NS_CROWD + "journAL");
			Resource r14 = model.createResource(Constants.NS_CROWD + "journal");
			setOfResources.add(r13);
			setOfResources.add(r14);
			// Relation rel3 = Relation.SIMILAR;

			Resource r15 = model.createResource(Constants.NS_CROWD + "house");
			Resource r16 = model.createResource(Constants.NS_CROWD + "house");
			setOfResources.add(r15);
			setOfResources.add(r16);
			// Relation rel4 = Relation.SIMILAR;

			Resource r17 = model.createResource(Constants.NS_CROWD + "company");
			Resource r18 = model.createResource(Constants.NS_CROWD + "COMPANY");
			setOfResources.add(r17);
			setOfResources.add(r18);
			// Relation rel = Relation.SIMILAR;

			Resource r19 = model
					.createResource(Constants.NS_CROWD + "Director");
			Resource r20 = model
					.createResource(Constants.NS_CROWD + "director");
			setOfResources.add(r19);
			setOfResources.add(r20);
			// Relation rel2 = Relation.SIMILAR;

			Resource r21 = model.createResource(Constants.NS_CROWD + "country");
			Resource r22 = model.createResource(Constants.NS_CROWD + "COUNTRY");
			setOfResources.add(r21);
			setOfResources.add(r22);
			// Relation rel3 = Relation.SIMILAR;

			Resource r23 = model.createResource(Constants.NS_CROWD + "Picture");
			Resource r24 = model.createResource(Constants.NS_CROWD + "Picture");
			setOfResources.add(r23);
			setOfResources.add(r24);
			// Relation rel4 = Relation.SIMILAR;

			Resource r25 = model.createResource(Constants.NS_CROWD + "video");
			Resource r26 = model.createResource(Constants.NS_CROWD + "VIDEO");
			setOfResources.add(r25);
			setOfResources.add(r26);
			// Relation rel = Relation.SIMILAR;

			Resource r27 = model.createResource(Constants.NS_CROWD + "city");
			Resource r28 = model.createResource(Constants.NS_CROWD + "city");
			setOfResources.add(r27);
			setOfResources.add(r28);
			// Relation rel2 = Relation.SIMILAR;

			Resource r29 = model.createResource(Constants.NS_CROWD + "car");
			Resource r30 = model.createResource(Constants.NS_CROWD + "Car");
			setOfResources.add(r29);
			setOfResources.add(r30);
			// Relation rel3 = Relation.SIMILAR;

			Resource r31 = model.createResource(Constants.NS_CROWD + "System");
			Resource r32 = model.createResource(Constants.NS_CROWD + "System");
			setOfResources.add(r31);
			setOfResources.add(r32);
			// Relation rel4 = Relation.SIMILAR;

			Resource r33 = model.createResource(Constants.NS_CROWD + "window");
			Resource r34 = model.createResource(Constants.NS_CROWD + "window");
			setOfResources.add(r33);
			setOfResources.add(r34);

			Resource r35 = model.createResource(Constants.NS_CROWD
					+ "publication");
			Resource r36 = model.createResource(Constants.NS_CROWD + "paper");
			setOfResources.add(r31);
			setOfResources.add(r32);
			// -------------------------------------
			Resource r37 = model.createResource(Constants.NS_CROWD + "ADvisor");
			Resource r38 = model.createResource(Constants.NS_CROWD + "advisor");
			setOfResources.add(r37);
			setOfResources.add(r38);

			Resource r39 = model.createResource(Constants.NS_CROWD + "student");
			Resource r40 = model.createResource(Constants.NS_CROWD + "student");
			setOfResources.add(r39);
			setOfResources.add(r40);

			Resource r41 = model.createResource(Constants.NS_CROWD + "teacher");
			Resource r42 = model.createResource(Constants.NS_CROWD + "teachER");
			setOfResources.add(r41);
			setOfResources.add(r42);

			Resource r43 = model.createResource(Constants.NS_CROWD + "server");
			Resource r44 = model.createResource(Constants.NS_CROWD + "SERVER");
			setOfResources.add(r43);
			setOfResources.add(r44);

			Resource r45 = model
					.createResource(Constants.NS_CROWD + "computer");
			Resource r46 = model
					.createResource(Constants.NS_CROWD + "Computer");
			setOfResources.add(r45);
			setOfResources.add(r46);

			Resource r47 = model.createResource(Constants.NS_CROWD
					+ "participant");
			Resource r48 = model.createResource(Constants.NS_CROWD
					+ "Participant");
			setOfResources.add(r47);
			setOfResources.add(r48);

			Resource r49 = model.createResource(Constants.NS_CROWD
					+ "Registration");
			Resource r50 = model.createResource(Constants.NS_CROWD
					+ "registration");
			setOfResources.add(r49);
			setOfResources.add(r50);

			Resource r51 = model.createResource(Constants.NS_CROWD
					+ "Proceedings");
			Resource r52 = model.createResource(Constants.NS_CROWD
					+ "proceedings");
			setOfResources.add(r51);
			setOfResources.add(r52);

			Resource r53 = model.createResource(Constants.NS_CROWD + "tablet");
			Resource r54 = model.createResource(Constants.NS_CROWD + "tablet");
			setOfResources.add(r53);
			setOfResources.add(r54);

			Resource r55 = model.createResource(Constants.NS_CROWD + "trash");
			Resource r56 = model.createResource(Constants.NS_CROWD + "trash");
			setOfResources.add(r55);
			setOfResources.add(r56);

			Resource r57 = model
					.createResource(Constants.NS_CROWD + "SUITCASE");
			Resource r58 = model
					.createResource(Constants.NS_CROWD + "suitcase");
			setOfResources.add(r57);
			setOfResources.add(r58);

			Resource r59 = model.createResource(Constants.NS_CROWD + "FOLDER");
			Resource r60 = model.createResource(Constants.NS_CROWD + "folder");
			setOfResources.add(r59);
			setOfResources.add(r60);

			Resource r61 = model.createResource(Constants.NS_CROWD
					+ "television");
			Resource r62 = model.createResource(Constants.NS_CROWD + "TV");
			setOfResources.add(r61);
			setOfResources.add(r62);

			Resource r63 = model.createResource(Constants.NS_CROWD + "radio");
			Resource r64 = model.createResource(Constants.NS_CROWD + "Radio");
			setOfResources.add(r63);
			setOfResources.add(r64);

			Resource r65 = model.createResource(Constants.NS_CROWD + "Sea");
			Resource r66 = model.createResource(Constants.NS_CROWD + "sea");
			setOfResources.add(r65);
			setOfResources.add(r66);

			Resource r67 = model.createResource(Constants.NS_CROWD + "science");
			Resource r68 = model.createResource(Constants.NS_CROWD + "science");
			setOfResources.add(r67);
			setOfResources.add(r68);

			Resource r69 = model.createResource(Constants.NS_CROWD
					+ "Signature");
			Resource r70 = model.createResource(Constants.NS_CROWD
					+ "signature");
			setOfResources.add(r69);
			setOfResources.add(r70);

			Resource r71 = model
					.createResource(Constants.NS_CROWD + "Notebook");
			Resource r72 = model
					.createResource(Constants.NS_CROWD + "Notebook");
			setOfResources.add(r71);
			setOfResources.add(r72);

			Resource r73 = model.createResource(Constants.NS_CROWD + "Bag");
			Resource r74 = model.createResource(Constants.NS_CROWD + "Bag");
			setOfResources.add(r73);
			setOfResources.add(r74);

			Resource r75 = model.createResource(Constants.NS_CROWD + "Podcast");
			Resource r76 = model.createResource(Constants.NS_CROWD + "PODcast");
			setOfResources.add(r75);
			setOfResources.add(r76);

			Resource r77 = model.createResource(Constants.NS_CROWD + "Wokshop");
			Resource r78 = model
					.createResource(Constants.NS_CROWD + "workshop");
			setOfResources.add(r77);
			setOfResources.add(r78);

			Resource r79 = model.createResource(Constants.NS_CROWD + "Partner");
			Resource r80 = model.createResource(Constants.NS_CROWD + "partner");
			setOfResources.add(r79);
			setOfResources.add(r80);

			Resource r81 = model.createResource(Constants.NS_CROWD + "Project");
			Resource r82 = model.createResource(Constants.NS_CROWD + "PROJECT");
			setOfResources.add(r81);
			setOfResources.add(r82);

			Resource r83 = model.createResource(Constants.NS_CROWD + "Email");
			Resource r84 = model.createResource(Constants.NS_CROWD + "E-mail");
			setOfResources.add(r83);
			setOfResources.add(r84);

			Resource r85 = model.createResource(Constants.NS_CROWD + "Meeting");
			Resource r86 = model.createResource(Constants.NS_CROWD + "MEETing");
			setOfResources.add(r85);
			setOfResources.add(r86);

			Resource r87 = model.createResource(Constants.NS_CROWD + "Call");
			Resource r88 = model.createResource(Constants.NS_CROWD + "call");
			setOfResources.add(r87);
			setOfResources.add(r88);

			Resource r89 = model.createResource(Constants.NS_CROWD
					+ "experiment");
			Resource r90 = model.createResource(Constants.NS_CROWD
					+ "Experiment");
			setOfResources.add(r89);
			setOfResources.add(r90);

			Resource r91 = model.createResource(Constants.NS_CROWD + "Result");
			Resource r92 = model.createResource(Constants.NS_CROWD + "result");
			setOfResources.add(r91);
			setOfResources.add(r92);

			Resource r93 = model.createResource(Constants.NS_CROWD + "Test");
			Resource r94 = model.createResource(Constants.NS_CROWD + "test");
			setOfResources.add(r93);
			setOfResources.add(r94);

			Resource r95 = model.createResource(Constants.NS_CROWD + "Change");
			Resource r96 = model.createResource(Constants.NS_CROWD + "change");
			setOfResources.add(r95);
			setOfResources.add(r96);

			Resource r97 = model.createResource(Constants.NS_CROWD + "Trial");
			Resource r98 = model.createResource(Constants.NS_CROWD + "TRIAL");
			setOfResources.add(r97);
			setOfResources.add(r98);

			Resource r99 = model.createResource(Constants.NS_CROWD + "Team");
			Resource r100 = model.createResource(Constants.NS_CROWD + "Team");
			setOfResources.add(r99);
			setOfResources.add(r100);

			// int r =
			// (this.numberOfMinimumGold*2*jobIndex)-(this.numberOfMinimumGold*2);
			int r = this.lastAvailableIndex;

			switch (caseId) {
			case 1: // context + validation

				int ri = r;
				for (int i = 1; i <= this.numberOfMinimumGold; i++) {
					// check that ri is not bigger than size-1 otherwise go to
					// the beginning
					
					System.out.println(" ");
					if (ri > (setOfResources.size() - 1)
							|| ri + 1 > (setOfResources.size() - 1)) {
						ri = 0;
						this.lastAvailableIndex = 0;
					}

					InterlinkValidationWithFullContextUnitDataEntryImpl goldIntlValCont = new InterlinkValidationWithFullContextUnitDataEntryImpl(
							setOfResources.get(ri).getLocalName(),
							setOfResources.get(ri + 1).getLocalName(), "=",
							this.candidateInterlinking.getDataset1(),
							this.candidateInterlinking.getDataset2());
					ri = ri + 2;
					goldIntlValCont.setDifficulty(1);
					goldIntlValCont.setGoldenUnit(true);
					goldenUnits.add(goldIntlValCont);
				}

				/*
				 * MappingValidationWithFullContextUnitDataEntryImpl
				 * goldMapValCont1 = new
				 * MappingValidationWithFullContextUnitDataEntryImpl
				 * (r1.getLocalName(), r2.getLocalName(), "=",
				 * this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapValCont1.setDifficulty(1);
				 * goldMapValCont1.setGoldenUnit(true);
				 * goldenUnits.add(goldMapValCont1);
				 * 
				 * if (this.numberOfMinimumGold>=2){
				 * MappingValidationWithFullContextUnitDataEntryImpl
				 * goldMapIdCont2 = new
				 * MappingValidationWithFullContextUnitDataEntryImpl
				 * (r3.getLocalName(), r4.getLocalName(), "=",
				 * this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapIdCont2.setDifficulty(1);
				 * goldMapIdCont2.setGoldenUnit(true);
				 * goldenUnits.add(goldMapIdCont2);}
				 * 
				 * if (this.numberOfMinimumGold>=3){
				 * MappingValidationWithFullContextUnitDataEntryImpl
				 * goldMapIdCont1b = new
				 * MappingValidationWithFullContextUnitDataEntryImpl
				 * (r5.getLocalName(), r6.getLocalName(), "=",
				 * this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapIdCont1b.setDifficulty(1);
				 * goldMapIdCont1b.setGoldenUnit(true);
				 * goldenUnits.add(goldMapIdCont1b);}
				 * 
				 * if (this.numberOfMinimumGold>=4){
				 * MappingValidationWithFullContextUnitDataEntryImpl
				 * goldMapIdCont2b = new
				 * MappingValidationWithFullContextUnitDataEntryImpl
				 * (r7.getLocalName(), r8.getLocalName(), "=",
				 * this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapIdCont2b.setDifficulty(1);
				 * goldMapIdCont2b.setGoldenUnit(true);
				 * goldenUnits.add(goldMapIdCont2b);}
				 */

				break;
			case 2: // !context + validation

				int ri2 = r;
				for (int i = 1; i <= this.numberOfMinimumGold; i++) {

					// check that ri is not bigger than size-1 otherwise go to
					// the beginning
					if (ri2 > (setOfResources.size() - 1)
							|| ri2 + 1 > (setOfResources.size() - 1)) {
						ri2 = 0;
						this.lastAvailableIndex = 0;
					}

					InterlinkValidationUnitDataEntryImpl goldIntlValCont = new InterlinkValidationUnitDataEntryImpl(
							setOfResources.get(ri2).getLocalName(),
							setOfResources.get(ri2 + 1).getLocalName(), "=",
							this.candidateInterlinking.getDataset1(),
							this.candidateInterlinking.getDataset2());
					ri2 = ri2 + 2;
					goldIntlValCont.setDifficulty(1);
					goldIntlValCont.setGoldenUnit(true);
					goldenUnits.add(goldIntlValCont);
				}

				/*
				 * MappingValidationUnitDataEntryImpl goldMapValCont3 = new
				 * MappingValidationUnitDataEntryImpl(r1.getLocalName(),
				 * r2.getLocalName(), "=",
				 * this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapValCont3.setDifficulty(1);
				 * goldMapValCont3.setGoldenUnit(true);
				 * goldenUnits.add(goldMapValCont3);
				 * 
				 * if (this.numberOfMinimumGold>=2){
				 * MappingValidationUnitDataEntryImpl goldMapIdCont4 = new
				 * MappingValidationUnitDataEntryImpl(r3.getLocalName(),
				 * r4.getLocalName(), "=",
				 * this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapIdCont4.setDifficulty(1);
				 * goldMapIdCont4.setGoldenUnit(true);
				 * goldenUnits.add(goldMapIdCont4);}
				 * 
				 * if (this.numberOfMinimumGold>=3){
				 * MappingValidationUnitDataEntryImpl goldMapIdCont3b = new
				 * MappingValidationUnitDataEntryImpl(r5.getLocalName(),
				 * r6.getLocalName(), "=",
				 * this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapIdCont3b.setDifficulty(1);
				 * goldMapIdCont3b.setGoldenUnit(true);
				 * goldenUnits.add(goldMapIdCont3b);}
				 * 
				 * if (this.numberOfMinimumGold>=4){
				 * MappingValidationUnitDataEntryImpl goldMapIdCont4b = new
				 * MappingValidationUnitDataEntryImpl(r7.getLocalName(),
				 * r8.getLocalName(), "=",
				 * this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapIdCont4b.setDifficulty(1);
				 * goldMapIdCont4b.setGoldenUnit(true);
				 * goldenUnits.add(goldMapIdCont4b);}
				 */

				break;
			case 3: // context + identificationa/identificationb

				int ri3 = r;

				for (int i = 1; i <= this.numberOfMinimumGold; i++) {

					// check that ri is not bigger than size-1 otherwise go to
					// the beginning
					if (ri3 > (setOfResources.size() - 1)
							|| ri3 + 1 > (setOfResources.size() - 1)) {
						ri3 = 0;
						this.lastAvailableIndex = 0;
					}
					InterlinkIdentificationWithFullContextUnitDataEntryImpl goldIntlValCont = new InterlinkIdentificationWithFullContextUnitDataEntryImpl(
							setOfResources.get(ri3).getLocalName(),
							setOfResources.get(ri3 + 1).getLocalName(),
							this.candidateInterlinking.getDataset1(),
							this.candidateInterlinking.getDataset2());
					ri3 = ri3 + 2;
					goldIntlValCont.setDifficulty(1);
					goldIntlValCont.setGoldenUnit(true);
					goldenUnits.add(goldIntlValCont);
				}

				/*
				 * MappingIdentificationWithFullContextUnitDataEntryImpl
				 * goldMapValCont5 = new
				 * MappingIdentificationWithFullContextUnitDataEntryImpl
				 * (r1.getLocalName(), r2.getLocalName(),
				 * this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapValCont5.setDifficulty(1);
				 * goldMapValCont5.setGoldenUnit(true);
				 * goldenUnits.add(goldMapValCont5);
				 * 
				 * if (this.numberOfMinimumGold>=2){
				 * MappingIdentificationWithFullContextUnitDataEntryImpl
				 * goldMapIdCont6 = new
				 * MappingIdentificationWithFullContextUnitDataEntryImpl
				 * (r3.getLocalName(), r4.getLocalName(),
				 * this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapIdCont6.setDifficulty(1);
				 * goldMapIdCont6.setGoldenUnit(true);
				 * goldenUnits.add(goldMapIdCont6);}
				 * 
				 * if (this.numberOfMinimumGold>=3){
				 * MappingIdentificationWithFullContextUnitDataEntryImpl
				 * goldMapIdCont5b = new
				 * MappingIdentificationWithFullContextUnitDataEntryImpl
				 * (r5.getLocalName(), r6.getLocalName(),
				 * this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapIdCont5b.setDifficulty(1);
				 * goldMapIdCont5b.setGoldenUnit(true);
				 * goldenUnits.add(goldMapIdCont5b);}
				 * 
				 * if (this.numberOfMinimumGold>=4){
				 * MappingIdentificationWithFullContextUnitDataEntryImpl
				 * goldMapIdCont6b = new
				 * MappingIdentificationWithFullContextUnitDataEntryImpl
				 * (r7.getLocalName(), r8.getLocalName(),
				 * this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapIdCont6b.setDifficulty(1);
				 * goldMapIdCont6b.setGoldenUnit(true);
				 * goldenUnits.add(goldMapIdCont6b);}
				 */

				break;
			case 4: // !context + identificationa/identificationb

				int ri4 = r;
				for (int i = 1; i <= this.numberOfMinimumGold; i++) {
					// check that ri is not bigger than size-1 otherwise go to
					// the beginning
					if (ri4 > (setOfResources.size() - 1)
							|| ri4 + 1 > (setOfResources.size() - 1)) {
						ri4 = 0;
						this.lastAvailableIndex = 0;
					}

					InterlinkIdentificationUnitDataEntryImpl goldIntlValCont = new InterlinkIdentificationUnitDataEntryImpl(
							setOfResources.get(ri4).getLocalName(),
							setOfResources.get(ri4 + 1).getLocalName(),
							this.candidateInterlinking.getDataset1(),
							this.candidateInterlinking.getDataset2());
					ri4 = ri4 + 2;
					goldIntlValCont.setDifficulty(1);
					goldIntlValCont.setGoldenUnit(true);
					goldenUnits.add(goldIntlValCont);
				}
				/*
				 * MappingIdentificationUnitDataEntryImpl goldMapValCont7 = new
				 * MappingIdentificationUnitDataEntryImpl(r1.getLocalName(),
				 * r2.getLocalName(), this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapValCont7.setDifficulty(1);
				 * goldMapValCont7.setGoldenUnit(true);
				 * goldenUnits.add(goldMapValCont7);
				 * 
				 * if (this.numberOfMinimumGold>=2){
				 * MappingIdentificationUnitDataEntryImpl goldMapIdCont8 = new
				 * MappingIdentificationUnitDataEntryImpl(r3.getLocalName(),
				 * r4.getLocalName(), this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapIdCont8.setDifficulty(1);
				 * goldMapIdCont8.setGoldenUnit(true);
				 * goldenUnits.add(goldMapIdCont8);}
				 * 
				 * if (this.numberOfMinimumGold>=3){
				 * MappingIdentificationUnitDataEntryImpl goldMapIdCont7b = new
				 * MappingIdentificationUnitDataEntryImpl(r5.getLocalName(),
				 * r6.getLocalName(), this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapIdCont7b.setDifficulty(1);
				 * goldMapIdCont7b.setGoldenUnit(true);
				 * goldenUnits.add(goldMapIdCont7b);}
				 * 
				 * if (this.numberOfMinimumGold>=4){
				 * MappingIdentificationUnitDataEntryImpl goldMapIdCont8b = new
				 * MappingIdentificationUnitDataEntryImpl(r7.getLocalName(),
				 * r8.getLocalName(), this.candidateInterlinking.getOntology1(),
				 * this.candidateInterlinking.getOntology2());
				 * goldMapIdCont8b.setDifficulty(1);
				 * goldMapIdCont8b.setGoldenUnit(true);
				 * goldenUnits.add(goldMapIdCont8b);}
				 */

				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return goldenUnits;
	}

	public Interlinking getcandidateInterlinking() {
		return candidateInterlinking;
	}

	public void setcandidateInterlinking(Interlinking candidateInterlinking) {
		this.candidateInterlinking = candidateInterlinking;
	}

	public TypeOfMappingGoal getMappingGoal() {
		return mappingGoal;
	}

	public void setMappingGoal(TypeOfMappingGoal mappingGoal) {
		this.mappingGoal = mappingGoal;
	}

	public boolean isContext() {
		return context;
	}

	public void setContext(boolean context) {
		this.context = context;
	}

}

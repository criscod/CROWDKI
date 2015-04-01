package org.crowdsourcedinterlinking.mgeneration;

import org.crowdsourcedinterlinking.model.*;
import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.ParseLinksInOrder;
import org.crowdsourcedinterlinking.util.TypeOfMicrotask;

import java.io.File;
import java.util.*;

/**
 * @author csarasua
 * Class for generating all the microtasks of type interlinking validations within CrowdFlower. It handles the gold unit generation, the creation of microtask instances containing the candidate links to review.
 */
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

    //Jobs have gold units (links) and real units (links)
    private int numberOfRealUnitsOfJob;
    //CrowdFlower calls assignment or page to the group of microtasks that may appear at once (in one page).
    private int numberOfAssignmentsInJob;

    private int lastAvailableIndex = 0;
    private Set<Interlink> setOfLinksFromRDFFile = new HashSet<Interlink>();

    private Set<Interlink> setOfExperiseLinks = new HashSet<Interlink>();
    private Set<Interlink> goldenUnitsPos = new HashSet<Interlink>();
    private Set<Interlink> goldenUnitsNeg = new HashSet<Interlink>();

    private static int counter = Integer.parseInt(ConfigurationManager
            .getInstance().getJobTitleCounter());

    private int jobNumber;


    public CwdfInterlinkingMicrotaskGeneratorImpl(TypeOfMappingGoal mappingGoal,
                                                  boolean context) {

        this.mappingGoal = mappingGoal; //Validation or Identification
        this.context = context;

    }

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

            List<Interlink> interlinks = null;
            //to force that links are parsed in a particular order
            if (ConfigurationManager.getInstance().getLinksInOrder().equals(ParseLinksInOrder.Lists)) {
                interlinks = new ArrayList<Interlink>(
                        this.candidateInterlinking.getOrderedListOfInterlinks());
            } else {
                interlinks = new ArrayList<Interlink>(
                        this.candidateInterlinking.getSetOfInterLinks());
            }


            int sizeOfSetOfInterlinks = interlinks.size();

            int realUnitsPerAssignment = ConfigurationManager.getInstance()
                    .getUnitsPerAssignment();
            int normalUnitsPerAssignment = realUnitsPerAssignment
                    - ConfigurationManager.getInstance().getGoldPerAssignment();

            // number of jobs that we need to create, taking into account the limit that we set for the size of jobs (in batches or pages).
            this.numberOfJobs = (int) (sizeOfSetOfInterlinks / (ConfigurationManager.getInstance().getBatch() * normalUnitsPerAssignment));
            numberOfJobs = numberOfJobs + 1;



            System.out
                    .println("number of Jobs to create: " + this.numberOfJobs);
            System.out.println("batch: "
                    + ConfigurationManager.getInstance().getBatch()
                    + " sizeInterlinks: " + sizeOfSetOfInterlinks);

            //TODO: refactor and update
            //Set<UnitDataEntryImpl> setOfUnits = new HashSet<UnitDataEntryImpl>();
            List<UnitDataEntryImpl> listOfUnits = new ArrayList<UnitDataEntryImpl>();
            Set<UnitDataEntryImpl> goldenUnits = new HashSet<UnitDataEntryImpl>();





            int unitsPerJob = (int) (ConfigurationManager.getInstance()
                    .getBatch() * (ConfigurationManager.getInstance()
                    .getUnitsPerAssignment() - 1));
            if (numberOfJobs == 1) {
                numberOfJobs = 2;
                unitsPerJob =
                        (int) sizeOfSetOfInterlinks / 2;
            }


            int minIndex = 0;
            int maxIndex = 0;

            for (int job = 1; maxIndex < interlinks.size() - 1; job++) {

                // create jobs with related units inside based on the
                // setOfMappings + gold if needed form goldenMappings


                if ((job % 2 == 1 && maxIndex + unitsPerJob < interlinks

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



                //setOfUnits = new HashSet<UnitDataEntryImpl>();
                goldenUnits = new HashSet<UnitDataEntryImpl>();

                UnitDataEntryImpl sampleUnitData = null;

                // we create and add the golden to the set of units to be
                // included in the job. By default validation HIT without
                // context




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

                }

                if (context
                        && (this.mappingGoal
                        .equals(TypeOfMappingGoal.VALIDATION))) {






                    listOfUnits = new ArrayList<UnitDataEntryImpl>();
                    goldenUnits = new HashSet<UnitDataEntryImpl>();
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

                        if (sampleUnitData == null) {
                            sampleUnitData = intlValCont;
                        }

                    }

                } else {
                    if (!context
                            && (this.mappingGoal
                            .equals(TypeOfMappingGoal.VALIDATION))) {

                        // goldenUnits = addGoldStandardMappings(2,
                        // numberOfRealUnitsOfJob, job);


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

                            if (sampleUnitData == null) {
                                sampleUnitData = intlVal;
                            }

                        }
                    } else {
                        if (context
                                && ((this.mappingGoal
                                .equals(TypeOfMappingGoal.IDENTIFICATIONA)) || (this.mappingGoal
                                .equals(TypeOfMappingGoal.IDENTIFICATIONB)))) {


                            for (int i = minIndex; i <= maxIndex; i++) {

                                Interlink interlink = interlinks.get(i);
                                // create the appropriate units
                                InterlinkIdentificationWithFullContextUnitDataEntryImpl intlIdCont = new InterlinkIdentificationWithFullContextUnitDataEntryImpl(
                                        interlink.getElementA().getURI(), interlink
                                        .getElementB().getURI(),
                                        this.candidateInterlinking.getDataset1(),
                                        this.candidateInterlinking.getDataset2());
                                intlIdCont.setDifficulty(2);
                                intlIdCont.loadInfo();
                                listOfUnits.add(intlIdCont);

                                if (sampleUnitData == null) {
                                    sampleUnitData = intlIdCont;
                                }

                            }


                        } else {
                            if (!context
                                    && ((this.mappingGoal
                                    .equals(TypeOfMappingGoal.IDENTIFICATIONA)) || (this.mappingGoal
                                    .equals(TypeOfMappingGoal.IDENTIFICATIONB)))) {

                                // goldenUnits = addGoldStandardMappings(4,
                                // numberOfRealUnitsOfJob, job);



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

                                    if (sampleUnitData == null) {
                                        sampleUnitData = intlId;
                                    }

                                }
                            }

                        }


                    }

                }

                File fInterlinkingPos = new File("datasets/" + this.candidateInterlinking.getDataset1().getTitle() + this.candidateInterlinking.getDataset2().getTitle() + "_posgoldLinks.nt");
                File fInterlinkingNeg = new File("datasets/" + this.candidateInterlinking.getDataset1().getTitle() + this.candidateInterlinking.getDataset2().getTitle() + "_neggoldLinks.nt");


                goldenUnits = addGoldStandardMappingsFromRDFFile(job, sampleUnitData, this.candidateInterlinking.getDataset1(), this.candidateInterlinking.getDataset2(), fInterlinkingPos, fInterlinkingNeg);


                Iterator<UnitDataEntryImpl> it = goldenUnits.iterator();
                while (it.hasNext()) {
                    UnitDataEntryImpl ud = it.next();
                    if (ud instanceof InterlinkValidationWithFullContextUnitDataEntryImpl) {
                        InterlinkValidationWithFullContextUnitDataEntryImpl iv = (InterlinkValidationWithFullContextUnitDataEntryImpl) ud;

                    } else if (ud instanceof InterlinkIdentificationWithFullContextUnitDataEntryImpl) {
                        InterlinkIdentificationWithFullContextUnitDataEntryImpl ii = (InterlinkIdentificationWithFullContextUnitDataEntryImpl) ud;
                    }
                }
                // the value for this.numberOfMinimumGold should have been set
                // within the specific cases, where the addGOldStandardMappings
                // has been invoked -- that method is the one filling in the
                // info of the minum number of golden units
                // ConfigurationManager.getInstance().setMaxJudgmentsPerWorker(this.numberOfMinimumGold*ConfigurationManager.getInstance().getUnitsPerAssignment());
                ConfigurationManager.getInstance().setMaxJudgmentsPerWorker(
                        this.numberOfMinimumGold * realUnitsPerAssignment);



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


            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private Set<UnitDataEntryImpl> addGoldStandardMappingsFromRDFFile(
            int jobIndex, UnitDataEntryImpl sampleUnitData, Dataset d1, Dataset d2, File fInterlinkingPos, File fInterlinkingNeg) {
        Set<UnitDataEntryImpl> goldenUnits = new HashSet<UnitDataEntryImpl>();


        try {

            int index = this.lastAvailableIndex;


            //to be sure it can still be retrieved and treated as sets
            if (this.setOfLinksFromRDFFile.size() == 0) {

                DirectLinksGeneratorImpl directLinksGenPos = new DirectLinksGeneratorImpl(d1, d2, fInterlinkingPos);
                Interlinking interlinkingPos = directLinksGenPos.generateLinks();
                if (ConfigurationManager.getInstance().getLinksInOrder().equals(ParseLinksInOrder.Lists)) {
                    this.goldenUnitsPos = new HashSet<Interlink>(interlinkingPos.getOrderedListOfInterlinks());

                } else {
                    this.goldenUnitsPos = interlinkingPos.getSetOfInterLinks();
                }

                DirectLinksGeneratorImpl directLinksGenNeg = new DirectLinksGeneratorImpl(d1, d2, fInterlinkingNeg);
                Interlinking interlinkingNeg = directLinksGenNeg.generateLinks();
                if (ConfigurationManager.getInstance().getLinksInOrder().equals(ParseLinksInOrder.Lists)) {
                    this.goldenUnitsNeg = new HashSet<Interlink>(interlinkingNeg.getOrderedListOfInterlinks());
                } else {
                    this.goldenUnitsNeg = interlinkingNeg.getSetOfInterLinks();
                }

                this.setOfLinksFromRDFFile.addAll(this.goldenUnitsPos);
                this.setOfLinksFromRDFFile.addAll(this.goldenUnitsNeg);

                List<Interlink> listAll = new ArrayList<Interlink>(this.setOfLinksFromRDFFile);
                Collections.shuffle(listAll);
                this.setOfLinksFromRDFFile = new HashSet<Interlink>(listAll);

            }

            int numGoldenUnitsAvailable = setOfLinksFromRDFFile.size();


            //update- should not be repeated
            Iterator<Interlink> itLinks = setOfLinksFromRDFFile.iterator();
            ArrayList<Interlink> listLinks = new ArrayList<Interlink>();

            while (itLinks.hasNext()) {
                listLinks.add(itLinks.next());
            }


            Interlink goldenLinkI = null;

            //int ri = r;
            for (int i = 0; i <= this.numberOfMinimumGold - 1; i++) {

                System.out.println(" ");
                if (index > (numGoldenUnitsAvailable - 1))

                {
                    index = 0;
                    this.lastAvailableIndex = 0;
                }

                goldenLinkI = listLinks.get(index);


                if (sampleUnitData instanceof InterlinkValidationWithFullContextUnitDataEntryImpl) {


                    InterlinkValidationWithFullContextUnitDataEntryImpl sampleIntValUnit = (InterlinkValidationWithFullContextUnitDataEntryImpl) sampleUnitData;
                    List<FeatureTextValue> listOfFeaturesA = sampleIntValUnit.getListOfFeaturesA();
                    List<FeatureTextValue> listOfFeaturesB = sampleIntValUnit.getListOfFeaturesB();//just as reference list of features


                    InterlinkValidationWithFullContextUnitDataEntryImpl goldIntValUnit = new InterlinkValidationWithFullContextUnitDataEntryImpl(goldenLinkI.getElementA().getURI(), goldenLinkI.getElementB().getURI(), goldenLinkI.getRelation().getURI(), d1, d2);
                    goldIntValUnit.loadInfo();


                    goldIntValUnit.setDifficulty(1);
                    goldIntValUnit.setGoldenUnit(true);
                    goldenUnits.add(goldIntValUnit);
                    index = index + 1;

                } else if (sampleUnitData instanceof InterlinkIdentificationWithFullContextUnitDataEntryImpl) {


                    InterlinkIdentificationWithFullContextUnitDataEntryImpl sampleIntIdentifUnit = (InterlinkIdentificationWithFullContextUnitDataEntryImpl) sampleUnitData;
                    List<FeatureTextValue> listOfFeaturesA = sampleIntIdentifUnit.getListOfFeaturesA(); //just as reference list of features
                    List<FeatureTextValue> listOfFeaturesB = sampleIntIdentifUnit.getListOfFeaturesB();//just as reference list of features

                    InterlinkIdentificationWithFullContextUnitDataEntryImpl goldIntIdentifUnit = new InterlinkIdentificationWithFullContextUnitDataEntryImpl(goldenLinkI.getElementA().getURI(), goldenLinkI.getElementB().getURI(), d1, d2);
                    goldIntIdentifUnit.loadInfo();

                    goldIntIdentifUnit.setDifficulty(1);
                    goldIntIdentifUnit.setGoldenUnit(true);
                    goldenUnits.add(goldIntIdentifUnit);

                    index = index + 1;


                }


            }
            this.lastAvailableIndex = index + 1;


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

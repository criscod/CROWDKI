package org.crowdsourcedinterlinking.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;
/**
 * @author csarasua
 */
public class ConfigurationManager {

    private Configuration config;

    static private ConfigurationManager singleton;

    private ConfigurationManager() {

        try {

            String workingDir = System.getProperty("user.dir");
            File f = new File(workingDir + "/config.properties");
            config = new PropertiesConfiguration(f);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static synchronized ConfigurationManager getInstance() {

        if (singleton == null) {

            singleton = new ConfigurationManager();

        }
        return singleton;
    }

    public int getExpertiseCount() {
        return config.getInt("exp.expertisecount");
    }

    public void setLinksInOrder(ParseLinksInOrder setting) {
        if (setting.equals(ParseLinksInOrder.Lists)) {
            this.config.setProperty("exp.orderedlinks", "lists");

        } else {
            this.config.setProperty("exp.orderedlinks", "sets");

        }

    }

    public ParseLinksInOrder getLinksInOrder() {
        String order = this.config.getString("exp.orderedlinks");
        ParseLinksInOrder result = null;
        if (order.equals("lists")) {
            result = ParseLinksInOrder.Lists;
        } else if (order.equals("sets")) {
            result = ParseLinksInOrder.Sets;
        }
        return result;
    }


    public void test() {
        // System.out.println(config.getInt("job.assignmentspermicrotask"));
    }

	/*
     * public String getAlignmentResultFile() { return
	 * this.config.getString("alignment.resultfile"); }
	 */

    public String getMySqlURL() {
        return this.config.getString("db.url");
    }

    public String getMySqlUser() {
        return this.config.getString("db.user");
    }

    public String getMySqlPassword() {
        return this.config.getString("db.password");
    }

    public String getOntologiesDirectory() {
        return this.config.getString("alignment.ontodirectory");
    }

    public String getReferenceAlignmentsDirectory() {
        return this.config.getString("alignment.refalignmentsdirectory");
    }

    public String getCrowdAlignmentsDirectory() {
        return this.config.getString("alignment.cwdalignmentsdirectory");
    }

    public String getCrowdInterlinkingsDirectory() {
        return this.config.getString("interlinking.crowdinterlinkingdirectory");
    }

    public String getReferenceBaseFileName() {
        return this.config.getString("alignment.refbasefilename");
    }

    public String getCrowdBaseFileName() {
        return this.config.getString("alignment.cwdbasefilename");
    }

    public String getEvaluationsDirectory() {
        return this.config.getString("alignment.evaldirectoy");
    }

    public String getEvaluationBaseFileName() {
        return this.config.getString("alignment.evalbasefilename");
    }

    public String getResultsJudgmentsCsvFileDirectory() {
        return this.config.getString("results.judgmentscsvdirectory");
    }

    public String getResultsJudgmentsCsvBaseFileName() {
        return this.config.getString("results.judgmentscsvbasefilename");
    }

    public String getTransformationsDirectory() {
        return this.config.getString("alignment.transformdirectory");
    }

    public String getTransformationsBaseFileName() {
        return this.config.getString("alignment.transformfilename");
    }

    public String getCwdfKey() {
        return this.config.getString("cwdf.key");
    }

    public double getBatch() {
        return this.config.getDouble("job.batch");
    }

    public String getMappingValidationFile() {
        return this.config.getString("cml.filemappingvalidation");
    }

    public String getMappingValidationTurnedFile() {
        return this.config.getString("cml.filemappingvalidationturned");
    }

    public String getMappingValidationFullContextFile() {
        return this.config.getString("cml.filemappingvalidationfullcontext");
    }

    public String getMappingValidationFullContextTurnedFile() {
        return this.config
                .getString("cml.filemappingvalidationfullcontextturned");
    }

    public String getMappingIdentificationAFile() {
        return this.config.getString("cml.filemappingidentificationA");
    }

    public String getMappingIdentificationATurnedFile() {
        return this.config.getString("cml.filemappingidentificationAturned");
    }

    public String getMappingIdentificationAFullContextFile() {
        return this.config
                .getString("cml.filemappingidentificationAfullcontext");
    }

    public String getMappingIdentificationAFullContextTurnedFile() {
        return this.config
                .getString("cml.filemappingidentificationAfullcontextturned");
    }

    public String getMappingIdentificationBFile() {
        return this.config.getString("cml.filemappingidentificationB");
    }

    public String getMappingIdentificationBTurnedFile() {
        return this.config.getString("cml.filemappingidentificationBturned");
    }

    public String getMappingIdentificationBFullContextFile() {
        return this.config
                .getString("cml.filemappingidentificationBfullcontext");
    }

    public String getMappingIdentificationBFullContextTurnedFile() {
        return this.config
                .getString("cml.filemappingidentificationBfullcontextturned");
    }

    public String getCmlVerifwordNotTurnedString() {
        return this.config.getString("cml.verifwordnotturnedstring");
    }

    public String getCmlVerifwordTurnedString() {
        return this.config.getString("cml.verifwordturnedstring");
    }


    public String getCmlVerifwordNotTurnedStringInterlinking() {
        return this.config.getString("cml.verifwordnotturnedstringinterlinking");
    }

    public String getCmlVerifwordTurnedStringInterlinking() {
        return this.config.getString("cml.verifwordturnedstringinterlinking");
    }

    public int getjudgmentsPerUnitTwoOptions() {
        return this.config.getInt("job.judgmentsPerUnitTwoOptions");
    }

    public int getjudgmentsPerUnitFourOptions() {
        return this.config.getInt("job.judgmentsPerUnitFourOptions");
    }

    public int getjudgmentsPerUnitFourOptionsChanged() {
        return this.config.getInt("job.judgmentsPerUnitFourOptionsChanged");
    }

    public int getMaxJudgmentsPerWorker() {
        return this.config.getInt("job.maxJudgmentsPerWorker");
    }

    public void setMaxJudgmentsPerWorker(int num) {
        this.config.setProperty("job.maxJudgmentsPerWorker", num);
    }

    public int getPagerPerAssignment() {
        return this.config.getInt("job.pagesPerAssignment");
    }

    public int getUnitsPerAssignment() {
        return this.config.getInt("job.unitsPerAssignment");
    }

    public int getGoldPerAssignment() {
        return this.config.getInt("job.goldPerAssignment");
    }

    public int getCwdfRestrictionGolden() {

        return this.config.getInt("cwdf.restrictiongolden");
    }

    public String getCsvValidationContext() {
        return this.config.getString("unit.csvvalidationcontext");
    }

    public String getCsvValidation() {
        return this.config.getString("unit.csvvalidation");
    }

    public String getCsvIdentificationContext() {
        return this.config.getString("unit.csvidentificationcontext");
    }

    public String getCsvIdentification() {
        return this.config.getString("unit.csvidentification");
    }

    public String getCwdfBaseURL() {
        return this.config.getString("cwdf.baseurl");
    }

    public String getCwdfAuthenticationKey() {
        return this.config.getString("cwdf.key");
    }

    public String getCwdPostJobUrl() {
        return this.config.getString("cwdf.postjoburl");
    }

    public String getCwdfPostJobAccept() {
        return this.config.getString("cwdf.postjobaccept");
    }

    public String getCwdfPostJobContentType() {
        return this.config.getString("cwdf.postjobcontenttype");
    }

    public String getCwdPostUploadUnitsJobUrl() {
        return this.config.getString("cwdf.postuploadunitstojoburl");
    }

    public String getCwdfPostUploadUnitsJobAccept() {
        return this.config.getString("cwdf.postuploadunitstojobaccept");
    }

    public String getCwdfPostUploadUnitsJobContentType() {
        return this.config.getString("cwdf.postuploadunitstojobcontenttype");
    }

    public String getCwdPostOrderToJobUrl() {
        return this.config.getString("cwdf.postordertojoburl");
    }

    public String getCwdfPostOrderToJobAccept() {
        return this.config.getString("cwdf.postordertojobaccept");
    }

    public String getCwdfPostOrderToJobContentType() {
        return this.config.getString("cwdf.postordertojobcontenttype");
    }

    public String getCwdfPostUnitUrl() {
        return this.config.getString("cwdf.postuniturl");
    }

    public String getCwdfPostUnitAccept() {
        return this.config.getString("cwdf.postunitaccept");
    }

    public String getCwdfPostUnitContentType() {
        return this.config.getString("cwdf.postunitcontenttype");
    }

    public String getCwdfGetJudgmentsZipUrl() {
        return this.config.getString("cwdf.getjudgmentszipurl");
    }

    public String getCwdfGetJudgmentsZipAccept() {
        return this.config.getString("cwdf.getjudgmentsziaccept");
    }

    public String getJobsToControl() {
        return this.config.getString("jobs.idstocontrol");
    }

    public int getControlTime() {
        return this.config.getInt("jobs.controlmilliseconds");
    }

    public void addJobToControl(String idJob) {

        String current = this.config.getString("jobs.idstocontrol");
        if (current.equals("0")) {
            // it is the first one
            this.config.setProperty("jobs.idstocontrol", idJob);
        } else {
            // concatenate
            this.config.setProperty("jobs.idstocontrol", current + "," + idJob);
        }

    }

    public String getGoldMessageValidation() {
        return this.config.getString("gold.messagevalidation");
    }

    public String getGoldNegMessageValidation() {
        return this.config.getString("gold.negmessagevalidation");
    }

    public String getGoldMessageIdentification() {
        return this.config.getString("gold.messageidentification");
    }

    public String getGoldNegMessageIdentification() {
        return this.config.getString("gold.negmessageidentification");
    }

    public int getCentsPerPager() {
        return this.config.getInt("job.centsperpage");
    }

    public String getListOfOnlineJobsToAnalyseFile() {
        return this.config.getString("results.listofjobs");
    }

    public String getCurrentTrackFile() {
        return this.config.getString("results.currenttrackfile");
    }

    public void setCurrentTrackFile(String trackFilePath) {
        this.config.setProperty("results.currenttrackfile", trackFilePath);
    }

    public String getTrackDirectory() {
        return this.config.getString("results.trackdirectory");
    }

    public String getCwdfGetJobUnits() {
        return this.config.getString("cwdf.getjobunits");
    }

    public String getCwdfGetJobUnitsAccept() {
        return this.config.getString("cwdf.getjobunitsaccept");
    }

    public String getCwdfGetUnitURL() {
        return this.config.getString("cwdf.getuniturl");
    }

    public String getCwdfGetUnitAccept() {
        return this.config.getString("cwdf.getunitaccept");
    }

    public String getCwdfPutJobURL() {
        return this.config.getString("cwdf.putjoburl");
    }

    public String getCwdfPutJobAccept() {
        return this.config.getString("cwdf.putjobaccept");
    }

    public String getCwdfPutJobContentType() {
        return this.config.getString("cwdf.putjobcontenttype");
    }

    public String getCwdfGetJobURL() {
        return this.config.getString("cwdf.getjoburl");
    }

    public String getCwdfGetJobAccept() {
        return this.config.getString("cwdf.getjobaccept");
    }

    public String getNameFormElementToWatch() {
        return this.config.getString("results.nameformelementtowatch");
    }

    public String getResponseValidation() {
        return this.config.getString("results.responsevalidation");
    }

    public String getResponseIdentificationABSame() {
        return this.config.getString("results.responseidentificationabsame");
    }

    public String getResponseIdentificationAGeneral() {
        return this.config.getString("results.responseidentificationageneral");
    }

    public String getResponseIdentificationASpecific() {
        return this.config.getString("results.repsonseidentificationaspecific");
    }

    public String getCmlValidationRadioLabelSame() {
        return this.config.getString("cml.validationradiolabelsame");
    }

    public String getCmlValidationInterlinkingRadioLabelSame() {
        return this.config.getString("cml.validationinterlinkingradiolabelsame");
    }

    public String getCmlValidationRadioLabelGeneral() {
        return this.config.getString("cml.validationradiolabelgeneral");
    }

    public String getCmlValidationRadioLabelSpecific() {
        return this.config.getString("cml.validationradiolabelspecific");
    }

    public String getJobTitleCounter() {
        return this.config.getString("cwdf.jobtitlecounter");
    }

    public String getAlignmentElements() {
        return this.config.getString("alignment.elements");
    }

    public String getAlignmentParsingFormat() {
        return this.config.getString("alignment.parsingformat");
    }

    public void setAlignmentParsingFormat(String format) {
        this.config.setProperty("alignment.parsingformat", format);
    }

    public String getListOfFeaturesA() {
        return this.config.getString("listoffeaturenamesa");
    }

    public String getListOfFeaturesB() {
        return this.config.getString("listoffeaturenamesb");
    }

    public String getListOfMessagesFeaturesAForUI() {
        return this.config.getString("messagesfeaturesa");
    }

    public String getListOfMessagesFeaturesBForUI() {
        return this.config.getString("messagesfeaturesb");
    }

    public String getValidationRadioLabelByLocalName(String localName) {
        return this.config.getString("cml.validationinterlinkingradiolabel" + localName);
    }

    public String getIdentificationRadioLabel1() {
        return this.config.getString("cml.identificationradiolabel1");
    }

    public String getIdentificationRadioLabel2() {
        return this.config.getString("cml.identificationradiolabel2");
    }
	/* not needed
	public String getInterlinkValidationFile() {
		return this.config.getString("cml.fileinterlinkvalidation");
	}

	public String getInterlinkValidationTurnedFile() {
		return this.config.getString("cml.fileinterlinkvalidationturned");
	}
	*/

    public String getInterlinkValidationFullContextFile() {
        return this.config.getString("cml.fileinterlinkvalidationfullcontext");
    }

    public String getInterlinkValidationFullContextTurnedFile() {
        return this.config
                .getString("cml.fileinterlinkvalidationfullcontextturned");
    }

    /*
      not needed
     public String getInterlinkIdentificationFile() {
        return this.config.getString("cml.fileinterlinkidentification");
    }

    public String getInterlinkIdentificationTurnedFile() {
        return this.config.getString("cml.fileinterlinkidentificationturned");
    }
*/
    public String getInterlinkIdentificationFullContextFile() {
        return this.config
                .getString("cml.fileinterlinkidentificationfullcontext");
    }

    public String getInterlinkDefaultProperty() {
        return this.config
                .getString("interlinking.defaultproperty");
    }

    public String getInterlinkIdentificationFullContextTurnedFile() {
        return this.config
                .getString("cml.fileinterlinkidentificationfullcontextturned");
    }

    public String getGoldenUnitsFilePath() {
        return this.config.getString("gold.unitsfile");
    }

    public int getMaxGoldenUnitsAvaible() {
        return this.config.getInt("gold.maxunitsavailable");
    }

    public String getG1() {
        return this.config.getString("g1");
    }

    public int getNumberOfLinksForLearning() {
        return this.config.getInt("workers.numberoflinksforlearning");
    }

    public String getJobTitleBasis(TypeOfMicrotask type) {
        String result = null;

        if (type.equals(TypeOfMicrotask.Validation)) {
            result = this.config.getString("cwdf.jobtitlebasisvalidation");
        } else if (type.equals(TypeOfMicrotask.Validation)) {
            result = this.config.getString("cwdf.jobtitlebasisidentification");
        }
        return result;
    }


    public String getJobInstructions(TypeOfMicrotask type) {
        String result = null;
        if (type.equals(TypeOfMicrotask.Validation)) {
            result = this.config.getString("cwdf.jobinstructionsvalidation");
        } else if (type.equals(TypeOfMicrotask.Validation)) {
            result = this.config.getString("cwdf.jobinstructionsidentification");
        }
        return result;

    }

}

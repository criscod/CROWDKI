package org.crowdsourcedinterlinking.mpublication;

import org.crowdsourcedinterlinking.util.ConfigurationManager;

public class CwdfService extends Service {

	private String authenticationKey;

	/*
	 * private String getjoburl; private String postjoburl;
	 * 
	 * private String getuniturl; private String postuniturl;
	 * 
	 * private String getjudgmenturl; private String postjudgmenturl;
	 * 
	 * private String getorderurl; private String postorderurl;
	 */

	public CwdfService() {
		this.baseURL = ConfigurationManager.getInstance().getCwdfBaseURL();

		this.authenticationKey = ConfigurationManager.getInstance()
				.getCwdfKey();
	}

	public String getCreateJobURL() {
		return ConfigurationManager.getInstance().getCwdfBaseURL()
				+ ConfigurationManager.getInstance().getCwdPostJobUrl()
				+ "?key=" + ConfigurationManager.getInstance().getCwdfKey();
	}

	public String getCreateJobAccept() {
		return ConfigurationManager.getInstance().getCwdfPostJobAccept();
	}

	public String getCreateJobContentType() {
		return ConfigurationManager.getInstance().getCwdfPostJobContentType();
	}

	public String getUploadUnitsToJobURL(String jobId) {

		return ConfigurationManager.getInstance().getCwdfBaseURL()
				+ ConfigurationManager.getInstance().getCwdPostJobUrl()
				+ "/"
				+ jobId
				+ ConfigurationManager.getInstance()
						.getCwdPostUploadUnitsJobUrl() + "?key="
				+ ConfigurationManager.getInstance().getCwdfKey();
	}

	public String getUploadUnitsToJobAccept() {
		return ConfigurationManager.getInstance()
				.getCwdfPostUploadUnitsJobAccept();
	}

	public String getUploadUnitsToJobContentType() {
		return ConfigurationManager.getInstance()
				.getCwdfPostUploadUnitsJobContentType();
	}

	public String getOrderJobURL(String jobId) {

		return ConfigurationManager.getInstance().getCwdfBaseURL()
				+ ConfigurationManager.getInstance().getCwdPostJobUrl() + "/"
				+ jobId
				+ ConfigurationManager.getInstance().getCwdPostOrderToJobUrl()
				+ "?key=" + ConfigurationManager.getInstance().getCwdfKey();
	}

	public String getOrderJobAccept() {
		return ConfigurationManager.getInstance().getCwdfPostOrderToJobAccept();
	}

	public String getOrderJobContentType() {
		return ConfigurationManager.getInstance()
				.getCwdfPostOrderToJobContentType();
	}

	public String getCreateUnitInJobURL(String jobId) {
		return ConfigurationManager.getInstance().getCwdfBaseURL()
				+ ConfigurationManager.getInstance().getCwdPostJobUrl() + "/"
				+ jobId
				+ ConfigurationManager.getInstance().getCwdfPostUnitUrl()
				+ "?key=" + ConfigurationManager.getInstance().getCwdfKey();

	}

	public String getCreateUnitInJobAccept() {
		return ConfigurationManager.getInstance().getCwdfPostUnitAccept();
	}

	public String getCreateUnitInJobContentType() {
		return ConfigurationManager.getInstance().getCwdfPostUnitContentType();
	}

	public String getJudgmentsOfAJobURL(String jobId) {
		return ConfigurationManager.getInstance().getCwdfBaseURL()
				+ ConfigurationManager.getInstance().getCwdPostJobUrl()
				+ "/"
				+ jobId
				+ ConfigurationManager.getInstance()
						.getCwdfGetJudgmentsZipUrl() + "?key="
				+ ConfigurationManager.getInstance().getCwdfKey();

	}

	public String getJudgmentsOfAJobAccept() {
		return ConfigurationManager.getInstance()
				.getCwdfGetJudgmentsZipAccept();
	}

	public String getReadUnitsOfJobURL(String jobId) {
		return ConfigurationManager.getInstance().getCwdfBaseURL()
				+ ConfigurationManager.getInstance().getCwdPostJobUrl() + "/"
				+ jobId
				+ ConfigurationManager.getInstance().getCwdfGetJobUnits()
				+ "?key=" + ConfigurationManager.getInstance().getCwdfKey();
	}

	public String getReadUnitsOfJobAccept() {
		return ConfigurationManager.getInstance().getCwdfGetJobUnitsAccept();
	}

	public String getReadUnitURL(String jobId, String unitId) {
		return ConfigurationManager.getInstance().getCwdfBaseURL()
				+ ConfigurationManager.getInstance().getCwdPostJobUrl() + "/"
				+ jobId
				+ ConfigurationManager.getInstance().getCwdfGetUnitURL() + "/"
				+ unitId + "?key="
				+ ConfigurationManager.getInstance().getCwdfKey();
	}

	public String getReadUnitAccept() {
		return ConfigurationManager.getInstance().getCwdfGetUnitAccept();
	}

	public String getChangeJobURL(String jobId) {
		return ConfigurationManager.getInstance().getCwdfBaseURL()
				+ ConfigurationManager.getInstance().getCwdfPutJobURL() + "/"
				+ jobId + ".json?key="
				+ ConfigurationManager.getInstance().getCwdfKey();
	}

	public String getChangeJobAccept() {
		return ConfigurationManager.getInstance().getCwdfPutJobAccept();
	}

	public String getChangeJobContentType() {
		return ConfigurationManager.getInstance().getCwdfPutJobContentType();
	}

	public String getReadJobURL(String jobId) {
		return ConfigurationManager.getInstance().getCwdfBaseURL()
				+ ConfigurationManager.getInstance().getCwdfGetJobURL() + "/"
				+ jobId + "?key="
				+ ConfigurationManager.getInstance().getCwdfKey();
	}

	public String getReadJobAccept() {
		return ConfigurationManager.getInstance().getCwdfGetJobAccept();
	}
}

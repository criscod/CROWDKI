package org.crowdsourcedinterlinking.model;

public class JudgmentResponseImpl {

	// READ-WRITE CrowdFLower attributes
	private String webhookSentAt;
	private boolean reviewed;
	private boolean missed;
	private boolean tainted;

	private String country;
	private String region;
	private String city;

	private boolean golden;
	private String unitState;

	// READ-ONLY
	private String startedAt;
	private String createdAt;
	// private String jobId;
	private String workerId;
	// private String unitId;
	private String judgment;
	private String externalType;
	private String rejected;
	private String id;
	private String IP;
	private String data;

	public JudgmentResponseImpl() {
	}

	public String getWebhookSentAt() {
		return webhookSentAt;
	}

	public void setWebhookSentAt(String webhookSentAt) {
		this.webhookSentAt = webhookSentAt;
	}

	public boolean isReviewed() {
		return reviewed;
	}

	public void setReviewed(boolean reviewed) {
		this.reviewed = reviewed;
	}

	public boolean isMissed() {
		return missed;
	}

	public void setMissed(boolean missed) {
		this.missed = missed;
	}

	public boolean isTainted() {
		return tainted;
	}

	public void setTainted(boolean tainted) {
		this.tainted = tainted;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public boolean isGolden() {
		return golden;
	}

	public void setGolden(boolean golden) {
		this.golden = golden;
	}

	public String getUnitState() {
		return unitState;
	}

	public void setUnitState(String unitState) {
		this.unitState = unitState;
	}

	public String getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(String startedAt) {
		this.startedAt = startedAt;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}

	public String getJudgment() {
		return judgment;
	}

	public void setJudgment(String judgment) {
		this.judgment = judgment;
	}

	public String getExternalType() {
		return externalType;
	}

	public void setExternalType(String externalType) {
		this.externalType = externalType;
	}

	public String getRejected() {
		return rejected;
	}

	public void setRejected(String rejected) {
		this.rejected = rejected;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}

package org.crowdsourcedinterlinking.model;

import java.util.Set;

public abstract class UnitDataEntryImpl {

	// READ-WRITE CrowdFlower attributes
	// private String jobId;

	private String id;
	private int missedCount;
	private String data;

	private int difficulty; // 1- easy ... 5 difficult
	private String state;
	private double agreement;

	// READ-ONLY CrowdFlower
	private String updatedAt;
	private String createdAt;
	private int judgmentsCount;

	// own attributes
	private Set<JudgmentResponseImpl> setOfJudgments;
	private boolean goldenUnit;

	public UnitDataEntryImpl() {

	}

	public abstract void loadInfo();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getMissedCount() {
		return missedCount;
	}

	public void setMissedCount(int missedCount) {
		this.missedCount = missedCount;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public double getAgreement() {
		return agreement;
	}

	public void setAgreement(double agreement) {
		this.agreement = agreement;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public int getJudgmentsCount() {
		return judgmentsCount;
	}

	public void setJudgmentsCount(int judgmentsCount) {
		this.judgmentsCount = judgmentsCount;
	}

	public Set<JudgmentResponseImpl> getSetOfJudgements() {
		return setOfJudgments;
	}

	public void setSetOfJudgements(Set<JudgmentResponseImpl> setOfJudgements) {
		this.setOfJudgments = setOfJudgements;
	}

	public Set<JudgmentResponseImpl> getSetOfJudgments() {
		return setOfJudgments;
	}

	public void setSetOfJudgments(Set<JudgmentResponseImpl> setOfJudgments) {
		this.setOfJudgments = setOfJudgments;
	}

	public boolean isGoldenUnit() {
		return goldenUnit;
	}

	public void setGoldenUnit(boolean goldenUnit) {
		this.goldenUnit = goldenUnit;
	}

}

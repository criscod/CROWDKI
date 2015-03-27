package org.crowdsourcedinterlinking.model;

import java.util.List;
import java.util.Set;
/**
 * @author csarasua
 */
public abstract class JobMicrotaskImpl implements Microtask {

    // READ-WRITE CrowdFlower attributes

    // attributes that I might not use
    private boolean autoOrder;
    private double autorOrderThreshold;
    private int autorOrderTimeout;
    private String confidenceFields;
    private String css;
    private String customKey;
    private String js;
    private int minUnitConfidence;
    private String options;
    private String problem;

    // will use for sure
    private String instructions;
    private String title;
    private String cml;

    private String excludedCountries;
    private String language;
    private int goldPerAssignment;

    private int maxJudgmentsPerUnit; // ?
    private int judgmentsPerUnit; // CONFIGURED - 3
    private int maxJudgmentsPerWorker; // CONFIGURED - 1
    private int pagesPerAssignment; // CONFIGURED - 2
    private int unitsPerAssignment; // CONFIGURED - 2

    private boolean sendJudgmentsWebhook;
    private String state;
    private String webhookUri;

    // READ-ONLY CrowdFlower
    private String id;
    private boolean completed;

    private boolean gold;
    private int goldsCount;

    private int judgmentsCount;
    private int unitsCount;

    private String completedAt; // it could also be Date
    private String createdAt; // it could also be Date
    private String updatedAt; // it could also be Date

    // own attributes (JOB-->UNITS-->JUDGMENTS) (JOB-->ORDER)

    protected Set<UnitDataEntryImpl> setOfUnits;
    protected List<UnitDataEntryImpl> listOfUnits;
    protected Set<UnitDataEntryImpl> goldenUnits;
    protected Set<Interlink> goldenUnitsSourceNeg;
    protected Set<Interlink> goldenUnitsSourcePos;

    protected OrderFormalRequestImpl order;

    protected String pathOfCSVfile;

    public abstract void createUI();

    public abstract void serialiseUnitsIntoCVSFile();

    public boolean isAutoOrder() {
        return autoOrder;
    }

    public void setAutoOrder(boolean autoOrder) {
        this.autoOrder = autoOrder;
    }

    public double getAutorOrderThreshold() {
        return autorOrderThreshold;
    }

    public void setAutorOrderThreshold(double autorOrderThreshold) {
        this.autorOrderThreshold = autorOrderThreshold;
    }

    public int getAutorOrderTimeout() {
        return autorOrderTimeout;
    }

    public void setAutorOrderTimeout(int autorOrderTimeout) {
        this.autorOrderTimeout = autorOrderTimeout;
    }

    public String getConfidenceFields() {
        return confidenceFields;
    }

    public void setConfidenceFields(String confidenceFields) {
        this.confidenceFields = confidenceFields;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public String getCustomKey() {
        return customKey;
    }

    public void setCustomKey(String customKey) {
        this.customKey = customKey;
    }

    public String getJs() {
        return js;
    }

    public void setJs(String js) {
        this.js = js;
    }

    public int getMinUnitConfidence() {
        return minUnitConfidence;
    }

    public void setMinUnitConfidence(int minUnitConfidence) {
        this.minUnitConfidence = minUnitConfidence;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCml() {
        return cml;
    }

    public void setCml(String cml) {
        this.cml = cml;
    }

    public String getExcludedCountries() {
        return excludedCountries;
    }

    public void setExcludedCountries(String excludedCountries) {
        this.excludedCountries = excludedCountries;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getGoldPerAssignment() {
        return goldPerAssignment;
    }

    public void setGoldPerAssignment(int goldPerAssignment) {
        this.goldPerAssignment = goldPerAssignment;
    }

    public int getMaxJudgmentsPerUnit() {
        return maxJudgmentsPerUnit;
    }

    public void setMaxJudgmentsPerUnit(int maxJudgmentsPerUnit) {
        this.maxJudgmentsPerUnit = maxJudgmentsPerUnit;
    }

    public int getMaxJudgmentsPerWorker() {
        return maxJudgmentsPerWorker;
    }

    public void setMaxJudgmentsPerWorker(int maxJudgmentsPerWorker) {
        this.maxJudgmentsPerWorker = maxJudgmentsPerWorker;
    }

    public int getPagesPerAssignment() {
        return pagesPerAssignment;
    }

    public void setPagesPerAssignment(int pagesPerAssignment) {
        this.pagesPerAssignment = pagesPerAssignment;
    }

    public int getUnitsPerAssignment() {
        return unitsPerAssignment;
    }

    public void setUnitsPerAssignment(int unitsPerAssignment) {
        this.unitsPerAssignment = unitsPerAssignment;
    }

    public int getJudgmentsPerUnit() {
        return judgmentsPerUnit;
    }

    public void setJudgmentsPerUnit(int judgmentsPerUnit) {
        this.judgmentsPerUnit = judgmentsPerUnit;
    }

    public boolean isSendJudgmentsWebhook() {
        return sendJudgmentsWebhook;
    }

    public void setSendJudgmentsWebhook(boolean sendJudgmentsWebhook) {
        this.sendJudgmentsWebhook = sendJudgmentsWebhook;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getWebhookUri() {
        return webhookUri;
    }

    public void setWebhookUri(String webhookUri) {
        this.webhookUri = webhookUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isGold() {
        return gold;
    }

    public void setGold(boolean gold) {
        this.gold = gold;
    }

    public int getGoldsCount() {
        return goldsCount;
    }

    public void setGoldsCount(int goldsCount) {
        this.goldsCount = goldsCount;
    }

    public int getJudgmentsCount() {
        return judgmentsCount;
    }

    public void setJudgmentsCount(int judgmentsCount) {
        this.judgmentsCount = judgmentsCount;
    }

    public int getUnitsCount() {
        return unitsCount;
    }

    public void setUnitsCount(int unitsCount) {
        this.unitsCount = unitsCount;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<UnitDataEntryImpl> getSetOfUnits() {
        return setOfUnits;
    }

    public void setSetOfUnits(Set<UnitDataEntryImpl> setOfUnits) {
        this.setOfUnits = setOfUnits;
    }

    public OrderFormalRequestImpl getOrder() {
        return order;
    }

    public void setOrder(OrderFormalRequestImpl order) {
        this.order = order;
    }

    public String getPathOfCSVfile() {
        return pathOfCSVfile;
    }

    public void setPathOfCSVfile(String pathOfCSVfile) {
        this.pathOfCSVfile = pathOfCSVfile;
    }

    public Set<UnitDataEntryImpl> getGoldenUnits() {
        return goldenUnits;
    }

    public void setGoldenUnits(Set<UnitDataEntryImpl> goldenUnits) {
        this.goldenUnits = goldenUnits;
    }

    //TODO: interlink here no independence

    public Set<Interlink> getGoldenUnitsSourceNeg() {
        return goldenUnitsSourceNeg;
    }

    public void setGoldenUnitsSourceNeg(Set<Interlink> goldenUnitsSourceNeg) {
        this.goldenUnitsSourceNeg = goldenUnitsSourceNeg;
    }

    public Set<Interlink> getGoldenUnitsSourcePos() {
        return goldenUnitsSourcePos;
    }

    public void setGoldenUnitsSourcePos(Set<Interlink> goldenUnitsSourcePos) {
        this.goldenUnitsSourcePos = goldenUnitsSourcePos;
    }

    public List<UnitDataEntryImpl> getListOfUnits() {
        return listOfUnits;
    }

    public void setListOfUnits(List<UnitDataEntryImpl> listOfUnits) {
        this.listOfUnits = listOfUnits;
    }
}
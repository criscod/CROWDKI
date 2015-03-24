package org.crowdsourcedinterlinking.model;

public class FeatureTextValue {
	
	private String feature; 
	private String messageText; // this is for the UI
	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	private String value; 
	
	public FeatureTextValue(String feat, String messageText, String val)
	{
		this.feature = feat;
		this.messageText=messageText;
		this.value = val;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}


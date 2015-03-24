package org.crowdsourcedinterlinking.model;

public class OrderFormalRequestImpl {

	// READ-WRITE CrowdFlower
	// private String jobId;

	// READ-ONLY CrowdFlower
	private String id;
	private String type;
	private String meta;
	private String createdAt;
	private String updatedAt;
	private String userId;

	public OrderFormalRequestImpl() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMeta() {
		return meta;
	}

	public void setMeta(String meta) {
		this.meta = meta;
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}

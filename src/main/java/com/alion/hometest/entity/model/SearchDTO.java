package com.alion.hometest.entity.model;

import java.io.Serializable;

public class SearchDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String projectId;
	private String fromWeek;
	private String toWeek;
	private String types;
	private String status;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getFromWeek() {
		return fromWeek;
	}

	public void setFromWeek(String fromWeek) {
		this.fromWeek = fromWeek;
	}

	public String getToWeek() {
		return toWeek;
	}

	public void setToWeek(String toWeek) {
		this.toWeek = toWeek;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}

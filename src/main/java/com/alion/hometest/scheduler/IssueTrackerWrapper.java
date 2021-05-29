package com.alion.hometest.scheduler;

import java.util.List;

public class IssueTrackerWrapper {

	private String project_id;
	private List<IssueTracker> issues;

	public String getProject_id() {
		return project_id;
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public List<IssueTracker> getIssues() {
		return issues;
	}

	public void setIssues(List<IssueTracker> issues) {
		this.issues = issues;
	}
	
}

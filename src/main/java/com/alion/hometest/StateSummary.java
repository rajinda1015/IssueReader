package com.alion.hometest.entity.model;

import java.io.Serializable;
import java.util.List;

import com.alion.hometest.util.STATE;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class StateSummary implements Serializable {

	private STATE state;
	private int count;
	private List<Issue> issues;

	public STATE getState() {
		return state;
	}

	public void setState(STATE state) {
		this.state = state;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<Issue> getIssues() {
		return issues;
	}

	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}
	
}

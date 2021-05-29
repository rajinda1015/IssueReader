package com.alion.hometest.entity.model;

import java.io.Serializable;

import com.alion.hometest.util.TYPE;

public class Issue implements Serializable {

	private String issue_Id;
	private TYPE type;

	public String getIssue_Id() {
		return issue_Id;
	}

	public void setIssue_Id(String issue_Id) {
		this.issue_Id = issue_Id;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

}

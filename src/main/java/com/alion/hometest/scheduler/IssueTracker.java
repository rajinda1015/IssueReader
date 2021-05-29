package com.alion.hometest.scheduler;

import java.util.List;

import com.alion.hometest.util.STATE;
import com.alion.hometest.util.TYPE;


public class IssueTracker {

	private String issue_id;
	private TYPE type;
	private STATE current_state;
	private List<ChangeLogs> changelogs;

	public String getIssue_id() {
		return issue_id;
	}
	
	public void setIssue_id(String issue_id) {
		this.issue_id = issue_id;
	}
	
	public TYPE getType() {
		return type;
	}
	
	public void setType(TYPE type) {
		this.type = type;
	}
	
	public STATE getCurrent_state() {
		return current_state;
	}
	
	public void setCurrent_state(STATE current_state) {
		this.current_state = current_state;
	}
	
	public List<ChangeLogs> getChangelogs() {
		return changelogs;
	}
	
	public void setChangelogs(List<ChangeLogs> changelogs) {
		this.changelogs = changelogs;
	}
	
}

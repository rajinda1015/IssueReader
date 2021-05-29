package com.alion.hometest.entity.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class WeeklySummary implements Serializable {

	private String week;
	@JsonIgnore
	private Integer weekNo;
	private List<StateSummary> state_summaries;
	
	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public Integer getWeekNo() {
		return weekNo;
	}

	public void setWeekNo(Integer weekNo) {
		this.weekNo = weekNo;
	}

	public List<StateSummary> getState_summaries() {
		return state_summaries;
	}

	public void setState_summaries(List<StateSummary> state_summaries) {
		this.state_summaries = state_summaries;
	}
	
}

package com.alion.hometest.entity.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Wrapper class to store all the summary details
 * @author rajinda
 *
 */
@Document(collection = "SummaryWrapper")
public class SummaryWrapper implements Serializable {

	@Id
	private String project_Id;
	private List<WeeklySummary> weekly_summaries;

	public String getProject_Id() {
		return project_Id;
	}

	public void setProject_Id(String project_Id) {
		this.project_Id = project_Id;
	}

	public List<WeeklySummary> getWeekly_summaries() {
		return weekly_summaries;
	}

	public void setWeekly_summaries(List<WeeklySummary> weekly_summaries) {
		this.weekly_summaries = weekly_summaries;
	}

}

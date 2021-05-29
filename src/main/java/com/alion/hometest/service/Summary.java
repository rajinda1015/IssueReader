package com.alion.hometest.service;

import org.springframework.stereotype.Service;

import com.alion.hometest.entity.model.SearchDTO;
import com.alion.hometest.entity.model.SummaryWrapper;

@Service(value = "summary")
public interface Summary {

	public void updateWeeklySummary(String json) throws Exception;
	public SummaryWrapper getWeelySummary(SearchDTO searchParam) throws Exception;
}

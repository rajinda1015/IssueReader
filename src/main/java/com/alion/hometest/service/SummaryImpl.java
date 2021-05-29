package com.alion.hometest.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.alion.hometest.entity.model.Issue;
import com.alion.hometest.entity.model.SearchDTO;
import com.alion.hometest.entity.model.StateSummary;
import com.alion.hometest.entity.model.SummaryWrapper;
import com.alion.hometest.entity.model.WeeklySummary;
import com.alion.hometest.scheduler.ChangeLogs;
import com.alion.hometest.scheduler.IssueTracker;
import com.alion.hometest.scheduler.IssueTrackerWrapper;
import com.alion.hometest.util.STATE;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SummaryImpl implements Summary {

	private static final Logger LOGGER = LogManager.getLogger(SummaryImpl.class);
	
	@Autowired
	MongoTemplate mongoTemplate;

	/**
	 * Update weekly summary
	 */
	public void updateWeeklySummary(String json) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		IssueTrackerWrapper wrapper = mapper.readValue(json, IssueTrackerWrapper.class);
		
		SummaryWrapper sw = new SummaryWrapper();
		sw.setProject_Id(wrapper.getProject_id());
		
		// Initial data read
		Map<String, Map<String, List<Issue>>> masterMap = setWeeklySummary(wrapper);
		
		// Create SummaryWrapper object
		sw = createSummaryWrapper(masterMap, sw);
		
		// Save Summary Wrapper
		mongoTemplate.save(sw);
	}
	
	/**
	 * Initial data read from json map which was received from WS call
	 * 
	 * @param wrapper
	 * @throws Exception
	 */
	private Map<String, Map<String, List<Issue>>> setWeeklySummary(IssueTrackerWrapper wrapper) throws Exception {
		// Hole a master map. (Issue to the week). Key - WeekNo, Value - Children map
		Map<String, Map<String, List<Issue>>> masterMap = new HashMap<String, Map<String,List<Issue>>>();
		
		// Looping issues from tracking system
		List<IssueTracker> issues = null;
		if (null != wrapper.getIssues()) {
			issues = wrapper.getIssues();
			
			for (IssueTracker issueTracker : issues) {
				List<ChangeLogs> changeLogs = issueTracker.getChangelogs();
				
				for (ChangeLogs changeLog : changeLogs) {
					
					// 1. Set week details from cjanged_on value
					String weekWithNo = null;
					try {
						weekWithNo = getWeekWithNo(changeLog.getChanged_on());
					} catch (Exception e) {
						LOGGER.info("Error wen calculating the week : " + e.getMessage());
					}
					
					// 2. Set issue object
					Issue issue = new Issue();
					issue.setIssue_Id(issueTracker.getIssue_id());
					issue.setType(issueTracker.getType());
					
					// 3. Get children maps to the weekNo
					Map<String, List<Issue>> childrenMaps = masterMap.get(weekWithNo);
					if (null == childrenMaps) {
						childrenMaps = new HashMap<String, List<Issue>>();
					}
					
					// 3. Considered to_state as a state for state summary
					switch(changeLog.getTo_state()) {
						case open :
							List<Issue> opIssues = null;
							if (childrenMaps.containsKey(STATE.open.name())) {
								opIssues = childrenMaps.get(STATE.open.name());
								if (null == opIssues) {
									opIssues = new ArrayList<Issue>();
								}
							} else {
								opIssues = new ArrayList<Issue>();
							}
							opIssues.add(issue);
							childrenMaps.put(STATE.open.name(), opIssues);
							break;
							
						case in_progress :
							List<Issue> inIssues = null;
							if (childrenMaps.containsKey(STATE.in_progress.name())) {
								inIssues = childrenMaps.get(STATE.in_progress.name());
								if (null == inIssues) {
									inIssues = new ArrayList<Issue>();
								}
							} else {
								inIssues = new ArrayList<Issue>();
							}
							inIssues.add(issue);
							childrenMaps.put(STATE.in_progress.name(), inIssues);
							break;
							
						case testing :
							List<Issue> tsIssues = null;
							if (childrenMaps.containsKey(STATE.testing.name())) {
								tsIssues = childrenMaps.get(STATE.testing.name());
								if (null == tsIssues) {
									tsIssues = new ArrayList<Issue>();
								}
							} else {
								tsIssues = new ArrayList<Issue>();
							}
							tsIssues.add(issue);
							childrenMaps.put(STATE.testing.name(), tsIssues);
							break;
							
						case deploy :
							List<Issue> dpIssues = null;
							if (childrenMaps.containsKey(STATE.deploy.name())) {
								dpIssues = childrenMaps.get(STATE.deploy.name());
								if (null == dpIssues) {
									dpIssues = new ArrayList<Issue>();
								}
							} else {
								dpIssues = new ArrayList<Issue>();
							}
							dpIssues.add(issue);
							childrenMaps.put(STATE.deploy.name(), dpIssues);
							break;
					}
					
					// Set children map back to the master map
					masterMap.put(weekWithNo, childrenMaps);
				}
			}
		}
		
		return masterMap;
	}
	
	/**
	 * Return week no from a date
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	private String getWeekWithNo(String input) throws Exception {
		String dateFormat = "yyyy-MM-dd";
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = sdf.parse(input);
		String WeekOfYear = "yyyy'W'ww";
		String WeekNo = "ww";
		SimpleDateFormat week = new SimpleDateFormat(WeekOfYear);
		SimpleDateFormat weekNo = new SimpleDateFormat(WeekNo);
		return week.format(date) + "@" + weekNo.format(date);
	}
	
	/**
	 * Prepare SummaryWrapper to be saved in MongoDB
	 * 
	 * @param masterMap
	 * @param sWrapper
	 * @return
	 * @throws Exception
	 */
	private SummaryWrapper createSummaryWrapper(Map<String, Map<String, List<Issue>>> masterMap, SummaryWrapper sWrapper) 
			throws Exception {
		
		List<WeeklySummary> weeklySummaries = new ArrayList<WeeklySummary>();
		
		// Set week details
		masterMap.entrySet().stream().forEach(entry -> {
			
			// Get week number details (Ex: 2017W05|05)
			String weekWithNo = entry.getKey();
			Map<String, List<Issue>> childrenMaps = masterMap.get(weekWithNo);
			
			// 1. Set Weekly summary object
			WeeklySummary weeklySummary = new WeeklySummary();
			weeklySummary.setWeek(weekWithNo.split("@")[0]);
			weeklySummary.setWeekNo(Integer.parseInt(weekWithNo.split("@")[1]));
			
			// 2. Set state summary details
			List<StateSummary> stateSummaries = new ArrayList<StateSummary>();
			// 2.1 Set open state children
			List<Issue> opIssues = childrenMaps.get(STATE.open.name());
			if (null != opIssues && !opIssues.isEmpty()) {
				StateSummary stateSummary = new StateSummary();
				stateSummary.setState(STATE.open);
				stateSummary.setCount(opIssues.size());
				stateSummary.setIssues(opIssues);
				stateSummaries.add(stateSummary);
			}
			
			// 2.2 Set in_progress state children
			List<Issue> isIssues = childrenMaps.get(STATE.in_progress.name());
			if (null != isIssues && !isIssues.isEmpty()) {
				StateSummary stateSummary = new StateSummary();
				stateSummary.setState(STATE.in_progress);
				stateSummary.setCount(isIssues.size());
				stateSummary.setIssues(isIssues);
				stateSummaries.add(stateSummary);
			}
			
			// 2.3 Set testing state children
			List<Issue> testingIssues = childrenMaps.get(STATE.testing.name());
			if (null != testingIssues && !testingIssues.isEmpty()) {
				StateSummary stateSummary = new StateSummary();
				stateSummary.setState(STATE.testing);
				stateSummary.setCount(testingIssues.size());
				stateSummary.setIssues(testingIssues);
				stateSummaries.add(stateSummary);
			}

			// 2.4 Set testing state children
			List<Issue> dpIssues = childrenMaps.get(STATE.deploy.name());
			if (null != dpIssues && !dpIssues.isEmpty()) {
				StateSummary stateSummary = new StateSummary();
				stateSummary.setState(STATE.deploy);
				stateSummary.setCount(dpIssues.size());
				stateSummary.setIssues(dpIssues);
				stateSummaries.add(stateSummary);
			}
			
			weeklySummary.setState_summaries(stateSummaries);
			weeklySummaries.add(weeklySummary);
			
		});
		sWrapper.setWeekly_summaries(weeklySummaries);
		return sWrapper;
	}

	/**
	 * Filter details based on given parameters
	 * 
	 * @param SearchDTO
	 */
	@Override
	public SummaryWrapper getWeelySummary(SearchDTO searchParam) throws Exception {
		
		// 1. Retrieve object from DB
		SummaryWrapper wrapper = mongoTemplate.findById(searchParam.getProjectId(), SummaryWrapper.class);
		
		// 2. Filter result based on parameters
		List<WeeklySummary> weeklySummaryList = wrapper.getWeekly_summaries();
		if (null != wrapper) {
			int fromWeek = Integer.parseInt(searchParam.getFromWeek().split("W")[1]);
			int toWeek = Integer.parseInt(searchParam.getToWeek().split("W")[1]);
			
			List<WeeklySummary> filteredList = new ArrayList<WeeklySummary>();
			weeklySummaryList.forEach(wsummary -> {
				// 1. Filter weekly summaries
				if (wsummary.getWeekNo().intValue() >= fromWeek && wsummary.getWeekNo().intValue() <= toWeek) {
					
					List<StateSummary> stateSummaries = wsummary.getState_summaries();
					wsummary.setState_summaries(null);
					List<StateSummary> filteredStateSummaries = new ArrayList<StateSummary>();
					stateSummaries.forEach(stateSummary -> {
						
						// 2. Filter state summaries
						if (stateSummary.getState().name().equals(searchParam.getStatus())) {
							
							List<Issue> issues = stateSummary.getIssues();
							stateSummary.setIssues(null);
							List<Issue> filteredIssue = new ArrayList<Issue>();
							
							// 3. Filter issues
							issues.forEach(issue ->{
								if (issue.getType().name().equals(searchParam.getTypes())) {
									filteredIssue.add(issue);
								}
							});
							
							if (!filteredIssue.isEmpty()) {
								stateSummary.setIssues(filteredIssue);
								filteredStateSummaries.add(stateSummary);
							}
						}
					});
					
					if (!filteredStateSummaries.isEmpty()) {
						wsummary.setState_summaries(filteredStateSummaries);
						filteredList.add(wsummary);
					}
				}
			});

			wrapper.setWeekly_summaries(filteredList);
		}
		
		return wrapper;
	}

}

package com.alion.hometest.scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alion.hometest.service.Summary;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
public class GetIssuesScheduler {
	
	private static final Logger LOGGER = LogManager.getLogger(GetIssuesScheduler.class);
	
	@Value("${app.system.project_id}")
	private String project_id;
	
	@Autowired
	private Summary summary;
	
	@Autowired
	private RestTemplate restTemplate;

	@Scheduled(cron = "0 * 16 * * ?")
	public void getIssues() {
		String json = null;
		
		// I assume project id can be configured in a property file
		String projectId = project_id;
		
		try {
			// 1. Call getIssues API end point to get JSON object
			json = callGetIssues(projectId);
			
			// Assume we received following details from above API
			/*json = "{ \"project_id\" : \"project1\", \"issues\" : [ { \"issue_id\" : \"issue1\", \"type\" : \"bug\", "
					+ "\"current_state\" : \"open\", \"changelogs\" : [ { \"changed_on\" : \"2017-01-01 12:00pm UTC\", "
					+ "\"from_state\" : \"open\", \"to_state\" : \"in_progress\" }, { \"changed_on\" : \"2017-01-03 12:00pm UTC\", "
					+ "\"from_state\" : \"in_progress\", \"to_state\" : \"testing\" }, { \"changed_on\" : \"2017-01-21 12:00pm UTC\","
					+ " \"from_state\" : \"testing\", \"to_state\" : \"deploy\" } ] }, { \"issue_id\" : \"issue2\", \"type\" : "
					+ "\"bug\", \"current_state\" : \"open\", \"changelogs\" : [ { \"changed_on\" : \"2017-02-01 12:00pm UTC\", "
					+ "\"from_state\" : \"open\", \"to_state\" : \"in_progress\" }, { \"changed_on\" : \"2017-02-03 12:00pm UTC\", "
					+ "\"from_state\" : \"in_progress\", \"to_state\" : \"testing\" }, { \"changed_on\" : "
					+ "\"2017-02-21 12:00pm UTC\", \"from_state\" : \"testing\", \"to_state\" : \"deploy\" } ] }, { \"issue_id\" : "
					+ "\"issue3\", \"type\" : \"bug\", \"current_state\" : \"open\", \"changelogs\" : [ { \"changed_on\" : "
					+ "\"2017-03-01 12:00pm UTC\", \"from_state\" : \"open\", \"to_state\" : \"in_progress\" }, { \"changed_on\" : "
					+ "\"2017-03-03 12:00pm UTC\", \"from_state\" : \"in_progress\", \"to_state\" : \"testing\" }, { "
					+ "\"changed_on\" : \"2017-03-21 12:00pm UTC\", \"from_state\" : \"testing\", \"to_state\" : \"deploy\" "
					+ "} ] } ] }"; */
		
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date date = new Date();
			String strDate = sdf.format(date);
			LOGGER.info("Date & Time is : " + strDate);
		
		} catch (Exception e) {
			LOGGER.info("Exception occured when accessing getIssues API end point : " + e.getMessage());
		}
		
		
		// 2. Call service layer to store the details
		try {
			updateSummary(json, projectId);
			
		} catch (Exception e) {
			LOGGER.info("Exception occured in service layer : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Get response from API call
	 * 
	 * @param projectId
	 * @return json pattern object
	 * @throws Exception
	 */
	private String callGetIssues(String projectId) throws Exception {
		/*
		 * Since this is a GET call we can append the project_id parameter to the URI
		 * Otherwise we can attach to the HttpEntity object to send it with header information
		 */
		String url = "<api location URI>/getIssues?project_id=" + projectId;
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> entity = new HttpEntity<Object>(headers);
		
		ResponseEntity<Object[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Object[].class);
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		String jsonObject = gson.toJson(response);
		return jsonObject;
	}
	
	/**
	 * Call service layer to store the details
	 */
	private void updateSummary(String json, String projectId) throws Exception {
		if (null != json && json.length() > 0) {
			summary.updateWeeklySummary(json);
		} else {
			LOGGER.info("Cannot store details. Details not found for project " + projectId);
		}
	}
}

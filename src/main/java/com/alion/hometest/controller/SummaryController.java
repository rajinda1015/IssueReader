package com.alion.hometest.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alion.hometest.entity.model.SearchDTO;
import com.alion.hometest.service.Summary;

@RestController
@RequestMapping("/hometest")
public class SummaryController {

	private static final Logger LOGGER = LogManager.getLogger(SummaryController.class);
	
	@Autowired
	private Summary summary;
	
	/**
	 * Return weekly summary
	 * 
	 * @return weekly summaries
	 * @throws Exception
	 */
	@RequestMapping(value = "/weeklySummary", method = RequestMethod.GET)
	public ResponseEntity<?> getWeeklySummary(@RequestBody SearchDTO param) throws Exception {
		return ResponseEntity.status(HttpStatus.OK).body(summary.getWeelySummary(param));
	}
}

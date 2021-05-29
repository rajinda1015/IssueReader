package com.alion.hometest.scheduler;

import com.alion.hometest.util.STATE;


public class ChangeLogs {

	private String changed_on;
	private STATE from_state;
	private STATE to_state;
	
	public String getChanged_on() {
		return changed_on;
	}
	
	public void setChanged_on(String changed_on) {
		this.changed_on = changed_on;
	}
	
	public STATE getFrom_state() {
		return from_state;
	}
	
	public void setFrom_state(STATE from_state) {
		this.from_state = from_state;
	}
	
	public STATE getTo_state() {
		return to_state;
	}
	
	public void setTo_state(STATE to_state) {
		this.to_state = to_state;
	}

}

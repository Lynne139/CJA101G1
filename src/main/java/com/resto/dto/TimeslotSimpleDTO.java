package com.resto.dto;


public class TimeslotSimpleDTO {
	
	
    private Integer timeslotId;
    private String timeslotName;
    
    
    
	public TimeslotSimpleDTO() {
		super();
	}
	
		
	public TimeslotSimpleDTO(Integer timeslotId, String timeslotName) {
		super();
		this.timeslotId = timeslotId;
		this.timeslotName = timeslotName;
	}





	public Integer getTimeslotId() {
		return timeslotId;
	}
	public void setTimeslotId(Integer timeslotId) {
		this.timeslotId = timeslotId;
	}
	public String getTimeslotName() {
		return timeslotName;
	}
	public void setTimeslotName(String timeslotName) {
		this.timeslotName = timeslotName;
	}

    
}
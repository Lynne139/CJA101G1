package com.resto.dto;

import java.util.List;

public class RestoPeriodTimeslotDTO {
	
	 	private Integer restoId;
	    private String restoName;
	    private Integer periodId;
	    private String periodName;
	    private List<TimeslotSimpleDTO> timeslots;
	    

		public RestoPeriodTimeslotDTO() {
			super();
		}

		
		public RestoPeriodTimeslotDTO(Integer restoId, String restoName, Integer periodId, String periodName,
				List<TimeslotSimpleDTO> timeslots) {
			super();
			this.restoId = restoId;
			this.restoName = restoName;
			this.periodId = periodId;
			this.periodName = periodName;
			this.timeslots = timeslots;
		}




		public Integer getRestoId() {
			return restoId;
		}
		public void setRestoId(Integer restoId) {
			this.restoId = restoId;
		}
		public String getRestoName() {
			return restoName;
		}
		public void setRestoName(String restoName) {
			this.restoName = restoName;
		}
		public Integer getPeriodId() {
			return periodId;
		}
		public void setPeriodId(Integer periodId) {
			this.periodId = periodId;
		}
		public String getPeriodName() {
			return periodName;
		}
		public void setPeriodName(String periodName) {
			this.periodName = periodName;
		}
		public List<TimeslotSimpleDTO> getTimeslots() {
			return timeslots;
		}
		public void setTimeslots(List<TimeslotSimpleDTO> timeslots) {
			this.timeslots = timeslots;
		}
	    
	    

}

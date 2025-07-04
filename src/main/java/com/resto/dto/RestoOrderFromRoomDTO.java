package com.resto.dto;

import java.time.LocalDate;
import java.util.Objects;

public class RestoOrderFromRoomDTO {
	
	Integer restoOrderId;  // 新增的話為null
	Integer restoId;
    Integer timeslotId;
    LocalDate regiDate;
    
    
    
	public RestoOrderFromRoomDTO() {
		super();
	}


	
	public Integer getRestoOrderId() {
		return restoOrderId;
	}




	public void setRestoOrderId(Integer restoOrderId) {
		this.restoOrderId = restoOrderId;
	}




	public Integer getRestoId() {
		return restoId;
	}



	public void setRestoId(Integer restoId) {
		this.restoId = restoId;
	}



	public Integer getTimeslotId() {
		return timeslotId;
	}



	public void setTimeslotId(Integer timeslotId) {
		this.timeslotId = timeslotId;
	}



	public LocalDate getRegiDate() {
		return regiDate;
	}



	public void setRegiDate(LocalDate regiDate) {
		this.regiDate = regiDate;
	}



	
	
	
	
	

}


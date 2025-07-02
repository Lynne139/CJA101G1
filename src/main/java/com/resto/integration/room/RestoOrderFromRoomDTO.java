package com.resto.integration.room;

import java.time.LocalDate;
import java.util.Objects;

public class RestoOrderFromRoomDTO {
	
	Integer restoId;
    Integer timeslotId;
    LocalDate regiDate;
    
    
    
	public RestoOrderFromRoomDTO() {
		super();
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



	@Override
	public int hashCode() {
		return Objects.hash(regiDate, restoId, timeslotId);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RestoOrderFromRoomDTO other = (RestoOrderFromRoomDTO) obj;
		return Objects.equals(regiDate, other.regiDate) && Objects.equals(restoId, other.restoId)
				&& Objects.equals(timeslotId, other.timeslotId);
	}



	@Override
	public String toString() {
		return "RestoOrderFromRoomDTO [restoId=" + restoId + ", timeslotId=" + timeslotId + ", regiDate=" + regiDate
				+ "]";
	}
	
	
	
	
	
	
	
	

}


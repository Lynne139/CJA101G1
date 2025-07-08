package com.resto.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;

public class PreBookingDTO {
	
	@NotNull
    Integer restoId;

    @NotNull(message = "請選擇日期")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate regiDate;

    @NotNull(message = "請選擇人數")
    Integer regiSeats;

    @NotNull(message = "請選擇一個時段")
    Integer timeslotId;
    
    
    
    
    
	public PreBookingDTO() {
		super();
	}

	public PreBookingDTO(Integer restoId, LocalDate regiDate, Integer regiSeats, Integer timeslotId) {
		super();
		this.restoId = restoId;
		this.regiDate = regiDate;
		this.regiSeats = regiSeats;
		this.timeslotId = timeslotId;
	}
    
	public boolean anyBlank() {
        return restoId == null || regiDate == null
                || regiSeats == null || timeslotId == null;
    }

	public Integer getRestoId() {
		return restoId;
	}

	public void setRestoId(Integer restoId) {
		this.restoId = restoId;
	}

	public LocalDate getRegiDate() {
		return regiDate;
	}

	public void setRegiDate(LocalDate regiDate) {
		this.regiDate = regiDate;
	}

	public Integer getRegiSeats() {
		return regiSeats;
	}

	public void setRegiSeats(Integer regiSeats) {
		this.regiSeats = regiSeats;
	}

	public Integer getTimeslotId() {
		return timeslotId;
	}

	public void setTimeslotId(Integer timeslotId) {
		this.timeslotId = timeslotId;
	}
	
	
    
    
    
    
    
    
    
    

}

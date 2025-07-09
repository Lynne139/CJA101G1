package com.resto.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class BookingFormDTO {
	
	Integer restoId;
    
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
	LocalDate regiDate;
    
	Integer regiSeats;
    
    Integer timeslotId;
    
    @NotBlank  
    String guestName;
   
    @NotBlank  
    String guestPhone;
    
    @NotBlank 
    @Email 
    String guestEmail;
    
    Integer highChairs;
    
    String  regiReq;

	public BookingFormDTO(Integer restoId, LocalDate regiDate, Integer regiSeats, Integer timeslotId,
			@NotBlank String guestName, @NotBlank String guestPhone, @NotBlank @Email String guestEmail,
			Integer highChairs, String regiReq) {
		super();
		this.restoId = restoId;
		this.regiDate = regiDate;
		this.regiSeats = regiSeats;
		this.timeslotId = timeslotId;
		this.guestName = guestName;
		this.guestPhone = guestPhone;
		this.guestEmail = guestEmail;
		this.highChairs = highChairs;
		this.regiReq = regiReq;
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

	public String getGuestName() {
		return guestName;
	}

	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}

	public String getGuestPhone() {
		return guestPhone;
	}

	public void setGuestPhone(String guestPhone) {
		this.guestPhone = guestPhone;
	}

	public String getGuestEmail() {
		return guestEmail;
	}

	public void setGuestEmail(String guestEmail) {
		this.guestEmail = guestEmail;
	}

	public Integer getHighChairs() {
		return highChairs;
	}

	public void setHighChairs(Integer highChairs) {
		this.highChairs = highChairs;
	}

	public String getRegiReq() {
		return regiReq;
	}

	public void setRegiReq(String regiReq) {
		this.regiReq = regiReq;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

}

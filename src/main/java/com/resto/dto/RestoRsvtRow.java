package com.resto.dto;

import java.time.LocalDate;

public class RestoRsvtRow {
	
	LocalDate  date;
    Integer    restoId;
    String     restoName;
    String     periodName;
    String     timeslotName;
    int        seatsTotal;
    int        seatsBooked;
    int        seatsLeft;
	public RestoRsvtRow(LocalDate date, Integer restoId, String restoName, String periodName, String timeslotName,
			int seatsTotal, int seatsBooked, int seatsLeft) {
		super();
		this.date = date;
		this.restoId = restoId;
		this.restoName = restoName;
		this.periodName = periodName;
		this.timeslotName = timeslotName;
		this.seatsTotal = seatsTotal;
		this.seatsBooked = seatsBooked;
		this.seatsLeft = seatsLeft;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
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
	public String getPeriodName() {
		return periodName;
	}
	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}
	public String getTimeslotName() {
		return timeslotName;
	}
	public void setTimeslotName(String timeslotName) {
		this.timeslotName = timeslotName;
	}
	public int getSeatsTotal() {
		return seatsTotal;
	}
	public void setSeatsTotal(int seatsTotal) {
		this.seatsTotal = seatsTotal;
	}
	public int getSeatsBooked() {
		return seatsBooked;
	}
	public void setSeatsBooked(int seatsBooked) {
		this.seatsBooked = seatsBooked;
	}
	public int getSeatsLeft() {
		return seatsLeft;
	}
	public void setSeatsLeft(int seatsLeft) {
		this.seatsLeft = seatsLeft;
	}
    
    
    
    
    
    
    

}

package com.resto.dto;

public class RestoOrderSummaryDTO {
	
	private String restoName;

    private Long total;
    private Long done;
    private Long noshow;
    private Long ongoing;
    private Long canceled;
    private Long totalSeats;

    public RestoOrderSummaryDTO() {
    }
    
    public RestoOrderSummaryDTO(String restoName, Long total, Long done, Long noshow,
            Long ongoing, Long canceled, Long totalSeats) {
	this.restoName = restoName;
	this.total = total;
	this.done = done;
	this.noshow = noshow;
	this.ongoing = ongoing;
	this.canceled = canceled;
	this.totalSeats = totalSeats;
	}

    public RestoOrderSummaryDTO(Long total, Long done, Long noshow, Long ongoing, Long canceled, Long totalSeats) {
        this.total = total;
        this.done = done;
        this.noshow = noshow;
        this.ongoing = ongoing;
        this.canceled = canceled;
        this.totalSeats = totalSeats;
    }

    
	public String getRestoName() {
		return restoName;
	}

	public void setRestoName(String restoName) {
		this.restoName = restoName;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getDone() {
		return done;
	}

	public void setDone(Long done) {
		this.done = done;
	}

	public Long getNoshow() {
		return noshow;
	}

	public void setNoshow(Long noshow) {
		this.noshow = noshow;
	}

	public Long getOngoing() {
		return ongoing;
	}

	public void setOngoing(Long ongoing) {
		this.ongoing = ongoing;
	}

	public Long getCanceled() {
		return canceled;
	}

	public void setCanceled(Long canceled) {
		this.canceled = canceled;
	}

	public Long getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(Long totalSeats) {
		this.totalSeats = totalSeats;
	}

    
    
    
}

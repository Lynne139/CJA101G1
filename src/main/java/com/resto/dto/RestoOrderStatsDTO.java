package com.resto.dto;

public class RestoOrderStatsDTO {

	private Long totalCount;
    private Long totalSeats;


    
    public RestoOrderStatsDTO() {
		super();
	}

	public RestoOrderStatsDTO(Long totalCount, Long totalSeats) {
        this.totalCount = totalCount;
        this.totalSeats = totalSeats;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public Long getTotalSeats() {
        return totalSeats;
    }
	
}

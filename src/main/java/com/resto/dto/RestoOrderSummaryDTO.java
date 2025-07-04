package com.resto.dto;

import java.util.Map;

public class RestoOrderSummaryDTO {
	
	private Integer totalCount;
    private Integer doneCount;
    private Integer noShowCount;
    private Map<String, Integer> restoBreakdown;
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public Integer getDoneCount() {
		return doneCount;
	}
	public void setDoneCount(Integer doneCount) {
		this.doneCount = doneCount;
	}
	public Integer getNoShowCount() {
		return noShowCount;
	}
	public void setNoShowCount(Integer noShowCount) {
		this.noShowCount = noShowCount;
	}
	public Map<String, Integer> getRestoBreakdown() {
		return restoBreakdown;
	}
	public void setRestoBreakdown(Map<String, Integer> restoBreakdown) {
		this.restoBreakdown = restoBreakdown;
	}
    
    
    

}

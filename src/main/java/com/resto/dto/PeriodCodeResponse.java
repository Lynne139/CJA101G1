package com.resto.dto;

import java.util.List;

public class PeriodCodeResponse {
	
	String label;
    String css;
    List<RestoPeriodTimeslotDTO> data;
    
    
	public PeriodCodeResponse() {
		super();
	}
	
	
	public PeriodCodeResponse(String label, String css, List<RestoPeriodTimeslotDTO> data) {
		super();
		this.label = label;
		this.css = css;
		this.data = data;
	}


	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getCss() {
		return css;
	}
	public void setCss(String css) {
		this.css = css;
	}
	public List<RestoPeriodTimeslotDTO> getData() {
		return data;
	}
	public void setData(List<RestoPeriodTimeslotDTO> data) {
		this.data = data;
	}
    
    

}

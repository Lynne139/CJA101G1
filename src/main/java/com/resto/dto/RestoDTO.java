package com.resto.dto;

public class RestoDTO {

    private Integer restoId;
    private String restoName;
    private String restoNameEn;
    private String restoLoc;
    private Integer restoSeatsTotal;
    private Boolean isEnabled;

    public RestoDTO() {
		super();
    }

    public RestoDTO(Integer restoId, String restoName, String restoNameEn,
                    String restoLoc, Integer restoSeatsTotal, Boolean isEnabled) {
        this.restoId = restoId;
        this.restoName = restoName;
        this.restoNameEn = restoNameEn;
        this.restoLoc = restoLoc;
        this.restoSeatsTotal = restoSeatsTotal;
        this.isEnabled = isEnabled;
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

    public String getRestoNameEn() {
    	return restoNameEn;
    	}
    public void setRestoNameEn(String restoNameEn) {
    	this.restoNameEn = restoNameEn; 
    	}

    public String getRestoLoc() { 
    	return restoLoc; 
    	}
    public void setRestoLoc(String restoLoc) { 
    	this.restoLoc = restoLoc; 
    	}

    public Integer getRestoSeatsTotal() {
    	return restoSeatsTotal; 
    	}
    public void setRestoSeatsTotal(Integer restoSeatsTotal) {
    	this.restoSeatsTotal = restoSeatsTotal; 
    	}

    public Boolean getIsEnabled() {
    	return isEnabled; 
    	}
    public void setIsEnabled(Boolean isEnabled) {
    	this.isEnabled = isEnabled; 
    	}

    @Override
    public String toString() {
        return "RestoDTO{" +
                "restoId=" + restoId +
                ", restoName='" + restoName + '\'' +
                ", restoNameEn='" + restoNameEn + '\'' +
                ", restoLoc='" + restoLoc + '\'' +
                ", restoSeatsTotal=" + restoSeatsTotal +
                ", isEnabled=" + isEnabled +
                '}';
    }
}

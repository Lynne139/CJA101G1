package com.resto.model;

public enum RestoOrderStatus {
	CANCELED(0), CREATED(1), DONE(2), WITHHOLD(3), NOSHOW(4);
	private int value;

	private RestoOrderStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}



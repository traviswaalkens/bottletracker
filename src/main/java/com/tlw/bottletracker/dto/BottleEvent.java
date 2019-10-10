package com.tlw.bottletracker.dto;

public class BottleEvent extends BabyStatsEvent {
	private String bottleOunces;
	private String uom;
	private String feedingMinutes;
	private String breastSide;
	
	public String getBottleOunces() {
		return bottleOunces;
	}
	public void setBottleOunces(String bottleOunces) {
		this.bottleOunces = bottleOunces;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
	public String getFeedingMinutes() {
		return feedingMinutes;
	}
	public void setFeedingMinutes(String feedingMinutes) {
		this.feedingMinutes = feedingMinutes;
	}
	public String getBreastSide() {
		return breastSide;
	}
	public void setBreastSide(String breastSide) {
		this.breastSide = breastSide;
	}

	
}

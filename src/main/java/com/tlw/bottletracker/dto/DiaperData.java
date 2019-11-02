package com.tlw.bottletracker.dto;

public class DiaperData extends MessageData {

	private boolean isStool = false;
	private boolean isWet = false;

	public boolean isStool() {
		return isStool;
	}

	public void setStool(boolean isStool) {
		this.isStool = isStool;
	}

	public boolean isWet() {
		return isWet;
	}

	public void setWet(boolean isWet) {
		this.isWet = isWet;
	}

}

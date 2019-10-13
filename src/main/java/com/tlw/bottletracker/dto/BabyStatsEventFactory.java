package com.tlw.bottletracker.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class BabyStatsEventFactory {
	private String id;
	private String token;
	DateFormat df;

	public BabyStatsEventFactory() {
		df = new SimpleDateFormat("HH:mm");
	}

	public BabyStatsEventFactory(Properties p) {
		id = p.getProperty("babystats.id");
		token = p.getProperty("babystats.token");
		df = new SimpleDateFormat("HH:mm");
	}

	public BabyStatsEvent factory(MessageData msgData) throws Exception {

		// TODO throw exception if the totken or id have not been set.
		if (msgData.isValid && msgData.getType() == "Bottle") {
			return buildBottleEvent((BottleData) msgData);
		} else {
			// TODO This needs to be a better more precise message.
			// TODO this might not be the correct kind of exception.
			throw new Exception("Unknown Type or invalid data");
		}
	}

	public BottleEvent buildBottleEvent(BottleData bd) {

		BottleEvent event;
		event = new BottleEvent();
		event.setBottleOunces(String.valueOf(bd.getOunces()));
		event.setEvent("AddFeeding");
		event.setEventTime(df.format(bd.getTime()));
		event.setId(id);
		event.setAccessToken(token);
		event.setUom("oz");

		return event;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}

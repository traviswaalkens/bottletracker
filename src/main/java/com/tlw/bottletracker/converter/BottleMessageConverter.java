package com.tlw.bottletracker.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import com.tlw.bottletracker.dto.BabyStatsEvent;
import com.tlw.bottletracker.dto.BottleData;
import com.tlw.bottletracker.dto.BottleEvent;
import com.tlw.bottletracker.dto.MessageData;

public class BottleMessageConverter extends MessageDataConverter
		implements Function<MessageData, Collection<BabyStatsEvent>> {

	public Collection<BabyStatsEvent> apply(MessageData t) {

		Collection<BabyStatsEvent> events = new ArrayList<BabyStatsEvent>();
		BottleData bd = (BottleData) t;

		BottleEvent be = new BottleEvent();
		be.setBottleOunces(String.valueOf(bd.getOunces()));
		be.setEvent("AddFeeding");
		be.setEventTime(df.format(bd.getTime()));
		be.setId(id);
		be.setAccessToken(token);
		be.setUom("oz");

		events.add(be);
		return events;
	}
}

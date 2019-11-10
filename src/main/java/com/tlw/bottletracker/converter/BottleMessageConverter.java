package com.tlw.bottletracker.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tlw.bottletracker.dto.BabyStatsEvent;
import com.tlw.bottletracker.dto.BottleData;
import com.tlw.bottletracker.dto.BottleEvent;
import com.tlw.bottletracker.dto.MessageData;

public class BottleMessageConverter extends MessageDataConverter
		implements Function<MessageData, Collection<BabyStatsEvent>> {

	public static final Logger LOG = LoggerFactory.getLogger(BottleMessageConverter.class);

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

		LOG.info("Adding Bottle event with {} ounces at {}.", bd.getOunces(), df.format(bd.getTime().getTime()));

		events.add(be);

		return events;
	}
}

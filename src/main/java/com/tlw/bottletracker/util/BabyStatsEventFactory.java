package com.tlw.bottletracker.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tlw.bottletracker.converter.BottleMessageConverter;
import com.tlw.bottletracker.dto.BabyStatsEvent;
import com.tlw.bottletracker.dto.MessageData;
import com.tlw.bottletracker.exception.UnknownMessageTypeException;

public class BabyStatsEventFactory {
	private String id;
	private String token;

	public static final Logger LOG = LoggerFactory.getLogger(BabyStatsEventFactory.class);

	public BabyStatsEventFactory() {

	}

	// TODO rename this to converter or something
	public BabyStatsEventFactory(Properties p) {
		id = p.getProperty("babystats.id");
		token = p.getProperty("babystats.token");
	}

	public Map<String, Function<MessageData, Collection<BabyStatsEvent>>> getConverterMap() {
		Map<String, Function<MessageData, Collection<BabyStatsEvent>>> messageDataConverters = new HashMap<String, Function<MessageData, Collection<BabyStatsEvent>>>();

		// TODO finish these converters
		// TODO Lookup more about Function<> like how it knows to create the proper
		// apply method?
		messageDataConverters.put("Bottle", new BottleMessageConverter().setId(id).setToken(token));
		messageDataConverters.put("Diaper", null /* new DiaperMessageConverter() */);

		return messageDataConverters;
	}

	public Collection<BabyStatsEvent> factory(MessageData msgData) throws Exception {

		// TODO use InvalidBabyStatsException
		if (!msgData.isValid()) {
			throw new RuntimeException("type is invalid");
		}

		// TODO throw exception if the token or id have not been set.

		Function<MessageData, Collection<BabyStatsEvent>> converter = getConverterMap().get(msgData.getType());
		if (converter != null) {
			return converter.apply(msgData);
		} else {
			throw new UnknownMessageTypeException("Unknown Type " + msgData.getType());
		}
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

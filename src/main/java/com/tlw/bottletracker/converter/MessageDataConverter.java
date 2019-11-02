package com.tlw.bottletracker.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.function.Function;

import com.tlw.bottletracker.dto.BabyStatsEvent;
import com.tlw.bottletracker.dto.MessageData;

public abstract class MessageDataConverter implements Function<MessageData, Collection<BabyStatsEvent>> {

	protected String id;
	protected String token;
	protected static final DateFormat df = new SimpleDateFormat("HH:mm");

	// TODO this need to be set some other way.
	public String getId() {
		return id;
	}

	public MessageDataConverter setId(String id) {
		this.id = id;
		return this;
	}

	public String getToken() {
		return token;
	}

	public MessageDataConverter setToken(String token) {
		this.token = token;
		return this;
	}
}

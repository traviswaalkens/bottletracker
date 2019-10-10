package com.tlw.bottletracker.service;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tlw.bottletracker.dto.BabyStatsEvent;

public class BabyStatsHttpService {
	private String url;

	public void setUrl(String url) {
		this.url = url;
	}

	public String addEvent(BabyStatsEvent event) throws IOException {

		ObjectMapper om = new ObjectMapper();
		String json = om.writeValueAsString(event);

		OkHttpClient client = new OkHttpClient();

		RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
		Request request = new Request.Builder().url(this.url).method("POST", requestBody).build();

		Response local = client.newCall(request).execute();

		return local.body().string();
	}
}

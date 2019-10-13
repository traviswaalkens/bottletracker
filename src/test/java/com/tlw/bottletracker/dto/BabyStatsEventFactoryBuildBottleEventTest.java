package com.tlw.bottletracker.dto;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

public class BabyStatsEventFactoryBuildBottleEventTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testBuildBottleEvent() {
		BottleData bd = new BottleData();
		bd.setContents("test content");
		bd.setNotes("test note");
		bd.setOunces(6.5f);
		bd.setTime(new Date("10/13/2019 12:23:13"));
		bd.setValid(true);

		BabyStatsEventFactory factory = new BabyStatsEventFactory();
		factory.setId("testid");
		factory.setToken("test token");
		BottleEvent bottle = factory.buildBottleEvent(bd);

		assertEquals("testid", bottle.getId());
		assertEquals("test token", bottle.getAccessToken());
		assertEquals("6.5", bottle.getBottleOunces());
		assertEquals("12:23", bottle.getEventTime());

	}

}

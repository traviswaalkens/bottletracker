package com.tlw.bottletracker.dto;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

public class BabyStatsEventFactoryFactoryTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testFactory() throws Exception {

		BottleData bd = new BottleData();
		bd.setContents("test content");
		bd.setNotes("test note");
		bd.setOunces(6.5f);
		bd.setTime(new Date("10/13/2019 12:23:13"));
		bd.setValid(true);
		bd.setType("Bottle");

		BabyStatsEventFactory factory = new BabyStatsEventFactory();
		factory.setId("testid");
		factory.setToken("test token");

		BabyStatsEvent event = factory.factory(bd);

		assertEquals("testid", event.getId());
		assertEquals("test token", event.getAccessToken());
		assertEquals("AddFeeding", event.getEvent());
	}

	@Test(expected = Exception.class)
	public void testIsValidFalseData() throws Exception {

		BottleData bd = new BottleData();
		bd.setValid(false);
		bd.setType("Bottle");

		BabyStatsEventFactory factory = new BabyStatsEventFactory();
		factory.setId("testid");
		factory.setToken("test token");
		factory.factory(bd);
	}

	@Test(expected = Exception.class)
	public void testUnknownType() throws Exception {

		BottleData bd = new BottleData();
		bd.setValid(true);
		bd.setType("--__--Unknown--__--");

		BabyStatsEventFactory factory = new BabyStatsEventFactory();
		factory.setId("testid");
		factory.setToken("test token");
		factory.factory(bd);
	}

}

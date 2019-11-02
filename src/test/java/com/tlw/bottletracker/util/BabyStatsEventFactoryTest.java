package com.tlw.bottletracker.util;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tlw.bottletracker.dto.BabyStatsEvent;
import com.tlw.bottletracker.dto.BottleData;
import com.tlw.bottletracker.dto.BottleEvent;
import com.tlw.bottletracker.exception.UnknownMessageTypeException;

public class BabyStatsEventFactoryTest {

	private static BabyStatsEventFactory factory;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		factory = new BabyStatsEventFactory();
		factory.setId("testid");
		factory.setToken("test token");

	}

	private BottleData getBottleData001() {

		BottleData bd = new BottleData();
		bd.setContents("test content");
		bd.setNotes("test note");
		bd.setOunces(6.5f);
		bd.setTime(new Date("10/13/2019 12:23:13"));
		bd.setValid(true);
		bd.setType("Bottle");

		return bd;
	}

	private BottleData getBottleData002() {

		BottleData bd = new BottleData();
		bd.setValid(false);
		bd.setType("Bottle");

		return bd;
	}

	private BottleData getBottleData003() {

		BottleData bd = new BottleData();
		bd.setValid(true);
		bd.setType("--__--Unknown--__--");
		return bd;
	}

	@Test
	public void testFactoryWithBottlData() throws Exception {

		Collection<BabyStatsEvent> events;
		events = factory.factory(getBottleData001());

		assertEquals(1, events.size());
		BottleEvent event = (BottleEvent) events.iterator().next();

		assertEquals("testid", event.getId());
		assertEquals("test token", event.getAccessToken());
		assertEquals("AddFeeding", event.getEvent());
	}

	@Test(expected = RuntimeException.class)
	public void testIsValidFalseData() throws Exception {
		factory.factory(getBottleData002());
	}

	@Test(expected = UnknownMessageTypeException.class)
	public void testUnknownType() throws Exception {
		factory.factory(getBottleData003());
	}

}

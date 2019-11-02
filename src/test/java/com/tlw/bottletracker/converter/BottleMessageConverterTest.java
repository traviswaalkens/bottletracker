package com.tlw.bottletracker.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tlw.bottletracker.dto.BabyStatsEvent;
import com.tlw.bottletracker.dto.BottleData;
import com.tlw.bottletracker.dto.BottleEvent;

public class BottleMessageConverterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@SuppressWarnings("deprecation")
	@Test
	public void test() {
		BottleData bd = new BottleData();
		bd.setContents("test content");
		bd.setNotes("test note");
		bd.setOunces(6.5f);
		bd.setTime(new Date("10/13/2019 12:23:13"));
		bd.setValid(true);
		bd.setType("Bottle");

		BottleMessageConverter c = new BottleMessageConverter();
		Collection<BabyStatsEvent> events = c.apply(bd);

		assertEquals(1, events.size());
		BottleEvent e = (BottleEvent) events.iterator().next();
		assertEquals(e.getEvent(), "AddFeeding");
		assertEquals(e.getBottleOunces(), "6.5");
	}

	@Test(expected = Exception.class)
	public void testIsValidFalseData() throws Exception {

		BottleData bd = new BottleData();
		bd.setValid(false);
		bd.setType("Bottle");

		BottleMessageConverter c = new BottleMessageConverter();
		Collection<BabyStatsEvent> events = c.apply(bd);
		fail("An exception should be thrown if the messagedata is invalid.");

	}

	@Test(expected = Exception.class)
	public void testWrongType() throws Exception {

		BottleData bd = new BottleData();
		bd.setValid(true);
		bd.setType("--__--Unknown--__--");

		BottleMessageConverter c = new BottleMessageConverter();
		c.apply(bd);
		fail("Wrong types should produce an exception.");
	}

}

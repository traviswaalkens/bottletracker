package com.tlw.bottletracker.service;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.tlw.bottletracker.util.PropertyReader;

public class EmailRepositoryServiceTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	@Ignore
	public void test() throws Exception {
		PropertyReader r = new PropertyReader();
		Properties p = r.getCredentialsProperties();
		EmailRepositoryService service = new EmailRepositoryService();

		service.setHost(p.getProperty("host"));
		service.setUsername(p.getProperty("username"));
		service.setPassword(p.getProperty("password"));
		service.setProvider(p.getProperty("provider"));
		service.setPort(p.getProperty("port"));

		service.connect();

		assertTrue(service.getStore().isConnected());
		assertTrue(service.getInbox().isOpen());
		assertTrue(service.getCompletedArchive().isOpen());
		assertTrue(service.getNoiseArchive().isOpen());
	}
}

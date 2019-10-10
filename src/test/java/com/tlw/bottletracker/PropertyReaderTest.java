package com.tlw.bottletracker;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

public class PropertyReaderTest {

	@Test
	public void testGetCredentialsProperties() throws IOException {
		PropertyReader pr = new PropertyReader();
		Properties p = pr.getCredentialsProperties();
		
		assertEquals(5,p.size() );		
	}

	@Test
	public void testGetConfigProperties() throws IOException {
		PropertyReader pr = new PropertyReader();
		Properties p = pr.getConfigProperties();
		
		assertEquals(3, p.size() );		
	}

}

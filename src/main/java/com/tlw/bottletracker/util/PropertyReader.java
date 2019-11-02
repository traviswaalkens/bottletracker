package com.tlw.bottletracker.util;
 
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader
{
	public Properties getCredentialsProperties() throws IOException { return getProperties("credentials.properties"); }
	public Properties getConfigProperties() throws IOException { return getProperties("config.properties"); }
	
	private Properties getProperties(String name) throws IOException {
		InputStream credentialsFile = getClass().getClassLoader().getResourceAsStream(name);
		Properties p = new Properties();
		p.load(credentialsFile);
		return p;
	}
}


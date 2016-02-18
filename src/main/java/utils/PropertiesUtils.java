package utils;

import java.io.*;
import java.util.*;

import spinnerCalendar.SpinnerCalendar;
import spinnerCalendar.SpinnerEvent;

public class PropertiesUtils {
	private static final String propertiesFileName = "spinner.properties";
	//Properties prop = new Properties();
	private static Properties prop = null;
	private static InputStream input = null;
	private static PropertiesUtils propertiesUtils = null;

	private PropertiesUtils() {
		try {			
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			prop = new Properties();
			prop.load(classLoader.getResourceAsStream(propertiesFileName));
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	

	public static PropertiesUtils getSpinnerProperties()  {
		synchronized (PropertiesUtils.class) {
			if (propertiesUtils == null) {
				propertiesUtils = new PropertiesUtils();				
			}
		}
		return propertiesUtils;
	}

	public void printProperties() {
		Enumeration<?> e = prop.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = prop.getProperty(key);
			System.out.println(key + "=" + value);
		}

	}

	public String getProperty(String propName) {
		return prop.getProperty(propName);
	}

}

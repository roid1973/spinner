package utils;

import java.io.*;
import java.util.*;

public class PropertiesUtils {
	private static final String PROPERTIES_FILE = "spinner.properties";
	private static Properties prop = null;

	public static Properties getSpinnerProperties() {
		if (prop == null) {
			synchronized (PropertiesUtils.class) {
				if (prop == null) {
					try (InputStream is = PropertiesUtils.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
						prop = new Properties();
						prop.load(is);
					} catch (IOException e) {
						throw new RuntimeException("Cannot read spinner properties", e);
					}
				}
			}
		}
		return prop;
	}
}

package db.spinner;

import java.sql.*;
import java.util.Properties;

import utils.PropertiesUtils;

import com.google.appengine.api.utils.SystemProperty;

import org.apache.commons.dbcp.BasicDataSource;

public class DButils {

	private static BasicDataSource dbcp = setupDataSource();

	private static BasicDataSource setupDataSource() {
		String prefix = (SystemProperty.Environment.Value.Production.equals(SystemProperty.environment.value()) ? "production." : "test.");

		try {
			Properties props = PropertiesUtils.getSpinnerProperties();

			BasicDataSource dbcp = new BasicDataSource();
			dbcp.setDriverClassName(props.getProperty(prefix + "driver"));
			dbcp.setUrl(props.getProperty(prefix + "url"));
			dbcp.setUsername(props.getProperty(prefix + "user"));
			dbcp.setPassword(props.getProperty(prefix + "passwd"));
			dbcp.setMinIdle(5);
			dbcp.setMaxIdle(20);
			dbcp.setMaxActive(30);

			return dbcp;
		} catch (Exception e) {
			throw new RuntimeException("Cannot initialize dbcp", e);
		}
	}

	public static Connection getDBconnection() throws Exception {
		return dbcp.getConnection();
	}

	public static ResultSet sqlQuery(String statement) throws Exception {
		return getDBconnection().createStatement().executeQuery(statement);
	}

}

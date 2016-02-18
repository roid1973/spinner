package db.spinner;

import java.sql.*;

import utils.PropertiesUtils;

import com.google.appengine.api.utils.SystemProperty;

import com.google.appengine.api.utils.SystemProperty.Environment.Value;

//import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbcp.BasicDataSource;

public class DButils {

	private static BasicDataSource dbcp;

	private static Value env;
	private static String user = null;
	private static String passwd = null;
	private static String db = null;
	private static String driver = null;
	private static String url = null;

	static {

		try {
			initProperties();
			if (env != SystemProperty.Environment.Value.Production) {
				dbcp = setupDataSource();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Connection getDBconnection() throws Exception {
		Connection connection = null;
		if (env == SystemProperty.Environment.Value.Production) {
			connection = DriverManager.getConnection(url, user, passwd);			
		} else {
			connection = dbcp.getConnection();
		}
		return connection;
	}

	public static BasicDataSource setupDataSource() throws Exception {
		dbcp = new BasicDataSource();
		dbcp.setDriverClassName(driver);
		dbcp.setUrl(url);
		dbcp.setUsername(user);
		dbcp.setPassword(passwd);

		dbcp.setMinIdle(5);
		dbcp.setMaxIdle(20);
		dbcp.setMaxActive(30);

		return dbcp;

		
	}

	private static void initProperties() throws ClassNotFoundException {
		env = SystemProperty.environment.value();

		PropertiesUtils props = PropertiesUtils.getSpinnerProperties();
		user = props.getProperty("user");
		passwd = props.getProperty("passwd");
		db = props.getProperty("db");

		if (env == SystemProperty.Environment.Value.Production) {
			driver = props.getProperty("driver.production");
			Class.forName(driver);
			url = props.getProperty("url.production");
		} else {
			driver = props.getProperty("driver.test");
			Class.forName(driver);
			url = props.getProperty("url.test");
		}
		url = url + db;
	}

	public static ResultSet sqlQuery(String statement) throws Exception {
		ResultSet rs = null;
		Connection conn = getDBconnection();
		rs = conn.createStatement().executeQuery(statement);
		return rs;
	}

}

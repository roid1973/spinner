package junit.spinnerCalendarTest;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.*;

import db.spinner.DButils;

public class DBTest {

	@Test
	public void testGetConnection() {
		try {
			assertNotNull(DButils.getDBconnection());
			String statement = "select * from events";
			ResultSet rs = DButils.sqlQuery(statement);
			while (rs.next()) {
				String eventName = rs.getString("eventName");

				Date fromDate = rs.getTimestamp("fromDate");
				//System.out.println(eventName);
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//System.out.printf("from.date : %s%n", dateFormat.format(fromDate));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

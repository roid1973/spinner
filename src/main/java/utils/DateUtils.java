package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

	// public static Date stringToSpinnerEventDate(String stringDate, String TMZ) throws ParseException {
	// SimpleDateFormat dFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	// dFormater.setTimeZone(TimeZone.getTimeZone(TMZ));
	// Date date = dFormater.parse(stringDate);
	// return date;
	// }

	public static Date stringToSpinnerEventDate(String dateTime, String TMZ) {
		Date date = null;
		if (dateTime != null && dateTime.compareTo("") != 0) {
			try {
				SimpleDateFormat dFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm");
				dFormater.setTimeZone(TimeZone.getTimeZone(TMZ));
				date = dFormater.parse(dateTime);
			} catch (ParseException pe) {
				System.out.println("string is not in format dd/MM/yyyy HH:mm " + dateTime);
				System.out.println("trying to convert string to yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				date = stringToEventDateTime(dateTime);
			}
		}
		return date;
	}

	public static Date stringToEventDateTime(String dateTime) {
		Date date = null;
		if (dateTime != null && dateTime.compareTo("") != 0) {
			try {
				SimpleDateFormat dFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				date = dFormater.parse(dateTime);
			} catch (ParseException pe) {
				System.out.println("string is not in format yyyy-MM-dd'T'HH:mm:ss.SSS'Z': " + dateTime);
			}
		}
		return date;
	}

	public static Date stringToStudentBirthDate(String stringDate) {
		Date date = null;
		if (stringDate != null && stringDate.compareTo("") != 0) {
			try {
				SimpleDateFormat dFormater = new SimpleDateFormat("dd/MM/yyyy");
				date = dFormater.parse(stringDate);
			} catch (ParseException pe) {
				System.out.println("string is not in format dd/MM/yyyy: " + stringDate);
			}
		}
		return date;
	}

	public static String dateToString(Date date) {
		String sDate = null;
		if (date != null) {
			SimpleDateFormat dFormater = new SimpleDateFormat("dd/MM/yyyy");
			sDate = dFormater.format(date);
		}
		return sDate;
	}

	public static Date getFirstDateOfTheCurrentWeek() {
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(1);
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		Date d = c.getTime();
		return d;
	}

	public static Date getLastDateOfTheCurrentWeek() {
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(1);
		c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		Date d = c.getTime();
		return d;
	}

	public static Date addDaysToDate(Date d, int numberOfDaysToAdd) {
		Date newDate = null;
		if (d != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			c.add(Calendar.DATE, numberOfDaysToAdd);
			newDate = c.getTime();
		}
		return newDate;
	}
}

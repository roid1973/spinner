package utils;

public class SpinnerConstants {
	static String DAILY_STRING = "DAILY";
	static int DAILY = 1;
	static String WEEKLY_STRING = "WEEKLY";
	static int WEEKLY = 7;

	public static int getIntervalInt(String intervalString) {
		int interval = 0;
		if (intervalString.compareTo(DAILY_STRING) == 0) {
			interval = DAILY;
		}
		if (intervalString.compareTo(WEEKLY_STRING) == 0) {
			interval = WEEKLY;
		}
		return interval;
	}

}

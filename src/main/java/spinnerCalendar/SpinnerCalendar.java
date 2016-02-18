package spinnerCalendar;

import java.util.HashMap;

import db.spinner.DBspinner;

public class SpinnerCalendar {
	private HashMap<Integer, SpinnerEvent> eventsHashMap = null;

	// private static SpinnerCalendar spinnerCalendarInstance = null;

	public SpinnerCalendar(int classId) throws Exception {
		eventsHashMap = DBspinner.getSpinnerCalendarEventsFromDB(classId);
	}

	// public static SpinnerCalendar getSpinnerCalendarInstance() throws
	// Exception {
	// synchronized (SpinnerCalendar.class) {
	// if (spinnerCalendarInstance == null) {
	// spinnerCalendarInstance = new SpinnerCalendar();
	// spinnerCalendarInstance.initSpinnerEventsHashMap();
	// }
	// }
	// return spinnerCalendarInstance;
	// }

	public HashMap<Integer, SpinnerEvent> getSpinnerCalendarEventsHashMap() {
		return eventsHashMap;
	}

	public SpinnerEvent addSpinnerEventToSpinnerCalendar(SpinnerEvent se) throws Exception {
		int seId = findEvent(se);
		if (seId > 0) {
			System.out.println("Event already exist, eventId: " + seId);
			se = getSpinnerEvent(seId);
		} else {
			se = DBspinner.addSpinnerEventInToDB(se);
			eventsHashMap.put(se.getEventId(), se);
		}
		return se;
	}

	public SpinnerEvent updateSpinnerEventToSpinnerCalendar(int eventId, SpinnerEvent newEvent) throws Exception {
		SpinnerEvent se = null;
		if (eventsHashMap.containsKey(eventId)) {
			se = eventsHashMap.get(eventId);
			se.updateEventDetails(newEvent);
		} else {
			throw new Exception("updateSpinnerEventToSpinnerCalendar Failed: " + eventId + " not found in Spinner Calendar Event List");
		}
		return se;
	}

	private int findEvent(SpinnerEvent se) throws Exception {
		int seId = DBspinner.findEvent(se.getClassId(), se.getName(), se.getFromDate());
		return seId;
	}

	public void deleteSpinnerEventFromSpinnerCalendar(int eventId) throws Exception {
		if (eventsHashMap.containsKey(eventId)) {
			DBspinner.deleteSpinnerEventFromDB(eventId);
			eventsHashMap.remove(eventId);
		} else {
			throw new Exception("deleteSpinnerEventFromSpinnerCalendar Failed: " + eventId + " not found in Spinner Calendar Event List");
		}
	}

	public SpinnerEvent getSpinnerEvent(int eventId) throws Exception {
		SpinnerEvent se = eventsHashMap.get(eventId);
		if (se == null) {
			throw new Exception("getSpinnerEvent Failed: " + eventId + " not found in Spinner Calendar Event List");
		}
		return se;
	}


}

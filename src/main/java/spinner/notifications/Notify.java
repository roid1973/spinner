package spinner.notifications;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import spinnerCalendar.SpinnerEvent;
import spinnerClass.SpinnerClass;
import utils.PropertiesUtils;
import charcters.PersonSpinnerClass;

public class Notify {

	// public static final String API_KEY =
	// "AIzaSyBvGXuTQGTLFIUfFFvLpD46-6kn-P9vUxU";
	public static final String API_KEY = PropertiesUtils.getSpinnerProperties().getProperty("api.key");

	public static void notifyEventAvailablePlaces(SpinnerEvent se) throws Exception {
		String msg = "Available Place in: " + se.getName();
		List<Integer> standBy = se.getStandby();
		Iterator<Integer> it = standBy.iterator();
		while (it.hasNext()) {
			int studentId = it.next();
			String userTopic = "userTopic"+studentId;
			notifyMsgToUserTopic(msg,userTopic );			
		}		
	}
	
	public static void notifyNewEvents(SpinnerClass sc) throws Exception {
		String msg = sc.getSpinnerClassName()+": New events now open for registration";
		List<PersonSpinnerClass> p = sc.studentsList();
		Iterator<PersonSpinnerClass> it = p.iterator();
		while (it.hasNext()) {
			PersonSpinnerClass psc = it.next();
			int studentId = psc.getPersonId();
			String userTopic = "userTopic"+studentId;
			notifyMsgToUserTopic(msg,userTopic );			
		}		
	}


	private static void notifyMsgToUserTopic(String msg, String userTopic) throws Exception {
		// try {
		// Prepare JSON containing the GCM message content. What to send and
		// where to send.
		JSONObject jGcmData = new JSONObject();
		JSONObject jData = new JSONObject();
		// String msg = "Available Place in: " + se.getName();
		jData.put("message", msg);
		// Where to send GCM message.
		// String topics = "global";
		jGcmData.put("to", "/topics/" + userTopic);

		// What to send in GCM message.
		jGcmData.put("data", jData);

		// Create connection to send GCM Message request.
		URL url = new URL("https://android.googleapis.com/gcm/send");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("Authorization", "key=" + API_KEY);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);

		// Send GCM message content.
		OutputStream outputStream = conn.getOutputStream();
		outputStream.write(jGcmData.toString().getBytes());

		// Read GCM response.
		InputStream inputStream = conn.getInputStream();
		String resp = IOUtils.toString(inputStream);
		System.out.println(resp + "{msg:" + msg + "}" + "{topics:" + userTopic + "}");

		// System.out.println("Check your device/emulator for notification or logcat for "
		// + "confirmation of the receipt of the GCM message.");

		// } catch (IOException e) {
		// System.out.println("Unable to send GCM message.");
		// System.out.println("Please ensure that API_KEY has been replaced by the server "
		// +
		// "API key, and that the device's registration token is correct (if specified).");
		// e.printStackTrace();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}
}

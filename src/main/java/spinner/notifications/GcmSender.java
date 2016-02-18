package spinner.notifications;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import utils.PropertiesUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

public class GcmSender {
	
	public static final String API_KEY = "AIzaSyBvGXuTQGTLFIUfFFvLpD46-6kn-P9vUxU";
	public static final String topic = "global";

	public static void main(String[] args) throws Exception {	
		
		if (args.length < 1 || args.length > 2 || args[0] == null) {
			System.err.println("usage: ./gradlew run -Pargs=\"MESSAGE[,DEVICE_TOKEN]\"");
			System.err.println("");
			System.err.println("Specify a test message to broadcast via GCM. If a device's GCM registration token is\n"
					+ "specified, the message will only be sent to that device. Otherwise, the message \n" + "will be sent to all devices subscribed to the \"global\" topic.");
			System.err.println("");
			System.err.println("Example (Broadcast):\n" + "On Windows:   .\\gradlew.bat run -Pargs=\"<Your_Message>\"\n" + "On Linux/Mac: ./gradlew run -Pargs=\"<Your_Message>\"");
			System.err.println("");
			System.err.println("Example (Unicast):\n" + "On Windows:   .\\gradlew.bat run -Pargs=\"<Your_Message>,<Your_Token>\"\n"
					+ "On Linux/Mac: ./gradlew run -Pargs=\"<Your_Message>,<Your_Token>\"");
			System.exit(1);
		}
		try {
			// Prepare JSON containing the GCM message content. What to send and
			// where to send.
			JSONObject jGcmData = new JSONObject();
			JSONObject jData = new JSONObject();
			jData.put("message", args[0].trim());
			// Where to send GCM message.
			if (args.length > 1 && args[1] != null) {
				jGcmData.put("to", args[1].trim());
			} else {
				jGcmData.put("to", "/topics/"+topic);
			}
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
			System.out.println(resp);

			System.out.println("Check your device/emulator for notification or logcat for " + "confirmation of the receipt of the GCM message.");
		} catch (IOException e) {
			System.out.println("Unable to send GCM message.");
			System.out.println("Please ensure that API_KEY has been replaced by the server " + "API key, and that the device's registration token is correct (if specified).");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
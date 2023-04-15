package com.voice.common.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

@Service
public class SendSmsService {


	static String key = "";

	public static void sendSms(String message, String number)
	// public static void sendSms(String message,List<String> numberList)
	{
		// System.out.println(message);
		// System.out.println(number);
		try {

			String apiKey = key;
			String sendId = "VOICE";

			// important step...
			message = URLEncoder.encode(message, "UTF-8");
			String language = "english";

			String route = "v3";
			// String numString = "";
			// for(String number : numberList)
			// {
			// numString += number + ",";
			// }
			//
			// numString = numString.substring(0, numString.lastIndexOf(","));
			//

			String myUrl = "https://www.fast2sms.com/dev/bulkV2?authorization=" + apiKey + "&route=" + route
					+ "&sender_id=" + sendId + "&message=" + message + "&language=" + language + "&flash=0&numbers="
					+ number;
			// String
			// myUrl="https://www.fast2sms.com/dev/bulkV2?authorization="+apiKey+"&route="+route+"&sender_id="+sendId+"&message="+message+"&language="+language+"&flash=0&numbers="+numString;

			// sending get request using java..

			HttpResponse con = Unirest.get(myUrl).header("cache-control", "no-cache")
					.asString();

			System.out.println("Wait..............");

			int code = con.getStatus();

			System.out.println("Response code : " + code);

			StringBuffer response = new StringBuffer();

			BufferedReader br = new BufferedReader(new InputStreamReader(con.getRawBody()));

			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				response.append(line);
			}

			System.out.println(response);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}
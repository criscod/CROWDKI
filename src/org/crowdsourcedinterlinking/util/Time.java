package org.crowdsourcedinterlinking.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time {

	public static String currentTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// get current date time with Date()
		Date date = new Date();
		return dateFormat.format(date).toString();

	}
}

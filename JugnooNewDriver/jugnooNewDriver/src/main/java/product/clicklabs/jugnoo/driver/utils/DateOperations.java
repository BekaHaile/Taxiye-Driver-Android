package product.clicklabs.jugnoo.driver.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;

/**
 * date and time related operations functions
 * @author shankar
 *
 */
@SuppressLint("SimpleDateFormat")
public class DateOperations {

	public DateOperations(){
		
	}
	
	/**
	 * Converts UTC time to local time
	 * @param utcTime UTC time String
	 * @return Local time String
	 */
	@SuppressLint("SimpleDateFormat")
	public static String utcToLocal(String utcTime) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			Date myDate = simpleDateFormat.parse(utcTime);
			String localDate = sdf.format(myDate);
			return localDate;
		} catch (Exception e1) {
			e1.printStackTrace();
			return utcTime;
		}
	}

	
	public static Calendar getCalendarFromTimeStamp(String timeStamp){
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = format.parse(timeStamp);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date1);
			return calendar;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static String getTimeStampfromCalendar(Calendar calendar) {
	    long foo = calendar.getTimeInMillis();
	    Date date = new Date(foo);
	    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return formatter.format(date);
	}
	

	/**
	 * Converts date string from 2014-01-12 00:00 to 12 Jan, 2014 12:00 AM
	 * @param dateTime 
	 * @return
	 */
	public static String convertDate(String dateTime){
		try{
		String date = dateTime.split(" ")[0];
		
		String year = date.split("-")[0];
		String month = date.split("-")[1];
		String day = date.split("-")[2];
		
		
		if("01".equalsIgnoreCase(month)){
			month = "Jan";
		}
		else if("02".equalsIgnoreCase(month)){
			month = "Feb";
		}
		else if("03".equalsIgnoreCase(month)){
			month = "Mar";
		}
		else if("04".equalsIgnoreCase(month)){
			month = "Apr";
		}
		else if("05".equalsIgnoreCase(month)){
			month = "May";
		}
		else if("06".equalsIgnoreCase(month)){
			month = "Jun";
		}
		else if("07".equalsIgnoreCase(month)){
			month = "Jul";
		}
		else if("08".equalsIgnoreCase(month)){
			month = "Aug";
		}
		else if("09".equalsIgnoreCase(month)){
			month = "Sept";
		}
		else if("10".equalsIgnoreCase(month)){
			month = "Oct";
		}
		else if("11".equalsIgnoreCase(month)){
			month = "Nov";
		}
		else if("12".equalsIgnoreCase(month)){
			month = "Dec";
		}
		
		
		String time = dateTime.split(" ")[1];
		
		String hour = time.split(":")[0];
		String min = time.split(":")[1];
		
		String amOrpm = Integer.parseInt(hour)>=12?"PM":"AM";
		String newHour = "";
		
		if(Integer.parseInt(hour)==0){
			newHour = "12";
		}
		else if(Integer.parseInt(hour)<=12){
			newHour = hour;
		}
		else{
			newHour = ""+(Integer.parseInt(hour) - 12);
		}
		
		String finalTime = ""+ newHour + ":" + min + " " + amOrpm; 
		
		
		String finalDate = ""+ day + " " + month + ", " + year + " " + finalTime; 
		
		return finalDate;
		}
		catch(Exception e){
			e.printStackTrace();
			return dateTime;
		}
	}
	
	
	public static String getTimeAMPM(String dateTime){
		try{
		String time = dateTime.split(" ")[1];
		
		String hour = time.split(":")[0];
		String min = time.split(":")[1];
		
		String amOrpm = Integer.parseInt(hour)>=12?"PM":"AM";
		String newHour = "";
		
		if(Integer.parseInt(hour)==0){
			newHour = "12";
		}
		else if(Integer.parseInt(hour)<=12){
			newHour = hour;
		}
		else{
			newHour = ""+(Integer.parseInt(hour) - 12);
		}
		
		String finalTime = ""+ newHour + ":" + min + " " + amOrpm; 
		
		return finalTime;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	
	/**
	 * Converts date string from 2014-01-12 00:00 to 12th Jan, 2014
	 * @param dateTime 
	 * @return
	 */
	public static String getDate(String dateTime){
		try{
		String date = dateTime.split(" ")[0];
		
		String year = date.split("-")[0];
		String month = date.split("-")[1];
		String day = date.split("-")[2];
		
		if("01".equalsIgnoreCase(month)){
			month = "Jan";
		}
		else if("02".equalsIgnoreCase(month)){
			month = "Feb";
		}
		else if("03".equalsIgnoreCase(month)){
			month = "Mar";
		}
		else if("04".equalsIgnoreCase(month)){
			month = "Apr";
		}
		else if("05".equalsIgnoreCase(month)){
			month = "May";
		}
		else if("06".equalsIgnoreCase(month)){
			month = "Jun";
		}
		else if("07".equalsIgnoreCase(month)){
			month = "Jul";
		}
		else if("08".equalsIgnoreCase(month)){
			month = "Aug";
		}
		else if("09".equalsIgnoreCase(month)){
			month = "Sept";
		}
		else if("10".equalsIgnoreCase(month)){
			month = "Oct";
		}
		else if("11".equalsIgnoreCase(month)){
			month = "Nov";
		}
		else if("12".equalsIgnoreCase(month)){
			month = "Dec";
		}
		
		String finalDate = ""+ day + " " + month + ", " + year; 
		
		return finalDate;
		}
		catch(Exception e){
			e.printStackTrace();
			return dateTime;
		}
	}
	
	
	public static String getCurrentTime() {
	    long foo = System.currentTimeMillis();
	    Date date = new Date(foo);
	    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return formatter.format(date);
	}
	
	public static String getCurrentTimeInUTC() {
	    long foo = System.currentTimeMillis();
	    Date date = new Date(foo);
	    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	    return formatter.format(date);
	}
	
	
	
	
	
	
	
	
	
	
	public static String getSixtySecAfterCurrentTime() {
	    long foo = System.currentTimeMillis() + 60000;
	    Date date = new Date(foo);
	    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return formatter.format(date);
	}
	
	public static String getDelayMillisAfterCurrentTime(long delayMillis) {
	    long foo = System.currentTimeMillis() + delayMillis;
	    Date date = new Date(foo);
	    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return formatter.format(date);
	}
	
	
	public static long getTimeDifference(String time1, String time2){
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = format.parse(time1);
			Date date2 = format.parse(time2);
			long millis = Math.abs(date1.getTime() - date2.getTime());
			return millis;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 60000;
	}
	
	
	public static long getMilliseconds(String time1){
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date1 = format.parse(time1);
			long millis = date1.getTime();
			return millis;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 60000;
	}
	
	
	
	
	
	public static String getTimeStampFromMillis(long timeMillis){
		Date date = new Date(timeMillis);
	    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    return formatter.format(date);
	}
	
	
	
	
	
}

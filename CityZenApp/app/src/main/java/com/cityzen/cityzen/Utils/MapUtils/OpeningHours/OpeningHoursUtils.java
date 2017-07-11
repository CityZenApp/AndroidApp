package com.cityzen.cityzen.Utils.MapUtils.OpeningHours;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Valdio Veliu on 04/05/2017.
 */

public class OpeningHoursUtils {

    /**
     * opening_hours formats:
     * Days: Mo | Tu | We | Th | Fr | Sa | Su
     * Hours: 08:00-09:00
     * Always Open: 24/7
     * opening_hours example:
     *                       24/7
     *                       08:00-09:00
     *                       Mo-Sa 10:00-20:00
     *                       Fr 08:30-20:00
     *                       Mo-Fr 10:00-20:00; Sa 10:00-14:00
     *                       Tu-Th 18:00-03:00; Fr-Sa 18:00-04:00
     *                       Only this formats are supported by this class,
     *                       OSM has more opening_hours formats
     *                       <a href="http://wiki.openstreetmap.org/wiki/Key:opening_hours">Key:opening_hours</a>.
     *
     * @param opening_hours OSM opening_hours String
     * @return true if "NOW" is in the interval specified by opening_hours, false otherwise
     */
    public static boolean isOpenNow(String opening_hours) {
        try {
            if (opening_hours == "" || opening_hours.equals("")) return false;
            if (opening_hours.equals("24/7")) return true;
            if (opening_hours.contains(";"))
                return parseTwoTimeFrameOpeningHours(opening_hours);

            if (opening_hours.replaceAll("\\s+", "").length() == "08:00-09:00".length()/* 11 */ && opening_hours.charAt(5) == '-') {
                String startTime = opening_hours.substring(0, 5); //08:00
                String endTime = opening_hours.substring(6);      //09:00
                Time openingHours = Time.valueOf(startTime.replaceAll("\\s+", "") + ":00");//add seconds, remove spaces
                Time closingHours = Time.valueOf(endTime.replaceAll("\\s+", "") + ":00");//add seconds
                Time now = Time.valueOf(now());
                if (now.getTime() > openingHours.getTime() && now.getTime() < closingHours.getTime()) {
                    return true;
                }
            } else {
                return parseDayFormatStrings(opening_hours);
            }
            return false;
        } catch (Exception e) {
            //there may be string construction exceptions
            e.printStackTrace();
            return false;
        }
    }

    private static boolean parseTwoTimeFrameOpeningHours(String opening_hours) {
        String firstTimeStamp = opening_hours.substring(0, opening_hours.indexOf(";"));
        String secondTimeStamp = opening_hours.substring(opening_hours.indexOf(";") + 2);
        if (parseDayFormatStrings(firstTimeStamp))
            return true;
        else
            return parseDayFormatStrings(secondTimeStamp);
    }

    private static boolean parseDayFormatStrings(String opening_hours) {
        //parse the day of the string
        String dayModule = opening_hours.substring(0, 5);
        if (dayModule.contains(" ")) {
            //Fr 08:30-20:00
            String day = dayModule.substring(0, 2);
            DAY today = DAY.getDAY(day());
            if (day.equals(today.toString())) {
                String timing = opening_hours.substring(3);
                if (timing.length() == 11) { //make sure the string has the correct length
                    String startTime = timing.substring(0, 5); //08:30
                    String endTime = timing.substring(6);      //20:00
                    Time openingHours = Time.valueOf(startTime.replaceAll("\\s+", "") + ":00");//add seconds
                    Time closingHours = Time.valueOf(endTime.replaceAll("\\s+", "") + ":00");//add seconds
                    Time now = Time.valueOf(now());
                    if (now.getTime() > openingHours.getTime() && now.getTime() < closingHours.getTime()) {
                        return true;
                    }
                }
            }
        } else {
            //Mo-Sa 10:00-20:00
            String startDay = dayModule.substring(0, 2);
            String endDay = dayModule.substring(3);
            DAY today = DAY.getDAY(day());
            DAY startDayNr = DAY.valueOf(startDay);
            DAY endDayNr = DAY.valueOf(endDay);

            if (today.getDayID() >= startDayNr.getDayID() && today.getDayID() <= endDayNr.getDayID()) {
                String timing = opening_hours.substring(6);
                if (timing.length() == 11) { //make sure the string has the correct length
                    String startTime = timing.substring(0, 5); //08:30
                    String endTime = timing.substring(6);      //20:00
                    Time openingHours = Time.valueOf(startTime.replaceAll("\\s+", "") + ":00");//add seconds
                    Time closingHours = Time.valueOf(endTime.replaceAll("\\s+", "") + ":00");//add seconds
                    Time now = Time.valueOf(now());
                    if (now.getTime() > openingHours.getTime() && now.getTime() < closingHours.getTime()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String now() {
        SimpleDateFormat sdf = new SimpleDateFormat("H:m:s");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    /**
     * @return Day number of week (1 = Monday, ..., 7 = Sunday)
     */
    private static int day() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        //sunday -> 1
        //monday -> 2
        //...
        //saturday-> 7
        if (day == 1)
            return 7;
        else
            return (day - 1);
    }

    /**
     * @return Hour in day (0-23)
     */
    private static int hour() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String currentDateandTime = sdf.format(new Date());
        return Integer.parseInt(currentDateandTime);
    }

    /**
     * @return Minute in hour
     */
    private static int minutes() {
        SimpleDateFormat sdf = new SimpleDateFormat("m");
        String currentDateandTime = sdf.format(new Date());
        return Integer.parseInt(currentDateandTime);
    }
}

package capstone.com.cybertracker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Arjel on 7/16/2016.
 */

public class CyberTrackerUtilities {

    private static SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEE, d MMM yyyy hh:mm aaa");

    public static Long persistDate(Date date) {
        if (date != null) {
            return date.getTime();
        }
        return null;
    }

    public static Date retrieveDate(Long date) {
        return new Date(date);
    }

    public static Boolean retrieveBool(Integer value) {
        if(value == 1) {
            return true;
        } else {
            return false;
        }
    }

    public static String getDisplayDate(Date date) {
        return displayDateFormat.format(date);
    }

}

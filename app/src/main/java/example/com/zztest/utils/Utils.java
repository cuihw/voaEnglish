package example.com.zztest.utils;

import android.util.Log;

import org.jsoup.nodes.Element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {


    private static final String TAG = "Utils";

    public static long getDateByTitle(String title) {
        long retDate = 0;
        String dateString = getStringInBracket(title);
        if (dateString != null) {
            // 2014-8-6
            String number = dateString.substring(0, dateString.indexOf("-"));
            dateString = dateString.substring(dateString.indexOf("-") + 1);
            String number2 = dateString.substring(0, dateString.indexOf("-"));
            if (number2.length() == 1) {
                number = number + "0" + number2;
            } else {
                number = number + number2;
            }

            dateString = dateString.substring(dateString.indexOf("-") + 1);
            number2 = dateString.substring(0);
            if (number2.length() == 1) {
                number = number + "0" + number2;
            } else {
                number = number + number2;
            }

            retDate = Long.parseLong(number);
        }
        Log.d(TAG, "date = " + retDate + ", title = " + title);
        return retDate;
    }

    static private String regStr =  "\\(([^)]*)\\)";
    static private Pattern mPattern = Pattern.compile(regStr);

    public static String getStringInBracket(String expression) {

        Matcher matcher = mPattern.matcher(expression);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static String getElementLink(Element ele) {
        String linkHref = ele.attr("href");
        return linkHref;
    }


}

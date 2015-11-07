package org.persac.service;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author mzhokha
 * @since 11/28/13
 */
public class MainUtil {

    public static void main(String[] args) throws ParseException {
//        Date date = new SimpleDateFormat("dd-MM-yyyy").parse("28-11-2013");
//        System.out.println(date);

        String year = "11-12-2013".substring(0,2);
        System.out.println(Integer.valueOf(year));

        System.out.println(new Date().toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formatedDate = dateFormat.format(new Date());
        System.out.println(formatedDate);
        System.out.println("2013-12-25".compareToIgnoreCase(formatedDate));


        System.out.println("******");
        System.out.println("2013-11-12".substring(0,4));
        System.out.println("2013-11-12".substring(5,7));

        String startDate = "06/27/2007";
        DateFormat df = new SimpleDateFormat("mm/dd/yyyy");
        Date startDate1 = df.parse(startDate);
        String newDate = df.format(startDate1);
        System.out.println(newDate);

        DateTime dateTime = new DateTime(startDate1);
        System.out.println(dateTime.getDayOfWeek());
    }
}

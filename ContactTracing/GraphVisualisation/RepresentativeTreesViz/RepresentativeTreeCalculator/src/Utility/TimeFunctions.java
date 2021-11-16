/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utility;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 *
 * @author MaxSondag
 */
public class TimeFunctions {

    /**
     * Date in format YYYY-MM-DD from a London timezone
     *
     * @param date
     * @return
     */
    public static long dateToUnixTimestamp(String date) {
        ZoneId londonTimeZone = ZoneId.of("Europe/London");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime ldt = LocalDate.parse(date, formatter).atStartOfDay();//no seconds, so set at start of date
        ZonedDateTime zd = ldt.atZone(londonTimeZone);
        
        return zd.toEpochSecond();
    }

}

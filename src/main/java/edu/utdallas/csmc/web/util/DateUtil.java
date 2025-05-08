package edu.utdallas.csmc.web.util;

import org.springframework.lang.Nullable;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.*;

public class DateUtil {

    public static Date trimDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(HOUR_OF_DAY, 0);
        cal.set(MINUTE, 0);
        cal.set(SECOND, 0);
        cal.set(MILLISECOND, 0);
        return cal.getTime();
    }

    public static int compareTimeWithCurrentTime(Date date1) {
        if (date1.before(new Date())) {
            return -1;
        }
        return 1;
    }

    public static boolean isStartTimeBeforeCurrentTime(Date startTime) {
        Date currTime = new Date();
        if (startTime.before(currTime) || startTime.compareTo(currTime) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isDateLaterThanCurrentDate(Date date) {
        Date d = trimDate(date);
        Date currDate = trimDate(new Date());
        if (d.after(currDate)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isDateEqualToCurrentDate(Date date) {
        Date d = trimDate(date);
        Date currDate = trimDate(new Date());
        if (d.compareTo(currDate) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isTimeLaterThanCurrentTime(Time time) {
        java.util.Date today = new java.util.Date();
        java.sql.Time currTime = new Time(today.getTime());
        if (time.after(currTime)) {
            return true;
        } else {
            return false;
        }
    }

    public static DateFormat dateFormatTo12Hours = new SimpleDateFormat("MM/dd/yy hh:mm a");

    public static DateFormat dateFormatToStartOfDay = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
    public static DateFormat dateFormatToEndOfDay = new SimpleDateFormat("yyyy-MM-dd 23:59:59");

    public static Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    public static Date atEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public String dateFormat(@Nullable Date date) {
        if (date != null) {
            return dateFormatTo12Hours.format(date);
        }
        return "";
    }


    public static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //0 means Sunday and 6 means Saturday
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static Date addNDays(Date inputDate, long n) {
        if (inputDate == null) {
            Date newDate = new Date();
            return new Date(newDate.getTime() + (n * 24 * 3600 * 1000) - 1);
        }
        return new Date(inputDate.getTime() + (n * 24 * 3600 * 1000) - 1);
    }

    public static Date subtractNDays(Date inputDate, long n) {
        if (inputDate == null) {
            Date newDate = new Date();
            return new Date(atEndOfDay(newDate).getTime() - (n * 24 * 3600 * 1000));
        }
        return new Date(atEndOfDay(inputDate).getTime() - (n * 24 * 3600 * 1000));
    }

    public static List<Date> listOfDatesBetweenGivenDates(Date startDate, Date endDate) {
        List<Date> datePeriod = new ArrayList<>();
        long numberOfDays = ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant());
        int day = 1;
        while (day <= numberOfDays) {
            datePeriod.add(DateUtil.addNDays(startDate, day));
            day++;
        }
        return datePeriod;
    }

    public static DateFormat timeFormatTo12Hours = new SimpleDateFormat("hh:mm a");
    public static DateFormat dateFormatToHoursMins = new SimpleDateFormat("hh:mm a");

    public static DateFormat dateFormatToTime = new SimpleDateFormat("hh:mm:ss");

    public static DateFormat dateFormatToDate = new SimpleDateFormat("yyyy-MM-dd");
}

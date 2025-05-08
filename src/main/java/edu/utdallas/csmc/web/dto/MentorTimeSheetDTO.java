package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

@Data
public class MentorTimeSheetDTO {

    LocalDateTime weekStartDate;

    HashMap<Date, HashMap<Date, Date>> timesheet;

    String dayofWeek;

    LocalDateTime dayOfWeekDateTime;

    HashMap<String, String> timeInOut;


    public LocalDateTime getnextDate(LocalDateTime currentDate) {
        LocalDateTime curdate = currentDate;
        return curdate.minusDays(-1);

    }

    public String getStringDate(String stringDate) {
        return stringDate.substring(0, 10);
    }

    public LocalDateTime getCurrentStartDateofTheWeek() {
        Date weekStartDate = new Date();
        LocalDateTime dateToday = LocalDateTime.now();
        int minudays = 0;
        String datyofWeek = dateToday.getDayOfWeek().toString();
        switch (datyofWeek.toLowerCase()) {
            case "sunday":
                minudays = 0;
                break;
            case "monday":
                minudays = 1;
                break;
            case "tuesday":
                minudays = 2;
                break;
            case "wednesday":
                minudays = 3;
                break;
            case "thursday":
                minudays = 4;
                break;
            case "saturday":
                minudays = 6;
                break;
            case "friday":
                minudays = 5;
                break;
        }
        return LocalDateTime.now().minusDays(minudays);
    }

    public LocalDateTime getPrevWeekStartDate(LocalDateTime curreWeekStartDate) {
        return curreWeekStartDate.minusWeeks(1);
    }

    public LocalDateTime getNextWeekStartDate(LocalDateTime curreWeekStartDate) {
        return curreWeekStartDate.minusWeeks(-1);
    }

    public Date getLocalDateAsDate(LocalDateTime localDateTime) {

        return Date.from(localDateTime.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public String getTimefromDate(Date dateinOut) {
        SimpleDateFormat localDateFormat = new SimpleDateFormat("hh.mm aa");
        long date = dateinOut.getTime();
        String time = localDateFormat.format(date);
        return time;
    }

    public String getDayOfWeek(LocalDateTime localDateTime) {
        return localDateTime.getDayOfWeek().toString();
    }

    public String getDate(LocalDateTime localDateTime) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(localDateTime);
    }


}

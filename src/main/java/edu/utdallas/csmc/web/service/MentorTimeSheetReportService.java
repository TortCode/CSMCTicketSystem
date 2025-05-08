package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.MentorTimeSheetDTO;
import edu.utdallas.csmc.web.helper.MentorTimeSheetReportHelper;
import edu.utdallas.csmc.web.model.schedule.Timesheet;
import edu.utdallas.csmc.web.repository.TimesheetRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Log4j2
public class MentorTimeSheetReportService {

    @Autowired
    private DefaultUsernameService defaultUsernameService;

    @Autowired
    private TimesheetRepository timesheetRepository;

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

    public LocalDateTime getLocalDateFromString(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDateTime.parse(dateString != null ? dateString : "", formatter);
    }


    public HashMap<Date, HashMap<Date, Date>> getTimeSheetofUser() {

        List<Timesheet> userTimeSheet = timesheetRepository.getTimeSheetForUser(defaultUsernameService.getUsername());

        MentorTimeSheetReportHelper mentorTimeSheetReportHelper = new MentorTimeSheetReportHelper();

        HashMap<Date, HashMap<Date, Date>> mentorTimeSheet = mentorTimeSheetReportHelper.getTimeSheetReport(userTimeSheet);

        return mentorTimeSheet;
    }

    public String getTimefromDate(Date dateinOut) {
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
        long date = dateinOut.getTime();
        String time = localDateFormat.format(date);
        return time;
    }

    public Date getLocalDateAsDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public String getLocalDateAsString(LocalDateTime localDateTime) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(localDateTime);
    }

    public List<MentorTimeSheetDTO> getTimeSheetoftheWeek(String startofWeekDate, HashMap<Date, HashMap<Date, Date>> userTimeSheet) {
        List<MentorTimeSheetDTO> mentorTimeSheetDTOList = new ArrayList<>();
        MentorTimeSheetDTO mentorTimeSheetDTO = new MentorTimeSheetDTO();
        LocalDateTime startofWeekDateTime = getLocalDateFromString(startofWeekDate);


        for (int i = 0; i < 7; i++) {

            mentorTimeSheetDTO.setWeekStartDate(startofWeekDateTime);
            mentorTimeSheetDTO.setDayOfWeekDateTime(startofWeekDateTime.plusDays(i));
            mentorTimeSheetDTO.setDayofWeek(mentorTimeSheetDTO.getWeekStartDate().getDayOfWeek().toString());
            HashMap<String, String> timeInOut = new HashMap<>();

            Date timeofDay = getLocalDateAsDate(mentorTimeSheetDTO.getDayOfWeekDateTime());
            for (Date timeIn : userTimeSheet.get(timeofDay).keySet()) {
                timeInOut.put(getTimefromDate(timeIn), getTimefromDate(userTimeSheet.get(timeofDay).get(timeIn)));
            }

            mentorTimeSheetDTO.setTimeInOut(timeInOut);

            mentorTimeSheetDTOList.add(mentorTimeSheetDTO);
        }

        return mentorTimeSheetDTOList;
    }


}

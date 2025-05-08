package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.AdminTimesheetResultDTO;
import edu.utdallas.csmc.web.model.schedule.Timesheet;
import edu.utdallas.csmc.web.model.user.User;
import lombok.extern.log4j.Log4j2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@Log4j2

public class AdminTimesheetHelper {
    public void buildMentorsList(final List<AdminTimesheetResultDTO> mentors, List<User> mentorList) {
        for (User mentor : mentorList) {
            AdminTimesheetResultDTO adminTimesheetResultDTO = new AdminTimesheetResultDTO();
            adminTimesheetResultDTO.setMentorName(mentor.getName());
            adminTimesheetResultDTO.setUserId(mentor.getId().toString());
            mentors.add(adminTimesheetResultDTO);
        }
    }

    public TreeMap<Date, HashMap<Date, Date>> getTimesheetReport(List<Timesheet> timeSheet) {
        TreeMap<Date, HashMap<Date, Date>> timeSheetReport = new TreeMap<Date, HashMap<Date, Date>>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        for (Timesheet ts : timeSheet) {
            String day = simpleDateFormat.format(ts.getTimeIn());
            try {
                Date dtt = simpleDateFormat.parse(simpleDateFormat.format(ts.getTimeIn()));
                if (timeSheetReport.containsKey(dtt)) {
                    timeSheetReport.get(dtt).put(ts.getTimeIn(), ts.getTimeOut());
                } else {
                    HashMap<Date, Date> times = new HashMap<Date, Date>();
                    times.put(ts.getTimeIn(), ts.getTimeOut());
                    timeSheetReport.put(dtt, times);
                }
            } catch (Exception e) {

            }
        }

        return timeSheetReport;
    }
}
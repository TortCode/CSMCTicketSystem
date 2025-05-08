package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.model.schedule.Timesheet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MentorTimeSheetReportHelper {

    public HashMap<Date, HashMap<Date, Date>> getTimeSheetReport(List<Timesheet> mentorTimeSheetReport) {


        HashMap<Date, HashMap<Date, Date>> timeSheetReport = new HashMap<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

        for (Timesheet ts : mentorTimeSheetReport) {

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

package edu.utdallas.csmc.web.dto;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

@Data
public class AdminTimesheetResultDTO {
    String userId;
    String mentorName;
    Date startDate;
    Date endDate;
    Boolean invalidDateMessage;

    TreeMap<Date, HashMap<Date,Date>> timesheet;
    int noOfDays;



}

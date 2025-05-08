package edu.utdallas.csmc.web.dto;

import edu.utdallas.csmc.web.model.schedule.ShiftSubject;
import lombok.Data;

import java.util.Date;

@Data
public class AdminScheduleTimesResultDTO {
    String day;
    Date startTime;
    Date endTime;
    ShiftSubject subject;
    int maxMentors;

}

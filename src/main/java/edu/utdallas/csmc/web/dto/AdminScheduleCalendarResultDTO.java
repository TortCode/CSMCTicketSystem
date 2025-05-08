package edu.utdallas.csmc.web.dto;

import edu.utdallas.csmc.web.model.misc.OperationHours;
import edu.utdallas.csmc.web.model.misc.Semester;
import edu.utdallas.csmc.web.model.misc.Subject;
import edu.utdallas.csmc.web.model.schedule.Schedule;
import edu.utdallas.csmc.web.model.user.User;
import lombok.Data;

import java.util.List;

@Data
public class AdminScheduleCalendarResultDTO {
    List<OperationHours> hours;
    List<User> mentors;
//    Semester semester;
    Schedule schedule;
    List<Subject> subjects;
}

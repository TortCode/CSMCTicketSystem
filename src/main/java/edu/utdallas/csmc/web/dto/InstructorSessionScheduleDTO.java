package edu.utdallas.csmc.web.dto;

import java.util.List;
import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.session.ScheduledSession;

import lombok.Data;


@Data
public class InstructorSessionScheduleDTO {
    Section section;
    List<ScheduledSession> quizzes;
    List<ScheduledSession> sessions;
}
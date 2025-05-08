package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MentorStaffScheduleResultDTO {
    Date currentDate;
    List<MentorStaffScheduleShiftResultDTO> scheduledShifts;
    List<MentorStaffScheduleSessionResultDTO> sessions;
    List<MentorStaffScheduleQuizzesResultDTO> quizzes;
}

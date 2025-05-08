package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MentorStaffScheduleShiftResultDTO {

    Date startTime;
    Date endTime;
    String shiftLeader;
    List<MentorStaffScheduleSubjectDetailsDTO> subjects;

}

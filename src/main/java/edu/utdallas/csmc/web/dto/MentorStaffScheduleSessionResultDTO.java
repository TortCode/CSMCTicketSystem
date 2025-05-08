package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class MentorStaffScheduleSessionResultDTO {

    Date startTime;
    String location;
    String topic;
    String mentors;
    UUID sessionTimeSlotId;
    List<MentorFileDetailsDTO> files;

}

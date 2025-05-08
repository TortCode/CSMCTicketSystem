package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class MentorStaffScheduleQuizzesResultDTO {
    String location;
    String topic;
    String description;
    List<MentorFileDetailsDTO> files;
}

package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MentorDisplayMentorsResultDTO {
    UUID mentorId;
    String preferredName;
    String userName;
    List<MentorDisplaySpecialtyResutltDTO> specialties;
}

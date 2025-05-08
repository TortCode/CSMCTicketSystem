package edu.utdallas.csmc.web.dto;

import lombok.Data;

@Data
public class MentorDisplaySpecialtyResutltDTO {
    Integer specialtyRating;
    Boolean showOnCalendar;
    String subjectName;
    String subjectColor;
    String subjectAbbreviation;
}

package edu.utdallas.csmc.web.dto;

import lombok.Data;

@Data
public class AdminSectionResultDTO {
    public String courseNumber;
    public String courseName;
    public int sectionNo;
    public String semester;
    public String instructor;
    public String ta;
    public int noofStudents;
    public String adminNotes;
}

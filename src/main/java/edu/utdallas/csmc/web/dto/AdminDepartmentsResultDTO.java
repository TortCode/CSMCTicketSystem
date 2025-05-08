package edu.utdallas.csmc.web.dto;

import lombok.Data;

@Data
public class AdminDepartmentsResultDTO {
    String departmentId;
    String name;
    String abbreviation;
    String adminNotes;
    String username;
}

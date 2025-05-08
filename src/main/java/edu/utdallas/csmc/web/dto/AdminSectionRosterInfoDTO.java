package edu.utdallas.csmc.web.dto;

import lombok.Data;

@Data
public class AdminSectionRosterInfoDTO {
    String firstName;
    String lastName;
    String netId;
    String department;
    String courseNumber;
    String sectionNumber;

    @Override
    public String toString() {
        return firstName + " " + lastName + "\t" + netId + "\t" + department + " " + courseNumber + "." + sectionNumber;
    }
}

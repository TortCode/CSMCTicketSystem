package edu.utdallas.csmc.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RequestGradeAttendanceObjectDTO {
    int grade;
    UUID attendance;

    public RequestGradeAttendanceObjectDTO(String grade, String attendance) {
        this.grade = Integer.parseInt(grade);
        this.attendance = UUID.fromString(attendance);
    }

    public RequestGradeAttendanceObjectDTO(){

    }

}

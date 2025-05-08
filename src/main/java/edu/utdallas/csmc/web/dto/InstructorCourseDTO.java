package edu.utdallas.csmc.web.dto;

import edu.utdallas.csmc.web.model.course.Course;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class InstructorCourseDTO {
    UUID id;
    Course course;
    String number;
    int rosterLength;
}

package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.StudentCourseResultDTO;
import edu.utdallas.csmc.web.model.course.Section;

import java.util.ArrayList;
import java.util.List;

/**
 * This class has all the helper functions that will convert Model objects into DTO objects with the required processing
 * for the Student Courses View.
 */
public class StudentCourseHelper {

    /**
     * This helper function converts the Section Model to the required StudentCourseResultDTO that will be sent back to the view.
     */
    public List<StudentCourseResultDTO> setCourseDetails(List<Section> studentCourse) {
        List<StudentCourseResultDTO> studentCourseResultDTO = new ArrayList<>();
        for(Section section: studentCourse){
            StudentCourseResultDTO studentCourseResult = new StudentCourseResultDTO();
            studentCourseResult.setCourseName(section.getCourse().getName());
            studentCourseResult.setCourseNumber(section.getCourse().getDepartment().getAbbreviation() + " " + section.getCourse().getNumber());
            studentCourseResult.setSectionNumber(section.getNumber());

            StringBuilder name = new StringBuilder();
            for(int i=0; i<section.getInstructors().size(); i++) {
                name.append(section.getInstructors().get(i).getFirstName() + " " + section.getInstructors().get(i).getLastName());
                if(i < section.getInstructors().size() - 1){
                    name.append(", ");
                }
            }
            studentCourseResult.setName(name.toString());

            studentCourseResultDTO.add(studentCourseResult);
        }
        return studentCourseResultDTO;
    }
}

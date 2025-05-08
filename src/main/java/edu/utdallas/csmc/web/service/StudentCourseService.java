package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.StudentCourseResultDTO;
import edu.utdallas.csmc.web.helper.StudentCourseHelper;
import edu.utdallas.csmc.web.model.course.Course;
import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.repository.CourseRepository;
import edu.utdallas.csmc.web.repository.SectionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines all the service functions related to the Student Courses View.
 */
@Service
@Log4j2
public class StudentCourseService {

    @Autowired
    private DefaultUsernameService defaultUsernameService;

    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Value("${app.sandbox:false}")
    private boolean isSandbox;
    /**
     * This function fetches all the details related to the courses associated with the student in that semester.
     */
    public List<StudentCourseResultDTO> getCourseDetails() {
        if (isSandbox) {
            return getAllSupportedCourseDetails();
        } else {
            List<Section> studentCourse = sectionRepository.getCourseDetails(defaultUsernameService.getUsername());

            StudentCourseHelper studentCourseHelper = new StudentCourseHelper();
            List<StudentCourseResultDTO> studentCourseResultDTO = studentCourseHelper.setCourseDetails(studentCourse);

            return studentCourseResultDTO;
        }
    }

    public List<StudentCourseResultDTO> getAllSupportedCourseDetails() {
        List<StudentCourseResultDTO> studentCourseResultDTOList = new ArrayList<>();
        List<Course> courses = courseRepository.findCourseBySupported(true);
        for (Course course : courses) {
            StudentCourseResultDTO studentCourseResultDTO = new StudentCourseResultDTO();
            studentCourseResultDTO.setCourseName(course.getName());
            studentCourseResultDTO.setCourseNumber(course.getFullNumber());
            studentCourseResultDTOList.add(studentCourseResultDTO);
        }
        return studentCourseResultDTOList;
    }
}

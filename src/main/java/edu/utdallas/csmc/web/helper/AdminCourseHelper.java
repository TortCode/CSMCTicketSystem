package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.AdminCourseDetailsResultDTO;
import edu.utdallas.csmc.web.dto.AdminCoursesResultDTO;
import edu.utdallas.csmc.web.model.course.Course;
import edu.utdallas.csmc.web.model.course.Department;
import edu.utdallas.csmc.web.model.user.User;
import lombok.extern.log4j.Log4j2;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Log4j2
public class AdminCourseHelper {

    public void buildAdminCourseResultDTOList(final List<AdminCoursesResultDTO> adminCoursesResultDTOList, List<Course> courseList) {
        for (Course currentCourse : courseList) {
            AdminCoursesResultDTO adminCoursesResultDTO = new AdminCoursesResultDTO();
            adminCoursesResultDTO.setCourseId(currentCourse.getId().toString());
            adminCoursesResultDTO.setNumber(currentCourse.getDepartment().getAbbreviation() + " " + currentCourse.getNumber());
            adminCoursesResultDTO.setName(currentCourse.getName());

            // Display maximum 20 characters of Description
            if (currentCourse.getDescription() != null) {
                String description = currentCourse.getDescription();
                adminCoursesResultDTO.setDescription(description.length() > 20 ? description.substring(0, 20) : description);
            }

            adminCoursesResultDTOList.add(adminCoursesResultDTO);
        }
    }

    public void buildDepartmentsList(final List<String> departments, List<Department> departmentList) {
        for (Department currentDepartment : departmentList) {
            departments.add(currentDepartment.getName());
        }
    }

    public Course buildNewCourseModel(Optional<Department> department, Optional<User> user, AdminCourseDetailsResultDTO adminCourseDetailsResultDTO) {
        Course newCourse = new Course();
        if (department.isPresent()) {

            newCourse.setDepartment(department.get());
            newCourse.setName(adminCourseDetailsResultDTO.getName());
            newCourse.setNumber(adminCourseDetailsResultDTO.getNumber());
            newCourse.setSupported(adminCourseDetailsResultDTO.isSupported());
            newCourse.setCreatedOn(new Date());
            newCourse.setLastModifiedOn(new Date());
            // Add these fields if the user is present
            if (user.isPresent()) {
                newCourse.setCreatedBy(user.get());
                newCourse.setLastModifiedBy(user.get());
            }
            // Add description if present
            if (adminCourseDetailsResultDTO.getDescription() != null) {
                newCourse.setDescription(adminCourseDetailsResultDTO.getDescription());
            }
        } else {
            log.info("Invalid department name found : " + adminCourseDetailsResultDTO.getDepartmentName());
        }

        return newCourse;
    }

    public void buildAdminCourseDetailsFromCourse(final AdminCourseDetailsResultDTO adminCourseDetailsResultDTO, Course currentCourse) {
        adminCourseDetailsResultDTO.setDepartmentName(currentCourse.getDepartment().getName());
        adminCourseDetailsResultDTO.setNumber(currentCourse.getNumber());
        adminCourseDetailsResultDTO.setName(currentCourse.getName());
        adminCourseDetailsResultDTO.setSupported(currentCourse.isSupported());
        adminCourseDetailsResultDTO.setDescription(currentCourse.getDescription());
    }

    public void buildUpdatedCourseFromDTO(final Course currentCourse, AdminCourseDetailsResultDTO adminCourseDetailsResultDTO, Optional<User> user, Optional<Department> department) {
        if (department.isPresent()) {
            currentCourse.setDepartment(department.get());
        }

        currentCourse.setNumber(adminCourseDetailsResultDTO.getNumber());
        currentCourse.setName(adminCourseDetailsResultDTO.getName());
        currentCourse.setSupported(adminCourseDetailsResultDTO.isSupported());
        if (adminCourseDetailsResultDTO.getDescription() != null) {
            currentCourse.setDescription(adminCourseDetailsResultDTO.getDescription());
        }

        if (user.isPresent()) {
            currentCourse.setLastModifiedBy(user.get());
        }
        currentCourse.setLastModifiedOn(new Date());
    }

}

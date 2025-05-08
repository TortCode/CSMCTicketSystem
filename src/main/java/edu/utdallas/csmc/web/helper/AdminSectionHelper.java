package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.AdminSectionResultDTO;
import edu.utdallas.csmc.web.model.course.Course;
import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.misc.Semester;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.service.DefaultUsernameService;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
public class AdminSectionHelper {

    public void printSectionList(final List<Section> sections){
        System.out.println("printing in helper "+sections);
    }

    public void buildSection(final Section section, Course course, Semester semester, List<User> instructor, String secNumber, String description, User createdBy){
//        System.out.println(semester+" "+course+" "+instructor+" "+secNumber);
        if(semester != null && course != null && instructor!=null && secNumber!= null){
            section.setCourse(course);
            section.setSemester(semester);
            section.setInstructors(instructor);
            section.setNumber(secNumber);
            section.setStudents(null);
            section.setDescription(description);
            section.setTeaching_assistants(null);
            section.setCreatedBy(createdBy);
            section.setCreatedOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
            section.setLastModifiedBy(createdBy);
            section.setLastModifiedOn(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        }
    }

    public void buildSectionResultDTO(final List<AdminSectionResultDTO> adminSectionResultDTOList, List<Section> sections){
        AdminSectionResultDTO adminSectionResultDTO;

        for(Section section:sections){
            if(section!=null){
                adminSectionResultDTO = new AdminSectionResultDTO();
                adminSectionResultDTO.setCourseName(section.getCourse().getName());
                adminSectionResultDTO.setCourseNumber(section.getCourse().getNumber());
//                adminSectionResultDTO.setSectionNo(Integer.parseInt(section.getNumber()));
                adminSectionResultDTO.setSemester(section.getSemester().getSeason()+" "+section.getSemester().getYear());
                adminSectionResultDTO.setNoofStudents(section.getStudents().size());
//                if(section.getTeaching_assistants().size > 0){
//                    adminSectionResultDTO.setTa(section.getTeaching_assistants().get(0).getUsername());
//                }
//                if(section.setInstructor().size > 0){
//                    adminSectionResultDTO.setInstructor(section.getInstructors().get(0).getUsername());
//                }
                adminSectionResultDTO.setAdminNotes("find where it is");
                adminSectionResultDTOList.add(adminSectionResultDTO);
            }
        }

        for(AdminSectionResultDTO adminSectionResultDTO1 : adminSectionResultDTOList){
            System.out.println(adminSectionResultDTO1.getCourseName());
            System.out.println(adminSectionResultDTO1.getCourseNumber());
            System.out.println(adminSectionResultDTO1.getInstructor());
            System.out.println(adminSectionResultDTO1.getNoofStudents());
            System.out.println(adminSectionResultDTO1.getSectionNo());
            System.out.println(adminSectionResultDTO1.getSemester());
            System.out.println(adminSectionResultDTO1.getAdminNotes());

        }
    }

}

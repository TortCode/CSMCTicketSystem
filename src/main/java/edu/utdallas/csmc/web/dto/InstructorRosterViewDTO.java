package edu.utdallas.csmc.web.dto;

import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.model.course.Section;

import java.util.List;
import lombok.Data;

@Data
public class InstructorRosterViewDTO {
    List<User> roster;
    Section section;
}

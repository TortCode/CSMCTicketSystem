package edu.utdallas.csmc.web.dto;

import edu.utdallas.csmc.web.model.misc.Subject;
import edu.utdallas.csmc.web.model.user.User;
import lombok.Data;

@Data
public class MentorDTO {
    User mentor;
    Subject subject;
    boolean assigned;
}

package edu.utdallas.csmc.web.dto;

import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.file.File;
import edu.utdallas.csmc.web.model.misc.Room;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;
import edu.utdallas.csmc.web.model.session.SessionType;
import edu.utdallas.csmc.web.model.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

public class AdminUnScheduledSessionDTO {

    public int repeats;
    public List<SessionTimeSlot> timeslots;
    public String defaultLocation;
    public int defaultCapacity;
    public String defaultDuration;
    public String id;
    public String topic;
    public String description;
    public String studentInstructions;
    public String mentorInstructions;
    public List<Section> sections;
    public SessionType type;
    public boolean graded;
    public boolean numericGrade;
    public String color;
    public String lastModifiedBy;
    public String createdBy;
    public Date lastModifiedOn;
    public Date createdOn;
    public List<File> files;
    public String roomId;
}

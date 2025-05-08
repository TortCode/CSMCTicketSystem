package edu.utdallas.csmc.web.model.session;

import edu.utdallas.csmc.web.model.misc.Swipe;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

//    FIXME: Original Database id -> (attendance table) id. Current Implementation shows id -> (session_attendance table) id. Check if change needed.
@Entity
@Getter
@Setter
@Table(name = "scheduled_session_attendance")
@DiscriminatorValue(value = "scheduled")
public class ScheduledSessionAttendance extends SessionAttendance implements Serializable {

    @ManyToMany
    @JoinTable(name = "scheduled_session_swipes", joinColumns = @JoinColumn(name = "attendance_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "swipe_id", referencedColumnName = "id"))
    private List<Swipe> swipes;

}

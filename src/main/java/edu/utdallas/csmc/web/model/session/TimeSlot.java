package edu.utdallas.csmc.web.model.session;

import edu.utdallas.csmc.web.model.event.Event;
import edu.utdallas.csmc.web.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "timeslot")
@Inheritance(strategy = InheritanceType.JOINED)
// @DiscriminatorColumn(name = "discr", discriminatorType =
// DiscriminatorType.STRING, columnDefinition = "VARCHAR(255)")
// @DiscriminatorValue(value = "timeslot")
@DiscriminatorOptions(force = true)
public class TimeSlot extends Event implements Serializable {

    @OneToMany(mappedBy = "timeSlot", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private List<SessionAttendance> attendances;

    public SessionAttendance getAttendance(User user) {
        for (SessionAttendance attendance : this.attendances) {
            if (attendance.getUser().getUsername().equals(user.getUsername())) {
                return attendance;
            }
        }
        return null;
    }

}

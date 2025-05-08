package edu.utdallas.csmc.web.model.session;

import edu.utdallas.csmc.web.model.misc.Room;
import edu.utdallas.csmc.web.model.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "scheduled_session")
@DiscriminatorValue(value="scheduledsession")
public class ScheduledSession extends Session implements Serializable {

    @Column(name = "repeats", nullable = false)
    private int repeats;

    @OneToMany(mappedBy = "session", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<SessionTimeSlot> timeslots;

    @ManyToOne
    @JoinColumn(name = "default_room_id", referencedColumnName = "id", nullable = true)
    private Room defaultLocation;

    @Column(name = "default_capacity", nullable = false)
    private int defaultCapacity;

    //    FIXME: Check type of this variable original type dateinterval
    @Column(name = "default_duration", columnDefinition = "VARCHAR(255)", nullable = false)
    private String defaultDuration;

    // TODO improve this, probably real inefficient and doesn't work when someone is in more than one section ????

    /**
     * This helper method returns whether the user is registered for any of the timeslots for this session.
     */
    public boolean isRegistered(String username) {
        for (SessionTimeSlot currentTimeSlot : this.timeslots) {
            if(currentTimeSlot.isRegistered(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This helper method returns whether the user has attended the session in any of the timeslots.
     */
    public boolean hasAttended(String username) {
        for (SessionTimeSlot currentTimeSlot : this.timeslots) {
            if (currentTimeSlot.hasAttended(username)) {
                return true;
            }
        }
        return false;
    }

    public Date getStartDate() {
        Date day = new Date();
        for (SessionTimeSlot ss : timeslots) {
            if (day.compareTo(ss.getStartTime())  > 0)
                day = ss.getStartTime();
        }
        return day;
    }

    public Date getEndDate() {
        Date day = new Date();
        for (SessionTimeSlot ss : timeslots) {
            if (day.compareTo(ss.getEndTime())  < 0)
                day = ss.getEndTime();
        }
        return day;
    }

    @Override
    public SessionAttendance getAttendance (User user) {
        for (TimeSlot timeSlot : this.timeslots) {
            SessionAttendance sessionAttendance = timeSlot.getAttendance(user);
            if (sessionAttendance != null) {
                return sessionAttendance;
            }
        }
        return null;
    }

}

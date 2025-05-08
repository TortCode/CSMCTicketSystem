package edu.utdallas.csmc.web.model.session;

import edu.utdallas.csmc.web.model.schedule.ShiftAssignment;
import edu.utdallas.csmc.web.model.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.Serializable;
import java.sql.Time;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "session_timeslot")
@DiscriminatorValue(value = "sessiontimeslot")
public class SessionTimeSlot extends TimeSlot implements Serializable {

    @ManyToOne
    @JoinColumn(name = "session_id", referencedColumnName = "id", nullable = true)
    private ScheduledSession session;

    @OneToMany(mappedBy = "timeSlot", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private List<Registration> registrations;

    @ManyToOne
    @JoinColumn(name = "sub_user_id", referencedColumnName = "id", nullable = true)
    private User subMentor;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @OneToMany(mappedBy = "session", cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    private List<ShiftAssignment> assignments;

    @Column(name = "actual_start_time", nullable = true)
    private Time actualStartTime;

    @Column(name = "actual_end_time", nullable = true)
    private Time actualEndTime;

    @JsonBackReference
    public ScheduledSession getSession() {
        return this.session;
    }

    @JsonManagedReference
    public List<Registration> getRegistrations() {
        return this.registrations;
    }

    @JsonBackReference
    public User getSubMentor() {
        return this.subMentor;
    }

    @JsonManagedReference
    public List<ShiftAssignment> getAssignments() {
        return this.assignments;
    }

    /**
     * This helper method returns whether the user is registered for this time slot.
     */
    public boolean isRegistered(String username) {
        for (Registration currentRegistration : this.registrations) {
            if (currentRegistration.getUser().getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This helper method returns whether the user has attended the session in this
     * time slot.
     */
    public boolean hasAttended(String username) {
        if (!this.getAttendances().isEmpty()) {
            for (SessionAttendance currentAttendance : this.getAttendances()) {
                if (currentAttendance.getUser().getUsername().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This helper method returns the remaining seats in the session for this time
     * slot.
     */
    public int getRemainingSeats() {
        int registrationsCount = this.getRegistrations().size();
        int attendancesCount = this.getAttendances().size();
        return this.capacity - (registrationsCount > attendancesCount ? registrationsCount : attendancesCount);
    }

    /**
     * This helper method returns whether the session for this time slot has
     * started.
     */
    public boolean hasStarted() {
        if (actualStartTime != null)
            return true;
        return false;
    }

    /**
     * This helper method returns whether the session for this time slot has ended.
     */
    public boolean hasEnded() {
        if (actualEndTime != null)
            return true;
        return false;
    }

    /**
     * This method is used to remove Registration entry from List<Registration>
     * above before deleting the actual Registration.
     */
    public void removeOneRegistration(Registration registration) {
        this.registrations.remove(registration);
    }

}

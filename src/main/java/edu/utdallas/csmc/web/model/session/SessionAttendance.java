package edu.utdallas.csmc.web.model.session;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "session_attendance")
@Inheritance(strategy = InheritanceType.JOINED)
//@DiscriminatorColumn(name = "discr", discriminatorType = DiscriminatorType.STRING, columnDefinition = "VARCHAR(255)")
@DiscriminatorValue(value = "review")

public class SessionAttendance extends Attendance implements Serializable {

    @ManyToOne
    @JoinColumn(name = "timeslot_id", referencedColumnName = "id")
    private TimeSlot timeSlot;

    @Column(name = "grade", nullable = true)
    private Integer grade;

    @Column(name = "comments", length = 128, nullable = true)
    private String comments;

}

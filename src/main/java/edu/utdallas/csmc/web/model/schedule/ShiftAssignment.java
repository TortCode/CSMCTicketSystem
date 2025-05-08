package edu.utdallas.csmc.web.model.schedule;

import edu.utdallas.csmc.web.model.misc.Subject;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;
import edu.utdallas.csmc.web.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "shift_assignment")
public class ShiftAssignment implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "scheduled_shift_id", referencedColumnName = "id", nullable = true)
    private ScheduledShift scheduledShift;

    @ManyToOne
    @JoinColumn(name = "subject_id", referencedColumnName = "id", nullable = true)
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true)
    private User mentor;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "timeslot_id", referencedColumnName = "id", nullable = true)
    private SessionTimeSlot session;

    @OneToOne
    @JoinColumn(name = "absence_id", referencedColumnName = "id", nullable = true)
    private Absence absence;

	@JsonBackReference
	public ScheduledShift getScheduledShift() {
		return this.scheduledShift;
	}

	@JsonBackReference
	public SessionTimeSlot getSession() {
		return this.session;
	}

	@JsonManagedReference
	public Absence getAbsence() {
		return this.absence;
	}
}

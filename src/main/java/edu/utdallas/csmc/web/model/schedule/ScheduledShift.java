package edu.utdallas.csmc.web.model.schedule;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Getter
@Setter
@Table(name = "scheduled_shift")
public class ScheduledShift implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "date_scheduled")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "shift_id", referencedColumnName = "id")
    private Shift shift;

    @OneToMany(mappedBy = "scheduledShift", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<ShiftAssignment> assignments;

    @ManyToOne
    private Schedule schedule;

	@JsonBackReference
	public Shift getShift() {
		return this.shift;
	}

	@JsonManagedReference
	public List<ShiftAssignment> getAssignments() {
		return this.assignments;
	}

	@JsonBackReference
	public Schedule getSchedule() {
		return this.schedule;
	}

}

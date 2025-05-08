package edu.utdallas.csmc.web.model.schedule;

import edu.utdallas.csmc.web.model.misc.Room;
import edu.utdallas.csmc.web.model.misc.Subject;
import edu.utdallas.csmc.web.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Getter
@Setter
@Table(name = "shift")
public class Shift implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "start_time", nullable = false)
    private Time startTime;

    @Column(name = "end_time", nullable = false)
    private Time endTime;

    @Column(name = "day", nullable = false, columnDefinition = "SMALLINT(6)")
    private Integer day;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id", nullable = true)
    private Room room;

    /* Not shown in the database */
    @OneToMany(mappedBy = "shift", cascade = CascadeType.PERSIST)
    private List<ShiftSubject> subjects;

    @ManyToOne
    @JoinColumn(name = "schedule_id", referencedColumnName = "id", nullable = true)
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "shift_leader_id", referencedColumnName = "id", nullable = true)
    private User shiftLeader;

	@JsonBackReference
	public Room getRoom() {
		return this.room;
	}

	@JsonBackReference
	public List<ShiftSubject> getSubjects() {
		return this.subjects;
	}

	@JsonBackReference
	public Schedule getSchedule() {
		return this.schedule;
	}

	@JsonBackReference
	public User getShiftLeader() {
		return this.shiftLeader;
	}

    public Shift removeMentor(Subject subject, User mentor) {
        for(ShiftSubject shiftSubject:this.getSubjects()){
            int comparison= shiftSubject.getSubject().getId().compareTo(subject.getId());
            if(comparison == 0){
                shiftSubject.removeMentor(mentor);
                return this;
            }
        }
        return this;
    }

    public Shift addMentor(Subject subject, User mentor) {
        for(ShiftSubject shiftSubject:this.getSubjects()){
            int comparison= shiftSubject.getSubject().getId().compareTo(subject.getId());
            if(comparison == 0){
                shiftSubject.addMentor(mentor);
                return this;
            }
        }
        return this;
    }


    public Shift addSubject(Subject subject, int max) {
        ShiftSubject shiftSubject =new ShiftSubject(subject,max);
        shiftSubject.setShift(this);
        this.subjects.add(shiftSubject);
        return this;
    }
}

package edu.utdallas.csmc.web.model.schedule;

import edu.utdallas.csmc.web.model.misc.Subject;
import edu.utdallas.csmc.web.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "shift_subject")
public class ShiftSubject implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "subject_id", referencedColumnName = "id", nullable = true)
    private Subject subject;

    @Column(name = "max_mentors", nullable = false, columnDefinition = "SMALLINT(6)")
    private Integer maxMentors;

    @ManyToMany
    @JoinTable(name = "shift_subject_mentors", joinColumns = @JoinColumn(name = "shift_subject_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "mentor_id", referencedColumnName = "id"))
    private List<User> mentors;

    @ManyToOne
    @JoinColumn(name = "shift_id", referencedColumnName = "id", nullable = true)
    private Shift shift;

	@JsonBackReference
	public Subject getSubject() {
		return this.subject;
	}

	@JsonBackReference
	public Shift getShift() {
		return this.shift;
	}

    public void removeMentor(User mentor) {
        //this.getMentors().contains(mentor);
        this.getMentors().remove(mentor);
    }
    public ShiftSubject addMentor(User mentor) {
        this.getMentors().add(mentor);
        return this;
    }
    public  ShiftSubject(Subject subject, int max){
        this.subject= subject;
        this.maxMentors= max;
        this.mentors = new ArrayList<>();

    }
    public ShiftSubject(){
        super();
    }
}

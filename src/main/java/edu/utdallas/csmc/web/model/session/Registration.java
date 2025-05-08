package edu.utdallas.csmc.web.model.session;

import edu.utdallas.csmc.web.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name="registration")
public class Registration implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true)
    private User user;

    @Column(name = "time", nullable = false)
    private Date time;

    @ManyToOne
    @JoinColumn(name = "timeslot_id", referencedColumnName = "id", nullable = true)
    private SessionTimeSlot timeSlot;

    /**
     * The following method removes the entry in List<Registration> in SessionTimeSlot before removing the Registration.
     */
    @PreRemove
    public void removeSessionTimeSlot() {
        this.timeSlot.removeOneRegistration(this);
        this.timeSlot = null;
    }

	@JsonBackReference
	public SessionTimeSlot getTimeSlot(){
		return this.timeSlot;
	}
}

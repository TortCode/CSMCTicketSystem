package edu.utdallas.csmc.web.model.schedule;

import edu.utdallas.csmc.web.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "absence")
public class Absence implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @OneToOne(mappedBy = "absence")
    private ShiftAssignment assignment;

    @Column(name = "reason", length = 2048)
    private String reason;

    //Doubt
    @OneToOne
    @JoinColumn(name = "substitute_shift_assignment_id", referencedColumnName = "id")
    private ShiftAssignment substitute;

    @ManyToOne
    @JoinColumn(name = "last_modified_by", nullable = true, columnDefinition = "CHAR(36)", referencedColumnName = "id")
    private User lastModifiedBy;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = true, columnDefinition = "CHAR(36)", referencedColumnName = "id")
    private User createdBy;

    @Column(name = "last_modified_on", nullable = true)
    private Date lastModifiedOn;

    @Column(name = "created_on", nullable = true)
    private Date createdOn;

	@JsonBackReference
	public ShiftAssignment getAssignment() {
		return this.assignment;
	}
}

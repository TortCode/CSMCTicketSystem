package edu.utdallas.csmc.web.model.misc;

import edu.utdallas.csmc.web.model.course.Course;
import edu.utdallas.csmc.web.model.session.WalkInActivity;
import edu.utdallas.csmc.web.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "ticket")
public class Ticket implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="student_id", nullable = true, referencedColumnName="id")
    private User student;

    @ManyToOne
    @JoinColumn(name="course_id", nullable = true, referencedColumnName="id")
    private Course course;

    @Column(name = "topic", length = 32, nullable = true)
    private String topic;

    @Column(name = "additional_info", length = 8192, nullable = true)
    private String additionalInfo;

    @OneToOne
    @JoinColumn(name = "status_id", nullable = true, referencedColumnName="id")
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = true, referencedColumnName = "id")
    private WalkInActivity activity;

    // Update the DB
    @OneToOne
    @JoinColumn(name = "semester_id", referencedColumnName = "id")
    private Semester semester;

    @OneToMany(mappedBy="ticket")
    @OrderBy("timeIn")
    private List<TicketClaim> claims;

    @Column(name = "created_at", nullable = true)
    private Date createdAt;

    @Column(name = "resolved_at", nullable = true)
    private Date resolvedAt;

    @Column(name = "table_no")
    private int tableNo;

    public TicketClaim getLastClaim() {
        List<TicketClaim> claims = this.getClaims();
        if (claims.isEmpty()) {
            return null;
        }
        return claims.get(claims.size() - 1);
    }
}

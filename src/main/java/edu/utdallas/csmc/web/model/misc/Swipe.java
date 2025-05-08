package edu.utdallas.csmc.web.model.misc;

import edu.utdallas.csmc.web.model.session.ScheduledSessionAttendance;
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
@Table(name = "swipe")
public class Swipe implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "ip_id", referencedColumnName = "id")
    private IpAddress ip;

    @Column(name = "time")
    private Date time;

    @Column(name = "valid", nullable = false, columnDefinition = "BIT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean valid;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "legacy", nullable = true, columnDefinition = "BIT")
    private boolean legacy;

    @ManyToMany(mappedBy = "swipes")
    private List<ScheduledSessionAttendance> scheduledSessionAttendanceList;

}

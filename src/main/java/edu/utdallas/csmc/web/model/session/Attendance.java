package edu.utdallas.csmc.web.model.session;

import edu.utdallas.csmc.web.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "attendance")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discr", discriminatorType = DiscriminatorType.STRING, columnDefinition = "VARCHAR(255)")
@DiscriminatorValue(value = "attendance")

public class Attendance implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = true, referencedColumnName="id")
    private User user;

    @Column(name = "time_in", nullable = true)
    private Date timeIn;

    @Column(name = "time_out", nullable = true)
    private Date timeOut;

    @ManyToMany
    @JoinTable(name = "attendance_mentors", joinColumns = @JoinColumn(name = "attendance_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private List<User> mentors;

}



package edu.utdallas.csmc.web.model.feedback;

import edu.utdallas.csmc.web.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "issue")
public class Issue implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "open_date", nullable = false)
    private Date openDate;

    @OneToOne
    @JoinColumn(name = "feedback_id", referencedColumnName = "id", nullable = true)
    private Feedback feedback;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true)
    private User user;

    @Column(name = "resolved", nullable = false, columnDefinition = "BIT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean resolved;

}

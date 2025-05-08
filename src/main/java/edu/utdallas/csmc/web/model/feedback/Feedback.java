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
@Table(name = "feedback")

public class Feedback implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "message", length = 1024, nullable = false)
    private String message;

    @Column(name = "post_date", nullable = false)
    private Date postDate;

    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private FeedbackType type;

}

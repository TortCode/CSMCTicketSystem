package edu.utdallas.csmc.web.model.feedback;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "feedback_type")
public class FeedbackType implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "name", length = 16, nullable = false)
    private String name;

    @Column(name = "action", length = 16, nullable = false)
    private String action;

    @Column(name = "priority", nullable = false)
    private int priority;

}

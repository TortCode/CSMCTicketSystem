package edu.utdallas.csmc.web.model.user.info;

import edu.utdallas.csmc.web.model.misc.Subject;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_specialty")
// TODO: Check UniqueConstriant
public class Specialty implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
//    @JoinTable(name = "info", joinColumns = @JoinColumn(name="specialty_id",referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name="info_id",referencedColumnName = "user_id"))
    @JoinColumn(name = "info_id", nullable = true, referencedColumnName = "user_id")
    private Info info;

    @ManyToOne
//    @JoinTable(name = "subject", joinColumns = @JoinColumn(name="specialty_id",referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name="subject_id",referencedColumnName = "id"))
    @JoinColumn(name="specialty_topic_id",nullable = true,referencedColumnName = "id")
    private Subject topic;

    @Column(name = "rating", length = 1, nullable = false)
    private int rating;

}

package edu.utdallas.csmc.web.model.event;

import edu.utdallas.csmc.web.model.misc.Room;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "event")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discr", discriminatorType = DiscriminatorType.STRING, columnDefinition = "VARCHAR(255)")
@DiscriminatorValue(value = "event")
public class Event implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "start_time", nullable = false)
    private Date startTime;

    @Column(name = "end_time", nullable = false)
    private Date endTime;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = true, referencedColumnName = "id")
    private Room location;

    @Column(name = "color", length = 7, nullable = true)
    private String color;

}

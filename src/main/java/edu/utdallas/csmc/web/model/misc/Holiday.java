package edu.utdallas.csmc.web.model.misc;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name ="holiday")

public class Holiday implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name="date")
    private  Date date;

    @Column(name="start_time")
    private Time startTime;

    @Column(name="end_tme")
    private Time endTime;

    @Column(name = "closed")
    private boolean closed;

    @Column(name = "description",length = 8192)
    private String description;
}

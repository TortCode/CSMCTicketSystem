package edu.utdallas.csmc.web.model.schedule;

import edu.utdallas.csmc.web.model.misc.Semester;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "schedule")
public class Schedule implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @OneToMany(mappedBy = "schedule")
    private List<Shift> shifts;

    @OneToMany(mappedBy = "schedule")
    private List<ScheduledShift> scheduleShifts;

    @OneToOne
    @JoinColumn(name = "semester_id", referencedColumnName = "id")
    private Semester semester;

}

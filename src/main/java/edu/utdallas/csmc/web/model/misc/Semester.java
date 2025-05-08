package edu.utdallas.csmc.web.model.misc;

import edu.utdallas.csmc.web.model.schedule.Schedule;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "semester")
// TODO: Check UniqueEntity and UniqueConstraint
public class Semester {
    public static final String SEASON_FALL = "fall";
    public static final String SEASON_SPRING = "spring";
    public static final String SEASON_SUMMER = "summer";
    public static final String SEASON_DEV = "dev";

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "season", length = 8, nullable = false)
    private String season;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "start_date", nullable = true)
    private Date startDate;

    @Column(name = "end_date", nullable = true)
    private Date endDate;

    @Column(name = "active", columnDefinition = "BIT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean active;

    @OneToOne(mappedBy = "semester")
    private Schedule schedule;

    @Override
    public String toString() {
        return season + " " + year;
    }
}

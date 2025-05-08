package edu.utdallas.csmc.web.model.misc;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "subject")
public class Subject {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "name", length = 32, unique = true)
    private String name;

    @Column(name = "abbreviation", length = 8, unique = true, nullable = true)
    private String abbreviation;

    @Column(name = "show_on_calendar")
    private boolean showOnCalendar;

    @Column(name = "`order`", unique = true, nullable = true)
    private Integer order;

    @Column(name = "color", length = 7, unique = true, nullable = true)
    private String color;

}

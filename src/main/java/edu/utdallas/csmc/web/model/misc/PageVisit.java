package edu.utdallas.csmc.web.model.misc;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "page_visit")
public class PageVisit {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "visit_id", nullable = true, referencedColumnName = "id")
    private Visit visit;

    @Column(name = "route", length = 255, nullable = false)
    private String route;

    @Column(name = "timestamp", nullable = false)
    private Date timestamp;

}

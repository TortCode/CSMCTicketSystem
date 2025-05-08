package edu.utdallas.csmc.web.model.course;

import edu.utdallas.csmc.web.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "department")
//TODO: UniqueEntity check that
public class Department implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "name", length = 32, nullable = false)
    private String name;

    @Column(name = "abbreviation", length = 3, nullable = false)
    private String abbreviation;

    @Column(name = "admin_notes", length = 8192, nullable = true)
    private String admin_notes;

    @OneToMany(mappedBy = "department")
    private List<Course> courses;

    @ManyToOne
    @JoinColumn(name = "last_modified_by", nullable = true, columnDefinition = "CHAR(36)", referencedColumnName = "id")
    private User lastModifiedBy;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = true, columnDefinition = "CHAR(36)", referencedColumnName = "id")
    private User createdBy;

    @Column(name = "last_modified_on", nullable = true)
    private Date lastModifiedOn;

    @Column(name = "created_on", nullable = true)
    private Date createdOn;

}

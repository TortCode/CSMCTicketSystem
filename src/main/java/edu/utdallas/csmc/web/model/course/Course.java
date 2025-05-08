package edu.utdallas.csmc.web.model.course;

import edu.utdallas.csmc.web.model.misc.Subject;
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
@Table(name = "course")
//TODO: UniqueEntity check that
public class Course implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = true, referencedColumnName = "id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = true, referencedColumnName = "id")
    private Subject subject;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "number", length = 4, nullable = false)
    private String number;

    @OneToMany(mappedBy = "course")
    private List<Section> sections;

    @Column(name = "supported", nullable = false, columnDefinition = "BIT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean supported;

    @Column(name = "description", length = 8192, nullable = true)
    private String description;

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

    public String getFullNumber() {
        return this.getDepartment().getAbbreviation() + ' ' + this.getNumber();
    }
}

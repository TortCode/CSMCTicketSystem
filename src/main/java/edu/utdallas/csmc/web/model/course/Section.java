package edu.utdallas.csmc.web.model.course;

import edu.utdallas.csmc.web.model.misc.Semester;
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
@Table(name = "section")
public class Section implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = true, referencedColumnName = "id")
    private Course course;

    @Column(name = "number", length = 4, nullable = false)
    private String number;

    @Column(name = "description", length = 8192, nullable = true)
    private String description;

    @ManyToOne
    @JoinColumn(name = "semester_id", nullable = true, referencedColumnName = "id")
    private Semester semester;

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

    @ManyToMany
    @JoinTable(name = "section_instructors", joinColumns = @JoinColumn(name = "section_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private List<User> instructors;

    @ManyToMany
    @JoinTable(name = "section_tas", joinColumns = @JoinColumn(name = "section_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private List<User> teaching_assistants;

    @ManyToMany
    @JoinTable(name = "section_students", joinColumns = @JoinColumn(name = "section_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private List<User> students;

    @Override
    public String toString() {
        return course.getDepartment().getAbbreviation() + " " + course.getNumber() + "." + number + " " + semester.toString();
    }
}

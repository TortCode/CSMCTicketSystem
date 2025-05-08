package edu.utdallas.csmc.web.model.session;

import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.file.File;
import edu.utdallas.csmc.web.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "session")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "discr", discriminatorType = DiscriminatorType.STRING, columnDefinition = "VARCHAR(255)")
//    FIXME: @DiscriminatorValue not present in old code so not added here. Can add: @DiscriminatorValue(value = "session")
@DiscriminatorValue(value = "session")
public class Session implements Serializable {

//    const REVIEW_TYPE = 'review';
//    const REWORK_TYPE = 'rework';
//    const QUIZ_TYPE = 'quiz';

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "request_id", referencedColumnName = "id", nullable = true)
    private Request request;

    @Column(name = "topic", length = 128, nullable = false)
    private String topic;

    @Column(name = "description", length = 1024, nullable = true)
    private String description;

    @Column(name = "student_instructions", length = 1024, nullable = true)
    private String studentInstructions;

    @Column(name = "mentor_instructions", length = 1024, nullable = true)
    private String mentorInstructions;

    @ManyToMany
    @JoinTable(name = "session_sections", joinColumns = @JoinColumn(name = "session_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "section_id", referencedColumnName = "id"))
    private List<Section> sections;

    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id", nullable = true)
    private SessionType type;


    @Column(name = "graded", nullable = false, columnDefinition = "BIT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean graded;


    @Column(name = "numeric_grade", nullable = false, columnDefinition = "BIT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean numericGrade;

    @Column(name = "color", length = 7, nullable = true)
    private String color;

    @ManyToOne
    @JoinColumn(name = "last_modified_by", nullable = true, referencedColumnName = "id")
    private User lastModifiedBy;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = true, referencedColumnName = "id")
    private User createdBy;

    @Column(name = "last_modified_on", nullable = true)
    private Date lastModifiedOn;

    @Column(name = "created_on", nullable = true)
    private Date createdOn;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "session_files", joinColumns = @JoinColumn(name = "session_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "id"))
    private List<File> files;


    public String getSectionsAsString() {
        StringBuilder sb = new StringBuilder();
        for (Section sec : sections) {
            sb.append(sec.toString()).append("\n");
        }
        return sb.toString();
    }

    public Set<String> getSectionSet() {
        Set<String> ss = new HashSet<>();
        for (Section sec : sections)
            ss.add(sec.toString());
        return ss;
    }

    public Attendance getAttendance (User user) {
        return null;
    }
}

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
@Table(name = "request")
public class Request implements Serializable {

    public enum RequestStatus {
        NEW, DENIED, COMPLETED, PENDING
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true, referencedColumnName = "id")
    private User user;

    @OneToOne(mappedBy = "request")
    private Session session;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "topic", length = 128, nullable = false)
    private String topic;

    @Column(name = "student_instructions", length = 5000, nullable = true)
    private String studentInstructions;

    @Column(name = "updated", nullable = true)
    private Date updated = new Date();

    @Column(name = "created", nullable = true)
    private Date created = new Date();

    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id", nullable = true)
    private SessionType type;

    @Column(name = "status", columnDefinition = "ENUM('new', 'denied', 'completed', 'pending')")
    @Convert(converter = RequestStatusEnumConverter.class)
    private RequestStatus status;

    @ManyToMany
    @JoinTable(name = "request_sections", joinColumns = @JoinColumn(name = "request_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "section_id", referencedColumnName = "id"))
    private List<Section> sections;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name = "request_files", joinColumns = @JoinColumn(name = "request_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "id"))
    private List<File> files;

    public Set<String> getSectionSet() {
        Set<String> ss = new HashSet<>();
        for (Section sec : sections)
            ss.add(sec.toString());
        return ss;
    }
//    @PreUpdate
//    public void updateToSession() {
//        this.getSession().setRequest(this);
//    }
}

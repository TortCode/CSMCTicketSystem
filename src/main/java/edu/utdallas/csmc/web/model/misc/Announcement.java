package edu.utdallas.csmc.web.model.misc;

import edu.utdallas.csmc.web.model.user.Role;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.model.user.UserGroup;
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
@Table(name ="announcement")
//UniqueEntity
public class Announcement implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToMany
    @JoinTable(name = "announcement_roles", joinColumns = @JoinColumn(name = "announcement_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @ManyToMany
    @JoinTable(name = "announcement_groups", joinColumns = @JoinColumn(name = "announcement_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id"))
    private List<UserGroup> userGroups;

    @ManyToMany
    @JoinTable(name = "announcement_users", joinColumns = @JoinColumn(name = "announcement_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"))
    private List<User> users;

    @Column(name="subject", length=191, unique=true, nullable = false)
    private String subject;

    @Column(name="message", length=8192, nullable = false)
    private String message;

    @Column(name = "post_date", nullable = false)
    private Date postDate;

    @Column(name="start_date", nullable = false)
    private  Date startDate;

    @Column(name="end_date", nullable = false)
    private  Date endDate;

    @Column(name="active", nullable = false, columnDefinition = "BIT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean active;

//    @Column(name="displayAnnouncement", nullable = false, columnDefinition = "BIT")
//    @Type(type = "org.hibernate.type.NumericBooleanType")
//    private boolean displayAnnouncement;

    @ManyToOne
    @JoinColumn(name = "last_modified_by", nullable = true, columnDefinition = "CHAR(36)", referencedColumnName="id")
    private User lastModifiedBy;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = true, columnDefinition = "CHAR(36)", referencedColumnName="id")
    private User createdBy;

    @Column(name = "last_modified_on", nullable = true)
    private Date lastModifiedOn;

    @Column(name = "created_on", nullable = true)
    private Date createdOn;

}

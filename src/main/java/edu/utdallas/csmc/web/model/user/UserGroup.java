package edu.utdallas.csmc.web.model.user;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_group")
public class UserGroup implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    // minLength
    @Column(name = "name", length = 32, unique = true, nullable = false)
    private String name;

    @Column(name = "description", length = 1024, nullable = true)
    private String description;

    @ManyToMany(mappedBy = "groups")
    private List<User> users;

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

    @JsonBackReference
    public List<User> getUsers() {
        return this.users;
    }

}

package edu.utdallas.csmc.web.model.user;

import edu.utdallas.csmc.web.model.misc.Visit;
import edu.utdallas.csmc.web.model.user.info.Info;
import edu.utdallas.csmc.web.model.user.info.Profile;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user")
// TODO" Check UniqueEntity
public class User implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "first_name", length = 32, nullable = true)
    private String firstName;

    @Column(name = "last_name", length = 64, nullable = true)
    private String lastName;

    @Column(name = "netid", length = 9, unique = true, nullable = false)
    private String username;

    @Column(name = "scancode", length = 16, unique = true, nullable = true)
    private String scancode;

    @Column(name = "card_id", length = 11, unique = true, nullable = true) // options attribute in column
    private String cardId;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = { CascadeType.PERSIST })
    private Profile profile;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "user", cascade = { CascadeType.PERSIST })
    private Info info;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_user_group", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "user_group_id", referencedColumnName = "id"))
    private List<UserGroup> groups;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<Visit> visits;

    public String getName() {
        return firstName + " " + lastName;

    }

    @JsonManagedReference
    public Profile getProfile() {
        return this.profile;
    }

    @JsonManagedReference
    public Info getInfo() {
        return this.info;
    }

    @JsonManagedReference
    public List<Role> getRoles() {
        return this.roles;
    }

    @JsonManagedReference
    public List<UserGroup> getGroups() {
        return this.groups;
    }

    @JsonManagedReference
    public List<Visit> getVisits() {
        return this.visits;
    }
    
    public void remove() {
        for (Role role : new ArrayList<>(roles)) {
            removeRole(role);
        }
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.getUser().remove(this);
    }
}

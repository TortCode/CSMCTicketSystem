package edu.utdallas.csmc.web.model.misc;

import edu.utdallas.csmc.web.model.file.File;
import edu.utdallas.csmc.web.model.user.Role;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "policy")
public class Policy {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(name = "policy_files", joinColumns = @JoinColumn(name = "policy_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "file_id", referencedColumnName = "id"))
    private List<File> file;

    @ManyToMany
    @JoinTable(name = "policy_roles", joinColumns = @JoinColumn(name = "policy_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

}

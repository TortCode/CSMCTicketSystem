package edu.utdallas.csmc.web.model.file;

import edu.utdallas.csmc.web.model.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "virtual_file")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discr", discriminatorType = DiscriminatorType.STRING, columnDefinition = "VARCHAR(255)")
@DiscriminatorValue(value = "virtual")
public class VirtualFile implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User owner;

    @OneToMany(mappedBy = "virtualFile")
    private List<FilePermissions> filePermissions;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private VirtualFile parent;

}

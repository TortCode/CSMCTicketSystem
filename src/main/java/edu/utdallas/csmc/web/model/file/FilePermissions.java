package edu.utdallas.csmc.web.model.file;

import edu.utdallas.csmc.web.model.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "file_permissions")
public class FilePermissions implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "virtual_file_id", referencedColumnName = "id")
    private VirtualFile virtualFile;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "view", nullable = false, columnDefinition = "BIT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean view;

    @Column(name = "edit", nullable = false, columnDefinition = "BIT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean edit;

}

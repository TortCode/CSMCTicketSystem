package edu.utdallas.csmc.web.model.user.info;

import edu.utdallas.csmc.web.model.file.File;
import edu.utdallas.csmc.web.model.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "user_profile")
public class Profile implements Serializable {

    @Id
    @OneToOne
    private User user;

    @Column(name = "preferred_name", length = 17, nullable = true)
    private String preferredName;

    @ManyToOne
    @JoinColumn(name = "image_file_id", nullable = true, referencedColumnName = "id")
    private File image;

    @JsonBackReference
    public User getUser() {
        return this.user;
    }

}

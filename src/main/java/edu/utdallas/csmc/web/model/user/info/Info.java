package edu.utdallas.csmc.web.model.user.info;

import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.model.user.info.Specialty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "user_info")
public class Info implements Serializable {

    @Id
    @OneToOne
    private User user;

    @ManyToMany
    @JoinTable(name = "user_majors", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"), inverseJoinColumns = @JoinColumn(name = "major_id", referencedColumnName = "id"))
    private List<Major> majors;

    @Column(name = "date_of_birth", nullable = true)
    private Date dateOfBirth;

    @Column(name = "phone_number", nullable = true)
    private String phoneNumber;

    @Column(name = "email", length = 254, nullable = true)
    private String email;

    @Column(name = "additional_information", nullable = true, columnDefinition = "LONGTEXT")
    private String additionalInformation;

    @Column(name = "adminNotes", nullable = true, columnDefinition = "LONGTEXT")
    private String adminNotes;

    @OneToMany(mappedBy = "info", cascade = { CascadeType.PERSIST })
    private List<Specialty> specialties;

    @ManyToMany
    @JoinTable(name = "user_dietary_restrictions", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"), inverseJoinColumns = @JoinColumn(name = "restriction_id", referencedColumnName = "id"))
    private List<DietaryRestriction> dietaryRestrictions;

    @JsonBackReference
    public User getUser() {
        return this.user;
    }

}

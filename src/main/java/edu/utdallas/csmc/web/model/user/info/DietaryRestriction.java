package edu.utdallas.csmc.web.model.user.info;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "dietary_restriction")
public class DietaryRestriction implements Serializable {

    /*
    TODO: use http://www.webster.edu/specialevents/planning/food-information.html for form stuff
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "name", columnDefinition = "LONGTEXT", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true, referencedColumnName = "id")
    private DietaryRestrictionCategory category;

}

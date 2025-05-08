package edu.utdallas.csmc.web.model.misc;

import edu.utdallas.csmc.web.model.user.User;
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
@Table(name = "visit")

public class Visit implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "ip", length = 45, nullable = false)
    private String ip;

    @Column(name = "browser", length = 255, nullable = false)
    private String browser;

    @Column(name = "timestamp", nullable = false)
    private Date timestamp;

    @ManyToOne(cascade = { CascadeType.PERSIST })
    @JoinColumn(name = "user_id", nullable = true, referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "visit", cascade = { CascadeType.PERSIST })
    private List<PageVisit> pageVisits;

    @JsonBackReference
    public User getUser() {
        return this.user;
    }

}

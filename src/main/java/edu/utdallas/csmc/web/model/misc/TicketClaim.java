package edu.utdallas.csmc.web.model.misc;

import edu.utdallas.csmc.web.model.user.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "ticket_claim")
public class TicketClaim implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "ticket_id", referencedColumnName = "id")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "mentor_id", referencedColumnName = "id")
    private User user;

    @Column(name = "time_in", nullable = true)
    private Date timeIn;

    @Column(name = "time_out", nullable = true)
    private Date timeOut;
}

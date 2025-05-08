package edu.utdallas.csmc.web.model.misc;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "ip_address")
public class IpAddress {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

//    Datatype for ipaddress
//    Regex check @assert
//    pattern="/(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]).){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])/",
//    match=true,
//    message="Invalid IP Address"
    @Column(name = "address", unique = true)
    private Long address;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;


    @Column(name = "blocked", nullable = false, columnDefinition = "BIT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean blocked;

    @OneToMany(mappedBy = "ip")
    private List<Swipe> swipes;

}

package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.misc.TicketStatus;
// import edu.utdallas.csmc.web.model.ticket.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketStatusRepository extends JpaRepository<TicketStatus, UUID>{
    TicketStatus findByName(String name);
}

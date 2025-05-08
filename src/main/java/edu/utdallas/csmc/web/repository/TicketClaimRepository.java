package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.misc.TicketClaim;
// import edu.utdallas.csmc.web.model.ticket.TicketClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TicketClaimRepository extends JpaRepository<TicketClaim, UUID>{

    
}

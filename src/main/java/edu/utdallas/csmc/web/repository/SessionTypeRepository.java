package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.session.SessionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SessionTypeRepository  extends JpaRepository<SessionType, UUID> {

    @Query("SELECT st FROM SessionType st WHERE st.name = :name")
    SessionType findSessionTypeByName(@Param("name") String name);

}

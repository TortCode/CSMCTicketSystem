package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.session.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
public interface SessionRepository<T extends Session> extends JpaRepository<T, UUID> {
    @Query("select se from Session se JOIN se.sections ss WHERE ss.id = :sectionId")
    List<Session> findAllSessionsForActiveSemesterForSelectedSection(@Param("sectionId") UUID sectionId);

    @Query("select se from Session se JOIN se.sections ss JOIN Section s on ss.id=s.id JOIN s.semester sm WHERE sm.active = true")
    List<Session> findAllSessionsForActiveSemester();
}

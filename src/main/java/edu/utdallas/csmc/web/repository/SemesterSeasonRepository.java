package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.misc.SemesterSeason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SemesterSeasonRepository extends JpaRepository<SemesterSeason, UUID> {

    @Query("select ss from SemesterSeason ss where ss.id = :id")
    SemesterSeason findSemesterSeasonbyId(@Param("id") UUID id);
}

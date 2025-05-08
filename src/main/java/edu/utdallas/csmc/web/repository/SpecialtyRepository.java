package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.model.user.info.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpecialtyRepository extends JpaRepository<Specialty, UUID> {

    @Query("select s from Specialty s JOIN fetch s.info i JOIN fetch i.user u where u.id = :userId")
    List<Specialty> findMentorSpeciality(@Param("userId") UUID mentorId);

    @Query("select s from Specialty s JOIN fetch s.info i where i.user = :user ")
    List<Specialty> getSpecialtiesDisplayDetails(@Param("user") User user);

}

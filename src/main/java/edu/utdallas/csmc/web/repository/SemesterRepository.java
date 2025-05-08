package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.misc.Semester;
import edu.utdallas.csmc.web.model.misc.SemesterSeason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SemesterRepository extends JpaRepository<Semester, UUID> {

    Semester findByActive(boolean b);

    Optional<Semester> findSemesterByActive(boolean active);

    @Query("select s from Semester s order by s.startDate")
    List<Semester> findSemesterList();

    @Query("select s from SemesterSeason s")
    List<SemesterSeason> findSeasons();

    @Query("select s from Semester s where s.id = :id")
    Semester getSemester(@Param("id") UUID id);

    @Query("select s from Semester s where s.season = :season and s.year = :year")
    List<Semester> getSemesterFromSeasonandYear(@Param("season") String season, @Param("year") int year);

    @Query("select s.id from Semester s where s.season = :season AND s.year = :year")
    String getSemesterID(@Param("season") String season, @Param("year") int year);

    @Query("select s.startDate from Semester s where s.season = :season AND s.year = :year")
    String getSemesterStartDateFromSeasonAndYear(@Param("season") String season, @Param("year")Integer year);

    @Query("select s.endDate from Semester s where s.season = :season AND s.year = :year")
    String getSemesterEndDateFromSeasonAndYear(@Param("season") String season, @Param("year")Integer year);

    @Query(nativeQuery = true, value = "select * from semester s order by year DESC, start_date DESC LIMIT 5")
    List<Semester> findLastFiveSemesters();

    @Query(nativeQuery = true, value = "select * from semester s order by year DESC, start_date DESC LIMIT 3")
    List<Semester> findLastThreeSemesters();
}


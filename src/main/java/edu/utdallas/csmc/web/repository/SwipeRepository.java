package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.misc.Swipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;


public interface SwipeRepository extends JpaRepository<Swipe, UUID>, DataTablesRepository<Swipe, UUID> {

    public interface SwipeDetails {
        String getNetId();
        String getFirstName();
    }

    @Query("select s from Swipe s where s.time between :start and :end")
    List<Swipe> findByTime(@Param("start") Date start,@Param("end") Date end);

    @Query(nativeQuery = true,
            value = "SELECT s.* FROM swipe s " +
                    "JOIN user u  ON s.user_id = u.id JOIN ip_address i ON s.ip_id = i.id " +
                    "WHERE LOWER(u.first_name) LIKE %:word% "+
                    "OR LOWER(u.last_name) LIKE %:word% " ,

            countQuery = "SELECT COUNT(*) FROM ( " +
                    "SELECT s.* FROM swipe s " +
                    "JOIN user u  ON s.user_id = u.id JOIN ip_address i ON s.ip_id = i.id "+
                    "WHERE LOWER(u.first_name) LIKE %:word% "+
                    "OR LOWER(u.last_name) LIKE %:word% " +
                    ") c")
    Page<Swipe> findUsersByPage2(@Param("word") String word, Pageable pageable);

    @Query(value = "select distinct u.netid as netid, u.first_name as firstname from swipe s join user u join user_roles ur \n" +
            "where DATE(s.time) > DATE_SUB(CURDATE(), INTERVAL 2 DAY) and s.user_id = u.id and s.user_id = ur.user_id \n" +
            "and ur.role_id='9920e9be-7891-11e7-b938-005056005c1e' and ur.user_id not in ( select ur2.user_id from user_roles ur2 \n" +
            "where  ur2.role_id != '9920e9be-7891-11e7-b938-005056005c1e')", nativeQuery = true)
    List<SwipeDetails> findYesterdaySwipesForReminderMails();

}

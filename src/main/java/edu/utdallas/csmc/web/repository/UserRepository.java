package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.model.user.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, DataTablesRepository<User, UUID> {

    Optional<User> findByUsername(String username);


    @Query("SELECT s.students FROM Section s where s.id = :sec_id")
    List<User> findStudentsBySectionId(@Param("sec_id") UUID id);

    List<User> findUserByRolesName(String name);

    @Query("select firstName,lastName from User u where u.id = :userId")
    String getMentorName(@Param("userId") UUID userId);

    @Query("select u from User u JOIN Fetch u.roles ur where ur.id = :id")
    List<User> getUserByRole(@Param("id") UUID id);

    @Query("select u from User u where u.id = :id")
    User getUser(@Param("id") UUID id);

    @Query("select u from User u where u.username = :netid")
    User getUser(@Param("netid") String netid);

    Optional<User> findByCardId(String scancode);

    @Query("select u from User u join u.roles ur where ur.name = 'mentor' order by u.firstName asc ")
    List<User> getMentors();

    Optional<User> findByScancode(String scanCode);


    @Query(nativeQuery = true,
            value = "SELECT distinct u.id,u.first_name,u.last_name,u.netid,u.card_id,u.scancode FROM user u " +
                    "LEFT JOIN user_roles ur " +
                    "ON u.id = ur.user_id " +
                    "LEFT JOIN role r " +
                    "ON ur.role_id = r.id " +
                    "WHERE LOWER(u.netid) LIKE %:word% " +
                    "OR LOWER(u.first_name) LIKE %:word% " +
                    "OR LOWER(u.last_name) LIKE %:word% " +
                    "OR LOWER(r.name) LIKE %:word% ",
            countQuery = "SELECT COUNT(*) FROM ( " +
                    "SELECT distinct u.id,u.first_name,u.last_name,u.netid,u.card_id,u.scancode FROM user u " +
                    "LEFT JOIN user_roles ur " +
                    "ON u.id = ur.user_id " +
                    "LEFT JOIN role r " +
                    "ON ur.role_id = r.id " +
                    "WHERE LOWER(u.netid) LIKE %:word% " +
                    "OR LOWER(u.first_name) LIKE %:word% " +
                    "OR LOWER(u.last_name) LIKE %:word% " +
                    "OR LOWER(r.name) LIKE %:word% " +
                    ") c")
    Page<User> findUsersByPage2(@Param("word") String word, Pageable pageable);

    @Transactional
    @Modifying
    @Query("delete from User u where u.id=:userId")
    void deleteUser(@Param("userId") UUID userId);

}

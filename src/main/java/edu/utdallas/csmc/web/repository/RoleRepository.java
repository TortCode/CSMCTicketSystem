package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String rolename);

    @Query("select r from Role r " +
            "JOIN r.user u " +
            "where r.name = :rolename ")
    List<Role> getUsersByRole(@Param("rolename") String rolename);

    @Query("select r from Role r where r.name = :name")
    Role getRole(@Param("name") String name);

    List<Role> findByUserUsername(String username);

    Role findRoleById(UUID roleId);
}

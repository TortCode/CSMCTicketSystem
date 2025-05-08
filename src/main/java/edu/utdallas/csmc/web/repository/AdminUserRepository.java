package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * The method has been moved to the respective ModelRepository Class.
 */
public interface AdminUserRepository extends JpaRepository<User, UUID> {


}

package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * This class will have the JPA repository for Student.
 */
public interface EventRepository<T extends Event> extends JpaRepository<T, UUID> {
}

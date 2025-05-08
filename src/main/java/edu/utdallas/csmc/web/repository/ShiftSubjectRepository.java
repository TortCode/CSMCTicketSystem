package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.schedule.ShiftSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ShiftSubjectRepository extends JpaRepository<ShiftSubject, UUID> {
}

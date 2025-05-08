package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.session.WalkInActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalkInActivityRepository extends JpaRepository<WalkInActivity, UUID> {
    WalkInActivity findByName(String name);
}

package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.misc.OperationHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OperationHoursRepository extends JpaRepository<OperationHours, UUID> {

    List<OperationHours> findAllByOrderByDay();

    Optional<OperationHours> findByDay(int day);
}

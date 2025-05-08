package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.misc.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    List<Room> findByActive(boolean active);
    List<Room> findAllByActive(boolean active);
}

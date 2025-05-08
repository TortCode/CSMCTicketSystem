package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.misc.IpAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IpAddressRepository extends JpaRepository<IpAddress, UUID> {

    Optional<IpAddress> findByAddress(Long address);
}

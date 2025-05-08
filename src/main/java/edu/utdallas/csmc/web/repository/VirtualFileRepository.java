package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.file.VirtualFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VirtualFileRepository extends JpaRepository<VirtualFile, UUID> {
}

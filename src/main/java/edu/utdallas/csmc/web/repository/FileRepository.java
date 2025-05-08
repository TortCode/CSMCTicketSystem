package edu.utdallas.csmc.web.repository;

import edu.utdallas.csmc.web.model.file.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID>{

    @Query("SELECT fm FROM FileMetaData fm WHERE fm.file = :file AND fm.key = :key")
    FileMetaData findByKey(@Param("file") File file, @Param("key") String key);

}

package edu.utdallas.csmc.web.model.file;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "file_hash")
public class FileHash implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "id", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "size", length = 191, nullable = false)
    private int size;

    @Column(name = "path", length = 191, unique = true, nullable = false)
    private String path;

}

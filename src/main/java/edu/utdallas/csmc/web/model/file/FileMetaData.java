package edu.utdallas.csmc.web.model.file;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "file_metadata")
public class FileMetaData implements Serializable {

    @Id
    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private File file;

    @Id
    @Column(name = "`key`", length = 32)
    private String key;

    @Column(name = "`value`", nullable = false, length = 2096)
    private String value;

}

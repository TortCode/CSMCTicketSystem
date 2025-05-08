package edu.utdallas.csmc.web.model.file;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue(value = "file")
public class File extends VirtualFile implements Serializable {

    @OneToMany(mappedBy = "file", cascade = {CascadeType.PERSIST})
    private List<FileMetaData> metadata;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "hash_id", nullable = true, referencedColumnName = "id")
    private FileHash hash;

}

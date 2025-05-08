package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.model.file.File;
import edu.utdallas.csmc.web.model.file.FileMetaData;
import edu.utdallas.csmc.web.repository.FileRepository;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class FileService {

    @Autowired
    FileRepository fileRepository;

    public Optional<File> getFileById(UUID id) {return fileRepository.findById(id);}

    public String getFileMetaDataByKey(File file, String key) {
        FileMetaData metaData = fileRepository.findByKey(file, key);
        if (metaData == null) {
            if ("mime".equals(key)) {
                return "application/pdf";
            } else if ("extension".equals(key)) {
                return "pdf";
            }
        }

        return metaData.getValue();
    }

    public void saveFile(File file){
        fileRepository.save(file);
    }
}

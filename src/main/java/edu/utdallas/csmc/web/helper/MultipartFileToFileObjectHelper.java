package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.model.file.File;
import edu.utdallas.csmc.web.model.file.FileHash;
import edu.utdallas.csmc.web.model.file.FileMetaData;
import edu.utdallas.csmc.web.model.file.VirtualFile;
import edu.utdallas.csmc.web.model.user.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class MultipartFileToFileObjectHelper {


    public File getFileObject(MultipartFile file, User user) throws IOException{
        try{
            // Create Java File object from MultipartFile Object
            Path filePath = Paths.get("./src/main/resources/uploads/temp/", file.getOriginalFilename());

            // Open that newly created Java File
            java.io.File javaFile = new java.io.File(filePath.toString());
            javaFile.createNewFile();

            // Copy data
            FileOutputStream fos = new FileOutputStream(javaFile);
            fos.write(file.getBytes());
            fos.close();

            // Now generate SHA-1 of the file
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // Get input stream
            FileInputStream fin = new FileInputStream(javaFile);
            byte[] fileContents = new byte[(int) javaFile.length()];

            // Get data into byte array
            fin.read(fileContents);
            fin.close();

            // Generate hash from the byte array fileContents
            byte[] messageDigest = md.digest(fileContents);
            BigInteger no = new BigInteger(1, messageDigest);
            String hashText = no.toString(16);

            // Creating FileHash Entity
            FileHash fileHash = new FileHash();
            fileHash.setSize((int) javaFile.length());
            String path = hashText + "." + getFileExtension(javaFile);
            fileHash.setPath(path);

            // Creating File Entity
            edu.utdallas.csmc.web.model.file.File serverFile = new File();
            serverFile.setName(file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.')));
            serverFile.setOwner(user);
            serverFile.setParent(null);

            // Creating FileMetaData Entity
            String contentType = file.getContentType();
            String extension = getFileExtension(javaFile);

            FileMetaData metaExtension = new FileMetaData();
            metaExtension.setKey("extension");
            metaExtension.setValue(extension);
            metaExtension.setFile(serverFile);

            FileMetaData metaMime = new FileMetaData();
            metaMime.setKey("mime");
            metaMime.setValue(contentType);
            metaMime.setFile(serverFile);

            List<FileMetaData> fileMetaData = new ArrayList<>();
            fileMetaData.add(metaExtension);
            fileMetaData.add(metaMime);

            // Adding more data to file entity
            serverFile.setMetadata(fileMetaData);
            serverFile.setHash(fileHash);

            // Saving file in the appropriate directory
            String directoryLevelOne = hashText.substring(0,2);
            String directoryLevelTwo = hashText.substring(2,4);
            Path serverFilePath = Paths.get("./src/main/resources/uploads/" + directoryLevelOne + "/" + directoryLevelTwo);
            Files.createDirectories(serverFilePath);
            String newPath = serverFilePath.toString() + "/" + path;
            java.io.File newFile = new java.io.File(newPath);
            Files.copy(javaFile.toPath(), newFile.toPath());
            javaFile.delete();

            return serverFile;

        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private static String getFileExtension(java.io.File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

}

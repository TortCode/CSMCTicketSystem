package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.InstructorRequestSessionDTO;

import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.file.File;
import edu.utdallas.csmc.web.model.session.Request;
import edu.utdallas.csmc.web.model.session.SessionType;
import edu.utdallas.csmc.web.model.user.User;

import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;

public class InstructorRequestSessionHelper {

    public static Request createRequestObject(InstructorRequestSessionDTO requestSessionDTO, User user, SessionType sessionType, List<Section> sectionIds) throws ParseException {
        Request newRequest = new Request();
        try{
            newRequest.setUser(user);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            newRequest.setStartDate(dateFormat.parse(requestSessionDTO.getStartDate()));
            newRequest.setEndDate(dateFormat.parse(requestSessionDTO.getEndDate()));
            newRequest.setTopic(requestSessionDTO.getTopic());
            newRequest.setStudentInstructions(requestSessionDTO.getStudentInstructions());
            newRequest.setType(sessionType);
            newRequest.setStatus(Request.RequestStatus.NEW);
            newRequest.setSections(sectionIds);
            List<File> files = new ArrayList<>();

            List<MultipartFile> multipartFiles = requestSessionDTO.getFiles();
            MultipartFileToFileObjectHelper converter = new MultipartFileToFileObjectHelper();

            for(int i = 0; i < multipartFiles.size(); i++){
                files.add(converter.getFileObject(multipartFiles.get(i), user));
            }

            newRequest.setFiles(files);

        } catch (ParseException pe){
            pe.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

        return newRequest;
    }

}

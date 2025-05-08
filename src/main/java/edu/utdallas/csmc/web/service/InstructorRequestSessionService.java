package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.InstructorRequestSessionDTO;
import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.file.File;
import edu.utdallas.csmc.web.model.session.Request;
import edu.utdallas.csmc.web.model.session.SessionType;
import edu.utdallas.csmc.web.model.user.User;

import edu.utdallas.csmc.web.repository.RequestRepository;

import edu.utdallas.csmc.web.helper.InstructorRequestSessionHelper;

import edu.utdallas.csmc.web.repository.SectionRepository;
import edu.utdallas.csmc.web.repository.SessionTypeRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.style.ToStringCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class InstructorRequestSessionService {

    @Autowired
    RequestRepository requestRepository;

    @Autowired
    SessionTypeRepository sessionTypeRepository;

    @Autowired
    SectionRepository sectionRepository;


    public List<Request> findSessionsByUser(User u){
        return requestRepository.findRequestsByUser(u);
    }

    public Request findByRequestId(UUID id){
        return requestRepository.findById(id).get();
    }

    public void updateSessionRequest(Request request) {
        requestRepository.save(request);
    }


    @Transactional
    public void addSessionRequest(InstructorRequestSessionDTO requestSessionDTO, User instructor, SessionType type){
        try{
            List<Section> sections = getSectionsFromIdList(requestSessionDTO.getSections());
            Request request = InstructorRequestSessionHelper.createRequestObject(requestSessionDTO, instructor, type, sections);
            requestRepository.save(request);
            return;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Section> getSectionsFromIdList(List<String> sectionList){
        List<UUID> sectionIds = new ArrayList<>();
        for(int i = 0; i < sectionList.size(); i++){
            sectionIds.add(UUID.fromString(sectionList.get(i)));
        }
        return sectionRepository.findSectionsByIdList(sectionIds);
    }

    public SessionType findSessionTypeById(UUID id){
        return sessionTypeRepository.findById(id).get();
    }

    public SessionType getSessionTypeByName(String name){
        return sessionTypeRepository.findSessionTypeByName(name);
    }
}

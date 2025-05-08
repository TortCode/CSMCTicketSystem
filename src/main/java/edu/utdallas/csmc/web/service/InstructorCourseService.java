package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.UserDTO;
import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.SectionRepository;
import edu.utdallas.csmc.web.repository.UserRepository;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import java.util.List;

@Service
@Log4j2
public class InstructorCourseService {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Section> getInstructorSections(User instructor) {
        return sectionRepository.findAllSectionsByInstructorUsername(instructor);
    }

    public List<Section> getInstructorCurrentSections(String netId){
        return sectionRepository.findSectionsByInstructorUsername(netId);
    }

    public List<User> getRoster(UUID id) {
        return userRepository.findStudentsBySectionId(id);
    }

    public Optional<Section> getSectionById(String id){
        return sectionRepository.findById(UUID.fromString(id));
    }

    public void enrollUserToSection(Section section, List<User> users){
        section.setStudents(users);
        sectionRepository.save(section);
    }

    public User findUserByNetId(String[] data){
        Optional<User> user = userRepository.findByUsername(data[2]);
        if(!user.isPresent()){
            User u = new User();
            u.setFirstName(data[1]);
            u.setLastName(data[0]);
            u.setUsername(data[2]);
            return userRepository.save(u);
        } else {
            return user.get();
        }
    }

    public List<Section> getInstructorSectionsWithNetID(String instructorNetID) {
        return sectionRepository.findSectionsByInstructorUsername(instructorNetID);
    }
}

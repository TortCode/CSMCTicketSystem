package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminAnnouncementResultDTO;
import edu.utdallas.csmc.web.helper.AdminAnnouncementHelper;
import edu.utdallas.csmc.web.model.misc.Announcement;
import edu.utdallas.csmc.web.model.user.Role;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.AnnouncementRepository;
import edu.utdallas.csmc.web.repository.RoleRepository;
import edu.utdallas.csmc.web.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintViolationException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class AdminAnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    AdminAnnouncementHelper adminAnnouncementHelper = new AdminAnnouncementHelper();
    public List<Announcement> getAnnouncement() {
        List<Announcement> announcementList = announcementRepository.findAll();

        return announcementList;
    }

    public List<Role> getRole() {
        List<Role> roleList = roleRepository.findAll();

        return roleList;
    }

    public void submitAnnouncement(AdminAnnouncementResultDTO adminAnnouncementResultDTO) throws ParseException {

        List<Role> roleResult = new ArrayList<Role>();
        for (UUID roleId : adminAnnouncementResultDTO.getRoles()){
            roleResult.add(roleRepository.findRoleById(roleId));
        }
        Optional<User> user = userRepository.findByUsername(adminAnnouncementResultDTO.getUsername());
        adminAnnouncementResultDTO.setRoleResult(roleResult);
        Announcement newAnnouncement = adminAnnouncementHelper.buildNewAnnouncementModel(adminAnnouncementResultDTO,user);
        announcementRepository.save(newAnnouncement);

    }

    public void getAnnouncementDetails(AdminAnnouncementResultDTO adminAnnouncementResultDTO) {
        Optional<Announcement> announcement = announcementRepository.findById(UUID.fromString(adminAnnouncementResultDTO.getAnnouncementId()));
        if (announcement.isPresent()){
            Announcement currentAnnouncement = announcement.get();
            adminAnnouncementResultDTO.setEditAnnouncement(currentAnnouncement);
        }
        else{
            log.info("Announcement Not Found!");
        }
}

    public void updateAnnouncement(AdminAnnouncementResultDTO adminAnnouncementResultDTO) throws ParseException {
        Optional<Announcement> announcement = announcementRepository.findById(UUID.fromString(adminAnnouncementResultDTO.getAnnouncementId()));

        if (announcement.isPresent()){
            Announcement currentAnnouncement = announcement.get();
            Optional<User> user = userRepository.findByUsername(adminAnnouncementResultDTO.getUsername());
            List<Role> roleResult = new ArrayList<Role>();
            for (UUID roleId : adminAnnouncementResultDTO.getRoles()){
                roleResult.add(roleRepository.findRoleById(roleId));
            }

            adminAnnouncementResultDTO.setRoleResult(roleResult);
            adminAnnouncementHelper.buildUpdateAnnouncementModel(currentAnnouncement, adminAnnouncementResultDTO,user);
            announcementRepository.save(currentAnnouncement);
        }
            else {
            log.info("Announcement not Found!");
        }

    }

    public String deleteCourse(UUID announcementId) {
        Optional<Announcement> announcement = announcementRepository.findById(announcementId);

        if (announcement.isPresent()){
            try{
                announcementRepository.delete(announcement.get());
            }
            catch (ConstraintViolationException ex){
                log.error(ex);
                log.info("This Announcement cannot be deleted.");
                return "redirect:/admin/announcement/edit/" + announcementId;
            }
        } else {
            log.info("Course not found for id" + announcementId.toString());
        }

        return "redirect:/admin/announcement";

        }

    }







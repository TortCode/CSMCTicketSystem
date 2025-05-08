package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.AdminAnnouncementResultDTO;
import edu.utdallas.csmc.web.model.misc.Announcement;
import edu.utdallas.csmc.web.model.user.User;
import lombok.extern.log4j.Log4j2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Log4j2
public class AdminAnnouncementHelper {

    public Date getDatefromString(String dateString) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
    }

    public Announcement buildNewAnnouncementModel(AdminAnnouncementResultDTO adminAnnouncementResultDTO, Optional<User> user) throws ParseException {
     Announcement announcement = new Announcement();
     Date startDate = getDatefromString(adminAnnouncementResultDTO.getStartdate());
     Date endDate = getDatefromString(adminAnnouncementResultDTO.getEnddate());


     announcement.setSubject(adminAnnouncementResultDTO.getSubject());
     announcement.setMessage(adminAnnouncementResultDTO.getMessage());
     announcement.setStartDate(startDate);
     announcement.setEndDate(endDate);
     announcement.setCreatedOn(new Date());
     announcement.setActive(adminAnnouncementResultDTO.getActive());
//     announcement.setDisplayAnnouncement(adminAnnouncementResultDTO.getDisplayannouncement());
     announcement.setRoles(adminAnnouncementResultDTO.getRoleResult());
     announcement.setLastModifiedOn(new Date());
     announcement.setPostDate(new Date());
        if (user.isPresent()) {
                announcement.setCreatedBy(user.get());
                announcement.setLastModifiedBy(user.get());
            }
     return announcement;

    }


    public void buildUpdateAnnouncementModel(Announcement currentAnnouncement, AdminAnnouncementResultDTO adminAnnouncementResultDTO, Optional<User> user) throws ParseException {

        Date startDate = getDatefromString(adminAnnouncementResultDTO.getStartdate());
        Date endDate = getDatefromString(adminAnnouncementResultDTO.getEnddate());

        currentAnnouncement.setSubject(adminAnnouncementResultDTO.getSubject());
        currentAnnouncement.setMessage(adminAnnouncementResultDTO.getMessage());
        currentAnnouncement.setStartDate(startDate);
        currentAnnouncement.setEndDate(endDate);
        currentAnnouncement.setLastModifiedOn(new Date());
        currentAnnouncement.setActive(adminAnnouncementResultDTO.getActive());
//        currentAnnouncement.setDisplayAnnouncement(adminAnnouncementResultDTO.getDisplayannouncement());
        currentAnnouncement.setRoles(adminAnnouncementResultDTO.getRoleResult());

        if (user.isPresent()) {
            currentAnnouncement.setLastModifiedBy(user.get());
        }

}

}

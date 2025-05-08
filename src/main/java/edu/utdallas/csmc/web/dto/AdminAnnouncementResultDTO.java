package edu.utdallas.csmc.web.dto;

import edu.utdallas.csmc.web.model.misc.Announcement;
import edu.utdallas.csmc.web.model.user.Role;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AdminAnnouncementResultDTO {
    List<Announcement> announcementList;

    List<Role> roleList;

    String subject;
    String message;
    String startdate;
    String enddate;
    List<UUID> roles;
    List<Role> roleResult;
    Boolean active;
    Boolean displayannouncement;
    String username;

    String announcementId;
    Announcement editAnnouncement;

    public void setActive(String activeInput) {
        this.active = activeInput != null ? true : false;
    }

    public void setActive(boolean activeInput) {
        this.active = activeInput;
    }

    public void setDisplayannouncement(String displayannouncementInput) {
        this.displayannouncement = displayannouncementInput != null ? true : false;
    }

    public void setDisplayannouncement(boolean displayannouncementInput) {
        this.displayannouncement = displayannouncementInput;
    }
}

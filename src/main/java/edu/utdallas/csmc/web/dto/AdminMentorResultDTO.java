package edu.utdallas.csmc.web.dto;

import edu.utdallas.csmc.web.model.misc.Subject;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.model.user.info.Info;
import lombok.Data;

import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

@Data
public class AdminMentorResultDTO {

    List<User> mentors;
    List<Subject> subjects;
    List<Info> info;

    TreeMap<UUID, TreeMap<String, Integer>> mentorSpecialization;
    TreeMap<UUID, Integer> mentorSession;

}

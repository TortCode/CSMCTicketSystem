package edu.utdallas.csmc.web.controller.newSite;

import edu.utdallas.csmc.web.dto.AnnouncementsResultDTO;
import edu.utdallas.csmc.web.dto.OperationHoursResultDTO;
import edu.utdallas.csmc.web.dto.RoomDetailsResultDTO;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.model.user.info.Specialty;
import edu.utdallas.csmc.web.repository.RoleRepository;
import edu.utdallas.csmc.web.repository.SpecialtyRepository;
import edu.utdallas.csmc.web.repository.SubjectRepository;
import edu.utdallas.csmc.web.repository.UserRepository;
import edu.utdallas.csmc.web.service.DefaultUsernameService;
import edu.utdallas.csmc.web.service.HomePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This Class will have the path to the Home Page.
 */
@Controller
@EnableAutoConfiguration
@RequestMapping("/new")
public class NewHomePageController {

    @Autowired
    private HomePageService homePageService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    SpecialtyRepository specialtyRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private DefaultUsernameService defaultUsernameService;

    /**
     * This API shows the announcements, operation hours and room details on the home page of the website.
     */
    @RequestMapping("/")
    public String home(ModelMap model) {

        List<AnnouncementsResultDTO> announcementsResultDTOList = homePageService.getAllAnnouncements();
        List<OperationHoursResultDTO> operationHoursResultDTOList = homePageService.getAllCourseHours();
        List<RoomDetailsResultDTO> roomDetailsResultDTOList = homePageService.getAllRoomDetails();

        model.addAttribute("announcements", announcementsResultDTOList);
        model.addAttribute("hours", operationHoursResultDTOList);
        model.addAttribute("rooms", roomDetailsResultDTOList);

        System.out.println("new defaultUsernameService.getLoginStatus()"+defaultUsernameService.getLoginStatus());

        if(!defaultUsernameService.getLoginStatus())
            return "shared/launch/home2.html";

        return "shared/home/home2.html";
    }

    /**
     * Method - Mentor Profile
     * @param model
     * @param mentorNetId
     * @return
     */
    @RequestMapping("/profile/{mentorNetId}")
    public String getMentorInfo(ModelMap model, @PathVariable("mentorNetId") String mentorNetId) {
        User user = userRepository.getUser(mentorNetId);
        model.addAttribute("user", user);
        List<Specialty> specialties = specialtyRepository.findMentorSpeciality(user.getId());
        Optional<Specialty> ds_specialty = specialties.stream().filter(s -> s.getTopic().getId().compareTo(UUID.fromString("bd8ad11f-85c3-11e7-ac79-005056005c1d"))==0).findFirst();
        Optional<Specialty> dm_specialty = specialties.stream().filter(s -> s.getTopic().getId().compareTo(UUID.fromString("c7151229-7df0-11e7-b5ef-005056005c1d"))==0).findFirst();
        Optional<Specialty> java_specialty = specialties.stream().filter(s -> s.getTopic().getId().compareTo(UUID.fromString("c71509c6-7df0-11e7-b5ef-005056005c1d"))==0).findFirst();
        Optional<Specialty> cpp_specialty = specialties.stream().filter(s -> s.getTopic().getId().compareTo(UUID.fromString("c7150e3e-7df0-11e7-b5ef-005056005c1d"))==0).findFirst();

        model.addAttribute("java",java_specialty.get().getRating());
        model.addAttribute("cpp",cpp_specialty.get().getRating());
        model.addAttribute("ds",ds_specialty.get().getRating());
        model.addAttribute("dm",dm_specialty.get().getRating());

        model.addAttribute("isAdmin", user.getRoles().contains(roleRepository.getRole("admin")));
        return "role/mentor/profile/profile.html";
    }


    @RequestMapping("/profile/edit/{mentorNetId}")
    public String updateMentorInfo(ModelMap model, @PathVariable("mentorNetId") String mentorNetId, @RequestParam String preferredName,
                                   @RequestParam String birthDate, @RequestParam String phoneNumber, @RequestParam String cppRange,
                                   @RequestParam String javaRange, @RequestParam String dsRange, @RequestParam String dmRange) {
        User user = userRepository.getUser(mentorNetId);
        user.getProfile().setPreferredName(preferredName);
        user.getInfo().setPhoneNumber(phoneNumber);

        userRepository.save(user);

        List<Specialty> specialties = specialtyRepository.findMentorSpeciality(user.getId());

        if(specialties.size() > 0) {
            Optional<Specialty> ds_specialty = specialties.stream().filter(s -> s.getTopic().getId().compareTo(UUID.fromString("bd8ad11f-85c3-11e7-ac79-005056005c1d"))==0).findFirst();
            if (ds_specialty.isPresent()) {
                ds_specialty.get().setRating(Integer.parseInt(dsRange));
                ds_specialty.get().setInfo(user.getInfo());
                specialtyRepository.save(ds_specialty.get());
            }

            // DM
            Optional<Specialty> dm_specialty = specialties.stream().filter(s -> s.getTopic().getId().compareTo(UUID.fromString("c7151229-7df0-11e7-b5ef-005056005c1d"))==0).findFirst();
            if (dm_specialty.isPresent()) {
                dm_specialty.get().setRating(Integer.parseInt(dmRange));
                dm_specialty.get().setInfo(user.getInfo());
                specialtyRepository.save(dm_specialty.get());
            }

            // Java
            Optional<Specialty> java_specialty = specialties.stream().filter(s -> s.getTopic().getId().compareTo(UUID.fromString("c71509c6-7df0-11e7-b5ef-005056005c1d"))==0).findFirst();
            if (java_specialty.isPresent()) {
                java_specialty.get().setRating(Integer.parseInt(javaRange));
                java_specialty.get().setInfo(user.getInfo());
                specialtyRepository.save(java_specialty.get());

            }

            // cpp
            Optional<Specialty> cpp_specialty = specialties.stream().filter(s -> s.getTopic().getId().compareTo(UUID.fromString("c7150e3e-7df0-11e7-b5ef-005056005c1d"))==0).findFirst();
            if (cpp_specialty.isPresent()) {
                cpp_specialty.get().setRating(Integer.parseInt(cppRange));
                cpp_specialty.get().setInfo(user.getInfo());
                specialtyRepository.save(cpp_specialty.get());
            }
        } else{
            // ds
            {
                Specialty specialty = new Specialty();
                specialty.setInfo(user.getInfo());
                specialty.setRating(Integer.parseInt(dsRange));
                specialty.setTopic(subjectRepository.getOne(UUID.fromString("bd8ad11f-85c3-11e7-ac79-005056005c1d")));
                specialtyRepository.save(specialty);
            }
            {
                Specialty specialty = new Specialty();
                specialty.setInfo(user.getInfo());
                specialty.setRating(Integer.parseInt(dmRange));
                specialty.setTopic(subjectRepository.getOne(UUID.fromString("c7151229-7df0-11e7-b5ef-005056005c1d")));
                specialtyRepository.save(specialty);
            }
            {
                Specialty specialty = new Specialty();
                specialty.setInfo(user.getInfo());
                specialty.setRating(Integer.parseInt(javaRange));
                specialty.setTopic(subjectRepository.getOne(UUID.fromString("c71509c6-7df0-11e7-b5ef-005056005c1d")));
                specialtyRepository.save(specialty);
            }
            {
                Specialty specialty = new Specialty();
                specialty.setInfo(user.getInfo());
                specialty.setRating(Integer.parseInt(cppRange));
                specialty.setTopic(subjectRepository.getOne(UUID.fromString("c7150e3e-7df0-11e7-b5ef-005056005c1d")));
                specialtyRepository.save(specialty);
            }

        }

        return home(model);
    }
}

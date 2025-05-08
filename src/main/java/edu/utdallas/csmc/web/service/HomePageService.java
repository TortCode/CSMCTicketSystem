package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AnnouncementsResultDTO;
import edu.utdallas.csmc.web.dto.OperationHoursResultDTO;
import edu.utdallas.csmc.web.dto.RoomDetailsResultDTO;
import edu.utdallas.csmc.web.helper.HomePageHelper;
import edu.utdallas.csmc.web.model.misc.Announcement;
import edu.utdallas.csmc.web.model.misc.OperationHours;
import edu.utdallas.csmc.web.model.misc.Room;
import edu.utdallas.csmc.web.repository.AnnouncementRepository;
import edu.utdallas.csmc.web.repository.OperationHoursRepository;
import edu.utdallas.csmc.web.repository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * This class will have the service functions related to the Home Page in the application.
 */
@Service
public class HomePageService {

    @Autowired
    private DefaultUsernameService defaultUsernameService;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private OperationHoursRepository operationHoursRepository;

    @Autowired
    private RoomRepository roomRepository;

    HomePageHelper homePageHelper = new HomePageHelper();

    /**
     * This method gets a list of all the active Announcements for this user from the database.
     */
    public List<AnnouncementsResultDTO> getAllAnnouncements() {

        List<AnnouncementsResultDTO> announcementsResultDTOList = new ArrayList<>();
        List<Announcement> announcementList = announcementRepository.findAnnouncementByUserAndLeStartDateAndGeEndDate(defaultUsernameService.getUsername());

        announcementList = announcementList.stream().distinct().collect(Collectors.toList());

        homePageHelper.buildAnnouncementDetails(announcementsResultDTOList, announcementList);

        return announcementsResultDTOList;
    }

    /**
     * This method gets a list of all the OperationHours from the database.
     */
    public List<OperationHoursResultDTO> getAllCourseHours() {

        List<OperationHoursResultDTO> operationHoursResultDTOList = new ArrayList<>();
        List<OperationHours> operationHoursList = operationHoursRepository.findAllByOrderByDay();
        homePageHelper.buildOperationHoursDetails(operationHoursResultDTOList, operationHoursList);

        return operationHoursResultDTOList;
    }

    /**
     * This method gets a list of details for the Rooms from the database.
     */
    public List<RoomDetailsResultDTO> getAllRoomDetails() {

        List<RoomDetailsResultDTO> roomDetailsResultDTOList = new ArrayList<>();
        List<Room> roomList = roomRepository.findAllByActive(true);
        homePageHelper.buildRoomDetails(roomDetailsResultDTOList, roomList);

        return roomDetailsResultDTOList;
    }
}

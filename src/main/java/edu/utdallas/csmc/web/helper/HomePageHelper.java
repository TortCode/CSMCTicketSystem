package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.AnnouncementsResultDTO;
import edu.utdallas.csmc.web.dto.OperationHoursResultDTO;
import edu.utdallas.csmc.web.dto.RoomDetailsResultDTO;
import edu.utdallas.csmc.web.model.misc.Announcement;
import edu.utdallas.csmc.web.model.misc.OperationHours;
import edu.utdallas.csmc.web.model.misc.Room;

import java.util.HashMap;
import java.util.List;

/**
 * This class has all the helper functions that will convert Model objects into DTO objects with the required processing
 * for the Student Courses View.
 */
public class HomePageHelper {

    /**
     * This helper function converts the Announcement Model to the required AnnouncementsResultDTO that will be sent back to the view.
     */
    public void buildAnnouncementDetails(final List<AnnouncementsResultDTO> announcementsResultDTOList, List<Announcement> announcementList) {
        for(Announcement announcement : announcementList){
            AnnouncementsResultDTO announcementsResultDTO = new AnnouncementsResultDTO();
            announcementsResultDTO.setPostDate(announcement.getPostDate());
            announcementsResultDTO.setMessage(announcement.getMessage());

            announcementsResultDTOList.add(announcementsResultDTO);
        }
    }

    /**
     * This helper function converts the OperationHours Model to the required OperationHoursResultDTO that will be sent back to the view.
     */
    public void buildOperationHoursDetails(final List<OperationHoursResultDTO> operationHoursResultDTOList, List<OperationHours> operationHoursList) {

        HashMap<Integer, String> dayMap = new HashMap<>();
        dayMap.put(0, "Sunday");
        dayMap.put(1, "Monday");
        dayMap.put(2, "Tuesday");
        dayMap.put(3, "Wednesday");
        dayMap.put(4, "Thursday");
        dayMap.put(5, "Friday");
        dayMap.put(6, "Saturday");

        for (OperationHours operationHours : operationHoursList) {

            OperationHoursResultDTO operationHoursResultDTO = new OperationHoursResultDTO();

            operationHoursResultDTO.setDay(dayMap.get(operationHours.getDay()));
            operationHoursResultDTO.setStartTime(operationHours.getStartTime());
            operationHoursResultDTO.setEndTime(operationHours.getEndTime());

            operationHoursResultDTOList.add(operationHoursResultDTO);

        }
    }

    /**
     * This helper function converts the Room Model to the required RoomDetailsResultDTO that will be sent back to the view.
     */
    public void buildRoomDetails(final List<RoomDetailsResultDTO> roomDetailsResultDTOList, List<Room> roomList) {
        for (Room room : roomList){

            RoomDetailsResultDTO roomDetailsResultDTO = new RoomDetailsResultDTO();

            roomDetailsResultDTO.setBuilding(room.getBuilding());
            roomDetailsResultDTO.setFloor(Integer.toString(room.getFloor()));
            roomDetailsResultDTO.setNumber(Integer.toString(room.getNumber()));
            roomDetailsResultDTO.setDescription(room.getDescription());

            roomDetailsResultDTOList.add(roomDetailsResultDTO);
        }
    }

}

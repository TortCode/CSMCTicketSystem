package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.AdminRoomResultDTO;
import edu.utdallas.csmc.web.model.misc.Room;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
public class AdminRoomHelper {
    public List<AdminRoomResultDTO> setRoomList(List<Room> rooms){
        List<AdminRoomResultDTO> roomsList = new ArrayList<>();
        for(Room room: rooms){
            AdminRoomResultDTO roomOnList = new AdminRoomResultDTO();
            roomOnList.setRoomId(room.getId());
            roomOnList.setBuilding(room.getBuilding());
            roomOnList.setFloor(Integer.toString(room.getFloor()));
            roomOnList.setClassRoomNumber(Integer.toString(room.getNumber()));
            roomOnList.setDescription(room.getDescription());
            roomOnList.setCapacity(room.getCapacity());
            roomOnList.setActive(room.isActive());
            roomsList.add(roomOnList);
        }
        return roomsList;
    }

    public Room setDetailsNewRoom(AdminRoomResultDTO adminRoomResultDTO) {
        Room newRoom = new Room();
        newRoom.setBuilding(adminRoomResultDTO.getBuilding());
        newRoom.setFloor(Integer.parseInt(adminRoomResultDTO.getFloor()));
        newRoom.setNumber(Integer.parseInt(adminRoomResultDTO.getClassRoomNumber()));
        if (adminRoomResultDTO.getDescription() != null) {
            newRoom.setDescription(adminRoomResultDTO.getDescription());
        }
        newRoom.setCapacity(adminRoomResultDTO.getCapacity());
        newRoom.setActive(adminRoomResultDTO.isActive());
        newRoom.setCreatedOn(new Date());
        newRoom.setLastModifiedOn(new Date());

        return newRoom;
    }

    public void setEditRoomDetails(final AdminRoomResultDTO adminRoomResultDTO, Room editRoom) {
        adminRoomResultDTO.setBuilding(editRoom.getBuilding());
        adminRoomResultDTO.setFloor(Integer.toString(editRoom.getFloor()));
        adminRoomResultDTO.setClassRoomNumber(Integer.toString(editRoom.getNumber()));
        adminRoomResultDTO.setDescription(editRoom.getDescription());
        adminRoomResultDTO.setCapacity(editRoom.getCapacity());
        adminRoomResultDTO.setActive(editRoom.isActive());
    }

    public void setUpdateRoomDetails(AdminRoomResultDTO adminRoomResultDTO, final Room editRoom) {

        editRoom.setBuilding(adminRoomResultDTO.getBuilding());
        editRoom.setFloor(Integer.parseInt(adminRoomResultDTO.getFloor()));
        editRoom.setNumber(Integer.parseInt(adminRoomResultDTO.getClassRoomNumber()));
        if (adminRoomResultDTO.getDescription() != null) {
            editRoom.setDescription(adminRoomResultDTO.getDescription());
        }
        editRoom.setCapacity(adminRoomResultDTO.getCapacity());
        editRoom.setActive(adminRoomResultDTO.isActive());
        editRoom.setLastModifiedOn(new Date());
    }
}

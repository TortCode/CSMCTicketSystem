package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminRoomResultDTO;
import edu.utdallas.csmc.web.helper.AdminRoomHelper;
import edu.utdallas.csmc.web.model.misc.Room;
import edu.utdallas.csmc.web.repository.RoomRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Log4j2
public class AdminRoomsService {

    @Autowired
    private RoomRepository roomRepository;

    AdminRoomHelper adminRoomHelper = new AdminRoomHelper();

    public List<AdminRoomResultDTO> getListofRooms() {
        List<Room> rooms = roomRepository.findAll();
        List<AdminRoomResultDTO> roomList = adminRoomHelper.setRoomList(rooms);
        return roomList;
    }

    public void submitRoomDetails(AdminRoomResultDTO adminRoomResultDTO) {
        Room newRoom = adminRoomHelper.setDetailsNewRoom(adminRoomResultDTO);
        roomRepository.save(newRoom);
    }

    public void getRoomDetails(final AdminRoomResultDTO adminRoomResultDTO) {
        Optional<Room> room = roomRepository.findById(adminRoomResultDTO.getRoomId());
        if(room.isPresent()){
            Room editRoom = room.get();
            adminRoomHelper.setEditRoomDetails(adminRoomResultDTO, editRoom);
        }else{
            log.info("Room Not Found");
        }
    }

    public void updateRoomDetails(AdminRoomResultDTO adminRoomResultDTO) {
        Optional<Room> room = roomRepository.findById(adminRoomResultDTO.getRoomId());
        if(room.isPresent()){
            Room editRoom = room.get();
            adminRoomHelper.setUpdateRoomDetails(adminRoomResultDTO,editRoom);
            roomRepository.save(editRoom);
        }else{
            log.info("Room Not Found");
        }
    }

    public String deleteRoom(UUID roomId) {
        Optional<Room> room = roomRepository.findById(roomId);
        if (room.isPresent()) {
            try {
                roomRepository.delete(room.get());

            } catch (ConstraintViolationException ex) {
                log.error(ex);
                return "redirect:/admin/room/edit/" + roomId;
            }
        } else {
            log.info("Room not found for id" + roomId.toString());
        }

        return "redirect:/admin/room/list";
    }
}

package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.AdminIpResultDTO;
import edu.utdallas.csmc.web.model.misc.IpAddress;
import edu.utdallas.csmc.web.model.misc.Room;
import edu.utdallas.csmc.web.util.IpChecker;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class AdminIpHelper {

    public List<AdminIpResultDTO> setIpDetails(List<IpAddress> ipAddresses) {
        List<AdminIpResultDTO> adminIpResultDTOList = new ArrayList<>();

        for(IpAddress ip : ipAddresses) {
            AdminIpResultDTO resultIp = new AdminIpResultDTO();
            resultIp.setIpId(ip.getId());
            resultIp.setIpAddress(IpChecker.longToIp(ip.getAddress()));
            if(ip.getRoom()!= null) {
                resultIp.setBuilding(ip.getRoom().getBuilding());
                resultIp.setFloor(String.valueOf(ip.getRoom().getFloor()));
                resultIp.setClassRoomNumber(String.valueOf(ip.getRoom().getNumber()));
            }
            resultIp.setBlocked(ip.isBlocked());

            adminIpResultDTOList.add(resultIp);
        }
        return adminIpResultDTOList;
    }

    public IpAddress setNewIp(AdminIpResultDTO adminIpResultDTO) {
        IpAddress newIp = new IpAddress();
        newIp.setAddress(IpChecker.ipToLong(adminIpResultDTO.getIpAddress()));
        if(adminIpResultDTO.getBuilding()!=null && adminIpResultDTO.getFloor()!=null && adminIpResultDTO.getClassRoomNumber()!=null) {
            Room room = new Room();
            room.setBuilding(adminIpResultDTO.getBuilding());
            room.setFloor(Integer.parseInt(adminIpResultDTO.getFloor()));
            room.setNumber(Integer.parseInt(adminIpResultDTO.getClassRoomNumber()));
            newIp.setRoom(room);
        }
        newIp.setBlocked(adminIpResultDTO.isBlocked());
        return newIp;
    }

    public void setEditIpDetails(final AdminIpResultDTO adminIpResultDTO, IpAddress editIp) {
        adminIpResultDTO.setIpId(editIp.getId());
        adminIpResultDTO.setIpAddress(IpChecker.longToIp(editIp.getAddress()));
        if(editIp.getRoom()!=null){
            adminIpResultDTO.setBuilding(editIp.getRoom().getBuilding());
            adminIpResultDTO.setFloor(String.valueOf(editIp.getRoom().getFloor()));
            adminIpResultDTO.setClassRoomNumber(String.valueOf(editIp.getRoom().getNumber()));
        }
        adminIpResultDTO.setBlocked(editIp.isBlocked());
    }

    public void updateIp(final IpAddress currentIp, AdminIpResultDTO adminIpResultDTO) {
        currentIp.setAddress(IpChecker.ipToLong(adminIpResultDTO.getIpAddress()));
        if(adminIpResultDTO.getBuilding()!="" && adminIpResultDTO.getFloor() != "" && adminIpResultDTO.getClassRoomNumber() !="") {
            Room room = new Room();
            room.setBuilding(adminIpResultDTO.getBuilding());
            room.setFloor(Integer.parseInt(adminIpResultDTO.getFloor()));
            room.setNumber(Integer.parseInt(adminIpResultDTO.getClassRoomNumber()));
            currentIp.setRoom(room);
        }
        currentIp.setBlocked(adminIpResultDTO.isBlocked());
    }
}

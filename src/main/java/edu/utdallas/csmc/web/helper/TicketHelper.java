package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.model.misc.Ticket;
import edu.utdallas.csmc.web.dto.TicketDTO;
import edu.utdallas.csmc.web.model.misc.TicketClaim;
import edu.utdallas.csmc.web.model.user.User;

public class TicketHelper {
    public static TicketDTO ticket2DTO(Ticket ticket) {
        TicketDTO tixDTO = new TicketDTO();
        tixDTO.setId(ticket.getId());
        tixDTO.setStudentNetId(ticket.getStudent().getUsername());
        tixDTO.setStudentName(ticket.getStudent().getName());
        TicketClaim lastClaim = ticket.getLastClaim();
        if (ticket.getStatus().getName().equals("CLAIMED") && lastClaim != null) {
            User u = ticket.getLastClaim().getUser();
            tixDTO.setMentorNetId(u.getUsername());
            tixDTO.setMentorName(u.getFirstName()+" "+u.getLastName());
        }
        tixDTO.setCourse(ticket.getCourse().getFullNumber());
        tixDTO.setTopic(ticket.getTopic());
        tixDTO.setType(ticket.getActivity().getName());
        tixDTO.setStatus(ticket.getStatus().getName());
        tixDTO.setCreatedAt(ticket.getCreatedAt());
        tixDTO.setResolvedAt(ticket.getResolvedAt());
        tixDTO.setInfo(ticket.getAdditionalInfo());
        tixDTO.setTableNo(ticket.getTableNo());
        return tixDTO;
    }
}

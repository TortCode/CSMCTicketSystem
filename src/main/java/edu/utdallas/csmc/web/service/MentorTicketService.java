package edu.utdallas.csmc.web.service;


import edu.utdallas.csmc.web.dto.MentorSwipeEntryCourseResultDTO;
import edu.utdallas.csmc.web.dto.MentorSwipeEntryCourseResultDTO;
import edu.utdallas.csmc.web.dto.TicketDTO;
import edu.utdallas.csmc.web.model.misc.TicketClaim;
import edu.utdallas.csmc.web.model.schedule.ScheduledShift;
import edu.utdallas.csmc.web.model.schedule.Shift;
import edu.utdallas.csmc.web.model.schedule.ShiftAssignment;
import edu.utdallas.csmc.web.model.schedule.ShiftSubject;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.SemesterRepository;
import edu.utdallas.csmc.web.repository.TicketClaimRepository;
import edu.utdallas.csmc.web.repository.TicketRepository;
import edu.utdallas.csmc.web.repository.TicketStatusRepository;
import edu.utdallas.csmc.web.repository.UserRepository;
import edu.utdallas.csmc.web.util.DateUtil;
import edu.utdallas.csmc.web.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import edu.utdallas.csmc.web.model.misc.Ticket;
import edu.utdallas.csmc.web.helper.TicketHelper;


import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Date;


@Service
@Log4j2
public class MentorTicketService {
    @Autowired
    private TicketStatusRepository ticketStatusRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TicketClaimRepository ticketClaimRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SemesterRepository semesterRepository;


    public boolean claim(UUID ticketID, UUID mentorId) {
        Optional<Ticket> t = ticketRepository.findById(ticketID);
        if (t.isPresent()) {
            Ticket ticket = t.get();
            if (ticket.getStatus().getName().equals("UNCLAIMED")) {
                beginClaim(ticket, mentorId);
                ticket.setStatus(ticketStatusRepository.findByName("CLAIMED"));
                ticketRepository.save(ticket);
                return true;
            }
        }
        return false;
    }


    public boolean unclaim(UUID ticketID) {
        Optional<Ticket> t = ticketRepository.findById(ticketID);
        if (t.isPresent()) {
            Ticket ticket = t.get();
            if (ticket.getStatus().getName().equals("CLAIMED")) {
                if (!endClaim(ticket)) {
                    return false;
                }
                ticket.setStatus(ticketStatusRepository.findByName("UNCLAIMED"));
                ticketRepository.save(ticket);
                return true;
            }
        }
        return false;
    }


    public boolean finish(UUID ticketID) {
        Optional<Ticket> t = ticketRepository.findById(ticketID);
        if (t.isPresent()) {
            Ticket ticket = t.get();
            if (ticket.getStatus().getName().equals("CLAIMED")) {
                if (!endClaim(ticket)) {
                    return false;
                }
                ticket.setResolvedAt(new Date());
                ticket.setStatus(ticketStatusRepository.findByName("FINISHED"));
                ticketRepository.save(ticket);
                return true;
            }
        }
        return false;
    }


    private void beginClaim(Ticket ticket, UUID mentorId) {
        User mentor = userRepository.getUser(mentorId);
        TicketClaim claim = new TicketClaim();
        claim.setTicket(ticket);
        claim.setUser(mentor);
        claim.setTimeIn(new Date());
        ticket.getClaims().add(claim);
        ticketClaimRepository.save(claim);
    }


    private boolean endClaim(Ticket ticket) {
        TicketClaim lastClaim = ticket.getLastClaim();
        lastClaim.setTimeOut(new Date());
        ticketClaimRepository.save(lastClaim);
        return true;
    }


    public TicketDTO getMentorTicket(UUID ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        Ticket t = ticket.get();
        return TicketHelper.ticket2DTO(t);
    }


    public List<TicketDTO> getUnclaimedTickets() {
        List<Ticket> tickets = ticketRepository.findByStatusNameOrderByCreatedAt("UNCLAIMED");
        List<TicketDTO> unclaimeds = new ArrayList<TicketDTO>();
        for (Ticket t : tickets) {
            unclaimeds.add(TicketHelper.ticket2DTO(t));
        }
        return unclaimeds;
    }

    public List<TicketDTO> getClaimedTickets() {
        List<Ticket> tickets = ticketRepository.findByStatusNameOrderByCreatedAt("CLAIMED");
        List<TicketDTO> claimedTickets = new ArrayList<TicketDTO>();
        for (Ticket t : tickets) {
            claimedTickets.add(TicketHelper.ticket2DTO(t));
        }
        return claimedTickets;
    }

    public boolean kill(UUID ticketId) {
        Optional<Ticket> t = ticketRepository.findById(ticketId);
        if (t.isPresent()) {
            ticketRepository.delete(t.get());
            return true;
        }
        return false;
    }

    public List<MentorSwipeEntryCourseResultDTO> getMentors() {
        List<MentorSwipeEntryCourseResultDTO> mentors = new ArrayList<>();
        List<User> users = userRepository.getMentors();
        for (User u: users) {
            MentorSwipeEntryCourseResultDTO mentorSwipeEntryCourseResultDTO = new MentorSwipeEntryCourseResultDTO();
            mentorSwipeEntryCourseResultDTO.setId(u.getId());


            if (u.getProfile() != null && u.getProfile().getPreferredName() != null) {
                mentorSwipeEntryCourseResultDTO.setName(u.getProfile().getPreferredName() + u.getLastName());
            } else {
                mentorSwipeEntryCourseResultDTO.setName(u.getName());
            }
            mentors.add(mentorSwipeEntryCourseResultDTO);
        }
        return mentors;
    }


    public List<User> getCurrentScheduledMentors() {
        List<ScheduledShift> scheduledShifts = semesterRepository.findSemesterByActive(true).get().getSchedule().getScheduleShifts();
        Date currentDate = new Date();
        LocalTime currentTime = LocalTime.now();

        // Find the shift for the current date and time
        Optional<ScheduledShift> scheduledShift = scheduledShifts
            .stream()
            .filter(currentShift ->
                currentShift.getDate().compareTo(DateUtil.atStartOfDay(currentDate)) == 0 &&
                currentTime.isAfter(currentShift.getShift().getStartTime().toLocalTime()) &&
                currentTime.isBefore(currentShift.getShift().getEndTime().toLocalTime())
            )
            .reduce((a, b) -> { throw new IllegalStateException("Multiple shifts found"); });

        if (!scheduledShift.isPresent()) {
            return userRepository.getMentors();
        }

        // Add all scheduled mentors to the list
        List<User> mentors = new ArrayList<>();
        for (ShiftAssignment sa : scheduledShift.get().getAssignments()) {
            if (sa.getAbsence() == null) {
                mentors.add(sa.getMentor());
            }
        }
        return mentors;
    }
}
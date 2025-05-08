package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.MentorAbsenceFormResultDTO;
import edu.utdallas.csmc.web.dto.MentorAbsenceResultDTO;
import edu.utdallas.csmc.web.dto.MentorMyAbsenceResultDTO;
import edu.utdallas.csmc.web.dto.MentorOtherAbsenceResultDTO;
import edu.utdallas.csmc.web.model.schedule.Absence;
import edu.utdallas.csmc.web.model.schedule.ShiftAssignment;
import edu.utdallas.csmc.web.model.schedule.ShiftSubject;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.AbsenceRepository;
import edu.utdallas.csmc.web.repository.ShiftAssignmentRepository;
import edu.utdallas.csmc.web.repository.UserRepository;
import edu.utdallas.csmc.web.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.*;

@Service
@Log4j2
public class MentorAbsenceService {

    @Autowired
    private DefaultUsernameService defaultUsernameService;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private ShiftAssignmentRepository shiftAssignmentRepository;

    @Autowired
    private UserRepository userRepository;

    private User mentor;
    private Map<String, List<String>> dateShiftMapping;


    public void submitAbsence(String date, String time, String reason, String absenceId) throws ParseException {

        if (absenceId == null || absenceId.isEmpty()) {
            Date dateConverted = DateUtil.dateFormatToDate.parse(date);
            Date timeConverted = DateUtil.dateFormatToHoursMins.parse(time);
            List<ShiftAssignment> sa = shiftAssignmentRepository.getShiftAssignmentByDateAndTime(defaultUsernameService.getUsername(), dateConverted, timeConverted);
            if (!sa.isEmpty() && sa.size() == 1 && sa.get(0).getAbsence() == null) {
                Absence newAbsence = new Absence();
                newAbsence.setReason(reason);
                newAbsence.setAssignment(sa.get(0));
                newAbsence.setCreatedOn(new Date());
                newAbsence.setLastModifiedOn(new Date());
                sa.get(0).setAbsence(newAbsence);
                absenceRepository.save(newAbsence);
                shiftAssignmentRepository.save(sa.get(0));
            }

        } else {

            Optional<Absence> absence = absenceRepository.findById(UUID.fromString(absenceId));
            if (absence.isPresent()) {
                absence.get().setReason(reason);
                absenceRepository.save(absence.get());
            }

        }
    }

    public MentorAbsenceFormResultDTO getMentorAbsenceFormResultDTO() {
        dateShiftMapping = new HashMap<>();
        MentorAbsenceFormResultDTO tmp = new MentorAbsenceFormResultDTO();
        tmp.setDateList(new TreeSet<>(new Date_Comparator()));
        tmp.setShiftList(new ArrayList<>());
        tmp.setReason("");
        List<ShiftAssignment> sa = shiftAssignmentRepository.getShiftAssignmentByMentorUserName(defaultUsernameService.getUsername());
        for (ShiftAssignment s : sa) {
            if (s.getAbsence() != null) continue;
            String sd = s.getScheduledShift().getDate().toString().split(" ")[0];
            String st = DateUtil.dateFormatToHoursMins.format(
                    s.getScheduledShift().getShift().getStartTime());

            dateShiftMapping.computeIfAbsent(sd, shifts -> new ArrayList<>()).add(st);
            tmp.getDateList().add(sd);
        }
        for (Map.Entry<String, List<String>> set : dateShiftMapping.entrySet()) {
            tmp.getShiftList().addAll(set.getValue());
            break;
        }

        return tmp;
    }


    public MentorAbsenceFormResultDTO updateAbsence(UUID absenceId) {
        MentorAbsenceFormResultDTO tmp = new MentorAbsenceFormResultDTO();
        tmp.setDateList(new TreeSet<>(new Date_Comparator()));
        tmp.setShiftList(new ArrayList<>());
        tmp.setReason("");
        tmp.setAbsenceId(absenceId);
        Optional<Absence> absence = absenceRepository.findById(absenceId);
        if (absence.isPresent()) {
            List<ShiftAssignment> sa = shiftAssignmentRepository.getShiftAssignmentByMentorUserName(defaultUsernameService.getUsername());
            for (ShiftAssignment s : sa) {
                if (s.getAbsence() == null) continue;
                if (s.getAbsence().getId().equals(absenceId)) {
                    String sd = s.getScheduledShift().getDate().toString().split(" ")[0];
                    String st = DateUtil.dateFormatToHoursMins.format(
                            s.getScheduledShift().getShift().getStartTime());

                    tmp.getDateList().add((sd + "\t" + st));
                    tmp.setDateShift((sd + "\t" + st));
                    tmp.setReason(absence.get().getReason());
                    break;
                }
            }
        }
        return tmp;
    }


    public void cancelAbsence(UUID absenceId) {
        Optional<Absence> absence = absenceRepository.findById(absenceId);
        if (absence.isPresent()) {
            Optional<ShiftAssignment> sa = shiftAssignmentRepository.findById(absence.get().getAssignment().getId());
            if (sa.isPresent()) {
                sa.get().setAbsence(null);
                shiftAssignmentRepository.save(sa.get());
                absenceRepository.deleteById(absenceId);
            }
        }
    }

    public void relieveShift(UUID absenceId) {
        Optional<Absence> absence = absenceRepository.findById(absenceId);
        if(absence.isPresent()){
            Optional<ShiftAssignment> sa = shiftAssignmentRepository.findById(absence.get().getSubstitute().getId());
            if(sa.isPresent()){
                UUID temp = absence.get().getSubstitute().getId();
                absence.get().setSubstitute(null);
                absenceRepository.save(absence.get());
                shiftAssignmentRepository.deleteById(temp);
            }
        }
    }

    public void claimShift(UUID absenceId) {
        Optional<Absence> absence = absenceRepository.findById(absenceId);
        if (absence.isPresent()) {
            ShiftAssignment shiftAssignment = absence.get().getAssignment();
            List<ShiftSubject> subjects = shiftAssignment.getScheduledShift().getShift().getSubjects();
            List<User> mentors = new ArrayList<>();
            for(ShiftSubject subject : subjects){
                mentors.addAll(subject.getMentors());
            }
            if(!mentors.contains(mentor)) {
                ShiftAssignment newShiftAssignment = new ShiftAssignment();

                newShiftAssignment.setScheduledShift(shiftAssignment.getScheduledShift());
                newShiftAssignment.setSession(shiftAssignment.getSession());
                newShiftAssignment.setSubject(shiftAssignment.getSubject());
                newShiftAssignment.setMentor(mentor);
                shiftAssignmentRepository.save(newShiftAssignment);

                absence.get().setSubstitute(newShiftAssignment);
                absenceRepository.save(absence.get());
            }
        }
    }

    public MentorAbsenceResultDTO getAbsenceDetails() {
        Optional<User> user = userRepository.findByUsername(defaultUsernameService.getUsername());
        if (user.isPresent()) {
            mentor = user.get();
        }

        String f_name = mentor.getFirstName();
        String l_name = mentor.getLastName();
        String curr_mentor = f_name + " " + l_name;

        MentorAbsenceResultDTO mentorAbsenceResultDTO = new MentorAbsenceResultDTO();

        List<MentorMyAbsenceResultDTO> mentorMyAbsenceResultDTOList = new ArrayList<>();
        List<Absence> myAbsenceDetails = absenceRepository.getAbsencesByMentorUserName(defaultUsernameService.getUsername());

        for (Absence absence : myAbsenceDetails) {

            MentorMyAbsenceResultDTO mentorMyAbsenceResultDTO = new MentorMyAbsenceResultDTO();
            mentorMyAbsenceResultDTO.setAbsenceId(absence.getId().toString());
            mentorMyAbsenceResultDTO.setShiftDate(absence.getAssignment().getScheduledShift().getDate());
            mentorMyAbsenceResultDTO.setStartTime(absence.getAssignment().getScheduledShift().getShift().getStartTime());
            mentorMyAbsenceResultDTO.setReason(absence.getReason());

            if (absence.getSubstitute() != null) {
                String firstName = absence.getSubstitute().getMentor().getFirstName();
                String lastName = absence.getSubstitute().getMentor().getLastName();

                if (!StringUtils.isEmpty(firstName) && !StringUtils.isEmpty(lastName)) {
                    String fullName = firstName + " " + lastName;
                    mentorMyAbsenceResultDTO.setSubstitute(fullName);
                    mentorMyAbsenceResultDTO.setSubstituteFlag(true);
                }

            }

            boolean futureDateFlag = DateUtil.isDateLaterThanCurrentDate(absence.getAssignment().getScheduledShift().getDate());
            boolean currentDateFlag = DateUtil.isDateEqualToCurrentDate(absence.getAssignment().getScheduledShift().getDate());
            boolean futureTimeFlag = DateUtil.isTimeLaterThanCurrentTime(absence.getAssignment().getScheduledShift().getShift().getStartTime());

            mentorMyAbsenceResultDTO.setFutureDateFlag(futureDateFlag);
            mentorMyAbsenceResultDTO.setCurrentDateFlag(currentDateFlag);
            mentorMyAbsenceResultDTO.setFutureTimeFlag(futureTimeFlag);

            if (futureDateFlag || (currentDateFlag && futureTimeFlag)) {
                mentorMyAbsenceResultDTO.setFutureFlag(true);
            }

            mentorMyAbsenceResultDTOList.add(mentorMyAbsenceResultDTO);
        }

        //Get Other mentor absence details
        List<MentorOtherAbsenceResultDTO> mentorOtherAbsenceResultDTOList = new ArrayList<>();
        List<Absence> otherAbsenceDetails = absenceRepository.getAbsencesByMentorUserNameAndToday(defaultUsernameService.getUsername());

        for (Absence absence : otherAbsenceDetails) {
            MentorOtherAbsenceResultDTO mentorOtherAbsenceResultDTO = new MentorOtherAbsenceResultDTO();
            mentorOtherAbsenceResultDTO.setId(absence.getId());
            mentorOtherAbsenceResultDTO.setShiftDate(absence.getAssignment().getScheduledShift().getDate());
            mentorOtherAbsenceResultDTO.setStartTime(absence.getAssignment().getScheduledShift().getShift().getStartTime());
            String topic = absence.getAssignment().getSession() != null ? absence.getAssignment().getSession().getSession().getTopic() : null;
            mentorOtherAbsenceResultDTO.setTopic(topic);

            String mentorFirstName = absence.getAssignment().getMentor().getFirstName();
            String mentorLastName = absence.getAssignment().getMentor().getLastName();
            if (!StringUtils.isEmpty(mentorFirstName) && !StringUtils.isEmpty(mentorLastName)) {
                String mentorFullName = mentorFirstName + " " + mentorLastName;
                mentorOtherAbsenceResultDTO.setMentor(mentorFullName);
            }

            if (absence.getSubstitute() != null) {
                String substituteFirstName = absence.getSubstitute().getMentor().getFirstName();
                String substituteLastName = absence.getSubstitute().getMentor().getLastName();

                if (!StringUtils.isEmpty(substituteFirstName) && !StringUtils.isEmpty(substituteLastName)) {
                    String substituteFullName = substituteFirstName + " " + substituteLastName;
                    mentorOtherAbsenceResultDTO.setSubstitute(substituteFullName);
                }
            }

            mentorOtherAbsenceResultDTOList.add(mentorOtherAbsenceResultDTO);
        }

        mentorAbsenceResultDTO.setMyAbsenceDetails(mentorMyAbsenceResultDTOList);
        mentorAbsenceResultDTO.setOtherAbsenceDetails(mentorOtherAbsenceResultDTOList);
        mentorAbsenceResultDTO.setCurrent_mentor(curr_mentor);
        return mentorAbsenceResultDTO;
    }

    public List<String> getShiftsByDate(String date) {
        return dateShiftMapping.get(date);
    }


    class Date_Comparator implements Comparator<String> {
        public int compare(String date1, String date2)
        {
            String first_Date;
            String second_Date;
            first_Date = date1;
            second_Date = date2;
            return first_Date.compareTo(second_Date);
        }
    }
}

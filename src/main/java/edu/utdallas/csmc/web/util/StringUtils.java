package edu.utdallas.csmc.web.util;

import edu.utdallas.csmc.web.model.course.Section;
import edu.utdallas.csmc.web.model.file.File;
import edu.utdallas.csmc.web.model.session.QuizTimeSlot;
import edu.utdallas.csmc.web.model.session.SessionTimeSlot;
import edu.utdallas.csmc.web.model.user.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringUtils {

    public static String SectionToString(List<Section> sectionList) {
        StringBuilder sb = new StringBuilder();

        Set<String> sectionSet = new HashSet<>();
        for (Section sec : sectionList)
            sectionSet.add(sec.getCourse().getDepartment().getAbbreviation() + " "
                    + sec.getCourse().getNumber() + "." + sec.getNumber());
        for (String sec : sectionSet) {
            if (sb.length() == 0)
                sb.append(sec);
            else
                sb.append(", ").append(sec);
        }

        return sb.toString();
    }
    private static String dateFormatting(Date d) {
        if (d == null) return "";
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        return formatter.format(d);
    }

    public static String SessionTimeSlotsToString(List<SessionTimeSlot> timeslots) {
        if (timeslots == null) return "";


        Date begin = null;
        Date end = null;
        for (SessionTimeSlot s : timeslots) {
            if (begin == null || begin.compareTo(s.getStartTime()) > 0)
                begin = s.getStartTime();
            if (end == null || end.compareTo(s.getEndTime()) < 0)
                end = s.getEndTime();
        }
        return dateFormatting(begin) + "-" + dateFormatting(end);
    }

    public static String QuiztimeSlotToString(QuizTimeSlot timeslot) {
        if (timeslot == null) return "";
        return dateFormatting(timeslot.getStartTime()) + "-" + dateFormatting(timeslot.getEndTime());
    }

    public static String StartDateAndEndDateToString(Date st, Date en) {
        return dateFormatting(st) + "-" + dateFormatting(en);
    }

    public static String FileListToString(List<File> files) {
        StringBuilder sb = new StringBuilder();

        Set<String> filename = new HashSet<>();
        for (File f : files)
            filename.add(f.getName());
        for (String f : filename) {
            if (sb.length() == 0)
                sb.append(f);
            else
                sb.append(", ").append(f);
        }

        return sb.toString();
    }

    public static String FileListToFileNameAndUIString(List<File> files) {
        StringBuilder sb = new StringBuilder();

        Set<String> filename = new HashSet<>();
        for (File f : files)
            filename.add(f.getName()+"##"+f.getId());
        for (String f : filename) {
            if (sb.length() == 0)
                sb.append(f);
            else
                sb.append(", ").append(f);
        }
        return sb.toString();
    }

    public static String InstructorsToString(List<Section> sectionList) {
        StringBuilder sb = new StringBuilder();
        Set<String> instructors = new HashSet<>();
        for (Section sec : sectionList) {
            for (User u : sec.getInstructors()) {
                String tmp = u.getFirstName() + " " + u.getLastName();
                instructors.add(tmp);
            }
        }
        for (String i : instructors)
            if (sb.length() == 0) sb.append(i);
            else sb.append(", ").append(i);
        return sb.toString();
    }
}

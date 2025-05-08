package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.model.session.Session;
import edu.utdallas.csmc.web.model.session.SessionAttendance;
import edu.utdallas.csmc.web.model.session.WalkInAttendance;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.util.DateUtil;
import javafx.util.Pair;
import org.springframework.lang.Nullable;

import java.util.List;

public class ReportExportHelper {
    public String convertAttendanceToString(List<? extends SessionAttendance> attendees, Session session)  {
        StringBuilder content = new StringBuilder();
        boolean isGraded = false;
        boolean isNumericGraded = false;
        if (attendees != null) {
            if (session.isGraded()) {
                content.append("First Name,Last Name,NetID,Grade,Time In,Time Out").append("\n");
                isGraded = true;
            } else {
                content.append("First Name,Last Name,NetID,Time In,Time Out").append("\n");
            }

            if (session.isNumericGrade()) {
                isNumericGraded = true;
            }
            for (SessionAttendance attendance : attendees) {
                content.append(safeToString(attendance.getUser().getFirstName())).append(",")
                        .append(safeToString(attendance.getUser().getLastName())).append(",")
                        .append(safeToString(attendance.getUser().getUsername())).append(",");
                if (isGraded) {
                    if (isNumericGraded){
                        content.append(safeToString(attendance.getGrade()));
                    } else {
                        if (attendance.getGrade().equals(1)) {
                            content.append("PASS");
                        } else {
                            content.append("FAIL");
                        }
                    }
                    content.append(",");
                }
                content.append(safeToString(attendance.getTimeIn())).append(",")
                        .append(safeToString(attendance.getTimeOut()));
                content.append("\n");
            }
        }
        return content.toString();
    }

    public String convertWalkInAttendanceToString(List<WalkInAttendance> walkInAttendances) {
        StringBuilder content = new StringBuilder();
        DateUtil dateUtil = new DateUtil();

        if (walkInAttendances != null) {
            content.append("Last Name, First Name, NetID, Time In, Time Out, Topic, Activity, Feedback").append("\n");
            for (WalkInAttendance attendance : walkInAttendances) {
                content.append(safeToString(attendance.getUser().getLastName())).append(",")
                        .append(safeToString(attendance.getUser().getFirstName())).append(",")
                        .append(safeToString(attendance.getUser().getUsername())).append(",")
                        .append(dateUtil.dateFormat(attendance.getTimeIn())).append(",")
                        .append(dateUtil.dateFormat(attendance.getTimeOut())).append(",")
                        .append(safeToString(attendance.getTopic())).append(",")
                        .append(safeToString(attendance.getActivity().getName())).append(",")
                        .append(safeToString(attendance.getFeedback())).append(",");
                content.append("\n");
            }
        }
        return content.toString();
    }

    public String convertSessionAttendanceToString (List<Pair<User, SessionAttendance>> sessionReport, Session session) {
        StringBuilder content = new StringBuilder();
        DateUtil dateUtil = new DateUtil();
        boolean isNumericGraded = false;

        if (session.isGraded() && session.isNumericGrade()) {
            isNumericGraded = true;
        }

        content.append("Last Name, First Name, NetID, Time In, Time Out, Grade").append("\n");

        for (Pair<User, SessionAttendance> student : sessionReport) {
            content.append(safeToString(student.getKey().getLastName())).append(",")
                    .append(safeToString(student.getKey().getFirstName())).append(",")
                    .append(safeToString(student.getKey().getUsername())).append(",");
            if (student.getValue() != null) {
                content.append(dateUtil.dateFormat(student.getValue().getTimeIn())).append(",")
                        .append(dateUtil.dateFormat(student.getValue().getTimeOut())).append(",");
                if (student.getValue().getGrade() != null) {
                    Integer grade = student.getValue().getGrade();
                    if (isNumericGraded) {
                        content.append(grade.toString());
                    } else {
                        if (grade.equals(1)) {
                            content.append("PASS");
                        } else {
                            content.append("FAIL");
                        }
                    }
                }
            }
            content.append("\n");
        }
        return content.toString();
    }

    static String safeToString(@Nullable Object obj) {
        return obj == null ? "" : obj.toString();
    }
}

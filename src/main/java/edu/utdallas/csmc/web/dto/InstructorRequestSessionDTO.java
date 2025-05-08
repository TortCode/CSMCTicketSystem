package edu.utdallas.csmc.web.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstructorRequestSessionDTO {
    String topic;
    String type;
    String studentInstructions;
  //  String mentorInstructions;
    String startDate;
    String endDate;
    List<String> sections;
    List<MultipartFile> files;
}

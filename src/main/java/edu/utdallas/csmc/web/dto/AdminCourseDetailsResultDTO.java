package edu.utdallas.csmc.web.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class AdminCourseDetailsResultDTO {
    String courseId;
    String departmentName;
    String name;
    String number;
    @Setter(AccessLevel.NONE)
    boolean supported;
    String description;
    String username;

    public void setSupported(String supportedInput){
        this.supported = supportedInput != null ? true : false;
    }

    public void setSupported(boolean supportedInput){
        this.supported = supportedInput;
    }
}

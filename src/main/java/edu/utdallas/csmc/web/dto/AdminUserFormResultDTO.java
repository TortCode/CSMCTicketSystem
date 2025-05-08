package edu.utdallas.csmc.web.dto;

import edu.utdallas.csmc.web.model.user.Role;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AdminUserFormResultDTO {

    UUID userId;
    String userName;
    String firstName;
    String lastName;
    String cardId;
    String scanCode;
    List<String> rolesNameList;
    List<String> rolesIdList;
    List<Boolean> roleSelectedList;
    String message = "";
}

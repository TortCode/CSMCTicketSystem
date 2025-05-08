package edu.utdallas.csmc.web.dto;

import edu.utdallas.csmc.web.model.user.User;
import lombok.Data;

@Data
public class RegisterLdapObject {
    User user;
    boolean valid;
}

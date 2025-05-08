package edu.utdallas.csmc.web.dto;

import lombok.Data;

@Data
public class LdapSSOObject {
    boolean valid;
    String firstName;
    String lastName;
}

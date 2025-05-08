package edu.utdallas.csmc.web.dto;

import edu.utdallas.csmc.web.model.user.User;

import java.util.List;

import lombok.Data;


@Data
public class UserDataTableResultDTO<UserResultDTO> {

    private int draw;
    private int start;
    private long recordsTotal;
    private long recordsFiltered;
    private List<UserResultDTO> data;

}

package edu.utdallas.csmc.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class SwipesDataTableResultDTO {
    private int draw;
    private int start;
    private long recordsTotal;
    private long recordsFiltered;
    private List<AdminSwipeResultDTO> data;
}

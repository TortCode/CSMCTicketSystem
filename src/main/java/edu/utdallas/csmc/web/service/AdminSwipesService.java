package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminSwipeResultDTO;
import edu.utdallas.csmc.web.dto.SwipesDataTableResultDTO;
import edu.utdallas.csmc.web.helper.AdminSwipeHelper;
import edu.utdallas.csmc.web.model.misc.Swipe;
import edu.utdallas.csmc.web.repository.SwipeRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Log4j2
public class AdminSwipesService {

    @Autowired
    private SwipeRepository swipeRepository;

    AdminSwipeHelper adminSwipeHelper = new AdminSwipeHelper();

    public SwipesDataTableResultDTO querySwipesByPage(DataTablesInput input) {
        SwipesDataTableResultDTO swipeDataTableResultDTO = new SwipesDataTableResultDTO();
        List<AdminSwipeResultDTO> swipeResultDTOList = new ArrayList<>();

        //custom sort by column
        Pageable pageable = adminSwipeHelper.sortByColumn(input);

        //query by tab or search box
        String word = adminSwipeHelper.filterWord(input);

        Page<Swipe> swipePage = swipeRepository.findUsersByPage2(word, pageable);
        List<Swipe> swipeList = swipePage.getContent();
        adminSwipeHelper.setSwipeList(swipeResultDTOList, swipeList);

        swipeDataTableResultDTO.setData(swipeResultDTOList);
        swipeDataTableResultDTO.setRecordsTotal(swipePage.getTotalElements());
        swipeDataTableResultDTO.setRecordsFiltered(swipePage.getTotalElements());
        swipeDataTableResultDTO.setDraw(input.getDraw());
        return swipeDataTableResultDTO;
    }

}

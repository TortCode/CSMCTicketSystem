package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.AdminSwipeResultDTO;
import edu.utdallas.csmc.web.model.misc.Swipe;
import edu.utdallas.csmc.web.util.DateUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.Order;

import java.util.List;

public class AdminSwipeHelper {

    public void setSwipeList(final List<AdminSwipeResultDTO> swipeResultDTOList, List<Swipe> swipeList) {

        for(Swipe swipes:swipeList)
        {
            AdminSwipeResultDTO adminSwipeResultDTO = new AdminSwipeResultDTO();

            adminSwipeResultDTO.setUser(swipes.getUser().getFirstName()+" "+swipes.getUser().getLastName());
            adminSwipeResultDTO.setTime(DateUtil.dateFormatTo12Hours.format(swipes.getTime()));
            adminSwipeResultDTO.setIpAddress(longToIp(swipes.getIp().getAddress()));

            swipeResultDTOList.add(adminSwipeResultDTO);
        }
    }

    public Pageable sortByColumn(DataTablesInput input){
        Pageable pageable = null;
        int page = (input.getStart())/(input.getLength());
        List<Order> orders = input.getOrder();
        if(!orders.isEmpty()){
            String columnName = "";
            Integer column = orders.get(0).getColumn();
            String dir = orders.get(0).getDir();
            switch (column) {
                case 0:
                    columnName = "user_id";
                    break;
                case 1:
                    columnName = "time";
                    break;
                case 2:
                    columnName = "ip_id";
                    break;
                default:
                    columnName = "user_id";
                    break;
            }
            if(dir.equals("desc")) {
                pageable = PageRequest.of(page, input.getLength(), Sort.by(Sort.Direction.DESC,columnName));
            }else{
                pageable = PageRequest.of(page, input.getLength(), Sort.by(Sort.Direction.ASC,columnName));
            }
        }else{
            pageable = PageRequest.of(page, input.getLength());
        }

        return pageable;
    }

    public String filterWord(DataTablesInput input){
        String word = "";
        word = input.getSearch().getValue();
        return word;
    }


    public String longToIp(long ip) {
        return ((ip >> 24) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 8) & 0xFF) + "."
                + (ip & 0xFF);
    }
}

package edu.utdallas.csmc.web.helper;

import edu.utdallas.csmc.web.dto.AdminUserFormResultDTO;
import edu.utdallas.csmc.web.model.user.Role;
import edu.utdallas.csmc.web.model.user.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.Order;

import java.util.ArrayList;
import java.util.List;

public class AdminUserHelper {

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
                    columnName = "netid";
                    break;
                case 1:
                    columnName = "first_name";
                    break;
                case 2:
                    columnName = "last_name";
                    break;
                default:
                    columnName = "netid";
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
        String tabFilter = input.getColumn("roles").getSearch().getValue();
        String word = "";
        if(tabFilter != null && !tabFilter.isEmpty()){
            word = tabFilter;
        }else{
            word = input.getSearch().getValue();
        }
        return word;
    }


    public void passParametersBackToForm(AdminUserFormResultDTO adminUserFormResultDTO, User user, List<Role> roles){

        adminUserFormResultDTO.setUserId(user.getId());
        adminUserFormResultDTO.setUserName(user.getUsername());
        adminUserFormResultDTO.setFirstName(user.getFirstName());
        adminUserFormResultDTO.setLastName(user.getLastName());
        adminUserFormResultDTO.setCardId(user.getCardId());
        adminUserFormResultDTO.setScanCode(user.getScancode());

        //fetch roles collection
        List<String> roleIDList = new ArrayList<>();
        List<String> roleNameList = new ArrayList<>();
        List<Boolean> roleSelectList = new ArrayList<>();
        for(Role role:roles){
            roleIDList.add(role.getId().toString());
            roleNameList.add(role.getName());
            roleSelectList.add(false);
        }
        adminUserFormResultDTO.setRolesIdList(roleIDList);
        adminUserFormResultDTO.setRolesNameList(roleNameList);

        List<Role> selectedRoles = user.getRoles();
        for(Role selected:selectedRoles){
            for(int i=0;i<roles.size();i++){
                if(roles.get(i).getId()==selected.getId()){
                    roleSelectList.set(i,true);
                    break;
                }
            }
        }
        adminUserFormResultDTO.setRoleSelectedList(roleSelectList);

    }

}
